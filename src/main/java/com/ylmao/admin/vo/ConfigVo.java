package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.Config;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfigVo {

    public record ConfigListVo(String configId, String configName, String configCode, String configValue,
                               String configGroup, String valueType, Integer isBuiltin, Integer isEnabled,
                               Integer orderNum, String configDesc,
                               @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createTime) {

        public static ConfigListVo from(Config config) {
            return new ConfigListVo(config.getConfigId(), config.getConfigName(), config.getConfigCode(),
                    config.getConfigValue(), config.getConfigGroup(), config.getValueType(),
                    config.getIsBuiltin(), config.getIsEnabled(), config.getOrderNum(),
                    config.getConfigDesc(), config.getCreateTime());
        }
    }

    public record ConfigGroupVo(String configGroup, Long configCount) {
    }
}
