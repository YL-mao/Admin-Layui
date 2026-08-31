package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleDto {

    public record RoleList(
            @Size(max = 64, message = "角色名称参数不合法") String roleName
    ) { }

    public record RoleInsert(
            @NotBlank(message = "角色名称不能为空") String roleName,
            @NotBlank(message = "角色编码不能为空") String roleCode,
            @NotNull(message = "角色排序不能为空") Integer orderNum,
            @NotNull(message = "角色状态参数不合法") @Min(value = 0, message = "角色状态参数不合法") @Max(value = 1, message = "角色状态参数不合法")
            Integer isEnabled
    ) { }

    public record RoleUpdate(
            @NotBlank(message = "角色ID不能为空") String roleId,
            @NotBlank(message = "角色名称不能为空") String roleName,
            @NotBlank(message = "角色编码不能为空") String roleCode,
            @NotNull(message = "角色排序不能为空") Integer orderNum,
            @NotNull(message = "角色状态参数不合法") @Min(value = 0, message = "角色状态参数不合法") @Max(value = 1, message = "角色状态参数不合法")
            Integer isEnabled
    ) { }

    public record UpdateEnabled(
            @NotBlank(message = "角色ID不能为空") String roleId,
            @NotNull(message = "角色状态参数不合法") @Min(value = 0, message = "角色状态参数不合法") @Max(value = 1, message = "角色状态参数不合法")
            Integer isEnabled
    ) { }
}
