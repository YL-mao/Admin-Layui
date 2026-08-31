package com.ylmao.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    public record LoginRequest(
            @NotBlank(message = "账号不能为空") String userAccount,
            @NotBlank(message = "密码不能为空") String userPassword,
            @NotBlank(message = "验证码不能为空") String captcha,
            Boolean rememberMe
    ) {
    }
}
