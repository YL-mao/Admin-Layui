package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_operate_log")
@EqualsAndHashCode(callSuper = false)
public final class OperateLog {

    @TableId(type = IdType.ASSIGN_ID)
    private String operateId;
    private String loggingType;
    private String businessType;
    private String operateTitle;
    private String requestMethod;
    private String operateMethod;
    private String requestUri;
    private String requestParam;
    private String requestBody;
    private String responseBody;
    private Integer isSuccess;
    private Integer statusCode;
    private String errorClass;
    private String errorMsg;
    private String errorStack;
    private String userId;
    private String operateName;
    private String operateIp;
    private String serverIp;
    private String userAgent;
    private String browser;
    private String systemOs;
    private String traceId;
    private Long costTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;
}
