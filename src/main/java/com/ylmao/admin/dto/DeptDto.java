package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeptDto {

    public record DeptList(
            @Size(max = 64, message = "部门名称参数不合法") String deptName,
            @Size(max = 64, message = "上级部门参数不合法") String parentId
    ) {
    }

    public record DeptInsert(
            String parentId,
            @NotBlank(message = "部门名称不能为空") String deptName,
            @NotNull(message = "部门排序不能为空") Integer orderNum,
            String deptLeader,
            String leaderPhone,
            String leaderEmail,
            @NotNull(message = "部门状态参数不合法") @Min(value = 0, message = "部门状态参数不合法") @Max(value = 1, message = "部门状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record DeptUpdate(
            @NotBlank(message = "部门ID不能为空") String deptId,
            String parentId,
            @NotBlank(message = "部门名称不能为空") String deptName,
            @NotNull(message = "部门排序不能为空") Integer orderNum,
            String deptLeader,
            String leaderPhone,
            String leaderEmail,
            @NotNull(message = "部门状态参数不合法") @Min(value = 0, message = "部门状态参数不合法") @Max(value = 1, message = "部门状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "部门ID不能为空") String deptId,
            @NotNull(message = "部门状态参数不合法") @Min(value = 0, message = "部门状态参数不合法") @Max(value = 1, message = "部门状态参数不合法")
            Integer isEnabled
    ) {
    }
}
