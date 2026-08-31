package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.common.SecurityConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 登录 / 验证码 Redis 固定窗口限流（多实例共享）。
 * 阈值读 security.*；0 表示关闭对应项，winMinLim=0 关闭整组软拦。
 */
@Service
@RequiredArgsConstructor
public class LoginRateLimitService {

    private static final String TOO_FREQUENT = "操作过于频繁，请稍后再试";

    private final StringRedisTemplate stringRedisTemplate;
    private final ConfigRuntimeService configRuntimeService;

    public void checkLoginIp(String ip) {
        // 登录 IP 软拦：超限抛业务异常。
        if (!allowHit(RedisKeys.rateLoginIp(normalizeIp(ip)), SecurityConfigCodes.LOGIN_IP_LIMIT)) {
            throw new BusinessException(TOO_FREQUENT);
        }
    }

    public void checkLoginAccount(String userAccount) {
        if (StrUtil.isBlank(userAccount)) {
            return;
        }
        // 登录账号软拦：超限抛业务异常。
        if (!allowHit(RedisKeys.rateLoginAccount(userAccount.trim()), SecurityConfigCodes.LOGIN_ACCOUNT_LIMIT)) {
            throw new BusinessException(TOO_FREQUENT);
        }
    }

    /**
     * 验证码 IP 软拦。
     *
     * @return true 允许发图；false 已超限（由调用方返回兜底图）
     */
    public boolean tryCaptchaIp(String ip) {
        return allowHit(RedisKeys.rateCaptchaIp(normalizeIp(ip)), SecurityConfigCodes.CAPTCHA_IP_LIMIT);
    }

    private boolean allowHit(String redisKey, String limitConfigCode) {
        int windowMinutes = configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.RATE_WINDOW_MINUTES);
        // 窗口为 0：整组软拦关闭。
        if (windowMinutes == 0) {
            return true;
        }
        int limit = configRuntimeService.requireNonNegativeInt(limitConfigCode);
        // 该项阈值为 0：仅关闭这一维限流。
        if (limit == 0) {
            return true;
        }
        Long count = stringRedisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(redisKey, Duration.ofMinutes(windowMinutes));
        }
        return count == null || count <= limit;
    }

    private static String normalizeIp(String ip) {
        return StrUtil.blankToDefault(ip, "unknown");
    }
}
