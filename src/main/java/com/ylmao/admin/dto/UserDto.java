package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    public record UserList(
            @Size(max = 64, message = "登录账号参数不合法") String userAccount,
            @Min(value = 0, message = "启用状态参数不合法") @Max(value = 1, message = "启用状态参数不合法")
            Integer isEnabled,
            @Min(value = 0, message = "锁定状态参数不合法") @Max(value = 1, message = "锁定状态参数不合法")
            Integer isLock
    ) { }

    public record UserInsert(
            @NotBlank(message = "登录账号不能为空") String userAccount,
            @NotBlank(message = "用户姓名不能为空") String userName,
            String userSex,
            String userEmail,
            String userPhone,
            String deptId,
            String postId,
            String roleIds
    ) { }

    public record UserUpdate(
            @NotBlank(message = "用户ID不能为空") String userId,
            @NotBlank(message = "登录账号不能为空") String userAccount,
            @NotBlank(message = "用户姓名不能为空") String userName,
            String userSex,
            String userEmail,
            String userPhone,
            String deptId,
            String postId,
            String roleIds
    ) { }

    public record UpdatePwd(
            @NotBlank(message = "用户ID不能为空") String userId,
            @NotBlank(message = "用户密码不能为空") String userPassword
    ) { }

    public record UpdateEnabled(
            @NotBlank(message = "用户ID不能为空") String userId,
            @NotNull(message = "用户状态参数不合法") @Min(value = 0, message = "用户状态参数不合法") @Max(value = 1, message = "用户状态参数不合法")
            Integer isEnabled
    ) { }

    public record Unlock(
            @NotBlank(message = "用户ID不能为空") String userId
    ) { }

    public record KickSessions(
            @NotBlank(message = "用户ID不能为空") String userId
    ) { }
}
