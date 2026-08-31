package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.FolderDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_folder")
@EqualsAndHashCode(callSuper = false)
public final class Folder {
    @TableId(type = IdType.ASSIGN_ID)
    private String folderId;
    private String parentId;
    private String folderPath;
    private String folderName;
    private Integer orderNum;
    /** 1=内置目录（如未分类），不可删除。 */
    private Integer isBuiltin;
    /** 审计字段由 MyBatis-Plus 自动填充创建人。 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 审计字段由 MyBatis-Plus 自动填充更新人。 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer isDel;
    /** 删除时间，逻辑删时写入。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;

    public Folder(FolderDto.FolderInsert folderInsert) {
        this.parentId = folderInsert.parentId();
        this.folderName = folderInsert.folderName();
        this.orderNum = folderInsert.orderNum();
        this.isBuiltin = 0;
    }

    public Folder(FolderDto.FolderUpdate folderUpdate) {
        this.folderId = folderUpdate.folderId();
        this.parentId = folderUpdate.parentId();
        this.folderName = folderUpdate.folderName();
        this.orderNum = folderUpdate.orderNum();
    }
}
