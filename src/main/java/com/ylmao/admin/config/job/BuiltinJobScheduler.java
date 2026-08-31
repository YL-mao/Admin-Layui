package com.ylmao.admin.config.job;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.common.JobConfigCodes;
import com.ylmao.admin.entity.Job;
import com.ylmao.admin.mapper.JobMapper;
import com.ylmao.admin.service.ConfigRuntimeService;
import com.ylmao.admin.service.JobService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务调度器：按 sys_job 的 is_enabled + job_cron 动态安排下一次触发。
 * <p>
 * 实际执行走 {@link JobService#runScheduled}；跨实例互斥由 JobService 的 Redis 锁保证。
 */
@Component
public class BuiltinJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(BuiltinJobScheduler.class);

    /** 缺省 / 非法配置时的扫描间隔（秒）。 */
    private static final int DEFAULT_SCAN_SECONDS = 60;
    /** 扫描间隔下限（秒）；无上限。 */
    private static final int MIN_SCAN_SECONDS = 60;

    private final JobMapper jobMapper;
    private final JobService jobService;
    private final ConfigRuntimeService configRuntimeService;
    private final ZoneId appZoneId;
    private final TaskScheduler taskScheduler;

    /** jobCode -> 下一次单次触发 ScheduledFuture */
    private final Map<String, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();
    /** jobCode -> 用于计算 next 的 cron 快照 */
    private final Map<String, String> scheduledCronSnapshot = new ConcurrentHashMap<>();
    /** 同一 jobCode 的注册/取消串行，避免并发 schedule 留下孤儿触发。 */
    private final ConcurrentHashMap<String, Object> scheduleLocks = new ConcurrentHashMap<>();
    /** 扫描链的下一次预约；销毁时取消，避免优雅停机窗口再扫一次。 */
    private volatile ScheduledFuture<?> scanFuture;
    /** 销毁后不再安排扫描与任务触发。 */
    private volatile boolean stopped;

    public BuiltinJobScheduler(
            JobMapper jobMapper,
            JobService jobService,
            ConfigRuntimeService configRuntimeService,
            ZoneId appZoneId,
            @Qualifier("jobTaskScheduler") TaskScheduler taskScheduler
    ) {
        this.jobMapper = jobMapper;
        this.jobService = jobService;
        this.configRuntimeService = configRuntimeService;
        this.appZoneId = appZoneId;
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void start() {
        // 每次扫描后按最新 job.scanSecs 安排下一次，支持配置热更新。
        runScanAndReschedule();
    }

    /** 优雅停机：取消扫描链与各任务未触发预约，不打断已在执行的任务。 */
    @PreDestroy
    public void stop() {
        stopped = true;
        ScheduledFuture<?> scan = scanFuture;
        scanFuture = null;
        if (scan != null) {
            scan.cancel(false);
        }
        for (String jobCode : Set.copyOf(scheduledFutures.keySet())) {
            cancel(jobCode);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobEnabledChanged(JobEnabledChangedEvent event) {
        if (stopped || event == null || StrUtil.isBlank(event.jobCode())) {
            return;
        }
        if (event.enabled()) {
            scheduleNextRun(event.jobCode());
        } else {
            cancel(event.jobCode());
        }
    }

    private void runScanAndReschedule() {
        if (stopped) {
            return;
        }
        try {
            syncEnabledJobs();
        } catch (Exception ex) {
            log.warn("定时任务扫描失败", ex);
        }
        // 销毁后不再挂下一次扫描。
        if (stopped) {
            return;
        }
        scanFuture = taskScheduler.schedule(
                this::runScanAndReschedule,
                Instant.now().plus(Duration.ofSeconds(resolveScanSeconds())));
    }

    /** 读取 job.scanSecs；缺失/非法回退默认，低于下限则抬到下限（无上限）。 */
    private long resolveScanSeconds() {
        long seconds = DEFAULT_SCAN_SECONDS;
        BigDecimal configured = configRuntimeService.getNumber(JobConfigCodes.SCAN_SECONDS).orElse(null);
        if (configured != null) {
            try {
                seconds = configured.longValueExact();
            } catch (ArithmeticException ex) {
                // seconds 仍为默认值，仅记录告警。
                log.warn("job.scanSecs 非整秒 value={}，使用默认 {}", configured, DEFAULT_SCAN_SECONDS);
            }
        }
        if (seconds < MIN_SCAN_SECONDS) {
            log.warn("job.scanSecs={} 低于下限 {}，使用下限", seconds, MIN_SCAN_SECONDS);
            seconds = MIN_SCAN_SECONDS;
        }
        return seconds;
    }

    private void syncEnabledJobs() {
        List<Job> enabledJobs = jobMapper.selectList(new LambdaQueryWrapper<Job>().eq(Job::getIsEnabled, 1));
        if (enabledJobs == null) {
            enabledJobs = List.of();
        }

        Set<String> enabledCodes = new HashSet<>();
        // 启用集合：cron 未变则不重建，cron 变了则重建下一次触发
        for (Job job : enabledJobs) {
            if (job == null || StrUtil.isBlank(job.getJobCode())) {
                continue;
            }
            String code = job.getJobCode();
            enabledCodes.add(code);
            String currentCron = job.getJobCron();
            String snapshotCron = scheduledCronSnapshot.get(code);
            if (scheduledFutures.containsKey(code) && StrUtil.equals(snapshotCron, currentCron)) {
                continue;
            }
            scheduleNextRun(code);
        }

        // 停用集合：取消遗留触发
        for (String code : Set.copyOf(scheduledFutures.keySet())) {
            if (!enabledCodes.contains(code)) {
                cancel(code);
            }
        }
    }

    private void scheduleNextRun(String jobCode) {
        if (stopped || StrUtil.isBlank(jobCode)) {
            return;
        }
        synchronized (lockFor(jobCode)) {
            if (stopped) {
                return;
            }
            cancelLocked(jobCode);

            Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobCode, jobCode));
            if (job == null || job.getIsEnabled() == null || job.getIsEnabled() != 1) {
                return;
            }
            String cron = job.getJobCron();
            if (StrUtil.isBlank(cron)) {
                log.warn("定时任务 jobCron 为空，跳过注册 jobCode={}", jobCode);
                return;
            }

            CronExpression expression;
            try {
                expression = CronExpression.parse(cron);
            } catch (Exception ex) {
                log.warn("定时任务 cron 解析失败 jobCode={} cron={}", jobCode, cron);
                return;
            }

            // 每次注册都按 app.timezone，与列表下次执行、执行日志口径一致。
            ZonedDateTime now = ZonedDateTime.now(appZoneId);
            ZonedDateTime next = expression.next(now);
            if (next == null) {
                return;
            }

            Instant triggerAt = next.toInstant();
            ScheduledFuture<?> future = taskScheduler.schedule(() -> {
                try {
                    jobService.runScheduled(jobCode);
                } finally {
                    // 单次触发：执行完毕后注册下一次（保证下一次能读取最新 job_cron）
                    scheduleNextRun(jobCode);
                }
            }, triggerAt);

            // 执行期间仍保留 map 中的 future，避免扫描误判“未注册”再挂一份。
            scheduledFutures.put(jobCode, future);
            scheduledCronSnapshot.put(jobCode, cron);
        }
    }

    private void cancel(String jobCode) {
        if (StrUtil.isBlank(jobCode)) {
            return;
        }
        synchronized (lockFor(jobCode)) {
            cancelLocked(jobCode);
        }
    }

    /** 调用方须已持有该 jobCode 的 schedule 锁。 */
    private void cancelLocked(String jobCode) {
        ScheduledFuture<?> future = scheduledFutures.remove(jobCode);
        scheduledCronSnapshot.remove(jobCode);
        if (future != null) {
            future.cancel(false);
        }
    }

    private Object lockFor(String jobCode) {
        return scheduleLocks.computeIfAbsent(jobCode, _ -> new Object());
    }
}
