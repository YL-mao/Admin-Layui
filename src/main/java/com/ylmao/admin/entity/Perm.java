package com.ylmao.admin.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.PermDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@TableName("sys_perm")
@EqualsAndHashCode(callSuper = false)
public final class Perm {
    @TableId(type = IdType.ASSIGN_ID)
    private String permId;
    private String parentId;
    private String permPath;
    private String permName;
    private String permDesc;
    private String permUrl;
    /** 空标识存 NULL，配合库唯一索引允许多个目录无码。 */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String permCode;
    private Integer permType;
    private String permIcon;
    private Integer orderNum;
    private Integer isBlank;
    private Integer isEnabled;
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
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDel;

    /** 内存组装子节点用，非数据库字段。 */
    @TableField(exist = false)
    private List<Perm> childPerm;
    /** 列表展示子节点数量，非数据库字段。 */
    @TableField(exist = false)
    private int childCount;
    /** 角色授权树勾选回显，非数据库字段。 */
    @TableField(exist = false)
    private String checkArr = "0";

    public Perm(PermDto.PermInsert permInsert) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.parentId = permInsert.parentId();
        this.permName = permInsert.permName();
        this.permDesc = permInsert.permDesc();
        this.permUrl = permInsert.permUrl();
        this.isBlank = permInsert.isBlank();
        this.permCode = normalizePermCode(permInsert.permCode());
        this.permType = permInsert.permType();
        this.permIcon = permInsert.permIcon();
        this.orderNum = permInsert.orderNum();
        this.isEnabled = permInsert.isEnabled();
    }

    public Perm(PermDto.PermUpdate permUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.permId = permUpdate.permId();
        this.parentId = permUpdate.parentId();
        this.permName = permUpdate.permName();
        this.permDesc = permUpdate.permDesc();
        this.permUrl = permUpdate.permUrl();
        this.isBlank = permUpdate.isBlank();
        this.permCode = normalizePermCode(permUpdate.permCode());
        this.permType = permUpdate.permType();
        this.permIcon = permUpdate.permIcon();
        this.orderNum = permUpdate.orderNum();
        this.isEnabled = permUpdate.isEnabled();
    }

    /** 空白权限标识统一为 NULL，避免空串撞唯一索引。 */
    private static String normalizePermCode(String permCode) {
        return StrUtil.isBlank(permCode) ? null : permCode.trim();
    }
}
