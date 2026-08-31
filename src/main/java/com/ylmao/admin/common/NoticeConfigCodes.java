package com.ylmao.admin.common;

/** 公告类配置编码契约，与 sys_config.config_code 对齐。 */
public final class NoticeConfigCodes {

    /** 收件箱已读保留天数；0 / 缺失不清理。 */
    public static final String READ_DAYS = "notice.readDays";
    /** 收件箱未读保留天数；0 / 缺失不清理。 */
    public static final String UNREAD_DAYS = "notice.unreadDays";

    private NoticeConfigCodes() {
    }
}
