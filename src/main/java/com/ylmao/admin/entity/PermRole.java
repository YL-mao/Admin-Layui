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

@Data
@NoArgsConstructor
@TableName("sys_perm_role")
@EqualsAndHashCode(callSuper = false)
public class PermRole {

    @TableId(type = IdType.ASSIGN_ID)
    private String permRoleId;
    private String roleId;
    private String permId;

    /** 审计字段由 MyBatis-Plus 自动填充创建人。 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 审计字段由 MyBatis-Plus 自动填充创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
