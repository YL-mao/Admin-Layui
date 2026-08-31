package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.GenDto;
import com.ylmao.admin.service.gen.GenCodeService;
import com.ylmao.admin.service.gen.GenMetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/gen")
@RequiredArgsConstructor
public class GenController extends BaseController {

    private static final String GEN_VIEW = "system/gen";

    private final GenMetaService genMetaService;
    private final GenCodeService genCodeService;

    @Log(title = "代码生成页面", businessType = "QUERY")
    @SaCheckPermission("system:gen:view")
    @GetMapping("/listView")
    public String genListView() {
        return GEN_VIEW;
    }

    @Log(title = "代码生成表清单", businessType = "QUERY")
    @SaCheckPermission("system:gen:view")
    @GetMapping("/tables")
    @ResponseBody
    public R<?> genTables() {
        return R.ok(genMetaService.listTables());
    }

    @Log(title = "代码生成字段预览", businessType = "QUERY")
    @SaCheckPermission("system:gen:view")
    @GetMapping("/columns")
    @ResponseBody
    public R<?> genColumns(String tableName) {
        return R.ok(genMetaService.loadColumnPreview(tableName));
    }

    @Log(title = "代码生成上级菜单", businessType = "QUERY")
    @SaCheckPermission("system:gen:view")
    @GetMapping("/parentMenus")
    @ResponseBody
    public R<?> genParentMenus() {
        return R.ok(genMetaService.listParentMenuOptions());
    }

    @Log(title = "代码生成下载", businessType = "OTHER")
    @SaCheckPermission("system:gen:download")
    @PostMapping("/download")
    public ResponseEntity<byte[]> genDownload(@Valid @RequestBody GenDto.Generate generate) {
        byte[] zipBytes = genCodeService.generateZip(generate);
        String filename = "codegen-" + generate.moduleName() + ".zip";
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(zipBytes);
    }
}
