package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.common.JobCodes;
import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.job.JobEnabledChangedEvent;
import com.ylmao.admin.dto.JobDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.Job;
import com.ylmao.admin.entity.JobLog;
import com.ylmao.admin.mapper.JobLogMapper;
import com.ylmao.admin.mapper.JobMapper;
import com.ylmao.admin.service.job.JobHandler;
import com.ylmao.admin.vo.JobVo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private static final int MESSAGE_LIMIT = 1000;
    /** 分布式锁最长持有时间，防止进程异常退出后死锁。 */
    private static final Duration JOB_LOCK_TTL = Duration.ofMinutes(30);
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class);


    private final JobMapper jobMapper;
    private final JobLogMapper jobLogMapper;
    private final List<JobHandler> jobHandlers;
    private final ZoneId appZoneId;
    private final ApplicationEventPublisher eventPublisher;
    private final StringRedisTemplate stringRedisTemplate;

    /** jobCode -> Handler，启动时构建，避免每次执行重建。 */
    private Map<String, JobHandler> handlerByCode = Map.of();

    @PostConstruct
    void initHandlerMap() {
        handlerByCode = jobHandlers.stream()
                .collect(Collectors.toMap(JobHandler::jobCode, Function.identity(), (left, _) -> left));
    }

    public IPage<JobVo.JobListVo> selectPage(PageQuery pageQuery, JobDto.JobList jobList) {
        Page<Job> page = pageQuery.toMpPage();
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        if (jobList != null) {
            wrapper.like(StrUtil.isNotBlank(jobList.jobCode()), Job::getJobCode, jobList.jobCode())
                    .like(StrUtil.isNotBlank(jobList.jobName()), Job::getJobName, jobList.jobName())
                    .eq(jobList.isEnabled() != null, Job::getIsEnabled, jobList.isEnabled());
        }
        wrapper.orderByAsc(Job::getOrderNum).orderByAsc(Job::getCreateTime);
        jobMapper.selectPage(page, wrapper);

        List<String> jobIds = page.getRecords().stream().map(Job::getJobId).toList();
        Map<String, JobLog> latestByJobId = loadLatestEffectiveLogs(jobIds);

        Page<JobVo.JobListVo> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream()
                .map(job -> {
                    LocalDateTime nextRunTime = null;
                    if (job.getIsEnabled() != null && job.getIsEnabled() == 1) {
                        nextRunTime = resolveNextRunTime(job.getJobCron(), appZoneId);
                    }
                    return JobVo.JobListVo.from(job, latestByJobId.get(job.getJobId()), nextRunTime);
                })
                .toList());
        return voPage;
    }

    public IPage<JobVo.JobLogListVo> selectLogPage(PageQuery pageQuery, JobDto.JobLogList jobLogList) {
        requireJob(jobLogList.jobId());
        Page<JobLog> page = pageQuery.toMpPage();
        LambdaQueryWrapper<JobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobLog::getJobId, jobLogList.jobId())
                .eq(StrUtil.isNotBlank(jobLogList.runStatus()), JobLog::getRunStatus, jobLogList.runStatus())
                .orderByDesc(JobLog::getStartTime);
        jobLogMapper.selectPage(page, wrapper);
        Page<JobVo.JobLogListVo> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(JobVo.JobLogListVo::from).toList());
        return voPage;
    }

    @Transactional
    public void updateJobEnabled(JobDto.UpdateEnabled updateEnabled) {
        Job job = requireJob(updateEnabled.jobId());
        jobMapper.update(null, new LambdaUpdateWrapper<Job>()
                .eq(Job::getJobId, job.getJobId())
                .set(Job::getIsEnabled, updateEnabled.isEnabled()));
        // 启停立即生效：通知调度器取消/重建下一次触发。
        eventPublisher.publishEvent(new JobEnabledChangedEvent(job.getJobCode(), updateEnabled.isEnabled()));
    }

    /** 管理端手动执行：不受启停开关限制。 */
    public void runManual(JobDto.JobRun jobRun) {
        Job job = requireJob(jobRun.jobId());
        executeInternal(job, JobCodes.TRIGGER_MANUAL, true);
    }

    /** 定时触发：停用则跳过。 */
    public void runScheduled(String jobCode) {
        Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobCode, jobCode));
        if (job == null) {
            log.warn("定时任务跳过：任务不存在 jobCode={}", jobCode);
            return;
        }
        if (job.getIsEnabled() == null || job.getIsEnabled() != 1) {
            return;
        }
        executeInternal(job, JobCodes.TRIGGER_SCHEDULED, false);
    }

    private void executeInternal(Job job, int triggerType, boolean throwOnFail) {
        // 起止时间与下次执行统一按 app.timezone 取本地时间。
        LocalDateTime start = LocalDateTime.now(appZoneId);
        long beginMs = System.currentTimeMillis();

        String jobCode = job.getJobCode();
        String lockToken = UUID.randomUUID().toString();
        boolean locked = tryLockJob(jobCode, lockToken);
        if (!locked) {
            // 并发互斥：只记跳过日志，不参与列表最近有效执行。
            String skipMsg = "任务执行中，跳过本次触发";
            persistJobLog(job, triggerType, start, beginMs, JobCodes.RUN_STATUS_SKIPPED, skipMsg);
            if (throwOnFail) {
                throw new BusinessException(skipMsg);
            }
            return;
        }

        try {
            JobHandler handler = handlerByCode.get(jobCode);
            if (handler == null) {
                String msg = "未注册任务处理器: " + jobCode;
                log.error(msg);
                persistJobLog(job, triggerType, start, beginMs, JobCodes.RUN_STATUS_FAILED, msg);
                if (throwOnFail) {
                    throw new BusinessException(msg);
                }
                return;
            }

            String message;
            String runStatus;
            try {
                message = handler.execute();
                runStatus = JobCodes.RUN_STATUS_SUCCESS;
            } catch (BusinessException ex) {
                runStatus = JobCodes.RUN_STATUS_FAILED;
                message = ex.getMessage();
                if (throwOnFail) {
                    persistJobLog(job, triggerType, start, beginMs, runStatus, message);
                    throw ex;
                }
            } catch (Exception ex) {
                runStatus = JobCodes.RUN_STATUS_FAILED;
                message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
                log.error("定时任务执行失败 jobCode={}", jobCode, ex);
                if (throwOnFail) {
                    persistJobLog(job, triggerType, start, beginMs, runStatus, message);
                    throw new BusinessException("任务执行失败: " + message);
                }
            }
            persistJobLog(job, triggerType, start, beginMs, runStatus, message);
        } finally {
            unlockJob(jobCode, lockToken);
        }
    }

    /** Redis SET NX EX：多实例互斥同一 jobCode。 */
    private boolean tryLockJob(String jobCode, String lockToken) {
        Boolean ok = stringRedisTemplate.opsForValue()
                .setIfAbsent(RedisKeys.jobLock(jobCode), lockToken, JOB_LOCK_TTL);
        return Boolean.TRUE.equals(ok);
    }

    /** 仅释放自身持有的锁，避免误删其他实例锁。 */
    private void unlockJob(String jobCode, String lockToken) {
        stringRedisTemplate.execute(UNLOCK_SCRIPT, List.of(RedisKeys.jobLock(jobCode)), lockToken);
    }

    /** 只写执行日志；任务主表不存运行快照。 */
    private void persistJobLog(
            Job job,
            int triggerType,
            LocalDateTime start,
            long beginMs,
            String runStatus,
            String message
    ) {
        LocalDateTime end = LocalDateTime.now(appZoneId);
        JobLog jobLog = new JobLog();
        jobLog.setJobId(job.getJobId());
        jobLog.setJobCode(job.getJobCode());
        jobLog.setTriggerType(triggerType);
        jobLog.setRunStatus(runStatus);
        jobLog.setStartTime(start);
        jobLog.setEndTime(end);
        jobLog.setCostMs(System.currentTimeMillis() - beginMs);
        jobLog.setMessage(StrUtil.maxLength(StrUtil.blankToDefault(message, ""), MESSAGE_LIMIT));
        jobLogMapper.insert(jobLog);
    }

    private Map<String, JobLog> loadLatestEffectiveLogs(List<String> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) {
            return Map.of();
        }
        List<JobLog> logs = jobLogMapper.selectLatestEffectiveByJobIds(jobIds);
        Map<String, JobLog> map = new HashMap<>();
        if (logs == null) {
            return map;
        }
        for (JobLog jobLog : logs) {
            if (jobLog == null || StrUtil.isBlank(jobLog.getJobId())) {
                continue;
            }
            JobLog exists = map.get(jobLog.getJobId());
            // 同一 end_time 多条时保留 job_log_id 更大的一条。
            if (exists == null
                    || (jobLog.getJobLogId() != null
                    && jobLog.getJobLogId().compareTo(StrUtil.blankToDefault(exists.getJobLogId(), "")) > 0)) {
                map.put(jobLog.getJobId(), jobLog);
            }
        }
        return map;
    }

    private Job requireJob(String jobId) {
        if (StrUtil.isBlank(jobId)) {
            throw new BusinessException("任务ID不能为空");
        }
        Job job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }
        return job;
    }

    private LocalDateTime resolveNextRunTime(String cron, ZoneId zoneId) {
        if (StrUtil.isBlank(cron) || zoneId == null) {
            return null;
        }
        try {
            CronExpression expression = CronExpression.parse(cron);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            ZonedDateTime next = expression.next(now);
            return next != null ? next.toLocalDateTime() : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
