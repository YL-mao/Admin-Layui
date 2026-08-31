package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.${className}Dto;
import com.ylmao.admin.entity.${className};
import com.ylmao.admin.mapper.${className}Mapper;
import com.ylmao.admin.vo.${className}Vo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ${className}Service {

    private final ${className}Mapper ${moduleName}Mapper;

    public IPage<${className}Vo.${className}ListVo> selectPage(PageQuery pageQuery, ${className}Dto.${className}List ${moduleName}List) {
        LambdaQueryWrapper<${className}> wrapper = new LambdaQueryWrapper<>();
        Page<${className}> page = pageQuery.toMpPage();
<#if hasOrderNum>
        page.addOrder(OrderItem.asc("order_num"));
</#if>
        if (${moduleName}List != null) {
<#list listQueryColumns as col>
    <#if col.javaType == "String">
            if (StrUtil.isNotBlank(${moduleName}List.${col.fieldName}())) {
                wrapper.like(${className}::get${col.fieldName?cap_first}, ${moduleName}List.${col.fieldName}());
            }
    <#else>
            if (${moduleName}List.${col.fieldName}() != null) {
                wrapper.eq(${className}::get${col.fieldName?cap_first}, ${moduleName}List.${col.fieldName}());
            }
    </#if>
</#list>
        }
        return ${moduleName}Mapper.selectPage(page, wrapper).convert(${className}Vo.${className}ListVo::from);
    }

    @Transactional
    public void insert(${className}Dto.${className}Insert dto) {
<#if hasCodeField>
        if (check${className}CodeUnique(dto.${codeColumn.fieldName}()) != null) {
            throw new BusinessException("${functionName}编码已存在");
        }
</#if>
<#if hasNameField>
        if (check${className}NameUnique(dto.${nameColumn.fieldName}()) != null) {
            throw new BusinessException("${functionName}名称已存在");
        }
</#if>
        ${className} entity = new ${className}(dto);
        int rows = ${moduleName}Mapper.insert(entity);
        if (rows <= 0) {
            throw new BusinessException("新增${functionName}失败");
        }
    }

    @Transactional
    public void updateById(${className}Dto.${className}Update dto) {
<#if hasCodeField>
        ${className} oldCode = check${className}CodeUnique(dto.${codeColumn.fieldName}());
        if (oldCode != null && !oldCode.get${pkFieldName?cap_first}().equals(dto.${pkFieldName}())) {
            throw new BusinessException("${functionName}编码已存在");
        }
</#if>
<#if hasNameField>
        ${className} oldName = check${className}NameUnique(dto.${nameColumn.fieldName}());
        if (oldName != null && !oldName.get${pkFieldName?cap_first}().equals(dto.${pkFieldName}())) {
            throw new BusinessException("${functionName}名称已存在");
        }
</#if>
        int rows = ${moduleName}Mapper.updateById(new ${className}(dto));
        if (rows <= 0) {
            throw new BusinessException("${functionName}不存在或修改失败");
        }
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的${functionName}");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        int rows = ${moduleName}Mapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("${functionName}不存在或删除失败");
        }
    }

<#if hasCodeField>
    public ${className} check${className}CodeUnique(String ${codeColumn.fieldName}) {
        LambdaQueryWrapper<${className}> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(${className}::get${codeColumn.fieldName?cap_first}, ${codeColumn.fieldName});
        return ${moduleName}Mapper.selectOne(wrapper);
    }
</#if>

<#if hasNameField>
    public ${className} check${className}NameUnique(String ${nameColumn.fieldName}) {
        LambdaQueryWrapper<${className}> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(${className}::get${nameColumn.fieldName?cap_first}, ${nameColumn.fieldName});
        return ${moduleName}Mapper.selectOne(wrapper);
    }
</#if>

<#if hasIsEnabled>
    @Transactional
    public void update${className}Enabled(${className}Dto.UpdateEnabled updateEnabled) {
        ${className} old = ${moduleName}Mapper.selectById(updateEnabled.${pkFieldName}());
        if (old == null) {
            throw new BusinessException("${functionName}不存在");
        }
        old.setIsEnabled(updateEnabled.isEnabled());
        int rows = ${moduleName}Mapper.updateById(old);
        if (rows <= 0) {
            throw new BusinessException("修改${functionName}状态失败");
        }
    }
</#if>
}
