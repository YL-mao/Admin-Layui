package com.ylmao.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserInfoDto {

    /** 当前用户可自助修改的基本资料（不含 userId，后端仅更新登录用户本人）。 */
    public record ProfileSave(
            @NotBlank(message = "姓名不能为空") String userName,
            @NotBlank(message = "性别不能为空") String userSex,
            String userEmail,
            String userPhone
    ) {
    }

    /** 当前用户修改自己的密码（接口不接受 userId，仅改当前登录账号）。 */
    public record UpdatePwd(
            @NotBlank(message = "原密码不能为空") String oldPassword,
            @NotBlank(message = "新密码不能为空") String newPassword
    ) {
    }

    /** 当前用户更新头像地址（上传接口返回的 accessUrl）。 */
    public record UpdateAvatar(
            @NotBlank(message = "头像地址不能为空") String userAvatar
    ) {
    }
}
