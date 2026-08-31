package com.ylmao.admin.controller.${packagePath};

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.${className}Dto;
import com.ylmao.admin.service.${className}Service;
import com.ylmao.admin.vo.${className}Vo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/${moduleName}")
@RequiredArgsConstructor
public class ${className}Controller extends BaseController {
    private static final String VIEW = "${packagePath}/${moduleName}";

    private final ${className}Service ${moduleName}Service;

    @Log(title = "${functionName}页面跳转", businessType = "QUERY")
    @SaCheckPermission("${permPrefix}:view")
    @GetMapping("/listView")
    public String ${moduleName}ListView(ModelMap model) {
        return VIEW;
    }

    @Log(title = "${functionName}分页查询", businessType = "QUERY")
    @SaCheckPermission("${permPrefix}:select")
    @GetMapping("/list")
    @ResponseBody
    public R<?> ${moduleName}List(@Valid PageQuery pageQuery, @Valid ${className}Dto.${className}List ${moduleName}List) {
        IPage<${className}Vo.${className}ListVo> iPage = ${moduleName}Service.selectPage(pageQuery, ${moduleName}List);
        return pageData(iPage.getRecords(), iPage.getTotal());
    }

    @Log(title = "新增${functionName}数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("${permPrefix}:insert")
    @PostMapping("/add")
    @ResponseBody
    public R<?> ${moduleName}Insert(@Valid @RequestBody ${className}Dto.${className}Insert dto) {
        ${moduleName}Service.insert(dto);
        return success();
    }

    @Log(title = "修改${functionName}数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("${permPrefix}:update")
    @PutMapping("/update")
    @ResponseBody
    public R<?> ${moduleName}Update(@Valid @RequestBody ${className}Dto.${className}Update dto) {
        ${moduleName}Service.updateById(dto);
        return success();
    }

    @Log(title = "删除${functionName}数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("${permPrefix}:delete")
    @DeleteMapping("/delete")
    @ResponseBody
    public R<?> ${moduleName}Delete(String ids) {
        ${moduleName}Service.deleteById(ids);
        return success();
    }

<#if hasCodeField>
    @Log(title = "查询${functionName}编码是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"${permPrefix}:insert", "${permPrefix}:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    @ResponseBody
    public R<Boolean> check${className}CodeUnique(String ${codeColumn.fieldName}) {
        return R.ok(${moduleName}Service.check${className}CodeUnique(${codeColumn.fieldName}) == null);
    }
</#if>

<#if hasNameField>
    @Log(title = "查询${functionName}名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"${permPrefix}:insert", "${permPrefix}:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    @ResponseBody
    public R<Boolean> check${className}NameUnique(String ${nameColumn.fieldName}) {
        return R.ok(${moduleName}Service.check${className}NameUnique(${nameColumn.fieldName}) == null);
    }
</#if>

<#if hasIsEnabled>
    @Log(title = "修改${functionName}状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("${permPrefix}:updateEnabled")
    @PatchMapping("/updateEnabled")
    @ResponseBody
    public R<?> update${className}Enabled(@Valid @RequestBody ${className}Dto.UpdateEnabled updateEnabled) {
        ${moduleName}Service.update${className}Enabled(updateEnabled);
        return success();
    }
</#if>
}
