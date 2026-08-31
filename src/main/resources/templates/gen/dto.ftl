package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ${className}Dto {

    public record ${className}List(
<#list listQueryColumns as col>
    <#if col.javaType == "String">
            @Size(max = ${col.columnSize?c}, message = "${functionName}${col.columnComment!col.fieldName}参数不合法") String ${col.fieldName}<#else>
            ${col.javaType} ${col.fieldName}</#if><#if col_has_next>,</#if>
</#list>
    ) {
    }

    public record ${className}Insert(
<#list formColumns as col>
    <#if col.enabledField>
            @NotNull(message = "${functionName}状态参数不合法") @Min(value = 0, message = "${functionName}状态参数不合法") @Max(value = 1, message = "${functionName}状态参数不合法") Integer ${col.fieldName}<#elseif col.javaType == "String" && (!col.nullable || col.nameField || col.codeField)>
            @NotBlank(message = "${functionName}${col.columnComment!col.fieldName}不能为空") @Size(max = ${col.columnSize?c}, message = "${functionName}${col.columnComment!col.fieldName}参数不合法") String ${col.fieldName}<#elseif col.javaType == "String">
            @Size(max = ${col.columnSize?c}, message = "${functionName}${col.columnComment!col.fieldName}参数不合法") String ${col.fieldName}<#elseif col.orderNumField>
            @NotNull(message = "${functionName}排序不能为空") Integer ${col.fieldName}<#else>
            @NotNull(message = "${functionName}${col.columnComment!col.fieldName}不能为空") ${col.javaType} ${col.fieldName}</#if><#if col_has_next>,</#if>
</#list>
    ) {
    }

    public record ${className}Update(
            @NotBlank(message = "${functionName}ID不能为空") String ${pkFieldName},
<#list formColumns as col>
    <#if col.enabledField>
            @NotNull(message = "${functionName}状态参数不合法") @Min(value = 0, message = "${functionName}状态参数不合法") @Max(value = 1, message = "${functionName}状态参数不合法") Integer ${col.fieldName}<#elseif col.javaType == "String" && (!col.nullable || col.nameField || col.codeField)>
            @NotBlank(message = "${functionName}${col.columnComment!col.fieldName}不能为空") @Size(max = ${col.columnSize?c}, message = "${functionName}${col.columnComment!col.fieldName}参数不合法") String ${col.fieldName}<#elseif col.javaType == "String">
            @Size(max = ${col.columnSize?c}, message = "${functionName}${col.columnComment!col.fieldName}参数不合法") String ${col.fieldName}<#elseif col.orderNumField>
            @NotNull(message = "${functionName}排序不能为空") Integer ${col.fieldName}<#else>
            @NotNull(message = "${functionName}${col.columnComment!col.fieldName}不能为空") ${col.javaType} ${col.fieldName}</#if><#if col_has_next>,</#if>
</#list>
    ) {
    }

<#if hasIsEnabled>
    public record UpdateEnabled(
            @NotBlank(message = "${functionName}ID不能为空") String ${pkFieldName},
            @NotNull(message = "${functionName}状态参数不合法") @Min(value = 0, message = "${functionName}状态参数不合法") @Max(value = 1, message = "${functionName}状态参数不合法")
            Integer isEnabled
    ) {
    }
</#if>
}
