package com.ylmao.admin.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.config.exception.BusinessException;
import com.pig4cloud.captcha.GifCaptcha;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

/**
 * 图形验证码：校验码存 Redis，凭 Cookie captchaId 跨实例校验。
 */
@Service
@RequiredArgsConstructor
public class CaptchaService {

    public static final String COOKIE_NAME = "captchaId";
    /** 超限兜底图响应头，供前端识别（同域可读）。 */
    public static final String LIMITED_HEADER = "X-Captcha-Limited";
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final String LIMITED_IMAGE_PATH = "static/admin/images/captcha-limited.png";

    private final StringRedisTemplate stringRedisTemplate;

    public void writeImage(HttpServletResponse response) throws IOException {
        GifCaptcha gifCaptcha = new GifCaptcha(130, 48, 4);
        String captchaId = IdUtil.fastSimpleUUID();
        // 验证码原文只进 Redis，不进 Session。
        stringRedisTemplate.opsForValue().set(RedisKeys.captcha(captchaId), gifCaptcha.text(), TTL);
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, captchaId)
                .httpOnly(true)
                .path("/")
                .maxAge(TTL)
                .sameSite("Lax")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        applyNoCacheHeaders(response, "image/gif");
        gifCaptcha.out(response.getOutputStream());
    }

    /**
     * 验证码 IP 超限：返回静态提示图，不写 Redis、不发 captchaId，并清掉旧 Cookie。
     */
    public void writeLimitedImage(HttpServletResponse response) throws IOException {
        clearCaptchaCookie(response);
        response.setHeader(LIMITED_HEADER, "1");
        applyNoCacheHeaders(response, "image/png");
        ClassPathResource resource = new ClassPathResource(LIMITED_IMAGE_PATH);
        if (!resource.exists()) {
            throw new IllegalStateException("验证码超限兜底图缺失: " + LIMITED_IMAGE_PATH);
        }
        try (InputStream in = resource.getInputStream()) {
            StreamUtils.copy(in, response.getOutputStream());
        }
    }

    /** 读取并删除 Redis 中的验证码；校验失败或缺失均视为错误。 */
    public void validateAndConsume(String inputCaptcha, HttpServletRequest request, HttpServletResponse response) {
        String captchaId = readCaptchaId(request);
        clearCaptchaCookie(response);
        if (StrUtil.isBlank(captchaId)) {
            throw new BusinessException("验证码错误");
        }
        String key = RedisKeys.captcha(captchaId);
        String verCode = stringRedisTemplate.opsForValue().getAndDelete(key);
        if (StrUtil.isBlank(inputCaptcha) || StrUtil.isBlank(verCode)
                || !inputCaptcha.equalsIgnoreCase(verCode.trim())) {
            throw new BusinessException("验证码错误");
        }
    }

    private static String readCaptchaId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static void clearCaptchaCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private static void applyNoCacheHeaders(HttpServletResponse response, String contentType) {
        response.setContentType(contentType);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
