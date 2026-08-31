package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostDto {

    public record PostList(
            @Size(max = 64, message = "岗位编码参数不合法") String postCode,
            @Size(max = 64, message = "岗位名称参数不合法") String postName
    ) {
    }

    public record PostInsert(
            @NotBlank(message = "岗位编码不能为空") String postCode,
            @NotBlank(message = "岗位名称不能为空") String postName,
            @NotNull(message = "岗位类型不能为空") Integer postType,
            @NotNull(message = "岗位排序不能为空") Integer orderNum,
            @NotNull(message = "岗位状态参数不合法") @Min(value = 0, message = "岗位状态参数不合法") @Max(value = 1, message = "岗位状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record PostUpdate(
            @NotBlank(message = "岗位ID不能为空") String postId,
            @NotBlank(message = "岗位编码不能为空") String postCode,
            @NotBlank(message = "岗位名称不能为空") String postName,
            @NotNull(message = "岗位类型不能为空") Integer postType,
            @NotNull(message = "岗位排序不能为空") Integer orderNum,
            @NotNull(message = "岗位状态参数不合法") @Min(value = 0, message = "岗位状态参数不合法") @Max(value = 1, message = "岗位状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "岗位ID不能为空") String postId,
            @NotNull(message = "岗位状态参数不合法") @Min(value = 0, message = "岗位状态参数不合法") @Max(value = 1, message = "岗位状态参数不合法")
            Integer isEnabled
    ) {
    }
}
