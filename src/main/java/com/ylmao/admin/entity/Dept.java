package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.DeptDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_dept")
@EqualsAndHashCode(callSuper = false)
public final class Dept {
    @TableId(type = IdType.ASSIGN_ID)
    private String deptId;
    private String parentId;
    private String deptPath;
    private String deptName;
    private Integer orderNum;
    private String deptLeader;
    private String leaderPhone;
    private String leaderEmail;
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

    public Dept(DeptDto.DeptInsert deptInsert) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.parentId = deptInsert.parentId();
        this.deptName = deptInsert.deptName();
        this.orderNum = deptInsert.orderNum();
        this.deptLeader = deptInsert.deptLeader();
        this.leaderPhone = deptInsert.leaderPhone();
        this.leaderEmail = deptInsert.leaderEmail();
        this.isEnabled = deptInsert.isEnabled();
    }

    public Dept(DeptDto.DeptUpdate deptUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.deptId = deptUpdate.deptId();
        this.parentId = deptUpdate.parentId();
        this.deptName = deptUpdate.deptName();
        this.orderNum = deptUpdate.orderNum();
        this.deptLeader = deptUpdate.deptLeader();
        this.leaderPhone = deptUpdate.leaderPhone();
        this.leaderEmail = deptUpdate.leaderEmail();
        this.isEnabled = deptUpdate.isEnabled();
    }
}
