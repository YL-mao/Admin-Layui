package com.ylmao.admin.config.saToken;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.*;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.thymeleaf.dialect.SaTokenDialect;
import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.R;
import com.ylmao.admin.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SaTokenConfigure.class);

    private static final List<String> corsOriginsDev = List.of("http://localhost:8080");
    /** 生产部署前改为真实域名（含 https://，无路径） */
    private static final List<String> corsOriginsProd = List.of(
            "https://www.example.com",
            "https://example.com");

    private final JsonMapper jsonMapper;
    private final List<String> corsOrigins;

    public SaTokenConfigure(JsonMapper jsonMapper, Environment env) {
        this.jsonMapper = jsonMapper;
        this.corsOrigins = env.acceptsProfiles(Profiles.of("prod")) ? corsOriginsProd : corsOriginsDev;
    }

    /**
     * CORS 跨域策略：由 Sa-Token 内置 CorsFilter 调用，按 Origin 白名单回写响应头。
     */
    @Bean
    public SaCorsHandleFunction corsHandle() {
        return (req, res, sto) -> {
            String origin = req.getHeader("Origin");
            if (StrUtil.isNotBlank(origin) && corsOrigins.contains(origin)) {
                res.setHeader("Access-Control-Allow-Origin", origin)
                        .setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS")
                        .setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type,saToken")
                        .setHeader("Access-Control-Max-Age", String.valueOf(3600))
                        .setHeader("Vary", "Origin");
            }
            // 预检请求直接返回，不进入鉴权
            if (SaHttpMethod.OPTIONS.name().equalsIgnoreCase(req.getMethod())) {
                log.debug("OPTIONS preflight request");
                SaRouter.back();
            }
        };
    }

    //开放权限的url
    private final String[] excludePaths = {
            "/favicon.ico", "/ico/favicon.ico", "/static/**",
            // 错误页
            "/error/**",
            // 对所有用户认证
            "/login",
            // 放验证码
            "/captcha/**",
            // 文件预览：匿名/登录校验在控制器内按 need_login 判断
            "/upload/**"};

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public SaServletFilter saServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude(excludePaths)
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    log.debug("Sa-Token global auth check");

                    // 登录认证 -- 拦截所有路由
                    SaRouter.match("/**", StpUtil::checkLogin);

                    // 更多拦截处理方式，请参考“路由拦截式鉴权”章节 */
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    if (!ServletUtils.isAjaxRequest((HttpServletRequest) SaHolder.getRequest().getSource())) {
                        if (e instanceof NotLoginException) {
                            return SaHolder.getRequest().forward("/login");
                        }
                        if (e instanceof NotPermissionException || e instanceof NotRoleException || e instanceof NotSafeException) {
                            return SaHolder.getRequest().forward("/error/403");
                        }
                        return SaHolder.getRequest().forward("/error/500");
                    }
                    try {
                        R<Void> result = toAuthErrorR(e);
                        SaHolder.getResponse()
                                .setStatus(result.code())
                                .setHeader("Content-Type", "application/json;charset=utf-8");
                        return jsonMapper.writeValueAsString(result);
                    } catch (JacksonException ex) {
                        log.warn("Failed to serialize R", ex);
                        return "{\"code\":500,\"msg\":\"error\"}";
                    }
                })
                .setBeforeAuth(r -> {
                    // 安全响应头（CORS 见 corsHandle Bean）
                    SaHolder.getResponse()
                            // 是否可以在 iframe 显示：DENY=不可以 | SAMEORIGIN=同域下可以
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            // 启用浏览器 XSS 防护并在检测到攻击时停止渲染
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff");
                });
    }

    // Sa-Token 标签方言 (Thymeleaf版)
    @Bean
    public SaTokenDialect getSaTokenDialect() {
        return new SaTokenDialect();
    }

    private R<Void> toAuthErrorR(Throwable e) {
        if (e instanceof NotLoginException) {
            return R.fail(401, e.getMessage());
        }
        if (e instanceof NotPermissionException || e instanceof NotRoleException || e instanceof NotSafeException) {
            return R.fail(403, e.getMessage());
        }
        return R.fail(e.getMessage());
    }

}
