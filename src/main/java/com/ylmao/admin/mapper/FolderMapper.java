package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.Folder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 目录 Mapper。不用 @TableLogic，删除靠显式 is_del 条件与 XML。
 */
@Mapper
public interface FolderMapper extends BaseMapper<Folder> {

    /** 按物化路径前缀查子孙目录（不含自身），pathPrefix 形如 0,{folderId}。 */
    List<Folder> selectDescendantFoldersByPath(@Param("pathPrefix") String pathPrefix);

    int softDeleteByIds(@Param("ids") Collection<String> ids, @Param("deleteTime") LocalDateTime deleteTime);
}
