package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.common.FilterCodes;
import com.ylmao.admin.entity.Filter;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilterVo {

    public record FilterListVo(
            String filterId,
            String filterType,
            String filterTypeName,
            String filterValue,
            String valueLabel,
            String filterSource,
            String filterSourceName,
            String policyMode,
            String policyModeName,
            String filterDesc,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expireTime,
            Integer permanent,
            Integer isEnabled,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createTime
    ) {
        public static FilterListVo from(Filter row, String valueLabel) {
            boolean permanent = row.getExpireTime() != null
                    && !row.getExpireTime().isBefore(FilterCodes.PERMANENT_EXPIRE);
            return new FilterListVo(
                    row.getFilterId(),
                    row.getFilterType(),
                    typeName(row.getFilterType()),
                    row.getFilterValue(),
                    valueLabel,
                    row.getFilterSource(),
                    sourceName(row.getFilterSource()),
                    row.getPolicyMode(),
                    modeName(row.getPolicyMode()),
                    row.getFilterDesc(),
                    row.getExpireTime(),
                    permanent ? 1 : 0,
                    row.getIsEnabled(),
                    row.getCreateTime()
            );
        }

        private static String typeName(String type) {
            if (FilterCodes.TYPE_IP.equals(type)) {
                return "IP";
            }
            if (FilterCodes.TYPE_USER_ID.equals(type)) {
                return "用户";
            }
            if (FilterCodes.TYPE_DEVICE.equals(type)) {
                return "设备";
            }
            return type == null ? "" : type;
        }

        private static String sourceName(String source) {
            if (FilterCodes.SOURCE_AUTO.equals(source)) {
                return "自动";
            }
            if (FilterCodes.SOURCE_MANUAL.equals(source)) {
                return "人工";
            }
            return source == null ? "" : source;
        }

        private static String modeName(String mode) {
            if (FilterCodes.MODE_WHITE.equals(mode)) {
                return "白名单";
            }
            if (FilterCodes.MODE_BLACK.equals(mode)) {
                return "黑名单";
            }
            return mode == null ? "" : mode;
        }
    }
}
