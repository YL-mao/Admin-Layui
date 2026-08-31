package com.ylmao.admin.service.gen;

import lombok.Builder;
import lombok.Getter;

/**
 * 单列元数据，供 Freemarker 模板渲染 generated 代码。
 */
@Getter
@Builder(toBuilder = true)
public class GenColumnModel {
    private final String columnName;
    private final String fieldName;
    private final String javaType;
    private final String jdbcTypeName;
    private final String columnComment;
    private final int columnSize;
    private final boolean nullable;
    private final boolean primaryKey;
    private final boolean auditColumn;
    private final boolean formField;
    private final boolean listQueryField;
    private final boolean listDisplayField;
    private final boolean nameField;
    private final boolean codeField;
    private final boolean enabledField;
    private final boolean orderNumField;
}
