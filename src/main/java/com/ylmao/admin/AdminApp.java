package com.ylmao.admin;

import cn.dev33.satoken.SaManager;
import com.ylmao.admin.config.AppTimezones;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@MapperScan("com.ylmao.admin.mapper")
@EnableScheduling
public class AdminApp {

    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplication(AdminApp.class);
        // 环境就绪后、Bean 创建前固定 JVM 默认时区，使 LocalDateTime.now() / ZoneId.systemDefault() 对齐 app.timezone。
        application.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event ->
                applyAppTimezone(event.getEnvironment().getProperty("app.timezone", AppTimezones.DEFAULT_ZONE_ID)));
        ConfigurableApplicationContext context = application.run(args);
        Environment env = context.getEnvironment();
        // 仅 dev 环境打印启动详情
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            String port = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "");
            if ("/".equals(contextPath)) {
                contextPath = "";
            }
            String host = InetAddress.getLocalHost().getHostAddress();
            String localUrl = "http://localhost:" + port + contextPath;
            String networkUrl = "http://" + host + ":" + port + contextPath;
            System.out.println("----------------------------------------------------------");
            System.out.println("启动成功");
            System.out.println("本地访问: " + localUrl);
            System.out.println("网络访问: " + networkUrl);
            System.out.println("Sa-Token配置: " + SaManager.getConfig());
            System.out.println("----------------------------------------------------------");
        } else if (env.acceptsProfiles(Profiles.of("prod"))) {
            System.out.println("应用已启动（生产环境）");
        }
    }

    /** 将 app.timezone 同步为 JVM 默认时区；空或非法直接失败，禁止静默回退。 */
    private static void applyAppTimezone(String raw) {
        ZoneId zoneId = AppTimezones.requireZoneId(raw);
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId));
    }

}
