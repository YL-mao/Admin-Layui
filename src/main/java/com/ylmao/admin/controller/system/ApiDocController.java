package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ylmao.admin.config.log.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 接口文档菜单入口：内嵌 springdoc Swagger UI；生产关闭 springdoc 时给出提示。
 */
@Controller
@RequestMapping("/apidoc")
public class ApiDocController {

    private static final String VIEW = "system/apidoc";

    @Value("${springdoc.api-docs.enabled:false}")
    private boolean apiDocsEnabled;

    @Log(title = "接口文档页面", businessType = "QUERY")
    @SaCheckPermission("system:apidoc:view")
    @GetMapping("/listView")
    public String apiDocListView(ModelMap model) {
        model.put("apiDocsEnabled", apiDocsEnabled);
        return VIEW;
    }
}
