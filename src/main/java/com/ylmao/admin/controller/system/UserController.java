package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.config.saToken.StpInterfaceImpl;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.UserDto;
import com.ylmao.admin.service.DeptService;
import com.ylmao.admin.service.PostService;
import com.ylmao.admin.service.RoleService;
import com.ylmao.admin.service.UserService;
import com.ylmao.admin.vo.UserVo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private static final String USER_LIST_VIEW = "system/user";

    private final UserService userService;
    private final DeptService deptService;
    private final PostService postService;
    private final RoleService roleService;

    @Log(title = "用户管理页面", businessType = "QUERY")
    @SaCheckPermission("system:user:view")
    @GetMapping("/listView")
    public String userListView(ModelMap model) {
        model.put("deptList", deptService.listOptions());
        model.put("postList", postService.listOptions());
        model.put("roleList", roleService.listOptions());
        model.put("currentUserId", SaTokenUtil.getUserId());
        return USER_LIST_VIEW;
    }


    @Log(title = "用户分页查询", businessType = "QUERY")
    @SaCheckPermission("system:user:select")
    @GetMapping(value = "/list")
    @ResponseBody
    public R<?> userList(@Valid PageQuery pageQuery, @Valid UserDto.UserList userDto ) {

        IPage<UserVo.UserListVo> userIPage = userService.selectPage(pageQuery, userDto);
        return pageData(userIPage.getRecords(), userIPage.getTotal());
    }

    @Log(title = "导出用户列表", businessType = "EXPORT")
    @SaCheckPermission("system:user:export")
    @GetMapping("/export")
    public void userExport(@Valid UserDto.UserList userDto, HttpServletResponse response) throws IOException {
        userService.exportUserList(userDto, response);
    }

    @Log(title = "查询账户是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:user:insert", "system:user:update"}, mode = SaMode.OR)
    @GetMapping("/checkAccount")
    @ResponseBody
    public R<Boolean> checkAccountUnique(String userAccount) {
        return R.ok(userService.getUserByAccount(userAccount) == null);
    }

    @Log(title = "新增用户数据", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:user:insert")
    @PostMapping("/add")
    @ResponseBody
    public R<?> userInsert(@Valid @RequestBody UserDto.UserInsert userInsert) {
        userService.insertUserRoles(userInsert);
        return success();
    }

    @Log(title = "修改用户数据", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:user:update")
    @PutMapping("/update")
    @ResponseBody
    public R<?> userUpdate(@Valid @RequestBody UserDto.UserUpdate userUpdate) {
        userService.userUpdate(userUpdate);
        // 角色变更后清理该用户 Session 中的角色与权限码缓存。
        SaSession userSession = StpUtil.getSessionByLoginId(userUpdate.userId(),false);
        if (userSession != null) {
            userSession.delete("Role_List");
            userSession.delete(StpInterfaceImpl.PERM_LIST);
        }
        return success();
    }


    @Log(title = "管理员重置密码", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:user:updatePwd")
    @PatchMapping("/updatePwd")
    @ResponseBody
    public R<?> updatePwd(@Valid @RequestBody UserDto.UpdatePwd updatePwd) {
        userService.updatePwd(updatePwd);
        return success();
    }

    @Log(title = "修改用户状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:user:updateEnabled")
    @PatchMapping("/updateEnabled")
    @ResponseBody
    public R<?> updateUserEnabled(@Valid @RequestBody UserDto.UpdateEnabled updateEnabled) {
        // 状态参数含义由 Service 统一校验，Controller 只负责转交 DTO。
        userService.updateUserEnabled(updateEnabled);
        return success();
    }

    @Log(title = "解锁用户", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:user:unlock")
    @PatchMapping("/unlock")
    @ResponseBody
    public R<?> unlockUser(@Valid @RequestBody UserDto.Unlock unlock) {
        userService.unlockUser(unlock.userId());
        return success();
    }

    @Log(title = "按用户强退全部会话", businessType = "OTHER", isSaveResponseData = true)
    @SaCheckPermission("system:online:kick")
    @PatchMapping("/kickSessions")
    @ResponseBody
    public R<?> kickUserSessions(@Valid @RequestBody UserDto.KickSessions kickSessions) {
        userService.kickUserSessions(kickSessions.userId());
        return success();
    }

    @Log(title = "删除用户数据", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/delete")
    @ResponseBody
    public R<?> userDelete(String ids) {
        userService.deleteRoleUser(ids);
        return success();
    }

    @Log(title = "用户权限详情", businessType = "QUERY")
    @SaCheckPermission("system:user:permDetail")
    @GetMapping("/permDetail")
    @ResponseBody
    public R<?> userPermDetail(String userId) {
        return okData(userService.getUserPermDetail(userId));
    }

}
