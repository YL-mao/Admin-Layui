package com.ylmao.admin.config.log;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求线程内暂存配置变更项；由 LogAspect 在业务成功后按项拆成操作日志。
 */
public final class ConfigAuditHolder {

    private static final ThreadLocal<List<ConfigAuditItem>> HOLDER = ThreadLocal.withInitial(ArrayList::new);

    private ConfigAuditHolder() {
    }

    public static void add(ConfigAuditItem item) {
        if (item == null) {
            return;
        }
        HOLDER.get().add(item);
    }

    /** 取出并清空当前请求的变更项。 */
    public static List<ConfigAuditItem> drain() {
        List<ConfigAuditItem> items = HOLDER.get();
        if (items.isEmpty()) {
            clear();
            return List.of();
        }
        // 先拷贝再清 ThreadLocal，避免调用方持有可变列表。
        List<ConfigAuditItem> copy = List.copyOf(items);
        clear();
        return copy;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
