package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.DictTypeDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.DictTypeService;
import com.ylmao.admin.vo.DictVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dictType")
@RequiredArgsConstructor
public class DictTypeController extends BaseController {

    private static final String DICT_VIEW = "system/dict";

    private final DictTypeService dictTypeService;

    @Log(title = "字典页面", businessType = "QUERY")
    @SaCheckPermission("system:dict:view")
    @GetMapping("/listView")
    public String listView(ModelMap model) {
        return DICT_VIEW;
    }

    @Log(title = "字典类型分页查询", businessType = "QUERY")
    @SaCheckPermission("system:dictType:select")
    @GetMapping("/list")
    @ResponseBody
    public R<?> dictTypeList(@Valid PageQuery pageQuery, @Valid DictTypeDto.DictTypeList dictTypeList) {
        // Controller 出口统一返回 VO，避免暴露字典类型 PO。
        IPage<DictVo.DictTypeListVo> dictTypePage = dictTypeService.selectPageList(pageQuery, dictTypeList);
        return pageData(dictTypePage.getRecords(), dictTypePage.getTotal());
    }

    @Log(title = "新增字典类型", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:dictType:insert")
    @PostMapping("/add")
    @ResponseBody
    public R<?> dictTypeInsert(@Valid @RequestBody DictTypeDto.DictTypeInsert dictTypeInsert) {
        dictTypeService.insert(dictTypeInsert);
        return success();
    }

    @Log(title = "修改字典类型", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dictType:update")
    @PutMapping("/update")
    @ResponseBody
    public R<?> dictTypeUpdate(@Valid @RequestBody DictTypeDto.DictTypeUpdate dictTypeUpdate) {
        dictTypeService.updateById(dictTypeUpdate);
        return success();
    }

    @Log(title = "删除字典类型", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:dictType:delete")
    @DeleteMapping("/delete")
    @ResponseBody
    public R<?> dictTypeDelete(String ids) {
        dictTypeService.deleteById(ids);
        return success();
    }

    @Log(title = "修改字典类型状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:dictType:updateEnabled")
    @PatchMapping("/updateEnabled")
    @ResponseBody
    public R<?> updateDictTypeEnabled(@Valid @RequestBody DictTypeDto.UpdateEnabled updateEnabled) {
        // 状态参数含义由 Service 统一校验，Controller 只负责转交 DTO。
        dictTypeService.updateEnabled(updateEnabled);
        return success();
    }

    @Log(title = "查询字典编码是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:dictType:insert", "system:dictType:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    @ResponseBody
    public R<Boolean> checkDictTypeCodeUnique(String dictTypeCode) {
        return R.ok(dictTypeService.checkDictTypeCodeUnique(dictTypeCode) == null);
    }
}
