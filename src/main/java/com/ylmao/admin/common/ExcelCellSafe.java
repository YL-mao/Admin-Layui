package com.ylmao.admin.common;

/**
 * 导出 Excel 单元格公式注入防护：危险前缀加单引号，仅影响写出内容。
 */
public final class ExcelCellSafe {

    private ExcelCellSafe() {
    }

    public static String escape(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        char c = value.charAt(0);
        if (c == '=' || c == '+' || c == '-' || c == '@' || c == '\t' || c == '\r' || c == '\n') {
            return "'" + value;
        }
        return value;
    }
}
