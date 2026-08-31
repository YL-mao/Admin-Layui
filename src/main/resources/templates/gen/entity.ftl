package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.${className}Dto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
<#assign needLdt = false>
<#assign needBd = false>
<#list allColumns as col>
<#if col.javaType == "LocalDateTime"><#assign needLdt = true></#if>
<#if col.javaType == "BigDecimal"><#assign needBd = true></#if>
</#list>
<#if needLdt>
import java.time.LocalDateTime;
</#if>
<#if needBd>
import java.math.BigDecimal;
</#if>

@Data
@NoArgsConstructor
@TableName("${tableName}")
@EqualsAndHashCode(callSuper = false)
public final class ${className} {
<#list allColumns as col>
    <#if col.primaryKey>
    @TableId(type = IdType.ASSIGN_ID)
    </#if>
    <#if col.columnName == "create_by">
    /** 审计字段由 MyBatis-Plus 自动填充创建人。 */
    @TableField(fill = FieldFill.INSERT)
    <#elseif col.columnName == "create_time">
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif col.columnName == "update_by">
    /** 审计字段由 MyBatis-Plus 自动填充更新人。 */
    @TableField(fill = FieldFill.UPDATE)
    <#elseif col.columnName == "update_time">
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif col.columnName == "is_del">
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    <#elseif col.javaType == "LocalDateTime">
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    </#if>
    private ${col.javaType} ${col.fieldName};
</#list>

    public ${className}(${className}Dto.${className}Insert dto) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
<#list formColumns as col>
        this.${col.fieldName} = dto.${col.fieldName}();
</#list>
    }

    public ${className}(${className}Dto.${className}Update dto) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.${pkFieldName} = dto.${pkFieldName}();
<#list formColumns as col>
        this.${col.fieldName} = dto.${col.fieldName}();
</#list>
    }
}
