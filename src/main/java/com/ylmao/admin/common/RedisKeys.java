package com.ylmao.admin.common;

/**
 * 业务 Redis key 约定；与 Sa-Token 自有 key 隔离，统一 md: 前缀。
 */
public final class RedisKeys {

    private RedisKeys() {
    }

    public static String captcha(String captchaId) {
        return "md:captcha:" + captchaId;
    }

    public static String loginFailAccount(String userAccount) {
        return "md:login:fail:account:" + userAccount;
    }

    public static String loginFailIp(String ip) {
        return "md:login:fail:ip:" + ip;
    }

    public static String rateLoginIp(String ip) {
        return "md:rate:login:ip:" + ip;
    }

    public static String rateLoginAccount(String userAccount) {
        return "md:rate:login:account:" + userAccount;
    }

    public static String rateCaptchaIp(String ip) {
        return "md:rate:captcha:ip:" + ip;
    }

    public static String jobLock(String jobCode) {
        return "md:job:lock:" + jobCode;
    }

    public static String config(String configCode) {
        return "md:config:data:" + configCode;
    }

    /** 已缓存配置编码列表（string JSON），刷新时用于清理旧 key。 */
    public static final String CONFIG_INDEX = "md:meta:config:codes";

    public static String dictOptions(String dictTypeCode) {
        return "md:dict:options:" + dictTypeCode;
    }

    /** 已缓存字典类型编码列表（string JSON），全量刷新时用于清理旧 key。 */
    public static final String DICT_INDEX = "md:meta:dict:codes";
}
