package com.ylmao.admin.config.monitor;

import com.aizuda.monitor.OshiMonitor;
import oshi.SystemInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 显式提供监控 Bean；aizuda-monitor 自带的自动配置会在已有同类型 Bean 时让出注册。
 */
@Configuration
public class AizudaMonitorConfig {

    @Bean
    public OshiMonitor oshiMonitor() {
        return new OshiMonitor(new SystemInfo());
    }
}
