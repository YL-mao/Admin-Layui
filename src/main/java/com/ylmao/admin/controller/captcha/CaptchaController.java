package com.ylmao.admin.controller.captcha;

import com.ylmao.admin.service.CaptchaService;
import com.ylmao.admin.service.LoginRateLimitService;
import com.ylmao.admin.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;
    private final LoginRateLimitService loginRateLimitService;

    /**
     * 验证码生成：校验码写入 Redis，captchaId 写入 Cookie。
     * IP 超限时返回静态兜底图（仍 HTTP 200），避免 img 裂图。
     */
    @RequestMapping("/captchaImage")
    public void generate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 验证码接口按 IP 软拦；超限写兜底图而非 JSON。
        if (!loginRateLimitService.tryCaptchaIp(ServletUtils.getIP(request))) {
            captchaService.writeLimitedImage(response);
            return;
        }
        captchaService.writeImage(response);
    }
}
