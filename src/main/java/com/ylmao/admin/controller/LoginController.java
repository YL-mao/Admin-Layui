package com.ylmao.admin.controller;
import cn.hutool.core.util.ObjectUtil;

import cn.dev33.satoken.stp.StpUtil;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.dto.LoginDto;
import com.ylmao.admin.service.LoginService;
import com.ylmao.admin.service.SystemDisplayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class LoginController extends BaseController {

    private final LoginService loginService;
    private final SystemDisplayService systemDisplayService;

    /** 根路径统一转到登录入口，登录态判断由 loginView 处理。 */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @RequestMapping("login")
    public String loginView(HttpServletResponse response, ModelMap model) {
        // 已登录访问登录页时直接进后台，无需前端再调 session 接口。
        if (StpUtil.isLogin() && ObjectUtil.isNotNull(SaTokenUtil.getUser())) {
            return "redirect:/admin/index";
        }
        setLoginPageNoStore(response);
        // 登录页展示 system.*，缺失或停用时页面对应位置为空。
        systemDisplayService.fillModel(model);
        return "login";
    }

    /** 登录页禁止缓存，避免浏览器后退展示旧页面。 */
    private static void setLoginPageNoStore(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    @Log(title = "用户登录", loggingType = "LOGIN", businessType = "LOGIN")
    @PostMapping("login")
    @ResponseBody
    public R<?> login(
            @Valid @RequestBody LoginDto.LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        loginService.login(loginRequest, request, response);
        return R.ok("登录成功");
    }

}
