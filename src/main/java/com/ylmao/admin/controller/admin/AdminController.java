package com.ylmao.admin.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.model.Menu;
import com.ylmao.admin.service.PermService;
import com.ylmao.admin.service.SystemDisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController extends BaseController {

    private final PermService permService;
    private final SystemDisplayService systemDisplayService;

    @Log(title = "后台首页跳转")
    @GetMapping({"", "/index"})
    public String index(ModelMap model) {
        // 框架页 Logo、标题、页脚读取 system.*，不做硬编码兜底。
        systemDisplayService.fillModel(model);
        return "index";
    }

    @GetMapping("/permMenu")
    @ResponseBody
    public R<List<Menu>> getUserPermMenu() {
        return okData(permService.getUserPermMenu(SaTokenUtil.getUserId()));
    }

    @Log(title = "用户注销", loggingType = "LOGIN", businessType = "LOGOUT")
    @PostMapping("/loginOut")
    @ResponseBody
    public R<?> loginOut() {
        StpUtil.logout();
        return success("注销成功");
    }
}
