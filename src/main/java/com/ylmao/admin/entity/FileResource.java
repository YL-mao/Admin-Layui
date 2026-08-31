package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 文件资源 PO，表名 sys_file（类名不用 File，避免与 java.io.File 冲突）。 */
@Data
@NoArgsConstructor
@TableName("sys_file")
@EqualsAndHashCode(callSuper = false)
public final class FileResource {
    @TableId(type = IdType.ASSIGN_ID)
    private String fileId;
    private String folderId;
    private String originalName;
    /** 相对 localPath 的物理存储键，如 2026/07/19/{fileId}.png。 */
    private String storageKey;
    private String storageType;
    private String fileSuffix;
    private String contentType;
    private Long fileSize;
    /** 上传场景：image / document / excel。 */
    private String fileScene;
    /** 1=预览需登录，0=可匿名预览。 */
    private Integer needLogin;
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
}
