package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.UserDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_user")
@EqualsAndHashCode(callSuper = false)
public final class User {
    @TableId(type = IdType.ASSIGN_ID)
    private String userId;
    private String userAccount;

    private String userPassword;
    private String userName;
    private String userSex;

    private String userEmail;

    private String userPhone;

    private String deptId;

    private String postId;

    /** 头像访问地址，形如 /upload/{fileId}。 */
    private String userAvatar;

    private Integer isEnabled;

    /** 是否锁定（1锁定 0正常），与启停独立。 */
    private Integer isLock;

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


    public User(UserDto.UserInsert userInsert) {
        this.userAccount = userInsert.userAccount();
        this.userName = userInsert.userName();
        this.userSex = userInsert.userSex();
        this.userEmail = userInsert.userEmail();
        this.userPhone = userInsert.userPhone();
        this.deptId = userInsert.deptId();
        this.postId = userInsert.postId();
    }

    public User(UserDto.UserUpdate userUpdate) {
        this.userId = userUpdate.userId();
        this.userAccount = userUpdate.userAccount();
        this.userName = userUpdate.userName();
        this.userSex = userUpdate.userSex();
        this.userEmail = userUpdate.userEmail();
        this.userPhone = userUpdate.userPhone();
        this.deptId = userUpdate.deptId();
        this.postId = userUpdate.postId();
    }
}
