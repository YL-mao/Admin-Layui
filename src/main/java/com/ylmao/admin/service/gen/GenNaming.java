package com.ylmao.admin.service.gen;

import cn.hutool.core.util.StrUtil;

import java.util.Locale;
import java.util.Set;

/**
 * 表名、列名与 Java 标识符之间的命名转换。
 */
public final class GenNaming {

    private static final Set<String> AUDIT_COLUMNS = Set.of(
            "create_by", "create_time", "update_by", "update_time", "is_del");

    private GenNaming() {
    }

    public static boolean isAuditColumn(String columnName) {
        return AUDIT_COLUMNS.contains(columnName.toLowerCase(Locale.ROOT));
    }

    /** sys_demo → demo；其它表去掉首个 sys_ 前缀。 */
    public static String tableToModuleName(String tableName) {
        String name = tableName.toLowerCase(Locale.ROOT);
        if (name.startsWith("sys_")) {
            name = name.substring(4);
        }
        return name;
    }

    public static String columnToFieldName(String columnName) {
        return toCamelCase(columnName);
    }

    public static String toCamelCase(String name) {
        if (StrUtil.isBlank(name)) {
            return name;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if (c == '_' || c == '-') {
                upperNext = true;
                continue;
            }
            if (upperNext) {
                sb.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String toPascalCase(String name) {
        String camel = toCamelCase(name);
        if (StrUtil.isBlank(camel)) {
            return camel;
        }
        return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }

    /** JDBC 类型名映射为 Java 类型（与项目 PO 习惯一致）。 */
    public static String mapJavaType(String jdbcTypeName) {
        if (jdbcTypeName == null) {
            return "String";
        }
        String type = jdbcTypeName.toUpperCase(Locale.ROOT);
        return switch (type) {
            case "BIT", "BOOLEAN", "TINYINT", "SMALLINT", "INTEGER", "INT" -> "Integer";
            case "BIGINT" -> "Long";
            case "FLOAT", "REAL" -> "Float";
            case "DOUBLE" -> "Double";
            case "DECIMAL", "NUMERIC" -> "BigDecimal";
            case "DATE", "TIME", "TIMESTAMP", "DATETIME" -> "LocalDateTime";
            default -> "String";
        };
    }

    /** 校验并规范化前端选择的 Java 类型。 */
    public static String normalizeJavaType(String javaType) {
        if (javaType == null || javaType.isBlank()) {
            throw new IllegalArgumentException("Java 类型不能为空");
        }
        String type = javaType.trim();
        return switch (type) {
            case "String", "Integer", "Long", "BigDecimal", "LocalDateTime", "Float", "Double" -> type;
            default -> throw new IllegalArgumentException("不支持的 Java 类型：" + type);
        };
    }
}
