package com.ylmao.admin.controller;

import com.ylmao.admin.service.FileResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文件预览流接口；路径由 upload.pubUrlPfx 配置（默认 /upload/{fileId}）。
 * 不挂权限注解；是否登录由单文件 needLogin 在 Service 内判断。
 */
@Controller
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadPreviewController {

    private final FileResourceService fileResourceService;

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> preview(@PathVariable String fileId) {
        return fileResourceService.preview(fileId);
    }
}
