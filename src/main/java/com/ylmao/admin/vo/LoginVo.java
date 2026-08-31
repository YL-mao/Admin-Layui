package com.ylmao.admin.vo;

import cn.dev33.satoken.stp.SaTokenInfo;

/** 登录成功响应 VO，避免直接暴露 User 实体。 */
public class LoginVo {

    public record LoginResult(SaTokenInfo tokenInfo) {
    }
}
