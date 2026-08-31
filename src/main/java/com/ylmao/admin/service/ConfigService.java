package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.common.ConfigAuditCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.log.ConfigAuditHolder;
import com.ylmao.admin.config.log.ConfigAuditItem;
import com.ylmao.admin.dto.ConfigDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.Config;
import com.ylmao.admin.mapper.ConfigMapper;
import com.ylmao.admin.vo.ConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private static final Set<String> VALUE_TYPES = Set.of("string", "number", "boolean", "json");
    private final ConfigMapper configMapper;
    private final JsonMapper jsonMapper;
    private final ConfigRuntimeService configRuntimeService;

    public IPage<ConfigVo.ConfigListVo> selectPage(PageQuery pageQuery, ConfigDto.ConfigList configList) {
        Page<Config> configPage = pageQuery.toMpPage();
        LambdaQueryWrapper<Config> wrapper = buildListWrapper(configList);
        wrapper.orderByAsc(Config::getConfigGroup, Config::getOrderNum, Config::getCreateTime);
        return configMapper.selectPage(configPage, wrapper).convert(ConfigVo.ConfigListVo::from);
    }

    public List<ConfigVo.ConfigListVo> selectByGroup(String configGroup) {
        if (StrUtil.isBlank(configGroup)) {
            throw new BusinessException("配置分组不能为空");
        }
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigGroup, configGroup);
        wrapper.orderByAsc(Config::getOrderNum, Config::getCreateTime);
        return configMapper.selectList(wrapper).stream().map(ConfigVo.ConfigListVo::from).toList();
    }

    public List<ConfigVo.ConfigGroupVo> selectGroupList(String configGroup) {
        return configMapper.selectGroupList(configGroup).stream()
                .map(this::toConfigGroupVo)
                .toList();
    }

    @Transactional
    public void insert(ConfigDto.ConfigInsert configInsert) {
        // 新增配置先校验值类型契约，再校验配置编码唯一性。
        validateConfigFields(configInsert.valueType(), configInsert.configValue());
        if (checkConfigCodeUnique(configInsert.configCode()) != null) {
            throw new BusinessException("配置编码已存在");
        }
        Config config = new Config(configInsert);
        int rows = configMapper.insert(config);
        if (rows <= 0) {
            throw new BusinessException("新增配置失败");
        }
        // 新增成功后登记审计项，由 LogAspect 落操作日志。
        ConfigAuditHolder.add(new ConfigAuditItem(
                ConfigAuditCodes.ACTION_INSERT,
                config.getConfigCode(),
                config.getConfigName(),
                config.getIsBuiltin(),
                null,
                config.getConfigValue(),
                null,
                config.getIsEnabled()
        ));
        // 写入后刷新运行时缓存，保证业务读取到最新启用配置。
        configRuntimeService.refreshCache();
    }

    @Transactional
    public void updateById(ConfigDto.ConfigUpdate configUpdate) {
        validateConfigFields(configUpdate.valueType(), configUpdate.configValue());
        Config beforeUpdate = configMapper.selectById(configUpdate.configId());
        if (beforeUpdate == null) {
            throw new BusinessException("配置不存在");
        }
        validateBuiltinContract(beforeUpdate, configUpdate);
        Config oldConfig = checkConfigCodeUnique(configUpdate.configCode());
        if (oldConfig != null && !oldConfig.getConfigId().equals(configUpdate.configId())) {
            throw new BusinessException("配置编码已存在");
        }
        int rows = configMapper.updateById(new Config(configUpdate));
        if (rows <= 0) {
            throw new BusinessException("配置不存在或修改失败");
        }
        // 仅在值或启停真正变化时记审计（与分组保存口径一致；名称/描述变化不记空壳审计）。
        if (configChanged(beforeUpdate.getConfigValue(), configUpdate.configValue(),
                beforeUpdate.getIsEnabled(), configUpdate.isEnabled())) {
            ConfigAuditHolder.add(new ConfigAuditItem(
                    ConfigAuditCodes.ACTION_UPDATE,
                    beforeUpdate.getConfigCode(),
                    configUpdate.configName(),
                    beforeUpdate.getIsBuiltin(),
                    beforeUpdate.getConfigValue(),
                    configUpdate.configValue(),
                    beforeUpdate.getIsEnabled(),
                    configUpdate.isEnabled()
            ));
        }
        configRuntimeService.refreshCache();
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的配置");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        List<Config> configs = configMapper.selectList(new LambdaQueryWrapper<Config>().in(Config::getConfigId, idList));
        if (configs.stream().anyMatch(config -> config.getIsBuiltin() != null && config.getIsBuiltin() == 1)) {
            throw new BusinessException("内置配置不能删除");
        }
        int rows = configMapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("配置不存在或删除失败");
        }
        for (Config config : configs) {
            ConfigAuditHolder.add(new ConfigAuditItem(
                    ConfigAuditCodes.ACTION_DELETE,
                    config.getConfigCode(),
                    config.getConfigName(),
                    config.getIsBuiltin(),
                    config.getConfigValue(),
                    null,
                    config.getIsEnabled(),
                    null
            ));
        }
        configRuntimeService.refreshCache();
    }

    @Transactional
    public void updateEnabled(ConfigDto.UpdateEnabled updateEnabled) {
        Config config = configMapper.selectById(updateEnabled.configId());
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        Integer beforeEnabled = config.getIsEnabled();
        config.setIsEnabled(updateEnabled.isEnabled());
        int rows = configMapper.updateById(config);
        if (rows <= 0) {
            throw new BusinessException("修改配置状态失败");
        }
        if (!Objects.equals(beforeEnabled, updateEnabled.isEnabled())) {
            ConfigAuditHolder.add(new ConfigAuditItem(
                    ConfigAuditCodes.ACTION_ENABLE,
                    config.getConfigCode(),
                    config.getConfigName(),
                    config.getIsBuiltin(),
                    config.getConfigValue(),
                    config.getConfigValue(),
                    beforeEnabled,
                    updateEnabled.isEnabled()
            ));
        }
        configRuntimeService.refreshCache();
    }

    @Transactional
    public void updateGroup(ConfigDto.GroupUpdate groupUpdate) {
        for (ConfigDto.GroupConfig groupConfig : groupUpdate.configs()) {
            updateGroupConfig(groupUpdate.configGroup(), groupConfig);
        }
        configRuntimeService.refreshCache();
    }

    public Config checkConfigCodeUnique(String configCode) {
        if (StrUtil.isBlank(configCode)) {
            return null;
        }
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigCode, configCode);
        return configMapper.selectOne(wrapper);
    }

    private LambdaQueryWrapper<Config> buildListWrapper(ConfigDto.ConfigList configList) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        if (configList != null) {
            wrapper.eq(StrUtil.isNotBlank(configList.configGroup()), Config::getConfigGroup, configList.configGroup());
            wrapper.like(StrUtil.isNotBlank(configList.configName()), Config::getConfigName, configList.configName());
            wrapper.like(StrUtil.isNotBlank(configList.configCode()), Config::getConfigCode, configList.configCode());
        }
        return wrapper;
    }

    private ConfigVo.ConfigGroupVo toConfigGroupVo(Map<String, Object> row) {
        // Mapper 聚合结果转换成 VO，避免分组列表直接暴露数据库返回结构。
        Object group = row.get("configGroup");
        if (group == null) {
            group = row.get("config_group");
        }
        Object count = row.get("configCount");
        if (count == null) {
            count = row.get("config_count");
        }
        return new ConfigVo.ConfigGroupVo(String.valueOf(group),
                count instanceof Number number ? number.longValue() : Long.parseLong(String.valueOf(count)));
    }

    private void updateGroupConfig(String configGroup, ConfigDto.GroupConfig groupConfig) {
        Config config = configMapper.selectById(groupConfig.configId());
        if (config == null || !configGroup.equals(config.getConfigGroup())) {
            throw new BusinessException("配置项不属于当前分组");
        }
        if (StrUtil.isNotBlank(groupConfig.configCode()) && !groupConfig.configCode().equals(config.getConfigCode())) {
            throw new BusinessException("配置编码参数不合法");
        }
        validateConfigValue(config.getValueType(), groupConfig.configValue());
        String beforeValue = config.getConfigValue();
        Integer beforeEnabled = config.getIsEnabled();
        Integer afterEnabled = groupConfig.isEnabled() != null ? groupConfig.isEnabled() : beforeEnabled;
        config.setConfigValue(groupConfig.configValue());
        if (groupConfig.isEnabled() != null) {
            if (invalidSwitch(groupConfig.isEnabled())) {
                throw new BusinessException("配置状态参数不合法");
            }
            config.setIsEnabled(groupConfig.isEnabled());
        }
        int rows = configMapper.updateById(config);
        if (rows <= 0) {
            throw new BusinessException("保存配置失败");
        }
        // 分组批量保存：值与启停都未变则不记。
        if (configChanged(beforeValue, groupConfig.configValue(), beforeEnabled, afterEnabled)) {
            ConfigAuditHolder.add(new ConfigAuditItem(
                    ConfigAuditCodes.ACTION_UPDATE,
                    config.getConfigCode(),
                    config.getConfigName(),
                    config.getIsBuiltin(),
                    beforeValue,
                    groupConfig.configValue(),
                    beforeEnabled,
                    afterEnabled
            ));
        }
    }

    private boolean configChanged(String beforeValue, String afterValue, Integer beforeEnabled, Integer afterEnabled) {
        return !Objects.equals(StrUtil.nullToEmpty(beforeValue), StrUtil.nullToEmpty(afterValue))
                || !Objects.equals(beforeEnabled, afterEnabled);
    }

    private void validateConfigFields(String valueType, String configValue) {
        // 配置值需符合值类型契约，保存前校验值类型与配置值。
        if (StrUtil.isBlank(valueType) || !VALUE_TYPES.contains(valueType)) {
            throw new BusinessException("值类型参数不合法");
        }
        validateConfigValue(valueType, configValue);
    }

    private void validateBuiltinContract(Config beforeUpdate, ConfigDto.ConfigUpdate configUpdate) {
        if (beforeUpdate.getIsBuiltin() == null || beforeUpdate.getIsBuiltin() != 1) {
            return;
        }
        if (!beforeUpdate.getConfigCode().equals(configUpdate.configCode())
                || !beforeUpdate.getValueType().equals(configUpdate.valueType())
                || !beforeUpdate.getConfigGroup().equals(configUpdate.configGroup())) {
            throw new BusinessException("内置配置不能修改编码、分组和值类型");
        }
    }

    private void validateConfigValue(String valueType, String configValue) {
        if ("number".equals(valueType) && StrUtil.isNotBlank(configValue)) {
            try {
                new BigDecimal(configValue);
            } catch (NumberFormatException ex) {
                throw new BusinessException("数字配置值不合法");
            }
        }
        if ("boolean".equals(valueType) && StrUtil.isNotBlank(configValue)
                && !"true".equals(configValue) && !"false".equals(configValue)) {
            throw new BusinessException("布尔配置值只能为 true 或 false");
        }
        if ("json".equals(valueType) && StrUtil.isNotBlank(configValue)) {
            try {
                String jsonValue = configValue.trim();
                if (!jsonValue.startsWith("{") && !jsonValue.startsWith("[")) {
                    throw new IllegalArgumentException("JSON value must be object or array");
                }
                jsonMapper.readTree(jsonValue);
            } catch (Exception ex) {
                throw new BusinessException("JSON配置值不合法");
            }
        }
    }

    private boolean invalidSwitch(Integer value) {
        return value == null || (value != 0 && value != 1);
    }
}
