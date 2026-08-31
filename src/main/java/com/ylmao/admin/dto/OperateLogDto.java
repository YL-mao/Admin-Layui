package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OperateLogDto {

    // 行为日志列表筛选入参，日志类型由页面页签传入。
    public record OperateLogList(
            @Size(max = 128, message = "操作标题参数不合法") String operateTitle,
            // true：标题精确匹配（配置变更页签）；默认 false 仍模糊搜。
            Boolean operateTitleExact,
            @Size(max = 32, message = "日志类型参数不合法") String loggingType,
            @Min(value = 0, message = "操作结果参数不合法") @Max(value = 1, message = "操作结果参数不合法")
            Integer isSuccess,
            @Size(max = 19, message = "开始时间参数不合法") String startTime,
            @Size(max = 19, message = "结束时间参数不合法") String endTime,
            @Size(max = 64, message = "操作人参数不合法") String operateName,
            @Size(max = 32, message = "业务类型参数不合法") String businessType,
            @Size(max = 255, message = "请求地址参数不合法") String requestUri,
            @Size(max = 64, message = "操作IP参数不合法") String operateIp
    ) { }
}
