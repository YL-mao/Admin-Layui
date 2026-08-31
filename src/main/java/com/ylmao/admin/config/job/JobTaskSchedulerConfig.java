package com.ylmao.admin.config.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 内置定时任务专用调度线程池（非数据库表）。
 * <p>
 * 供 {@link BuiltinJobScheduler} 预约 cron 触发与扫描重注册，与业务请求线程隔离。
 */
@Configuration
public class JobTaskSchedulerConfig {

    @Bean("jobTaskScheduler")
    public TaskScheduler jobTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 扫描 + 少量内置任务并发触发即可，池不宜过大。
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("md-job-scheduler-");
        // 与调度器 @PreDestroy 配合：优雅停机时尽量等已在执行的任务收尾。
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        // initialize 交给 Spring InitializingBean，避免重复初始化。
        return scheduler;
    }
}
