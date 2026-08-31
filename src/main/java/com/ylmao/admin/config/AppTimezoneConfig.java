package com.ylmao.admin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

/**
 * 业务唯一时区来源：{@code app.timezone}（配置文件）。
 * <p>
 * JVM 默认时区在 {@link com.ylmao.admin.AdminApp} 启动早期已同步；此处提供可注入的 {@link ZoneId}。
 * 不再使用库配置 {@code system.timezone}，避免运行期热改导致调度/日志/JDBC 口径分叉。
 */
@Configuration
public class AppTimezoneConfig {

    private static final Logger log = LoggerFactory.getLogger(AppTimezoneConfig.class);

    @Bean
    public ZoneId appZoneId(
            @Value("${app.timezone:" + AppTimezones.DEFAULT_ZONE_ID + "}") String timezone) {
        ZoneId zoneId = AppTimezones.requireZoneId(timezone);
        log.info("业务时区 Bean 已就绪 app.timezone={}", zoneId.getId());
        return zoneId;
    }
}
