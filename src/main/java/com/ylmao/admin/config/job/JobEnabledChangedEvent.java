package com.ylmao.admin.config.job;

/**
 * 启用/停用定时任务后用于通知调度器立即生效。
 */
public record JobEnabledChangedEvent(String jobCode, Integer isEnabled) {

    public boolean enabled() {
        return isEnabled != null && isEnabled == 1;
    }
}

