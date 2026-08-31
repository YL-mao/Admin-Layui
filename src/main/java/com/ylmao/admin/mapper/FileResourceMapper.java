package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.FileResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 文件 Mapper。不用 @TableLogic，删除靠显式 is_del 与 XML。
 */
@Mapper
public interface FileResourceMapper extends BaseMapper<FileResource> {

    FileResource selectByIdIncludeDeleted(@Param("fileId") String fileId);

    int softDeleteByIds(@Param("ids") Collection<String> ids, @Param("deleteTime") LocalDateTime deleteTime);
}
