package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DictDataDto {

    public record DictDataList(
            @Size(max = 64, message = "字典编码参数不合法") String dictTypeCode,
            @Size(max = 64, message = "数据标签参数不合法") String dictDataLabel
    ) {
    }

    public record DictDataInsert(
            @NotBlank(message = "请先选择字典类型") String dictTypeCode,
            @NotBlank(message = "数据标签不能为空") String dictDataLabel,
            @NotBlank(message = "数据值不能为空") String dictDataValue,
            @NotNull(message = "字典数据排序不能为空") Integer orderNum,
            @NotNull(message = "字典数据状态参数不合法") @Min(value = 0, message = "字典数据状态参数不合法") @Max(value = 1, message = "字典数据状态参数不合法")
            Integer isEnabled,
            String dictDataDesc
    ) {
    }

    public record DictDataUpdate(
            @NotBlank(message = "字典数据ID不能为空") String dictDataId,
            @NotBlank(message = "请先选择字典类型") String dictTypeCode,
            @NotBlank(message = "数据标签不能为空") String dictDataLabel,
            @NotBlank(message = "数据值不能为空") String dictDataValue,
            @NotNull(message = "字典数据排序不能为空") Integer orderNum,
            @NotNull(message = "字典数据状态参数不合法") @Min(value = 0, message = "字典数据状态参数不合法") @Max(value = 1, message = "字典数据状态参数不合法")
            Integer isEnabled,
            String dictDataDesc
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "字典数据ID不能为空") String dictDataId,
            @NotNull(message = "字典数据状态参数不合法") @Min(value = 0, message = "字典数据状态参数不合法") @Max(value = 1, message = "字典数据状态参数不合法")
            Integer isEnabled
    ) {
    }

    /** 字典数据默认项单独 PATCH，与启用状态拆分。 */
    public record UpdateDefault(
            @NotBlank(message = "字典数据ID不能为空") String dictDataId,
            @NotBlank(message = "默认状态参数不合法")
            @Pattern(regexp = "[01]", message = "默认状态参数不合法")
            String isDefault
    ) {
    }
}
