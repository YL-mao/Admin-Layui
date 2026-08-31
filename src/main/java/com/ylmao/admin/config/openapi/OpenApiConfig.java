package com.ylmao.admin.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.filters.OpenApiMethodFilter;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * springdoc OpenAPI 配置：只收录 JSON 接口；鉴权说明与 Cookie/请求头 saToken 对齐。
 */
@Configuration
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true")
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "saToken";

    @Bean
    public OpenAPI adminOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Admin Layui 管理后台 API")
                        .description("""
                                同域 Thymeleaf 后台默认走 Cookie（名称 saToken）；脚本/跨域可用同名请求头。
                                文档仅展示带 @ResponseBody / @RestController 的 JSON 接口；页面跳转不收录。
                                书面约定见 doc/接口文档与开发说明.md。
                                """)
                        .version("0.0.1"))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name("saToken")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("登录后的 saToken；同域也可由浏览器自动带 Cookie")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("system")
                .displayName("系统模块")
                .packagesToScan("com.ylmao.admin.controller.system")
                .addOpenApiMethodFilter(jsonApiOnly())
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("后台框架")
                .packagesToScan("com.ylmao.admin.controller.admin")
                .addOpenApiMethodFilter(jsonApiOnly())
                .build();
    }

    @Bean
    public GroupedOpenApi rootApi() {
        return GroupedOpenApi.builder()
                .group("root")
                .displayName("登录与其它")
                .packagesToScan("com.ylmao.admin.controller")
                .pathsToMatch("/login", "/captcha/**", "/home/**", "/upload/**")
                .addOpenApiMethodFilter(jsonApiOnly())
                .build();
    }

    private static OpenApiMethodFilter jsonApiOnly() {
        return method -> AnnotatedElementUtils.hasAnnotation(method, ResponseBody.class)
                || AnnotatedElementUtils.hasAnnotation(method.getDeclaringClass(), RestController.class)
                || AnnotatedElementUtils.hasAnnotation(method.getDeclaringClass(), ResponseBody.class);
    }
}
