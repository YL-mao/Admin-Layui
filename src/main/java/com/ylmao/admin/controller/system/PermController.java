package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.PermDto;
import com.ylmao.admin.service.PermService;
import com.ylmao.admin.vo.PermVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/perm")
@RequiredArgsConstructor
public class PermController extends BaseController {
    private static final String PERM_VIEW = "system/perm";

    private final PermService permService;

    @SaCheckPermission("system:role:auth")
    @GetMapping("/rolePermTree")
    @ResponseBody
    public R<?> queryRolePermTree(String roleId) {
        return okData(permService.queryPermCheckVoByRoleId(roleId));
    }

    @Log(title = "权限授权保存")
    // 保存后由 PermService 清理在线用户的权限码 Session 缓存。
    @SaCheckPermission("system:role:auth")
    @PutMapping("/rolePerm")
    @ResponseBody
    public R<?> saveRolePerm(@Valid @RequestBody PermDto.RolePermSave rolePermSave) {
        permService.updateRolePerm(rolePermSave.roleId(), rolePermSave.permIds());
        return success();
    }

    @Log(title = "权限管理页面", businessType = "QUERY")
    @SaCheckPermission("system:perm:view")
    @GetMapping("/listView")
    public String permListView(ModelMap model) {
        return PERM_VIEW;
    }

    @Log(title = "权限列表查询", businessType = "QUERY")
    @SaCheckPermission("system:perm:select")
    @GetMapping("/list")
    @ResponseBody
    public R<?> permList(@Valid PermDto.PermList permList) {
        List<PermVo.PermListVo> list = permService.selectList(permList);
        return pageData(list, list.size());
    }

    @Log(title = "新增权限数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:perm:insert")
    @PostMapping("/add")
    @ResponseBody
    public R<?> permInsert(@Valid @RequestBody PermDto.PermInsert permInsert) {
        permService.insert(permInsert);
        return success();
    }

    @Log(title = "修改权限数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:perm:update")
    @PutMapping("/update")
    @ResponseBody
    public R<?> permUpdate(@Valid @RequestBody PermDto.PermUpdate permUpdate) {
        permService.updateById(permUpdate);
        return success();
    }

    @Log(title = "删除权限数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:perm:delete")
    @DeleteMapping("/delete")
    @ResponseBody
    public R<?> permDelete(String ids) {
        permService.deleteById(ids);
        return success();
    }

    @Log(title = "查询权限名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:perm:insert", "system:perm:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    @ResponseBody
    public R<Boolean> checkPermNameUnique(String parentId, String permName) {
        return R.ok(permService.checkPermNameUnique(parentId, permName) == null);
    }

    @Log(title = "查询权限标识是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:perm:insert", "system:perm:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    @ResponseBody
    public R<Boolean> checkPermCodeUnique(String permCode) {
        return R.ok(permService.checkPermCodeUnique(permCode) == null);
    }

    @Log(title = "修改权限状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:perm:updateEnabled")
    @PatchMapping("/updateEnabled")
    @ResponseBody
    public R<?> updatePermEnabled(@Valid @RequestBody PermDto.UpdateEnabled updateEnabled) {
        permService.updatePermEnabled(updateEnabled);
        return success();
    }

    @SaCheckPermission("system:perm:selectParent")
    @GetMapping("/selectParent")
    @ResponseBody
    public R<?> selectPermParent() {
        return okData(permService.selectParentVoList());
    }
}