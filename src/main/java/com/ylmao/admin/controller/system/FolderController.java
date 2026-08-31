package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.FolderDto;
import com.ylmao.admin.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController extends BaseController {

    private final FolderService folderService;

    @Log(title = "目录树查询", businessType = "QUERY")
    @SaCheckPermission("system:file:tree")
    @GetMapping("/tree")
    @ResponseBody
    public R<?> folderTree() {
        return okData(folderService.listOptions());
    }

    @Log(title = "新增目录数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:file:folderInsert")
    @PostMapping("/add")
    @ResponseBody
    public R<?> folderInsert(@Valid @RequestBody FolderDto.FolderInsert folderInsert) {
        folderService.insert(folderInsert);
        return success();
    }

    @Log(title = "修改目录数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:file:folderUpdate")
    @PutMapping("/update")
    @ResponseBody
    public R<?> folderUpdate(@Valid @RequestBody FolderDto.FolderUpdate folderUpdate) {
        folderService.updateById(folderUpdate);
        return success();
    }

    @Log(title = "删除目录数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:file:folderDelete")
    @DeleteMapping("/delete")
    @ResponseBody
    public R<?> folderDelete(String ids) {
        folderService.deleteByIds(ids);
        return success();
    }
}
