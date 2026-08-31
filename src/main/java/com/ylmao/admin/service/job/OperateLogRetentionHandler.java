package com.ylmao.admin.service.job;

import com.ylmao.admin.common.JobCodes;
import com.ylmao.admin.service.OperateLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperateLogRetentionHandler implements JobHandler {

    private final OperateLogService operateLogService;

    @Override
    public String jobCode() {
        return JobCodes.OPERATE_LOG_RETENTION;
    }

    @Override
    public String execute() {
        int deleted = operateLogService.cleanExpiredByRetentionConfig();
        return "清理操作日志 " + deleted + " 条";
    }
}
