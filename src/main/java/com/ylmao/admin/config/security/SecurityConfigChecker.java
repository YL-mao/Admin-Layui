package com.ylmao.admin.config.security;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.common.SecurityConfigCodes;
import com.ylmao.admin.entity.Config;
import com.ylmao.admin.mapper.ConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 启动校验 security.* 强契约：失败处罚 + 软拦共 7 项，须存在、启用、number、整数且 ≥0。
 */
@Component
@Order(50)
@RequiredArgsConstructor
public class SecurityConfigChecker implements ApplicationRunner {

    private final ConfigMapper configMapper;

    @Override
    public void run(ApplicationArguments args) {
        for (String code : SecurityConfigCodes.REQUIRED_CODES) {
            validate(code);
        }
    }

    private void validate(String configCode) {
        Config config = configMapper.selectOne(new LambdaQueryWrapper<Config>()
                .eq(Config::getConfigCode, configCode)
                .eq(Config::getIsDel, 0)
                .last("LIMIT 1"));
        if (config == null) {
            throw new IllegalStateException("安全配置缺失: " + configCode);
        }
        if (config.getIsEnabled() == null || config.getIsEnabled() != 1) {
            throw new IllegalStateException("安全配置未启用: " + configCode);
        }
        if (!"number".equals(config.getValueType())) {
            throw new IllegalStateException("安全配置值类型必须为 number: " + configCode);
        }
        if (StrUtil.isBlank(config.getConfigValue())) {
            throw new IllegalStateException("安全配置值为空: " + configCode);
        }
        try {
            int n = new BigDecimal(config.getConfigValue().trim()).intValueExact();
            if (n < 0) {
                throw new IllegalStateException("安全配置不能为负数: " + configCode + "=" + config.getConfigValue());
            }
        } catch (NumberFormatException | ArithmeticException ex) {
            throw new IllegalStateException(
                    "安全配置必须为非负整数: " + configCode + "=" + config.getConfigValue(), ex);
        }
    }
}
