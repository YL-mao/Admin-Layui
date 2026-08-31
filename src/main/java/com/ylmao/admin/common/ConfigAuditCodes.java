package com.ylmao.admin.common;

/**
 * 配置变更审计约定：操作日志标题固定，供行为日志「配置变更」页签筛选。
 */
public final class ConfigAuditCodes {

    public static final String OPERATE_TITLE = "系统配置变更";

    public static final String ACTION_INSERT = "INSERT";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_ENABLE = "ENABLE";

    private ConfigAuditCodes() {
    }
}
