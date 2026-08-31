package com.ylmao.admin.service;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Service;

/**
 * 用户密码哈希与校验；统一 bcrypt cost，避免业务层散落 MD5/明文处理。
 */
@Service
public class PasswordService {

    /** bcrypt 成本因子：12 约比 10 慢 4 倍，适合后台管理登录场景。 */
    private static final int BCRYPT_COST = 12;

    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
