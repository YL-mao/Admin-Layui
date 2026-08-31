package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DictTypeDto {

    public record DictTypeList(
            @Size(max = 64, message = "字典名称参数不合法") String dictTypeName
    ) {
    }

    public record DictTypeInsert(
            @NotBlank(message = "字典名称不能为空") String dictTypeName,
            @NotBlank(message = "字典编码不能为空") String dictTypeCode,
            @NotNull(message = "字典排序不能为空") Integer orderNum,
            @NotNull(message = "字典类型状态参数不合法") @Min(value = 0, message = "字典类型状态参数不合法") @Max(value = 1, message = "字典类型状态参数不合法")
            Integer isEnabled,
            String dictTypeDesc
    ) {
    }

    public record DictTypeUpdate(
            @NotBlank(message = "字典类型ID不能为空") String dictTypeId,
            @NotBlank(message = "字典名称不能为空") String dictTypeName,
            @NotBlank(message = "字典编码不能为空") String dictTypeCode,
            @NotNull(message = "字典排序不能为空") Integer orderNum,
            @NotNull(message = "字典类型状态参数不合法") @Min(value = 0, message = "字典类型状态参数不合法") @Max(value = 1, message = "字典类型状态参数不合法")
            Integer isEnabled,
            String dictTypeDesc
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "字典类型ID不能为空") String dictTypeId,
            @NotNull(message = "字典类型状态参数不合法") @Min(value = 0, message = "字典类型状态参数不合法") @Max(value = 1, message = "字典类型状态参数不合法")
            Integer isEnabled
    ) {
    }
}
