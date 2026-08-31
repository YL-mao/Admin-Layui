package com.ylmao.admin.config.log;
import cn.hutool.core.util.StrUtil;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.ylmao.admin.common.ConfigAuditCodes;
import com.ylmao.admin.common.LogConfigCodes;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.dto.LoginDto;
import com.ylmao.admin.entity.OperateLog;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.service.ConfigRuntimeService;
import com.ylmao.admin.service.OperateLogService;
import com.ylmao.admin.utils.ServletUtils;
import com.ylmao.admin.utils.UserAgentUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    private static final int PARAM_LIMIT = 2000;
    private static final int BODY_LIMIT = 4000;
    private static final int STACK_LIMIT = 8000;

    private final OperateLogService operateLogService;
    private final ConfigRuntimeService configRuntimeService;
    private final JsonMapper jsonMapper;

    @Pointcut("@annotation(com.ylmao.admin.config.log.Log)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Log controllerLog = getAnnotationLog(joinPoint);
        // 业务方法内可能注销会话（如 loginOut），需在 proceed 前快照操作人。
        User logUser = snapshotLogUser();
        Object result = null;
        Throwable throwable = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            saveLog(joinPoint, controllerLog, result, throwable, System.currentTimeMillis() - startTime, logUser);
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Log controllerLog, Object result, Throwable throwable, long costTime, User logUser) {
        try {
            if (controllerLog == null) {
                return;
            }
            // 仅配置明确为 false 时关闭；缺失/停用时仍记日志。
            if (!isLoggingEnabled(controllerLog.loggingType())) {
                return;
            }
            HttpServletRequest request = ServletUtils.getRequest();
            OperateLog operateLog = new OperateLog();
            fillRequestInfo(operateLog, request);
            fillUserInfo(operateLog, logUser);
            fillMethodInfo(operateLog, joinPoint);
            fillAnnotationInfo(operateLog, controllerLog, joinPoint);
            fillResultInfo(operateLog, controllerLog, result, throwable);
            operateLog.setCostTime(costTime);
            operateLog.setOperateTime(LocalDateTime.now());
            // 配置写成功且有变更项时，按 configCode 拆成多条审计日志。
            List<ConfigAuditItem> auditItems = ConfigAuditHolder.drain();
            if (operateLog.getIsSuccess() != null && operateLog.getIsSuccess() == 1) {
                if (!auditItems.isEmpty()) {
                    for (ConfigAuditItem item : auditItems) {
                        OperateLog itemLog = copyOperateLog(operateLog);
                        itemLog.setOperateTitle(ConfigAuditCodes.OPERATE_TITLE);
                        itemLog.setRequestBody(StrUtil.sub(maskSensitive(toJson(item)), 0, BODY_LIMIT));
                        itemLog.setRequestParam("");
                        asyncInsert(itemLog);
                    }
                    return;
                }
                // 配置写接口成功但无实际变更时不记空审计。
                if (ConfigAuditCodes.OPERATE_TITLE.equals(operateLog.getOperateTitle())) {
                    return;
                }
            }
            asyncInsert(operateLog);
        } catch (Exception e) {
            log.error("组装操作日志失败", e);
        } finally {
            // 请求线程可能被池化复用，异常路径也要清掉审计暂存。
            ConfigAuditHolder.clear();
        }
    }

    private void asyncInsert(OperateLog operateLog) {
        // 请求上下文已在当前线程快照完成，落库交给虚拟线程，避免阻塞接口返回。
        Thread.startVirtualThread(() -> {
            try {
                // 日志写入失败由当前异步任务隔离，不能反向影响业务请求。
                operateLogService.insertOperateLog(operateLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        });
    }

    private OperateLog copyOperateLog(OperateLog source) {
        OperateLog target = new OperateLog();
        BeanUtils.copyProperties(source, target);
        // 每条审计日志独立主键，避免批量拆分时冲突。
        target.setOperateId(null);
        return target;
    }

    /** LOGIN / OPERATE 分别读对应开关；未识别类型默认记。 */
    private boolean isLoggingEnabled(String loggingType) {
        if ("LOGIN".equalsIgnoreCase(loggingType)) {
            return configRuntimeService.getBoolean(LogConfigCodes.LOGIN_ENABLED).orElse(true);
        }
        if ("OPERATE".equalsIgnoreCase(loggingType)) {
            return configRuntimeService.getBoolean(LogConfigCodes.OPERATE_ENABLED).orElse(true);
        }
        return true;
    }

        private void fillRequestInfo(OperateLog operateLog, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        operateLog.setRequestMethod(request.getMethod());
        operateLog.setRequestUri(request.getRequestURI());
        operateLog.setOperateIp(ServletUtils.getIP(request));
        operateLog.setServerIp(getServerIp());
        operateLog.setUserAgent(StrUtil.sub(userAgent, 0, 1000));
        operateLog.setBrowser(UserAgentUtils.parseBrowser(userAgent));
        operateLog.setSystemOs(UserAgentUtils.parseSystemOs(userAgent));
        operateLog.setTraceId(getTraceId(request));
        operateLog.setStatusCode(ServletUtils.getResponse() == null ? null : ServletUtils.getResponse().getStatus());
    }

    private User snapshotLogUser() {
        try {
            return SaTokenUtil.getUser();
        } catch (Exception e) {
            return null;
        }
    }

    private void fillUserInfo(OperateLog operateLog, User logUser) {
        User currentUser = logUser;
        if (currentUser == null) {
            try {
                // 登录在 proceed 后才建立会话；注销则依赖 proceed 前的快照。
                currentUser = SaTokenUtil.getUser();
            } catch (Exception e) {
                return;
            }
        }
        if (currentUser == null) {
            return;
        }
        operateLog.setUserId(currentUser.getUserId());
        // 操作人统一记录登录账号，与登录失败时的账号口径一致。
        operateLog.setOperateName(StrUtil.isNotBlank(currentUser.getUserAccount())
                ? currentUser.getUserAccount()
                : currentUser.getUserName());
    }

    private void fillMethodInfo(OperateLog operateLog, ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        operateLog.setOperateMethod(className + "." + methodName + "()");
    }

    private void fillAnnotationInfo(OperateLog operateLog, Log controllerLog, ProceedingJoinPoint joinPoint) {
        operateLog.setOperateTitle(controllerLog.title());
        operateLog.setLoggingType(controllerLog.loggingType());
        operateLog.setBusinessType(controllerLog.businessType());
        fillLoginAttemptName(operateLog, controllerLog, joinPoint);
        if (controllerLog.isSaveRequestData()) {
            operateLog.setRequestParam(getRequestParam());
            operateLog.setRequestBody(getRequestBody(joinPoint.getArgs()));
        }
    }

    private void fillResultInfo(OperateLog operateLog, Log controllerLog, Object result, Throwable throwable) {
        if (throwable != null) {
            operateLog.setIsSuccess(0);
            operateLog.setStatusCode(500);
            operateLog.setErrorClass(throwable.getClass().getName());
            operateLog.setErrorMsg(StrUtil.sub(throwable.getMessage(), 0, 1000));
            operateLog.setErrorStack(StrUtil.sub(getStackTrace(throwable), 0, STACK_LIMIT));
            return;
        }
        operateLog.setIsSuccess(isBusinessSuccess(result) ? 1 : 0);
        if (operateLog.getIsSuccess() == 0) {
            fillBusinessErrorInfo(operateLog, result);
        }
        if (controllerLog.isSaveResponseData()) {
            operateLog.setResponseBody(StrUtil.sub(maskSensitive(toJson(result)), 0, BODY_LIMIT));
        }
    }

    private void fillLoginAttemptName(OperateLog operateLog, Log controllerLog, ProceedingJoinPoint joinPoint) {
        if (!"LOGIN".equalsIgnoreCase(controllerLog.loggingType()) || StrUtil.isNotBlank(operateLog.getOperateName())) {
            return;
        }
        // 登录失败时还没有会话，使用提交账号作为日志操作人。
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof User user && StrUtil.isNotBlank(user.getUserAccount())) {
                operateLog.setOperateName(user.getUserAccount());
                return;
            }
            if (arg instanceof LoginDto.LoginRequest loginRequest && StrUtil.isNotBlank(loginRequest.userAccount())) {
                operateLog.setOperateName(loginRequest.userAccount());
                return;
            }
        }
        operateLog.setOperateName(ServletUtils.getParameter("userAccount"));
    }

    private boolean isBusinessSuccess(Object result) {
        Object body = unwrapResultBody(result);
        if (body instanceof R<?> response) {
            return response.code() < 400;
        }
        return true;
    }

    private void fillBusinessErrorInfo(OperateLog operateLog, Object result) {
        Object body = unwrapResultBody(result);
        if (body instanceof R<?> response && response.msg() != null) {
            // 业务失败只记录业务错误信息，statusCode 保持真实 HTTP 响应状态。
            // 业务失败没有异常堆栈，记录返回消息即可定位失败原因。
            operateLog.setErrorMsg(StrUtil.sub(response.msg(), 0, 1000));
        }
    }

    private Object unwrapResultBody(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getBody();
        }
        return result;
    }

    private String getRequestParam() {
        Map<String, String[]> map = ServletUtils.getRequest().getParameterMap();
        return StrUtil.sub(maskSensitive(toJson(map)), 0, PARAM_LIMIT);
    }

    private String getRequestBody(Object[] args) {
        String method = ServletUtils.getRequest().getMethod();
        String contentType = ServletUtils.getRequest().getContentType();
        if (!("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method))
                || contentType == null || !contentType.contains("application/json")) {
            return "";
        }
        Object[] serializableArgs = args == null ? new Object[0] : Arrays.stream(args)
                .filter(this::isSerializableArg)
                .toArray();
        return StrUtil.sub(maskSensitive(toJson(serializableArgs)), 0, BODY_LIMIT);
    }

    private boolean isSerializableArg(Object arg) {
        return arg != null
                && !(arg instanceof ServletRequest)
                && !(arg instanceof ServletResponse)
                && !(arg instanceof Model)
                && !(arg instanceof BindingResult)
                && !(arg instanceof MultipartFile);
    }

    private Log getAnnotationLog(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method == null ? null : method.getAnnotation(Log.class);
    }

    private String toJson(Object value) {
        try {
            return jsonMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            log.warn("序列化日志字段失败", e);
            return "";
        }
    }

    private String maskSensitive(String text) {
        if (StrUtil.isBlank(text)) {
            return text;
        }
        // 日志落库前脱敏，避免密码、token、验证码进入审计表。
        return text.replaceAll("(?i)(password|token|captcha|saToken)(\"?\\s*[:=]\\s*\"?)[^\",&}]*", "$1$2******");
    }

    private String getTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (StrUtil.isNotBlank(traceId)) {
            return StrUtil.sub(traceId, 0, 64);
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    private String getStackTrace(Throwable throwable) {
        return ExceptionUtil.stacktraceToString(throwable);
    }
}
