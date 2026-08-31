package com.ylmao.admin.common;

/** 定时任务类配置编码契约，与 sys_config.config_code 对齐。 */
public final class JobConfigCodes {

    /** 内置任务扫描注册间隔（秒）。 */
    public static final String SCAN_SECONDS = "job.scanSecs";

    private JobConfigCodes() {
    }
}
