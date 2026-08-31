package com.ylmao.admin.service;

import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.common.SecurityConfigCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 登录失败计数（Redis）：账号维度与 IP 维度独立累计。
 * 阈值读 security.* 强契约配置；0 表示关闭对应处罚。
 */
@Service
@RequiredArgsConstructor
public class LoginFailService {

    private final ConfigRuntimeService configRuntimeService;
    private final StringRedisTemplate stringRedisTemplate;

    /** 账号锁号阈值；0 表示不锁号。 */
    public int accountFailLimit() {
        return configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.ACCOUNT_FAIL_LIMIT);
    }

    /** IP 自动拉黑阈值；0 表示不按次数自动拉黑。 */
    public int ipFailLimit() {
        return configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.IP_FAIL_LIMIT);
    }

    /** 是否应对该累计次数执行自动拉黑（次数大于 0 且能被阈值整除）。 */
    public boolean hitIpBanThreshold(int ipFailCount) {
        int limit = ipFailLimit();
        return ipFailCount > 0 && limit > 0 && ipFailCount % limit == 0;
    }

    public int recordAccountFail(String userAccount) {
        if (userAccount == null) {
            return 0;
        }
        Long count = stringRedisTemplate.opsForValue().increment(RedisKeys.loginFailAccount(userAccount));
        return count == null ? 0 : count.intValue();
    }

    public int recordIpFail(String ip) {
        if (ip == null || ip.isBlank()) {
            return 0;
        }
        Long count = stringRedisTemplate.opsForValue().increment(RedisKeys.loginFailIp(ip));
        return count == null ? 0 : count.intValue();
    }

    public void clearAccountFail(String userAccount) {
        if (userAccount != null) {
            stringRedisTemplate.delete(RedisKeys.loginFailAccount(userAccount));
        }
    }

    public void clearIpFail(String ip) {
        if (ip != null) {
            stringRedisTemplate.delete(RedisKeys.loginFailIp(ip));
        }
    }
}
