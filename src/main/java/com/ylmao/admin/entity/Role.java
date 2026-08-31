package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.RoleDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_role")
@EqualsAndHashCode(callSuper = false)
public final class Role {
    @TableId(type = IdType.ASSIGN_ID)
    private String roleId;
    private String roleName;
    private String roleCode;
    private Integer orderNum;
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

    /** 角色授权树回显用，非数据库字段。 */
    @TableField(exist = false)
    private Boolean isCheck;

    public Role(RoleDto.RoleInsert roleInsert) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.roleName = roleInsert.roleName();
        this.roleCode = roleInsert.roleCode();
        this.orderNum = roleInsert.orderNum();
        this.isEnabled = roleInsert.isEnabled();
    }

    public Role(RoleDto.RoleUpdate roleUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.roleId = roleUpdate.roleId();
        this.roleName = roleUpdate.roleName();
        this.roleCode = roleUpdate.roleCode();
        this.orderNum = roleUpdate.orderNum();
        this.isEnabled = roleUpdate.isEnabled();
    }
}
