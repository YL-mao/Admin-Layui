package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobDto {

    public record JobList(
            @Size(max = 64, message = "任务编码参数不合法") String jobCode,
            @Size(max = 100, message = "任务名称参数不合法") String jobName,
            @Min(value = 0, message = "启停状态参数不合法") @Max(value = 1, message = "启停状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "任务ID不能为空") String jobId,
            @NotNull(message = "启停状态参数不合法") @Min(value = 0, message = "启停状态参数不合法") @Max(value = 1, message = "启停状态参数不合法")
            Integer isEnabled
    ) {
    }

    public record JobRun(
            @NotBlank(message = "任务ID不能为空") String jobId
    ) {
    }

    public record JobLogList(
            @NotBlank(message = "任务ID不能为空") String jobId,
            @Pattern(regexp = "^$|SUCCESS|FAILED|SKIPPED", message = "执行结果参数不合法")
            String runStatus
    ) {
    }
}
