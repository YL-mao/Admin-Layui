package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.${className};
import lombok.Data;
<#assign needLdt = false>
<#assign needBd = false>
<#list listDisplayColumns as col>
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
public class ${className}Vo {

    public record ${className}ListVo(
<#list listDisplayColumns as col>
    <#if col.javaType == "LocalDateTime">
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ${col.fieldName}<#else>
            ${col.javaType} ${col.fieldName}</#if><#if col_has_next>,</#if>
</#list>
    ) {

        public static ${className}ListVo from(${className} entity) {
            return new ${className}ListVo(
<#list listDisplayColumns as col>
                    entity.get${col.fieldName?cap_first}()<#if col_has_next>,</#if>
</#list>
            );
        }
    }
}
