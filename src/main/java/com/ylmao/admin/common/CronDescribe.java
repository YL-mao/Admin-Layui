package com.ylmao.admin.common;

import cn.hutool.core.util.StrUtil;

/**
 * 将 Spring 6 段 Cron（秒 分 时 日 月 周）转成简短中文说明，供列表展示。
 * 无法识别的表达式原样返回。
 */
public final class CronDescribe {

    private CronDescribe() {
    }

    public static String toZh(String cron) {
        if (StrUtil.isBlank(cron)) {
            return "";
        }
        String raw = cron.trim();
        String[] parts = raw.split("\\s+");
        if (parts.length != 6) {
            return raw;
        }
        String sec = parts[0];
        String min = parts[1];
        String hour = parts[2];
        String day = parts[3];
        String month = parts[4];
        String week = parts[5];

        // 每天固定时刻：0 10 3 * * ? → 每天 03:10
        if ("*".equals(day) && "*".equals(month) && isAnyWeek(week)
                && isNum(sec) && isNum(min) && isNum(hour)) {
            int h = Integer.parseInt(hour);
            int m = Integer.parseInt(min);
            int s = Integer.parseInt(sec);
            if (s == 0) {
                return String.format("每天 %02d:%02d", h, m);
            }
            return String.format("每天 %02d:%02d:%02d", h, m, s);
        }
        return raw;
    }

    private static boolean isAnyWeek(String week) {
        return "?".equals(week) || "*".equals(week);
    }

    private static boolean isNum(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
