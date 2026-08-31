package com.ylmao.admin.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ylmao.admin.common.FileNameSafeUtils;
import com.ylmao.admin.common.UploadConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.FileResourceDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.FileResource;
import com.ylmao.admin.entity.Folder;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.FileResourceMapper;
import com.ylmao.admin.mapper.FolderMapper;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.vo.FileResourceVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileResourceService {

    private static final Logger log = LoggerFactory.getLogger(FileResourceService.class);
    private static final String DEFAULT_AVATAR_CLASSPATH = "static/admin/images/avatar.jpg";
    private static final DateTimeFormatter YEAR_MONTH_DAY = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final FileResourceMapper fileResourceMapper;
    private final FolderMapper folderMapper;
    private final UserMapper userMapper;
    private final UploadConfigService uploadConfigService;
    private final FolderService folderService;

    public IPage<FileResourceVo.FileListVo> selectPage(PageQuery pageQuery, FileResourceDto.FileList fileList) {
        LambdaQueryWrapper<FileResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileResource::getIsDel, 0);
        if (fileList != null) {
            if (StrUtil.isNotBlank(fileList.folderId())) {
                wrapper.eq(FileResource::getFolderId, fileList.folderId());
            }
            if (StrUtil.isNotBlank(fileList.originalName())) {
                wrapper.like(FileResource::getOriginalName, fileList.originalName());
            }
        }
        wrapper.orderByDesc(FileResource::getCreateTime).orderByDesc(FileResource::getFileId);
        return fileResourceMapper.selectPage(pageQuery.toMpPage(), wrapper)
                .convert(file -> FileResourceVo.FileListVo.from(file, uploadConfigService.buildAccessUrl(file.getFileId())));
    }

    public FileResourceVo.UploadRulesVo uploadRules() {
        return uploadConfigService.uploadRules();
    }

    /**
     * 上传文件；folderId 为空时挂到「未分类」。
     * 先落盘再入库：入库失败则删除刚写入的磁盘文件（不把磁盘 IO 包进 DB 事务）。
     */
    public FileResourceVo.FileListVo upload(MultipartFile file, FileResourceDto.FileUpload fileUpload) {
        uploadConfigService.assertUploadEnabled();
        String storageType = uploadConfigService.currentStorageType();
        if (fileUpload == null) {
            throw new BusinessException("参数不合法");
        }
        String scene = uploadConfigService.normalizeScene(fileUpload.fileScene());
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        long maxBytes = uploadConfigService.maxBytes();
        if (file.getSize() > maxBytes) {
            throw new BusinessException("文件大小超出限制（最大 " + (maxBytes / 1024 / 1024) + "MB）");
        }
        // 展示名规范化（去路径段/控制字符）；磁盘仍用 storage_key。
        String originalName = FileNameSafeUtils.normalizeOriginalName(file.getOriginalFilename());
        String suffix = extractSuffix(originalName);
        Set<String> allowed = uploadConfigService.extensionsForScene(scene);
        if (!allowed.contains(suffix)) {
            throw new BusinessException("文件类型不被允许，仅支持：" + String.join("、", allowed));
        }
        String targetFolderId = resolveUploadFolderId(fileUpload.folderId());
        folderService.requireActiveFolder(targetFolderId);

        // 头像等图片场景默认需登录；未传时 document/excel 也默认需登录更安全。
        int loginFlag = fileUpload.needLogin() == null ? 1 : fileUpload.needLogin();
        if (loginFlag != 0 && loginFlag != 1) {
            throw new BusinessException("参数不合法");
        }

        FileResource existing = findActiveByFolderAndName(targetFolderId, originalName);
        if (existing != null) {
            if (!Boolean.TRUE.equals(fileUpload.forceOverwrite())) {
                throw new BusinessException("同目录下文件名已存在");
            }
            overwriteContent(existing, file, scene, suffix);
            existing.setNeedLogin(loginFlag);
            existing.setFileScene(scene);
            existing.setStorageType(storageType);
            fileResourceMapper.updateById(existing);
            return FileResourceVo.FileListVo.from(existing, uploadConfigService.buildAccessUrl(existing.getFileId()));
        }

        String fileId = IdWorker.getIdStr();
        String storageKey = buildStorageKey(fileId, suffix);
        Path diskPath = resolveDiskPath(storageKey);
        try {
            Files.createDirectories(diskPath.getParent());
            file.transferTo(diskPath);
        } catch (IOException e) {
            log.error("写入本地文件失败 storageKey={}", storageKey, e);
            throw new BusinessException("文件保存失败");
        }

        FileResource entity = new FileResource();
        entity.setFileId(fileId);
        entity.setFolderId(targetFolderId);
        entity.setOriginalName(originalName);
        entity.setStorageKey(storageKey);
        entity.setStorageType(storageType);
        entity.setFileSuffix(suffix);
        entity.setContentType(file.getContentType());
        entity.setFileSize(file.getSize());
        entity.setFileScene(scene);
        entity.setNeedLogin(loginFlag);
        try {
            int rows = fileResourceMapper.insert(entity);
            if (rows <= 0) {
                throw new BusinessException("文件保存失败");
            }
        } catch (RuntimeException e) {
            // 入库失败时回滚刚写入的磁盘文件，避免孤儿文件。
            deleteQuietly(diskPath);
            throw e;
        }
        return FileResourceVo.FileListVo.from(entity, uploadConfigService.buildAccessUrl(fileId));
    }

    /** 覆盖重传：保持同一 fileId，校验场景后缀。 */
    public FileResourceVo.FileListVo overwrite(String fileId, MultipartFile file) {
        uploadConfigService.assertUploadEnabled();
        uploadConfigService.requireLocalStorage();
        FileResource existing = fileResourceMapper.selectById(fileId);
        if (existing == null || !Integer.valueOf(0).equals(existing.getIsDel())) {
            throw new BusinessException("文件不存在");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        if (file.getSize() > uploadConfigService.maxBytes()) {
            long maxMb = uploadConfigService.maxBytes() / 1024 / 1024;
            throw new BusinessException("文件大小超出限制（最大 " + maxMb + "MB）");
        }
        String originalName = StrUtil.isBlank(file.getOriginalFilename())
                ? existing.getOriginalName()
                : file.getOriginalFilename();
        originalName = FileNameSafeUtils.normalizeOriginalName(originalName);
        String suffix = extractSuffix(originalName);
        Set<String> allowed = uploadConfigService.extensionsForScene(existing.getFileScene());
        if (!allowed.contains(suffix)) {
            throw new BusinessException("文件类型不被允许，仅支持：" + String.join("、", allowed));
        }
        overwriteContent(existing, file, existing.getFileScene(), suffix);
        // 覆盖后原始名可随新文件变化，但仍需同目录唯一。
        if (!Objects.equals(existing.getOriginalName(), originalName)) {
            FileResource conflict = findActiveByFolderAndName(existing.getFolderId(), originalName);
            if (conflict != null && !conflict.getFileId().equals(existing.getFileId())) {
                throw new BusinessException("同目录下文件名已存在");
            }
            existing.setOriginalName(originalName);
        }
        fileResourceMapper.updateById(existing);
        return FileResourceVo.FileListVo.from(existing, uploadConfigService.buildAccessUrl(existing.getFileId()));
    }

    /** 修改文件名、需登录；folderId 变化即移动到目标虚拟目录（物理路径不变）。 */
    @Transactional
    public void updateMetadata(FileResourceDto.FileUpdate fileUpdate) {
        FileResource existing = fileResourceMapper.selectById(fileUpdate.fileId());
        if (existing == null || !Integer.valueOf(0).equals(existing.getIsDel())) {
            throw new BusinessException("文件不存在");
        }
        String targetFolderId = resolveUploadFolderId(fileUpdate.folderId());
        folderService.requireActiveFolder(targetFolderId);
        String originalName = FileNameSafeUtils.normalizeOriginalName(fileUpdate.originalName());
        FileResource sameName = findActiveByFolderAndName(targetFolderId, originalName);
        if (sameName != null && !sameName.getFileId().equals(fileUpdate.fileId())) {
            throw new BusinessException("同目录下文件名已存在");
        }
        existing.setFolderId(targetFolderId);
        existing.setOriginalName(originalName);
        existing.setNeedLogin(fileUpdate.needLogin());
        int rows = fileResourceMapper.updateById(existing);
        if (rows <= 0) {
            throw new BusinessException("文件不存在或修改失败");
        }
    }

    public FileResource checkOriginalNameUnique(String folderId, String originalName) {
        // 与上传一致：空/根目录落到未分类后再查重。
        return findActiveByFolderAndName(resolveUploadFolderId(folderId),
                FileNameSafeUtils.normalizeOriginalName(originalName));
    }

    public FileResourceVo.CheckRefResult checkRef(String ids) {
        List<String> idList = splitIds(ids);
        for (String fileId : idList) {
            if (isReferencedByAvatar(fileId)) {
                return new FileResourceVo.CheckRefResult(true, "文件仍被用户头像引用，确认后将删除磁盘文件且不可恢复");
            }
        }
        return new FileResourceVo.CheckRefResult(false, "未被引用");
    }

    public boolean isReferencedByAvatar(String fileId) {
        if (StrUtil.isBlank(fileId)) {
            return false;
        }
        // 仅检测 user_avatar 是否包含该 fileId（相对地址或纯 ID 均可命中）。
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .like(User::getUserAvatar, fileId));
        return count != null && count > 0;
    }

    @Transactional
    public void softDeleteFiles(String ids) {
        List<String> idList = splitIds(ids);
        if (idList.isEmpty()) {
            throw new BusinessException("请选择要删除的文件");
        }
        List<FileResource> files = new ArrayList<>();
        for (String fileId : idList) {
            FileResource file = fileResourceMapper.selectById(fileId);
            if (file == null || !Integer.valueOf(0).equals(file.getIsDel())) {
                throw new BusinessException("文件不存在");
            }
            files.add(file);
        }
        // 磁盘立即物理删除；库表逻辑删除保留痕迹。
        for (FileResource file : files) {
            deleteQuietly(resolveDiskPath(file.getStorageKey()));
        }
        int rows = fileResourceMapper.softDeleteByIds(idList, LocalDateTime.now());
        if (rows <= 0) {
            throw new BusinessException("文件不存在或删除失败");
        }
    }

    /** 级联逻辑删目录（含子孙目录与其下文件），并立即删除对应磁盘文件；内置「未分类」禁止删除。 */
    @Transactional
    public void softDeleteFolders(String ids) {
        List<String> idList = splitIds(ids);
        if (idList.isEmpty()) {
            throw new BusinessException("请选择要删除的目录");
        }
        Set<String> folderIdsToDelete = new LinkedHashSet<>();
        for (String folderId : idList) {
            Folder folder = folderMapper.selectById(folderId);
            if (folder == null || !Integer.valueOf(0).equals(folder.getIsDel())) {
                throw new BusinessException("目录不存在");
            }
            folderService.assertNotBuiltin(folder);
            folderIdsToDelete.add(folder.getFolderId());
            String pathPrefix = buildDescendantPathPrefix(folder);
            List<Folder> descendants = folderMapper.selectDescendantFoldersByPath(pathPrefix);
            for (Folder child : descendants) {
                if (child.getIsDel() == null || child.getIsDel() == 0) {
                    folderIdsToDelete.add(child.getFolderId());
                }
            }
        }
        List<FileResource> files = fileResourceMapper.selectList(new LambdaQueryWrapper<FileResource>()
                .eq(FileResource::getIsDel, 0)
                .in(FileResource::getFolderId, folderIdsToDelete));
        for (FileResource file : files) {
            deleteQuietly(resolveDiskPath(file.getStorageKey()));
        }
        LocalDateTime now = LocalDateTime.now();
        if (!files.isEmpty()) {
            fileResourceMapper.softDeleteByIds(files.stream().map(FileResource::getFileId).toList(), now);
        }
        int rows = folderMapper.softDeleteByIds(folderIdsToDelete, now);
        if (rows <= 0) {
            throw new BusinessException("目录不存在或删除失败");
        }
    }

    /**
     * 预览：needLogin=1 且未登录 → 401；
     * 图片软删或磁盘缺失 → 默认头像字节；文档类 → 404。
     */
    public ResponseEntity<Resource> preview(String fileId) {
        if (StrUtil.isBlank(fileId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        FileResource file = fileResourceMapper.selectByIdIncludeDeleted(fileId);
        if (file == null) {
            return defaultAvatarResponse();
        }
        // /upload/** 已从全局登录拦截排除，此处按单文件 needLogin 校验。
        if (Integer.valueOf(1).equals(file.getNeedLogin())) {
            StpUtil.checkLogin();
        }
        boolean deleted = file.getIsDel() != null && file.getIsDel() == 1;
        Path diskPath = resolveDiskPath(file.getStorageKey());
        boolean missingDisk = StrUtil.isBlank(file.getStorageKey()) || !Files.isRegularFile(diskPath);
        boolean imageScene = isImageSceneSafe(file.getFileScene());
        if (deleted || missingDisk) {
            if (imageScene) {
                return defaultAvatarResponse();
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        org.springframework.core.io.FileSystemResource resource =
                new org.springframework.core.io.FileSystemResource(diskPath);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (StrUtil.isNotBlank(file.getContentType())) {
            try {
                mediaType = MediaType.parseMediaType(file.getContentType());
            } catch (Exception ignored) {
                // 非法 MIME 时退回二进制流。
            }
        }
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    private void overwriteContent(FileResource existing, MultipartFile file, String scene, String suffix) {
        Path oldPath = resolveDiskPath(existing.getStorageKey());
        // 覆盖时仍落在原 storage_key；后缀变化则换新键并删旧文件。
        String storageKey = existing.getStorageKey();
        if (StrUtil.isBlank(storageKey) || !suffix.equalsIgnoreCase(existing.getFileSuffix())) {
            storageKey = buildStorageKey(existing.getFileId(), suffix);
        }
        Path newPath = resolveDiskPath(storageKey);
        try {
            Files.createDirectories(newPath.getParent());
            file.transferTo(newPath);
            if (!Objects.equals(oldPath, newPath)) {
                deleteQuietly(oldPath);
            }
        } catch (IOException e) {
            log.error("覆盖写入本地文件失败 fileId={}", existing.getFileId(), e);
            throw new BusinessException("文件保存失败");
        }
        existing.setStorageKey(storageKey);
        existing.setFileSuffix(suffix);
        existing.setContentType(file.getContentType());
        existing.setFileSize(file.getSize());
        existing.setFileScene(scene);
        existing.setStorageType(uploadConfigService.currentStorageType());
    }

    private FileResource findActiveByFolderAndName(String folderId, String originalName) {
        if (StrUtil.isBlank(folderId) || StrUtil.isBlank(originalName)) {
            return null;
        }
        return fileResourceMapper.selectOne(new LambdaQueryWrapper<FileResource>()
                .eq(FileResource::getIsDel, 0)
                .eq(FileResource::getFolderId, folderId)
                .eq(FileResource::getOriginalName, originalName)
                .last("limit 1"));
    }

    private String resolveUploadFolderId(String folderId) {
        // 未指定或选中虚拟根时，落到内置「未分类」。
        if (StrUtil.isBlank(folderId) || "0".equals(folderId.trim())) {
            return UploadConfigCodes.UNCLASSIFIED_FOLDER_ID;
        }
        return folderId.trim();
    }

    private String buildStorageKey(String fileId, String suffix) {
        // 按日分目录；物理文件名用雪花 fileId，与主键一致且并发唯一。
        String ymd = LocalDate.now().format(YEAR_MONTH_DAY);
        return ymd + "/" + fileId + "." + suffix;
    }

    private Path resolveDiskPath(String storageKey) {
        if (StrUtil.isBlank(storageKey)) {
            return uploadConfigService.resolveLocalRoot().resolve("__missing__");
        }
        // 防止 storage_key 越出本地根目录。
        Path root = uploadConfigService.resolveLocalRoot().toAbsolutePath().normalize();
        Path target = root.resolve(storageKey).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException("参数不合法");
        }
        return target;
    }

    private String buildDescendantPathPrefix(Folder folder) {
        String path = StrUtil.blankToDefault(folder.getFolderPath(), "0");
        return path + "," + folder.getFolderId();
    }

    private String extractSuffix(String originalName) {
        int idx = originalName.lastIndexOf('.');
        if (idx < 0 || idx == originalName.length() - 1) {
            throw new BusinessException("文件类型不被允许");
        }
        return originalName.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private List<String> splitIds(String ids) {
        if (StrUtil.isBlank(ids)) {
            return List.of();
        }
        return StrUtil.splitTrim(ids, ',').stream().filter(StrUtil::isNotBlank).distinct().toList();
    }

    private boolean isImageSceneSafe(String fileScene) {
        if (StrUtil.isBlank(fileScene)) {
            return true;
        }
        return "image".equalsIgnoreCase(fileScene.trim());
    }

    private ResponseEntity<Resource> defaultAvatarResponse() {
        ClassPathResource resource = new ClassPathResource(DEFAULT_AVATAR_CLASSPATH);
        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
    }

    private void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("删除磁盘文件失败 path={}", path, e);
        }
    }
}
