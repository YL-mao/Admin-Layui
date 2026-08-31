package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.OperateLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperateLogVo {

    // 行为日志列表出口字段，同时供详情抽屉展示完整日志信息。
    public record OperateLogListVo(
            String operateId,
            String loggingType,
            String businessType,
            String operateTitle,
            String requestMethod,
            String operateMethod,
            String requestUri,
            String requestParam,
            String requestBody,
            String responseBody,
            Integer isSuccess,
            Integer statusCode,
            String errorClass,
            String errorMsg,
            String errorStack,
            String userId,
            String operateName,
            String operateIp,
            String serverIp,
            String userAgent,
            String browser,
            String systemOs,
            String traceId,
            Long costTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime operateTime
    ) {

        public static OperateLogListVo from(OperateLog operateLog) {
            return new OperateLogListVo(
                    operateLog.getOperateId(),
                    operateLog.getLoggingType(),
                    operateLog.getBusinessType(),
                    operateLog.getOperateTitle(),
                    operateLog.getRequestMethod(),
                    operateLog.getOperateMethod(),
                    operateLog.getRequestUri(),
                    operateLog.getRequestParam(),
                    operateLog.getRequestBody(),
                    operateLog.getResponseBody(),
                    operateLog.getIsSuccess(),
                    operateLog.getStatusCode(),
                    operateLog.getErrorClass(),
                    operateLog.getErrorMsg(),
                    operateLog.getErrorStack(),
                    operateLog.getUserId(),
                    operateLog.getOperateName(),
                    operateLog.getOperateIp(),
                    operateLog.getServerIp(),
                    operateLog.getUserAgent(),
                    operateLog.getBrowser(),
                    operateLog.getSystemOs(),
                    operateLog.getTraceId(),
                    operateLog.getCostTime(),
                    operateLog.getOperateTime()
            );
        }
    }
}
