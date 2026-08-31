package com.ylmao.admin.common;

/**
 * 纯文本安全工具：入库规范化与 HTML 转义。
 * 公告等展示内容按纯文本口径处理，不做全站请求体清洗。
 */
public final class TextSafeUtils {

    private TextSafeUtils() {
    }

    /**
     * 纯文本规范化：trim，去掉 NUL 与其它控制字符（保留 tab / 换行）。
     */
    public static String normalizePlainText(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\t' || c == '\n' || c == '\r') {
                sb.append(c);
                continue;
            }
            // 跳过 C0 控制符与 DEL，避免异常字符进入库与页面。
            if (c < 0x20 || c == 0x7F) {
                continue;
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }

    /** HTML 转义，供拼进页面或属性的出口使用。 */
    public static String escapeHtml(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
