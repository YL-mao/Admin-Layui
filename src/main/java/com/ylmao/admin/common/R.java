package com.ylmao.admin.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一 Ajax JSON 响应体：{@code code} / {@code msg} / {@code data}，分页列表额外带 {@code count}。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record R<T>(int code, String msg, T data, Long count) {

    private static final int SUCCESS = 200;
    private static final String DEFAULT_SUCCESS_MSG = "操作成功";

    public static R<Void> ok() {
        return new R<>(SUCCESS, DEFAULT_SUCCESS_MSG, null, null);
    }

    public static R<Void> ok(String msg) {
        return new R<>(SUCCESS, msg, null, null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(SUCCESS, DEFAULT_SUCCESS_MSG, data, null);
    }

    public static <T> R<T> ok(String msg, T data) {
        return new R<>(SUCCESS, msg, data, null);
    }

    /** Layui 表格分页：顶层 {@code count} + {@code data}。 */
    public static <T> R<T> page(T data, long count) {
        return new R<>(SUCCESS, "请求成功", data, count);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null, null);
    }

    public static R<Void> fail(String msg) {
        return fail(500, msg);
    }
}
