package com.ylmao.admin.config.exception;

import cn.dev33.satoken.exception.*;
import com.ylmao.admin.common.R;
import com.ylmao.admin.utils.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理。Ajax 返回 {@link R} JSON，页面返回 {@link ModelAndView}。
 * <p>
 * 使用 {@link ControllerAdvice} 而非 {@code RestControllerAdvice}，避免视图被当 JSON 序列化。
 * Ajax 错误约定：HTTP 状态码与 {@code R.code} 一致；400 参数校验，500 业务/系统异常（系统异常固定 {@link #SYSTEM_BUSY_MSG}）。
 * {@link #handleRuntimeException} 与 {@link #handleException} 分别兜底运行时异常与受检异常，响应逻辑需保持一致。
 *
 * @see ServletUtils#isAjaxRequest(HttpServletRequest)
 */
@ControllerAdvice
public class GlobalExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    private static final String SYSTEM_BUSY_MSG = "系统繁忙，请稍后重试";

    /** Sa-Token 鉴权异常：Ajax 返回 JSON，页面跳转登录/403/500。 */
    @ExceptionHandler(SaTokenException.class)
    public Object handleAuthorizationException(HttpServletRequest request, SaTokenException e) {
        if (e instanceof NotLoginException) {
            logger.warn("登录校验异常: {}", e.getMessage());
        } else if (e instanceof NotPermissionException || e instanceof NotRoleException || e instanceof NotSafeException) {
            logger.warn("权限校验异常: {}", e.getMessage());
        } else {
            logger.error("Sa-Token异常:", e);
        }
        if (ServletUtils.isAjaxRequest(request)) {
            if (e instanceof NotLoginException) {
                return ajaxError(401, e.getMessage());
            }
            if (e instanceof NotPermissionException || e instanceof NotRoleException || e instanceof NotSafeException) {
                return ajaxError(403, e.getMessage());
            }
            return ajaxError(500, e.getMessage());
        }
        if (e instanceof NotLoginException) {
            return new ModelAndView("/login");
        }
        if (e instanceof NotPermissionException || e instanceof NotRoleException || e instanceof NotSafeException) {
            return new ModelAndView("/error/403");
        }
        return new ModelAndView("/error/500");
    }

    /** 静态资源 404；{@code .map} 为 DevTools 自动请求，静默返回不记日志。 */
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(NoResourceFoundException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path != null && path.endsWith(".map")) {
            return ResponseEntity.notFound().build();
        }
        logger.debug("资源不存在: {}", path);
        if (ServletUtils.isAjaxRequest(request)) {
            return ajaxError(404, "资源不存在");
        }
        return new ModelAndView("/error/404");
    }

    /** Controller 路由不存在，返回 404 而非 500。 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFound(NoHandlerFoundException e, HttpServletRequest request) {
        logger.debug("路由不存在: {}", request.getRequestURI());
        if (ServletUtils.isAjaxRequest(request)) {
            return ajaxError(404, "请求地址不存在");
        }
        return new ModelAndView("/error/404");
    }

    /** {@code @Valid @RequestBody} 校验失败，HTTP 400。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<R<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        logger.warn("参数校验异常: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ajaxError(400, message);
    }

    /** 表单/Query 参数 {@code @Valid} 校验失败，HTTP 400。 */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<R<Void>> validatedBindException(BindException e) {
        logger.warn("参数校验异常: {}", e.getMessage());
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ajaxError(400, message);
    }

    /** HTTP 方法不匹配，HTTP 405。 */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public ResponseEntity<R<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        logger.warn("请求方式不支持: {}", e.getMethod());
        return ajaxError(405, "不支持「" + e.getMethod() + "」请求");
    }

    /** Service 业务规则失败，HTTP 500，{@code msg} 为业务文案。 */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<R<Void>> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        return ajaxError(500, e.getMessage());
    }

    /** 容器层文件过大（尚未进入业务校验）。 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<R<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.warn("上传文件过大: {}", e.getMessage());
        return ajaxError(500, "文件大小超出限制");
    }

    /** 运行时异常兜底，不向用户暴露内部信息。 */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("系统运行时异常", e);
        if (ServletUtils.isAjaxRequest(request)) {
            return ajaxError(500, SYSTEM_BUSY_MSG);
        }
        ModelAndView mv = new ModelAndView("/error/500");
        mv.addObject("message", SYSTEM_BUSY_MSG);
        return mv;
    }

    /** 受检异常兜底，不向用户暴露内部信息。 */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        logger.error("系统异常", e);
        if (ServletUtils.isAjaxRequest(request)) {
            return ajaxError(500, SYSTEM_BUSY_MSG);
        }
        ModelAndView mv = new ModelAndView("/error/500");
        mv.addObject("message", SYSTEM_BUSY_MSG);
        return mv;
    }

    private ResponseEntity<R<Void>> ajaxError(int code, String message) {
        return ResponseEntity.status(code).body(R.fail(code, message));
    }
}
