package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.config.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 固定密码策略：高复杂度（字母+数字+特殊字符），不走系统配置。
 */
@Service
public class PasswordPolicyService {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;
    private static final Pattern HAS_LETTER = Pattern.compile("[A-Za-z]");
    private static final Pattern HAS_DIGIT = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[^A-Za-z0-9]");

    /** 校验新密码：8～64 位，且须同时包含字母、数字与特殊字符。 */
    public void validateNewPassword(String rawPassword) {
        if (StrUtil.isBlank(rawPassword)) {
            throw new BusinessException("密码不能为空");
        }
        int length = rawPassword.length();
        if (length < MIN_LENGTH) {
            throw new BusinessException("密码长度不能少于 " + MIN_LENGTH + " 位");
        }
        if (length > MAX_LENGTH) {
            throw new BusinessException("密码长度不能超过 " + MAX_LENGTH + " 位");
        }
        if (!HAS_LETTER.matcher(rawPassword).find()
                || !HAS_DIGIT.matcher(rawPassword).find()
                || !HAS_SPECIAL.matcher(rawPassword).find()) {
            throw new BusinessException("密码须同时包含字母、数字和特殊字符");
        }
    }
}
