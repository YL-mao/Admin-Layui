package com.ylmao.admin.service.gen;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 一次代码生成的完整上下文。
 */
@Getter
@Builder
public class GenCodegenModel {
    private final String tableName;
    private final String moduleName;
    private final String className;
    private final String functionName;
    private final String permPrefix;
    private final String parentPermId;
    private final String parentPermPath;
    /** Controller 子包与 templates 子目录，如 demo / system。 */
    private final String packagePath;
    private final String author;
    private final String pkFieldName;
    private final String pkColumnName;
    private final String pkJavaType;
    private final boolean hasIsEnabled;
    private final boolean hasOrderNum;
    private final boolean hasNameField;
    private final boolean hasCodeField;
    private final GenColumnModel nameColumn;
    private final GenColumnModel codeColumn;
    private final List<GenColumnModel> allColumns;
    private final List<GenColumnModel> formColumns;
    private final List<GenColumnModel> listQueryColumns;
    private final List<GenColumnModel> listDisplayColumns;
    private final Map<String, String> permIds;
    private final String adminRoleId;
}
