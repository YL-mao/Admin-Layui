package com.ylmao.admin.common;

import java.time.LocalDateTime;

/**
 * 访问控制（黑/白名单）常量。
 */
public final class FilterCodes {

    private FilterCodes() {
    }

    public static final String TYPE_IP = "IP";
    public static final String TYPE_USER_ID = "USER_ID";
    public static final String TYPE_DEVICE = "DEVICE";

    public static final String SOURCE_MANUAL = "MANUAL";
    public static final String SOURCE_AUTO = "AUTO";

    public static final String MODE_BLACK = "BLACK";
    public static final String MODE_WHITE = "WHITE";

    /** 永久过期哨兵。 */
    public static final LocalDateTime PERMANENT_EXPIRE = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    public static final String BLOCK_MSG = "访问受限，请联系管理员";
}
