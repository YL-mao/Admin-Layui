package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.DictTypeDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.DictData;
import com.ylmao.admin.entity.DictType;
import com.ylmao.admin.mapper.DictDataMapper;
import com.ylmao.admin.mapper.DictTypeMapper;
import com.ylmao.admin.vo.DictVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictTypeService {

    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;
    private final DictRuntimeService dictRuntimeService;

    public IPage<DictVo.DictTypeListVo> selectPageList(PageQuery pageQuery, DictTypeDto.DictTypeList dictTypeList) {
        Page<DictType> dictTypePage = pageQuery.toMpPage();
        LambdaQueryWrapper<DictType> dictTypeQueryWrapper = new LambdaQueryWrapper<>();
        if (dictTypeList != null) {
            // 字典类型列表按名称模糊查询，并按创建时间倒序展示。
            dictTypeQueryWrapper.like(StrUtil.isNotBlank(dictTypeList.dictTypeName()), DictType::getDictTypeName, dictTypeList.dictTypeName());
        }
        dictTypeQueryWrapper.orderByDesc(DictType::getCreateTime);
        return dictTypeMapper.selectPage(dictTypePage, dictTypeQueryWrapper).convert(DictVo.DictTypeListVo::from);
    }

    public DictType selectById(String dictTypeId) {
        return dictTypeMapper.selectById(dictTypeId);
    }

    @Transactional
    public void insert(DictTypeDto.DictTypeInsert dictTypeInsert) {
        // 字典类型编码会关联字典数据，新增时校验编码唯一性。
        if (checkDictTypeCodeUnique(dictTypeInsert.dictTypeCode()) != null) {
            throw new BusinessException("字典编码已存在");
        }
        DictType dictType = new DictType(dictTypeInsert);
        int rows = dictTypeMapper.insert(dictType);
        if (rows <= 0) {
            throw new BusinessException("新增字典类型失败");
        }
        dictRuntimeService.refreshCache(dictTypeInsert.dictTypeCode());
    }

    @Transactional
    public void updateById(DictTypeDto.DictTypeUpdate dictTypeUpdate) {
        // 修改字典类型校验编码唯一性。
        DictType oldDictType = checkDictTypeCodeUnique(dictTypeUpdate.dictTypeCode());
        if (oldDictType != null && !oldDictType.getDictTypeId().equals(dictTypeUpdate.dictTypeId())) {
            throw new BusinessException("字典编码已存在");
        }
        DictType beforeUpdate = dictTypeMapper.selectById(dictTypeUpdate.dictTypeId());
        if (beforeUpdate == null) {
            throw new BusinessException("字典类型不存在");
        }
        DictType dictType = new DictType(dictTypeUpdate);
        int rows = dictTypeMapper.updateById(dictType);
        if (rows <= 0) {
            throw new BusinessException("字典类型不存在或修改失败");
        }
        if (!beforeUpdate.getDictTypeCode().equals(dictTypeUpdate.dictTypeCode())) {
            // 字典编码变更时同步右侧字典数据归属，避免数据留在旧编码下。
            dictDataMapper.update(null, new LambdaUpdateWrapper<DictData>()
                    .set(DictData::getDictTypeCode, dictTypeUpdate.dictTypeCode())
                    .eq(DictData::getDictTypeCode, beforeUpdate.getDictTypeCode()));
            dictRuntimeService.refreshCache(beforeUpdate.getDictTypeCode());
        }
        dictRuntimeService.refreshCache(dictTypeUpdate.dictTypeCode());
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的字典类型");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        List<DictType> dictTypes = dictTypeMapper.selectList(new LambdaQueryWrapper<DictType>().in(DictType::getDictTypeId, idList));
        List<String> typeCodes = dictTypes.stream().map(DictType::getDictTypeCode).toList();
        int rows = dictTypeMapper.delete(new LambdaQueryWrapper<DictType>().in(DictType::getDictTypeId, idList));
        if (rows <= 0) {
            throw new BusinessException("字典类型不存在或删除失败");
        }
        if (!typeCodes.isEmpty()) {
            // 删除字典类型后同步删除所属字典数据，避免右侧数据成为无归属记录。
            dictDataMapper.delete(new LambdaQueryWrapper<DictData>().in(DictData::getDictTypeCode, typeCodes));
        }
        // 删除后全量重建运行时缓存，移除已删类型条目。
        dictRuntimeService.refreshCache(null);
    }

    @Transactional
    public void updateEnabled(DictTypeDto.UpdateEnabled updateEnabled) {
        DictType oldDictType = dictTypeMapper.selectById(updateEnabled.dictTypeId());
        if (oldDictType == null) {
            throw new BusinessException("字典类型不存在");
        }
        oldDictType.setIsEnabled(updateEnabled.isEnabled());
        int rows = dictTypeMapper.updateById(oldDictType);
        if (rows <= 0) {
            throw new BusinessException("修改字典类型状态失败");
        }
        dictRuntimeService.refreshCache(oldDictType.getDictTypeCode());
    }

    public DictType checkDictTypeCodeUnique(String dictTypeCode) {
        if (StrUtil.isBlank(dictTypeCode)) {
            return null;
        }
        LambdaQueryWrapper<DictType> dictTypeQueryWrapper = new LambdaQueryWrapper<>();
        dictTypeQueryWrapper.eq(DictType::getDictTypeCode, dictTypeCode);
        return dictTypeMapper.selectOne(dictTypeQueryWrapper);
    }
}
