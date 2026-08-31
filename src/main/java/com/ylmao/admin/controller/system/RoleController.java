package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.RoleDto;
import com.ylmao.admin.service.RoleService;
import com.ylmao.admin.common.R;
import com.ylmao.admin.vo.RoleVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController extends BaseController {

    private static final String ROLE_LIST_VIEW = "system/role";

    private final RoleService roleService;

    @Log(title = "角色管理页面", businessType = "QUERY")
    @SaCheckPermission("system:role:view")
    @GetMapping("/listView")
    public String roleListView(ModelMap model) {
        return ROLE_LIST_VIEW;
    }

    @Log(title = "角色分页查询", businessType = "QUERY")
    @SaCheckPermission("system:role:select")
    @GetMapping("/list")
    @ResponseBody
    public R<?> roleList(@Valid PageQuery pageQuery, @Valid RoleDto.RoleList roleList) {
        IPage<RoleVo.RoleListVo> roleIPage = roleService.selectRolePageList(pageQuery, roleList.roleName());
        return pageData(roleIPage.getRecords(), roleIPage.getTotal());
    }

    @Log(title = "新增角色数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:role:insert")
    @PostMapping("/add")
    @ResponseBody
    public R<?> roleInsert(@Valid @RequestBody RoleDto.RoleInsert roleInsert) {
        roleService.insert(roleInsert);
        return success();
    }

    @Log(title = "修改角色数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:role:update")
    @PutMapping("/update")
    @ResponseBody
    public R<?> roleUpdate(@Valid @RequestBody RoleDto.RoleUpdate roleUpdate) {
        roleService.updateById(roleUpdate);
        return success();
    }

    @Log(title = "删除角色数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:role:delete")
    @DeleteMapping("/delete")
    @ResponseBody
    public R<?> roleDelete(String ids) {
        roleService.deleteById(ids);
        return success();
    }

    @Log(title = "查询角色名称是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:role:insert", "system:role:update"}, mode = SaMode.OR)
    @GetMapping("/checkName")
    @ResponseBody
    public R<Boolean> checkRoleNameUnique(String roleName) {
        return R.ok(roleService.checkRoleNameUnique(roleName) == null);
    }

    @Log(title = "查询角色编码是否唯一", businessType = "QUERY")
    @SaCheckPermission("system:role:checkCode")
    @GetMapping("/checkCode")
    @ResponseBody
    public R<Boolean> checkRoleCodeUnique(String roleCode) {
        return R.ok(roleService.checkRoleCodeUnique(roleCode) == null);
    }

    @Log(title = "修改角色状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:role:updateEnabled")
    @PatchMapping("/updateEnabled")
    @ResponseBody
    public R<?> updateRoleEnabled(@Valid @RequestBody RoleDto.UpdateEnabled updateEnabled) {
        // 状态参数含义由 Service 统一校验，Controller 只负责转交 DTO。
        roleService.updateEnabled(updateEnabled);
        return success();
    }

}
