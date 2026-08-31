package com.ylmao.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ConfigDto {

    public record GroupList(
            @Size(max = 64, message = "配置分组参数不合法") String configGroup
    ) {
    }

    public record ConfigList(
            @Size(max = 64, message = "配置分组参数不合法") String configGroup,
            @Size(max = 64, message = "配置名称参数不合法") String configName,
            @Size(max = 64, message = "配置编码参数不合法") String configCode
    ) {
    }

    public record ConfigInsert(
            @NotBlank(message = "配置名称不能为空") String configName,
            @NotBlank(message = "配置编码不能为空") String configCode,
            String configValue,
            @NotBlank(message = "配置分组不能为空") String configGroup,
            @NotBlank(message = "值类型参数不合法") String valueType,
            @NotNull(message = "内置标识参数不合法") @Min(value = 0, message = "内置标识参数不合法") @Max(value = 1, message = "内置标识参数不合法")
            Integer isBuiltin,
            @NotNull(message = "配置状态参数不合法") @Min(value = 0, message = "配置状态参数不合法") @Max(value = 1, message = "配置状态参数不合法")
            Integer isEnabled,
            @NotNull(message = "配置排序不能为空") Integer orderNum,
            String configDesc
    ) {
    }

    public record ConfigUpdate(
            @NotBlank(message = "配置ID不能为空") String configId,
            @NotBlank(message = "配置名称不能为空") String configName,
            @NotBlank(message = "配置编码不能为空") String configCode,
            String configValue,
            @NotBlank(message = "配置分组不能为空") String configGroup,
            @NotBlank(message = "值类型参数不合法") String valueType,
            @NotNull(message = "内置标识参数不合法") @Min(value = 0, message = "内置标识参数不合法") @Max(value = 1, message = "内置标识参数不合法")
            Integer isBuiltin,
            @NotNull(message = "配置状态参数不合法") @Min(value = 0, message = "配置状态参数不合法") @Max(value = 1, message = "配置状态参数不合法")
            Integer isEnabled,
            @NotNull(message = "配置排序不能为空") Integer orderNum,
            String configDesc
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "配置ID不能为空") String configId,
            @NotNull(message = "配置状态参数不合法") @Min(value = 0, message = "配置状态参数不合法") @Max(value = 1, message = "配置状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record GroupUpdate(
            @NotBlank(message = "配置分组不能为空") String configGroup,
            @NotEmpty(message = "配置项参数不合法") @Valid List<GroupConfig> configs
    ) {
    }

    public record GroupConfig(
            @NotBlank(message = "配置项参数不合法") String configId,
            String configCode,
            String configValue,
            Integer isEnabled
    ) {
    }
}
