package com.ylmao.admin.config.log;

/**
 * 单条配置变更快照，写入操作日志 requestBody（一码一条）。
 */
public record ConfigAuditItem(
        String action,
        String configCode,
        String configName,
        Integer isBuiltin,
        String beforeValue,
        String afterValue,
        Integer beforeEnabled,
        Integer afterEnabled
) {
}
