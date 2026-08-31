package com.ylmao.admin.common;

/** 内置定时任务编码，与 sys_job.job_code、JobHandler 一一对应。 */
public final class JobCodes {

    public static final String OPERATE_LOG_RETENTION = "operateLogRetention";
    public static final String NOTICE_EXPIRE_CLEAN = "noticeExpireClean";
    /** 按 notice.readDays 软删收件箱已读记录。 */
    public static final String NOTICE_READ_CLEAN = "noticeReadClean";
    /** 按 notice.unreadDays 软删收件箱未读记录。 */
    public static final String NOTICE_UNREAD_CLEAN = "noticeUnreadClean";

    /** 触发方式：定时。 */
    public static final int TRIGGER_SCHEDULED = 1;
    /** 触发方式：手动。 */
    public static final int TRIGGER_MANUAL = 2;

    /** 执行结果：成功。 */
    public static final String RUN_STATUS_SUCCESS = "SUCCESS";
    /** 执行结果：失败。 */
    public static final String RUN_STATUS_FAILED = "FAILED";
    /** 执行结果：跳过（互斥冲突等，不参与列表最近有效执行）。 */
    public static final String RUN_STATUS_SKIPPED = "SKIPPED";

    private JobCodes() {
    }
}
