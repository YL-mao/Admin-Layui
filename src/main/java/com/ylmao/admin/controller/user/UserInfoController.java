package com.ylmao.admin.controller.user;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.UserInfoDto;
import com.ylmao.admin.service.UserInfoService;
import com.ylmao.admin.vo.UserInfoVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user/info")
@RequiredArgsConstructor
public class UserInfoController extends BaseController {

    private static final String USER_INFO_VIEW = "user/info";

    private final UserInfoService userInfoService;

    @Log(title = "个人资料页面", businessType = "QUERY")
    @SaCheckPermission("user:info:view")
    @GetMapping("")
    public String userInfo() {
        return USER_INFO_VIEW;
    }

    @Log(title = "个人资料详情", businessType = "QUERY")
    @SaCheckPermission("user:info:view")
    @GetMapping("/detail")
    @ResponseBody
    public R<?> userInfoDetail() {
        UserInfoVo.ProfileDetailVo profileDetail = userInfoService.getCurrentProfileDetail();
        return R.ok(profileDetail);
    }

    @Log(title = "个人最近登录", businessType = "QUERY")
    @SaCheckPermission("user:info:view")
    @GetMapping("/loginLog")
    @ResponseBody
    public R<?> userInfoLoginLog() {
        List<UserInfoVo.LoginLogVo> loginLogs = userInfoService.getCurrentLoginLogs();
        return R.ok(loginLogs);
    }

    @Log(title = "保存个人资料", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("user:info:view")
    @PutMapping("/update")
    @ResponseBody
    public R<?> userInfoUpdate(@Valid @RequestBody UserInfoDto.ProfileSave profileSave) {
        // 请求体不含 userId，Service 仅更新当前登录用户允许自助修改的字段。
        userInfoService.updateCurrentProfile(profileSave);
        return success();
    }

    @Log(title = "修改个人密码", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("user:info:view")
    @PatchMapping("/updatePwd")
    @ResponseBody
    public R<?> updateUserInfoPwd(@Valid @RequestBody UserInfoDto.UpdatePwd updatePwd) {
        // 请求体不含 userId，Service 仅修改当前登录用户密码。
        userInfoService.updateCurrentPassword(updatePwd);
        return success("密码修改成功，请重新登录");
    }

    @Log(title = "修改个人头像", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("user:info:view")
    @PatchMapping("/updateAvatar")
    @ResponseBody
    public R<?> updateUserInfoAvatar(@Valid @RequestBody UserInfoDto.UpdateAvatar updateAvatar) {
        userInfoService.updateCurrentAvatar(updateAvatar);
        return success();
    }
}
