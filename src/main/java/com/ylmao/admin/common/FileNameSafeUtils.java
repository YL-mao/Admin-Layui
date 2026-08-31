package com.ylmao.admin.common;

import com.ylmao.admin.config.exception.BusinessException;

/**
 * 上传展示名（originalName）规范化：去掉路径段与控制字符，拒绝 . / .. 。
 * 不改变磁盘 storage_key；落盘仍走 resolveDiskPath。
 */
public final class FileNameSafeUtils {

    private FileNameSafeUtils() {
    }

    /** 规范化后的展示文件名；非法输入抛「参数不合法」。 */
    public static String normalizeOriginalName(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessException("参数不合法");
        }
        StringBuilder leaf = new StringBuilder();
        String source = raw.trim();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '/' || c == '\\') {
                leaf.setLength(0);
                continue;
            }
            // 文件名不保留控制字符与 DEL。
            if (c < 0x20 || c == 0x7F) {
                continue;
            }
            leaf.append(c);
        }
        String name = leaf.toString().trim();
        if (name.isEmpty() || ".".equals(name) || "..".equals(name)) {
            throw new BusinessException("参数不合法");
        }
        return name;
    }
}
