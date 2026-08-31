package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.common.UploadConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.FolderDto;
import com.ylmao.admin.entity.Folder;
import com.ylmao.admin.mapper.FolderMapper;
import com.ylmao.admin.vo.FolderVo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FolderService {

    private final FolderMapper folderMapper;
    private final FileResourceService fileResourceService;

    public FolderService(FolderMapper folderMapper, @Lazy FileResourceService fileResourceService) {
        this.folderMapper = folderMapper;
        this.fileResourceService = fileResourceService;
    }

    public List<FolderVo.FolderOptionVo> listOptions() {
        LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Folder::getIsDel, 0);
        wrapper.orderByAsc(Folder::getOrderNum).orderByAsc(Folder::getFolderId);
        return folderMapper.selectList(wrapper).stream().map(FolderVo.FolderOptionVo::from).toList();
    }

    @Transactional
    public void insert(FolderDto.FolderInsert folderInsert) {
        if (checkFolderNameUnique(folderInsert.parentId(), folderInsert.folderName()) != null) {
            throw new BusinessException("同级目录名称已存在");
        }
        Folder folder = new Folder(folderInsert);
        fillFolderPath(folder);
        int rows = folderMapper.insert(folder);
        if (rows <= 0) {
            throw new BusinessException("新增目录失败");
        }
    }

    @Transactional
    public void updateById(FolderDto.FolderUpdate folderUpdate) {
        Folder oldFolder = folderMapper.selectById(folderUpdate.folderId());
        if (oldFolder == null || !Integer.valueOf(0).equals(oldFolder.getIsDel())) {
            throw new BusinessException("目录不存在");
        }
        // 内置目录（如未分类）禁止改名或调整上级。
        assertNotBuiltin(oldFolder);
        if (folderUpdate.folderId().equals(folderUpdate.parentId())) {
            throw new BusinessException("上级目录不能选择自身");
        }
        if (isChildFolder(folderUpdate.folderId(), folderUpdate.parentId())) {
            throw new BusinessException("上级目录不能选择当前目录的下级");
        }
        Folder sameName = checkFolderNameUnique(folderUpdate.parentId(), folderUpdate.folderName());
        if (sameName != null && !sameName.getFolderId().equals(folderUpdate.folderId())) {
            throw new BusinessException("同级目录名称已存在");
        }
        Folder folder = new Folder(folderUpdate);
        // 内置标记不可被更新 DTO 抹掉。
        folder.setIsBuiltin(oldFolder.getIsBuiltin());
        fillFolderPath(folder);
        int rows = folderMapper.updateById(folder);
        if (rows <= 0) {
            throw new BusinessException("目录不存在或修改失败");
        }
    }

    /** 级联软删目录及其下文件，由 FileResourceService 统一处理。 */
    @Transactional
    public void deleteByIds(String ids) {
        fileResourceService.softDeleteFolders(ids);
    }

    public Folder checkFolderNameUnique(String parentId, String folderName) {
        LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Folder::getIsDel, 0);
        wrapper.eq(Folder::getParentId, normalizeParentId(parentId));
        wrapper.eq(Folder::getFolderName, folderName);
        return folderMapper.selectOne(wrapper);
    }

    public Folder requireActiveFolder(String folderId) {
        if (StrUtil.isBlank(folderId)) {
            throw new BusinessException("所属目录不能为空");
        }
        Folder folder = folderMapper.selectById(folderId);
        if (folder == null || !Integer.valueOf(0).equals(folder.getIsDel())) {
            throw new BusinessException("目录不存在");
        }
        return folder;
    }

    public void assertNotBuiltin(Folder folder) {
        if (folder != null && (Integer.valueOf(1).equals(folder.getIsBuiltin())
                || UploadConfigCodes.UNCLASSIFIED_FOLDER_ID.equals(folder.getFolderId()))) {
            throw new BusinessException("内置目录不能删除");
        }
    }

    private void fillFolderPath(Folder folder) {
        String parentId = normalizeParentId(folder.getParentId());
        folder.setParentId(parentId);
        if ("0".equals(parentId)) {
            folder.setFolderPath("0");
            return;
        }
        Folder parent = folderMapper.selectById(parentId);
        if (parent == null || !Integer.valueOf(0).equals(parent.getIsDel())) {
            throw new BusinessException("上级目录不存在");
        }
        folder.setFolderPath(StrUtil.isNotBlank(parent.getFolderPath())
                ? parent.getFolderPath() + "," + parent.getFolderId()
                : parent.getFolderId());
    }

    private boolean isChildFolder(String folderId, String parentId) {
        if (StrUtil.isBlank(parentId) || "0".equals(parentId)) {
            return false;
        }
        Folder parent = folderMapper.selectById(parentId);
        return parent != null && StrUtil.isNotBlank(parent.getFolderPath())
                && ("," + parent.getFolderPath() + ",").contains("," + folderId + ",");
    }

    private String normalizeParentId(String parentId) {
        return StrUtil.isBlank(parentId) ? "0" : parentId;
    }
}
