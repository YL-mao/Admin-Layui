package com.ylmao.admin.controller;

import com.ylmao.admin.config.log.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 真实错误页路由（非 Pear 示例）。GlobalExceptionResolver / Sa-Token 会转发到这里。
 */
@Controller
@RequestMapping("/error")
public class ErrorViewController {

    @Log(title = "403页跳转")
    @GetMapping("/403")
    public String error403() {
        return "error/403";
    }

    @Log(title = "404页跳转")
    @GetMapping("/404")
    public String error404() {
        return "error/404";
    }

    @Log(title = "500页跳转")
    @GetMapping("/500")
    public String error500() {
        return "error/500";
    }
}
