package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermDto {

    public record PermList(
            @Size(max = 64, message = "权限名称参数不合法") String permName,
            @Size(max = 128, message = "权限标识参数不合法") String permCode,
            @Size(max = 255, message = "权限地址参数不合法") String permUrl
    ) {
    }

    public record PermInsert(
            String parentId,
            @NotBlank(message = "权限名称不能为空") String permName,
            String permDesc,
            String permUrl,
            Integer isBlank,
            String permCode,
            @NotNull(message = "权限类型参数不合法") @Min(value = 0, message = "权限类型参数不合法") @Max(value = 2, message = "权限类型参数不合法")
            Integer permType,
            String permIcon,
            @NotNull(message = "权限排序不能为空") Integer orderNum,
            @NotNull(message = "权限状态参数不合法") @Min(value = 0, message = "权限状态参数不合法") @Max(value = 1, message = "权限状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record PermUpdate(
            @NotBlank(message = "权限ID不能为空") String permId,
            String parentId,
            @NotBlank(message = "权限名称不能为空") String permName,
            String permDesc,
            String permUrl,
            Integer isBlank,
            String permCode,
            @NotNull(message = "权限类型参数不合法") @Min(value = 0, message = "权限类型参数不合法") @Max(value = 2, message = "权限类型参数不合法")
            Integer permType,
            String permIcon,
            @NotNull(message = "权限排序不能为空") Integer orderNum,
            @NotNull(message = "权限状态参数不合法") @Min(value = 0, message = "权限状态参数不合法") @Max(value = 1, message = "权限状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "权限ID不能为空") String permId,
            @NotNull(message = "权限状态参数不合法") @Min(value = 0, message = "权限状态参数不合法") @Max(value = 1, message = "权限状态参数不合法")
            Integer isEnabled
    ) {
    }

    /** 角色授权保存：角色 ID 必填，权限 ID 列表允许为空表示清空授权。 */
    public record RolePermSave(
            @NotBlank(message = "角色ID不能为空") String roleId,
            String permIds
    ) {
    }
}
