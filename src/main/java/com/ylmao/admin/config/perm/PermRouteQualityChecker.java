package com.ylmao.admin.config.perm;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.entity.Perm;
import com.ylmao.admin.mapper.PermMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 启动时权限与路由质量检查：只打 WARN，不拦截启动。
 * 可通过 app.perm.quality-check=false 关闭。
 */
@Component
@ConditionalOnProperty(name = "app.perm.quality-check", havingValue = "true", matchIfMissing = true)
public class PermRouteQualityChecker implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(PermRouteQualityChecker.class);

    /** 配置分组动态权限前缀，对应 ConfigController.checkGroupPermission。 */
    private static final Set<String> DYNAMIC_CONFIG_PERM_CODES = Set.of(
            "system:config:system",
            "system:config:upload",
            "system:config:log",
            "system:config:security",
            "system:config:job",
            "system:config:notice"
    );

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final PermMapper permMapper;

    public PermRouteQualityChecker(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping requestMappingHandlerMapping,
            PermMapper permMapper) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.permMapper = permMapper;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            Set<String> routePaths = collectRoutePaths();
            List<Perm> perms = permMapper.selectList(new LambdaQueryWrapper<>());
            checkPermUrls(perms, routePaths);
            checkViewTemplates();
            checkAnnotationPermCodes(perms);
            checkDuplicatePermCodes(perms);
            checkBlankPermCodes(perms);
        } catch (Exception e) {
            // 质检失败本身不阻断启动，只记录原因。
            log.warn("[权限路由质检] 执行异常: {}", e.getMessage(), e);
        }
    }

    /** 权限表非空 perm_url 对照已注册路由；is_blank=1 跳过。 */
    private void checkPermUrls(List<Perm> perms, Set<String> routePaths) {
        for (Perm perm : perms) {
            if (perm.getIsBlank() != null && perm.getIsBlank() == 1) {
                continue;
            }
            String url = StrUtil.trim(perm.getPermUrl());
            if (StrUtil.isBlank(url)) {
                continue;
            }
            String normalized = normalizePath(url);
            if (!routePaths.contains(normalized)) {
                log.warn("[权限路由质检] perm_url 无对应路由: permId={}, permName={}, permUrl={}",
                        perm.getPermId(), perm.getPermName(), url);
            }
        }
    }

    /** 业务 Controller 的 *VIEW 常量及登录/框架页模板是否存在。 */
    private void checkViewTemplates() {
        Set<Class<?>> controllers = new HashSet<>();
        for (HandlerMethod handlerMethod : requestMappingHandlerMapping.getHandlerMethods().values()) {
            controllers.add(handlerMethod.getBeanType());
        }
        Set<String> views = new LinkedHashSet<>();
        for (Class<?> controller : controllers) {
            for (Field field : controller.getDeclaredFields()) {
                if (!isStaticFinalString(field) || !field.getName().endsWith("VIEW")) {
                    continue;
                }
                try {
                    if (!field.trySetAccessible()) {
                        continue;
                    }
                    Object value = field.get(null);
                    if (value instanceof String view && StrUtil.isNotBlank(view)
                            && !view.startsWith("redirect:")) {
                        views.add(view);
                    }
                } catch (IllegalAccessException ignored) {
                    // 反射失败时跳过该常量。
                }
            }
        }
        // 无 VIEW 常量的页面入口。
        views.add("login");
        views.add("index");
        for (String view : views) {
            String location = "templates/" + view + ".html";
            if (!new ClassPathResource(location).exists()) {
                log.warn("[权限路由质检] 视图模板不存在: view={}, resource={}", view, location);
            }
        }
    }

    /** 仅报警：注解有权限码，库中无对应非空 perm_code。 */
    private void checkAnnotationPermCodes(List<Perm> perms) {
        Set<String> dbCodes = new HashSet<>();
        for (Perm perm : perms) {
            if (StrUtil.isNotBlank(perm.getPermCode())) {
                dbCodes.add(perm.getPermCode().trim());
            }
        }
        Set<String> annotationCodes = new LinkedHashSet<>();
        for (HandlerMethod handlerMethod : requestMappingHandlerMapping.getHandlerMethods().values()) {
            SaCheckPermission ann = AnnotatedElementUtils.findMergedAnnotation(
                    handlerMethod.getMethod(), SaCheckPermission.class);
            if (ann == null) {
                continue;
            }
            for (String code : ann.value()) {
                if (StrUtil.isNotBlank(code)) {
                    annotationCodes.add(code.trim());
                }
            }
        }
        annotationCodes.addAll(DYNAMIC_CONFIG_PERM_CODES);
        for (String code : annotationCodes) {
            if (!dbCodes.contains(code)) {
                log.warn("[权限路由质检] 注解权限码在库中不存在: permCode={}", code);
            }
        }
    }

    private void checkDuplicatePermCodes(List<Perm> perms) {
        Map<String, List<String>> codeToIds = new HashMap<>();
        for (Perm perm : perms) {
            if (StrUtil.isBlank(perm.getPermCode())) {
                continue;
            }
            String code = perm.getPermCode().trim();
            codeToIds.computeIfAbsent(code, key -> new ArrayList<>()).add(perm.getPermId());
        }
        for (Map.Entry<String, List<String>> entry : codeToIds.entrySet()) {
            if (entry.getValue().size() > 1) {
                log.warn("[权限路由质检] 权限码重复: permCode={}, permIds={}",
                        entry.getKey(), entry.getValue());
            }
        }
    }

    /** 菜单/按钮不可空码；目录可空。 */
    private void checkBlankPermCodes(List<Perm> perms) {
        for (Perm perm : perms) {
            Integer type = perm.getPermType();
            if (type == null || type == 0) {
                continue;
            }
            if (StrUtil.isBlank(perm.getPermCode())) {
                log.warn("[权限路由质检] 菜单/按钮权限码为空: permId={}, permName={}, permType={}",
                        perm.getPermId(), perm.getPermName(), type);
            }
        }
    }

    private Set<String> collectRoutePaths() {
        Set<String> paths = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry
                : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            // Spring 6+ 默认 PathPattern；PatternsRequestCondition 在 7.0 已弃用。
            if (info.getPathPatternsCondition() != null) {
                addPaths(paths, info.getPathPatternsCondition().getPatternValues());
            }
        }
        return paths;
    }

    private void addPaths(Set<String> paths, Collection<String> patterns) {
        if (patterns == null) {
            return;
        }
        for (String pattern : patterns) {
            paths.add(normalizePath(pattern));
        }
    }

    private static String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static boolean isStaticFinalString(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers)
                && Modifier.isFinal(modifiers)
                && String.class.equals(field.getType());
    }
}
