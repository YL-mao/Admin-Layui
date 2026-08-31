package com.ylmao.admin.utils;

import cn.hutool.core.util.StrUtil;

/** 从 User-Agent 解析浏览器与操作系统（与操作日志解析规则保持一致）。 */
public final class UserAgentUtils {

    private UserAgentUtils() {
    }

    public static String parseBrowser(String userAgent) {
        if (StrUtil.isBlank(userAgent)) {
            return "";
        }
        if (userAgent.contains("Edg/")) {
            return "Edge";
        }
        if (userAgent.contains("Chrome/")) {
            return "Chrome";
        }
        if (userAgent.contains("Firefox/")) {
            return "Firefox";
        }
        if (userAgent.contains("Safari/")) {
            return "Safari";
        }
        return "Other";
    }

    public static String parseSystemOs(String userAgent) {
        if (StrUtil.isBlank(userAgent)) {
            return "";
        }
        if (userAgent.contains("Windows")) {
            return "Windows";
        }
        if (userAgent.contains("Mac OS X")) {
            return "macOS";
        }
        if (userAgent.contains("Android")) {
            return "Android";
        }
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        if (userAgent.contains("Linux")) {
            return "Linux";
        }
        return "Other";
    }
}
