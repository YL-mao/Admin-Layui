package com.ylmao.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilterDto {

    public record FilterList(
            @Size(max = 16, message = "类型参数不合法") String filterType,
            @Size(max = 128, message = "过滤值参数不合法") String filterValue,
            @Size(max = 16, message = "来源参数不合法") String filterSource,
            @Size(max = 16, message = "策略参数不合法") String policyMode,
            @Min(value = 0, message = "状态参数不合法") @Max(value = 1, message = "状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record FilterInsert(
            @NotBlank(message = "类型不能为空") String filterType,
            @NotBlank(message = "过滤值不能为空") @Size(max = 128, message = "过滤值参数不合法") String filterValue,
            @Size(max = 255, message = "说明参数不合法") String filterDesc,
            @NotBlank(message = "策略不能为空") String policyMode,
            @NotNull(message = "过期时间不能为空") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expireTime,
            @NotNull(message = "状态参数不合法") @Min(value = 0, message = "状态参数不合法") @Max(value = 1, message = "状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record FilterUpdate(
            @NotBlank(message = "访问控制ID不能为空") String filterId,
            @NotBlank(message = "类型不能为空") String filterType,
            @NotBlank(message = "过滤值不能为空") @Size(max = 128, message = "过滤值参数不合法") String filterValue,
            @Size(max = 255, message = "说明参数不合法") String filterDesc,
            @NotBlank(message = "策略不能为空") String policyMode,
            @NotNull(message = "过期时间不能为空") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expireTime,
            @NotNull(message = "状态参数不合法") @Min(value = 0, message = "状态参数不合法") @Max(value = 1, message = "状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "访问控制ID不能为空") String filterId,
            @NotNull(message = "状态参数不合法") @Min(value = 0, message = "状态参数不合法") @Max(value = 1, message = "状态参数不合法")
            Integer isEnabled
    ) {
    }
}
