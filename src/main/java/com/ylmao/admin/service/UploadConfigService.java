package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.UploadConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ylmao.admin.vo.FileResourceVo;

/** 读取 upload.* 运行时配置，供上传与预览共用。 */
@Service
@RequiredArgsConstructor
public class UploadConfigService {

    private static final String DEFAULT_PUBLIC_URL_PREFIX = "/upload";
    private static final String SCENE_IMAGE = "image";
    private static final String SCENE_DOCUMENT = "document";
    private static final String SCENE_EXCEL = "excel";

    private final ConfigRuntimeService configRuntimeService;

    /** 开关缺失、停用或 false 均禁止上传。 */
    public void assertUploadEnabled() {
        Optional<Boolean> enabled = configRuntimeService.getBoolean(UploadConfigCodes.ENABLED);
        if (enabled.isEmpty() || !Boolean.TRUE.equals(enabled.get())) {
            throw new BusinessException("上传功能已关闭");
        }
    }

    public void requireLocalStorage() {
        String storageType = configRuntimeService.getString(UploadConfigCodes.STORAGE_TYPE).orElse("");
        if (!"local".equalsIgnoreCase(storageType.trim())) {
            throw new BusinessException("暂不支持");
        }
    }

    public String currentStorageType() {
        requireLocalStorage();
        return "local";
    }

    /** 相对 user.dir 解析本地根目录，不存在则创建。 */
    public Path resolveLocalRoot() {
        String localPath = configRuntimeService.getString(UploadConfigCodes.LOCAL_PATH)
                .filter(StrUtil::isNotBlank)
                .orElse("upload");
        Path root = Paths.get(localPath);
        if (!root.isAbsolute()) {
            root = Paths.get(System.getProperty("user.dir")).resolve(root).normalize();
        }
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new BusinessException("本地存储目录创建失败");
        }
        return root;
    }

    public String publicUrlPrefix() {
        String prefix = configRuntimeService.getString(UploadConfigCodes.PUBLIC_URL_PREFIX)
                .filter(StrUtil::isNotBlank)
                .orElse(DEFAULT_PUBLIC_URL_PREFIX);
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        // 去掉末尾斜杠，便于拼接 fileId。
        while (prefix.endsWith("/") && prefix.length() > 1) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix;
    }

    public long maxBytes() {
        BigDecimal mb = configRuntimeService.getNumber(UploadConfigCodes.MAX_FILE_SIZE_MB)
                .orElseThrow(() -> new BusinessException("上传功能已关闭"));
        if (mb.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("上传功能已关闭");
        }
        return mb.multiply(BigDecimal.valueOf(1024L * 1024L)).longValue();
    }

    public Set<String> extensionsForScene(String fileScene) {
        String code = switch (normalizeScene(fileScene)) {
            case SCENE_IMAGE -> UploadConfigCodes.IMAGE_EXTENSIONS;
            case SCENE_DOCUMENT -> UploadConfigCodes.DOCUMENT_EXTENSIONS;
            case SCENE_EXCEL -> UploadConfigCodes.EXCEL_EXTENSIONS;
            default -> throw new BusinessException("参数不合法");
        };
        Optional<JsonNode> jsonOpt = configRuntimeService.getJson(code);
        if (jsonOpt.isEmpty() || !jsonOpt.get().isArray()) {
            throw new BusinessException("上传功能已关闭");
        }
        Set<String> extensions = new HashSet<>();
        for (JsonNode node : jsonOpt.get()) {
            if (node != null && node.isValueNode() && StrUtil.isNotBlank(node.asString())) {
                extensions.add(node.asString().trim().toLowerCase(Locale.ROOT).replace(".", ""));
            }
        }
        if (extensions.isEmpty()) {
            throw new BusinessException("上传功能已关闭");
        }
        return extensions;
    }

    public String normalizeScene(String fileScene) {
        if (StrUtil.isBlank(fileScene)) {
            throw new BusinessException("参数不合法");
        }
        String scene = fileScene.trim().toLowerCase(Locale.ROOT);
        if (!SCENE_IMAGE.equals(scene) && !SCENE_DOCUMENT.equals(scene) && !SCENE_EXCEL.equals(scene)) {
            throw new BusinessException("参数不合法");
        }
        return scene;
    }

    public String buildAccessUrl(String fileId) {
        if (StrUtil.isBlank(fileId)) {
            throw new BusinessException("参数不合法");
        }
        return publicUrlPrefix() + "/" + fileId.trim();
    }

    /** 供上传页限制选择器与提示文案。 */
    public FileResourceVo.UploadRulesVo uploadRules() {
        assertUploadEnabled();
        long maxMb = configRuntimeService.getNumber(UploadConfigCodes.MAX_FILE_SIZE_MB)
                .orElse(BigDecimal.TEN)
                .longValue();
        return new FileResourceVo.UploadRulesVo(
                maxMb,
                sortedExtensions(SCENE_IMAGE),
                sortedExtensions(SCENE_DOCUMENT),
                sortedExtensions(SCENE_EXCEL)
        );
    }

    private List<String> sortedExtensions(String scene) {
        return extensionsForScene(scene).stream().sorted().collect(Collectors.toCollection(ArrayList::new));
    }
}
