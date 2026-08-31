package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.common.RedisKeys;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.entity.DictData;
import com.ylmao.admin.entity.DictType;
import com.ylmao.admin.mapper.DictDataMapper;
import com.ylmao.admin.mapper.DictTypeMapper;
import com.ylmao.admin.vo.DictVo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 字典运行时缓存：启用选项写入 Redis，读取时直读 Redis（方案 A）。索引为 string JSON。 */
@Service
@RequiredArgsConstructor
public class DictRuntimeService {

    private static final Logger log = LoggerFactory.getLogger(DictRuntimeService.class);
    private static final TypeReference<List<DictVo.DictOptionVo>> OPTION_LIST_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };
    /** 旧版 set 索引，启动刷新时删除。 */
    private static final String LEGACY_DICT_INDEX = "md:dict:index";

    private final DictDataMapper dictDataMapper;
    private final DictTypeMapper dictTypeMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final JsonMapper jsonMapper;

    @PostConstruct
    public void initCache() {
        refreshCache(null);
    }

    public List<DictVo.DictOptionVo> getOptions(String dictTypeCode) {
        if (StrUtil.isBlank(dictTypeCode)) {
            return List.of();
        }
        String json = stringRedisTemplate.opsForValue().get(RedisKeys.dictOptions(dictTypeCode));
        if (StrUtil.isBlank(json)) {
            return List.of();
        }
        try {
            List<DictVo.DictOptionVo> options = jsonMapper.readValue(json, OPTION_LIST_TYPE);
            return options == null ? List.of() : options;
        } catch (Exception ex) {
            log.warn("字典缓存反序列化失败 dictTypeCode={} reason={}", dictTypeCode, ex.getMessage());
            return List.of();
        }
    }

    public Map<String, List<DictVo.DictOptionVo>> getOptionsBatch(List<String> dictTypeCodes) {
        if (dictTypeCodes == null || dictTypeCodes.isEmpty()) {
            return Map.of();
        }
        Map<String, List<DictVo.DictOptionVo>> result = new LinkedHashMap<>();
        for (String dictTypeCode : dictTypeCodes) {
            if (StrUtil.isNotBlank(dictTypeCode)) {
                result.put(dictTypeCode, getOptions(dictTypeCode));
            }
        }
        return result;
    }

    public String getLabel(String dictTypeCode, Object value) {
        if (value == null || StrUtil.isBlank(dictTypeCode)) {
            return "";
        }
        String valueText = String.valueOf(value);
        return getOptions(dictTypeCode).stream()
                .filter(option -> valueText.equals(option.dictDataValue()))
                .map(DictVo.DictOptionVo::dictDataLabel)
                .findFirst()
                .orElse("");
    }

    public boolean containsValue(String dictTypeCode, Object value) {
        if (value == null || StrUtil.isBlank(dictTypeCode)) {
            return false;
        }
        String valueText = String.valueOf(value);
        return getOptions(dictTypeCode).stream()
                .anyMatch(option -> valueText.equals(option.dictDataValue()));
    }

    public void validateValue(String dictTypeCode, Object value, String fieldName) {
        if (!containsValue(dictTypeCode, value)) {
            throw new BusinessException(fieldName + "参数不合法");
        }
    }

    /** dictTypeCode 为空时全量重建；否则只刷新指定类型。 */
    public void refreshCache(String dictTypeCode) {
        if (StrUtil.isBlank(dictTypeCode)) {
            clearAllDictCache();
            List<DictType> dictTypes = dictTypeMapper.selectList(new LambdaQueryWrapper<DictType>()
                    .eq(DictType::getIsEnabled, 1)
                    .orderByAsc(DictType::getOrderNum)
                    .orderByDesc(DictType::getCreateTime));
            List<String> codes = new ArrayList<>();
            for (DictType dictType : dictTypes) {
                String code = dictType.getDictTypeCode();
                writeOptions(code, loadOptions(code));
                codes.add(code);
            }
            stringRedisTemplate.opsForValue().set(RedisKeys.DICT_INDEX, jsonMapper.writeValueAsString(codes));
            return;
        }
        // 类型停用或不存在时移除 Redis 缓存，避免残留空列表。
        DictType dictType = dictTypeMapper.selectOne(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictTypeCode, dictTypeCode)
                .eq(DictType::getIsEnabled, 1));
        if (dictType == null) {
            removeDictCode(dictTypeCode);
            return;
        }
        writeOptions(dictTypeCode, loadOptions(dictTypeCode));
        addDictCode(dictTypeCode);
    }

    private void writeOptions(String dictTypeCode, List<DictVo.DictOptionVo> options) {
        if (StrUtil.isBlank(dictTypeCode)) {
            return;
        }
        stringRedisTemplate.opsForValue().set(
                RedisKeys.dictOptions(dictTypeCode),
                jsonMapper.writeValueAsString(options));
    }

    private void clearAllDictCache() {
        Set<String> keysToDelete = new HashSet<>();
        keysToDelete.add(LEGACY_DICT_INDEX);
        keysToDelete.add(RedisKeys.DICT_INDEX);
        Set<String> legacyCodes = stringRedisTemplate.opsForSet().members(LEGACY_DICT_INDEX);
        if (legacyCodes != null) {
            for (String code : legacyCodes) {
                if (StrUtil.isNotBlank(code)) {
                    keysToDelete.add(RedisKeys.dictOptions(code));
                }
            }
        }
        for (String code : readCodeIndex()) {
            keysToDelete.add(RedisKeys.dictOptions(code));
        }
        stringRedisTemplate.delete(keysToDelete);
    }

    private void removeDictCode(String dictTypeCode) {
        stringRedisTemplate.delete(RedisKeys.dictOptions(dictTypeCode));
        LinkedHashSet<String> codes = new LinkedHashSet<>(readCodeIndex());
        codes.remove(dictTypeCode);
        stringRedisTemplate.opsForValue().set(RedisKeys.DICT_INDEX, jsonMapper.writeValueAsString(new ArrayList<>(codes)));
    }

    private void addDictCode(String dictTypeCode) {
        LinkedHashSet<String> codes = new LinkedHashSet<>(readCodeIndex());
        codes.add(dictTypeCode);
        stringRedisTemplate.opsForValue().set(RedisKeys.DICT_INDEX, jsonMapper.writeValueAsString(new ArrayList<>(codes)));
    }

    private List<String> readCodeIndex() {
        String json = stringRedisTemplate.opsForValue().get(RedisKeys.DICT_INDEX);
        if (StrUtil.isBlank(json)) {
            return List.of();
        }
        try {
            List<String> codes = jsonMapper.readValue(json, STRING_LIST_TYPE);
            return codes == null ? List.of() : codes;
        } catch (Exception ex) {
            log.warn("字典索引反序列化失败 reason={}", ex.getMessage());
            return List.of();
        }
    }

    private List<DictVo.DictOptionVo> loadOptions(String dictTypeCode) {
        DictType dictType = dictTypeMapper.selectOne(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictTypeCode, dictTypeCode)
                .eq(DictType::getIsEnabled, 1));
        if (dictType == null) {
            return List.of();
        }
        List<DictData> dictDataList = dictDataMapper.selectList(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDictTypeCode, dictTypeCode)
                .eq(DictData::getIsEnabled, 1)
                .orderByAsc(DictData::getOrderNum)
                .orderByDesc(DictData::getCreateTime));
        if (dictDataList.isEmpty()) {
            return List.of();
        }
        List<DictVo.DictOptionVo> options = new ArrayList<>(dictDataList.size());
        for (DictData dictData : dictDataList) {
            options.add(DictVo.DictOptionVo.from(dictData));
        }
        return Collections.unmodifiableList(options);
    }
}
