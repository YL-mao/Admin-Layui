package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.common.CronDescribe;
import com.ylmao.admin.common.JobCodes;
import com.ylmao.admin.entity.Job;
import com.ylmao.admin.entity.JobLog;

import java.time.LocalDateTime;

public final class JobVo {

    private JobVo() {
    }

    public record JobListVo(
            String jobId,
            String jobCode,
            String jobName,
            String jobCron,
            /* Cron 中文说明，如「每天 03:00」。 */
            String jobCronDesc,
            String jobDesc,
            Integer orderNum,
            Integer isEnabled,
            /* 最近一次有效执行（成功/失败）结束时间。 */
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime lastRunTime,
            /*  SUCCESS / FAILED；无有效执行时为 null。 */
            String runStatus,
            /* 成功 / 失败 / — */
            String runStatusName,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime nextRunTime
    ) {
        public static JobListVo from(Job job, JobLog latestEffectiveLog, LocalDateTime nextRunTime) {
            String runStatus = latestEffectiveLog != null ? latestEffectiveLog.getRunStatus() : null;
            String jobCron = job.getJobCron();
            return new JobListVo(
                    job.getJobId(),
                    job.getJobCode(),
                    job.getJobName(),
                    jobCron,
                    CronDescribe.toZh(jobCron),
                    job.getJobDesc(),
                    job.getOrderNum(),
                    job.getIsEnabled(),
                    latestEffectiveLog != null ? latestEffectiveLog.getEndTime() : null,
                    runStatus,
                    resolveRunStatusName(runStatus),
                    nextRunTime
            );
        }
    }

    public record JobLogListVo(
            String jobLogId,
            String jobId,
            String jobCode,
            Integer triggerType,
            String triggerTypeName,
            String runStatus,
            String runStatusName,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime,
            Long costMs,
            String message
    ) {
        public static JobLogListVo from(JobLog jobLog) {
            Integer triggerType = jobLog.getTriggerType();
            String runStatus = jobLog.getRunStatus();
            return new JobLogListVo(
                    jobLog.getJobLogId(),
                    jobLog.getJobId(),
                    jobLog.getJobCode(),
                    triggerType,
                    triggerType != null && triggerType == JobCodes.TRIGGER_MANUAL ? "手动" : "定时",
                    runStatus,
                    resolveRunStatusName(runStatus),
                    jobLog.getStartTime(),
                    jobLog.getEndTime(),
                    jobLog.getCostMs(),
                    jobLog.getMessage()
            );
        }
    }

    private static String resolveRunStatusName(String runStatus) {
        if (JobCodes.RUN_STATUS_SUCCESS.equals(runStatus)) {
            return "成功";
        }
        if (JobCodes.RUN_STATUS_FAILED.equals(runStatus)) {
            return "失败";
        }
        if (JobCodes.RUN_STATUS_SKIPPED.equals(runStatus)) {
            return "跳过";
        }
        return "—";
    }
}
