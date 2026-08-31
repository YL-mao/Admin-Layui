package com.ylmao.admin.service;

import com.ylmao.admin.common.SecurityConfigCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * 登录失败处罚阈值契约的基础单测。
 */
@ExtendWith(MockitoExtension.class)
class LoginFailServiceTest {

    @Mock
    private ConfigRuntimeService configRuntimeService;
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @InjectMocks
    private LoginFailService loginFailService;

    @Test
    void hitIpBanThreshold_multipleOfLimit() {
        when(configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.IP_FAIL_LIMIT)).thenReturn(10);
        assertTrue(loginFailService.hitIpBanThreshold(10));
        assertTrue(loginFailService.hitIpBanThreshold(20));
        assertFalse(loginFailService.hitIpBanThreshold(9));
        assertFalse(loginFailService.hitIpBanThreshold(0));
    }

    @Test
    void hitIpBanThreshold_limitZero_neverHits() {
        when(configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.IP_FAIL_LIMIT)).thenReturn(0);
        assertFalse(loginFailService.hitIpBanThreshold(10));
        assertFalse(loginFailService.hitIpBanThreshold(1));
    }

    @Test
    void accountFailLimit_readsContract() {
        when(configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.ACCOUNT_FAIL_LIMIT)).thenReturn(5);
        assertEquals(5, loginFailService.accountFailLimit());
        when(configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.ACCOUNT_FAIL_LIMIT)).thenReturn(0);
        assertEquals(0, loginFailService.accountFailLimit());
    }
}
