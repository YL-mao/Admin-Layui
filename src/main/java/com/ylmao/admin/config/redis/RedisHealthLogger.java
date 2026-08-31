package com.ylmao.admin.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * 启动时探测 Redis；不可用则记错误日志（业务依赖 Redis，与 Sa-Token 一致）。
 */
@Component
public class RedisHealthLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RedisHealthLogger.class);

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisHealthLogger(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (var connection = redisConnectionFactory.getConnection()) {
            String pong = connection.ping();
            log.info("Redis 健康检查通过 ping={}", pong);
        } catch (Exception ex) {
            log.error("Redis 健康检查失败，登录态与业务共享状态均依赖 Redis", ex);
        }
    }
}
