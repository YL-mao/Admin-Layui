package com.ylmao.admin.config;

import java.time.DateTimeException;
import java.time.ZoneId;

/**
 * {@code app.timezone} 解析契约：空或非法一律启动失败，禁止静默回退。
 */
public final class AppTimezones {

    /** 与 application.yml 缺省值保持一致。 */
    public static final String DEFAULT_ZONE_ID = "Asia/Shanghai";

    private AppTimezones() {
    }

    /** 解析业务时区；非法配置直接抛错，避免调度/JDBC/展示口径分叉。 */
    public static ZoneId requireZoneId(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) {
            throw new IllegalStateException("app.timezone 不能为空");
        }
        try {
            return ZoneId.of(value);
        } catch (DateTimeException ex) {
            throw new IllegalStateException("app.timezone 无效: " + value, ex);
        }
    }
}
