package com.ylmao.admin.utils;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.convert.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 客户端工具类
 * @author fuce 
 * @date: 2018年9月30日 下午2:10:48
 */
public class ServletUtils
{
    private static final Logger log = LoggerFactory.getLogger(ServletUtils.class);

    /**
     * 获取String参数
     */
    public static String getParameter(String name)
    {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue)
    {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name)
    {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue)
    {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession()
    {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     * 
     * @param response 渲染对象
     * @param string 待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string)
    {
        try
        {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            log.error("响应 JSON 写入失败", e);
        }
        return null;
    }

    /**
     * 是否是Ajax异步请求
     * 
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request)
    {

        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("application/json"))
        {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest"))
        {
            return true;
        }

        String uri = request.getRequestURI();
        if (StrUtil.equalsAnyIgnoreCase(uri, ".json", ".xml"))
        {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        if (StrUtil.equalsAnyIgnoreCase(ajax, "json", "xml"))
        {
            return true;
        }

        return false;
    }


	private static boolean checkIp(String ip) {
        return !SaFoxUtil.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip);
    }
    
	/**
	 * 返回请求端 IP；多级代理时取 X-Forwarded-For 第一个地址。
	 */
	public static String getIP(HttpServletRequest request) {
		String ip = firstForwardedIp(request.getHeader("x-forwarded-for"));
		ip = checkIp(ip) ? ip : (
                checkIp(ip = request.getHeader("Proxy-Client-IP")) ? ip : (
                        checkIp(ip = request.getHeader("WL-Proxy-Client-IP")) ? ip :
                                request.getRemoteAddr()));
		if (ip != null) {
			ip = ip.trim();
		}
		return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
	}

	/** X-Forwarded-For 形如 client, proxy1, proxy2 时只取最左侧客户端 IP。 */
	private static String firstForwardedIp(String forwarded) {
		if (SaFoxUtil.isEmpty(forwarded)) {
			return forwarded;
		}
		int comma = forwarded.indexOf(',');
		return comma < 0 ? forwarded.trim() : forwarded.substring(0, comma).trim();
	}

}
