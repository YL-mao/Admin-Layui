package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.DictDataDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.DictData;
import com.ylmao.admin.mapper.DictDataMapper;
import com.ylmao.admin.vo.DictVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictDataService {

    private final DictDataMapper dictDataMapper;
    private final DictRuntimeService dictRuntimeService;

    public IPage<DictVo.DictDataListVo> selectPageList(PageQuery pageQuery, DictDataDto.DictDataList dictDataList) {
        Page<DictData> dictDataPage = pageQuery.toMpPage();
        if (dictDataList == null || StrUtil.isBlank(dictDataList.dictTypeCode())) {
            // 未选择左侧字典类型时右侧列表保持为空，避免误展示全部字典数据。
            return new Page<>(dictDataPage.getCurrent(), dictDataPage.getSize(), 0);
        }
        LambdaQueryWrapper<DictData> dictDataQueryWrapper = new LambdaQueryWrapper<>();
        // 字典数据必须隶属于当前选中的字典编码。
        dictDataQueryWrapper.eq(DictData::getDictTypeCode, dictDataList.dictTypeCode());
        dictDataQueryWrapper.like(StrUtil.isNotBlank(dictDataList.dictDataLabel()), DictData::getDictDataLabel, dictDataList.dictDataLabel());
        dictDataQueryWrapper.orderByAsc(DictData::getOrderNum).orderByDesc(DictData::getCreateTime);
        return dictDataMapper.selectPage(dictDataPage, dictDataQueryWrapper).convert(DictVo.DictDataListVo::from);
    }

    public DictData selectById(String dictDataId) {
        return dictDataMapper.selectById(dictDataId);
    }

    @Transactional
    public void insert(DictDataDto.DictDataInsert dictDataInsert) {
        validateDictDataUnique(dictDataInsert.dictTypeCode(), dictDataInsert.dictDataLabel(),
                dictDataInsert.dictDataValue(), null);
        DictData dictData = new DictData(dictDataInsert);
        // 新增字典数据默认不是默认项，默认项只能通过列表开关单独设置。
        dictData.setIsDefault("0");
        int rows = dictDataMapper.insert(dictData);
        if (rows <= 0) {
            throw new BusinessException("新增字典数据失败");
        }
        dictRuntimeService.refreshCache(dictDataInsert.dictTypeCode());
    }

    @Transactional
    public void updateById(DictDataDto.DictDataUpdate dictDataUpdate) {
        validateDictDataUnique(dictDataUpdate.dictTypeCode(), dictDataUpdate.dictDataLabel(),
                dictDataUpdate.dictDataValue(), dictDataUpdate.dictDataId());
        DictData dictData = new DictData(dictDataUpdate);
        int rows = dictDataMapper.updateById(dictData);
        if (rows <= 0) {
            throw new BusinessException("字典数据不存在或修改失败");
        }
        dictRuntimeService.refreshCache(dictDataUpdate.dictTypeCode());
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的字典数据");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        List<DictData> dictDataList = dictDataMapper.selectList(new LambdaQueryWrapper<DictData>().in(DictData::getDictDataId, idList));
        int rows = dictDataMapper.delete(new LambdaQueryWrapper<DictData>().in(DictData::getDictDataId, idList));
        if (rows <= 0) {
            throw new BusinessException("字典数据不存在或删除失败");
        }
        dictDataList.stream().map(DictData::getDictTypeCode).distinct().forEach(dictRuntimeService::refreshCache);
    }

    @Transactional
    public void updateEnabled(DictDataDto.UpdateEnabled updateEnabled) {
        DictData oldDictData = dictDataMapper.selectById(updateEnabled.dictDataId());
        if (oldDictData == null) {
            throw new BusinessException("字典数据不存在");
        }
        oldDictData.setIsEnabled(updateEnabled.isEnabled());
        int rows = dictDataMapper.updateById(oldDictData);
        if (rows <= 0) {
            throw new BusinessException("修改字典数据状态失败");
        }
        dictRuntimeService.refreshCache(oldDictData.getDictTypeCode());
    }

    public DictData checkDictDataLabelUnique(String dictTypeCode, String dictDataLabel) {
        if (StrUtil.isBlank(dictTypeCode) || StrUtil.isBlank(dictDataLabel)) {
            return null;
        }
        LambdaQueryWrapper<DictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictData::getDictTypeCode, dictTypeCode);
        queryWrapper.eq(DictData::getDictDataLabel, dictDataLabel);
        return dictDataMapper.selectOne(queryWrapper);
    }

    public DictData checkDictDataValueUnique(String dictTypeCode, String dictDataValue) {
        if (StrUtil.isBlank(dictTypeCode) || StrUtil.isBlank(dictDataValue)) {
            return null;
        }
        LambdaQueryWrapper<DictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictData::getDictTypeCode, dictTypeCode);
        queryWrapper.eq(DictData::getDictDataValue, dictDataValue);
        return dictDataMapper.selectOne(queryWrapper);
    }

    // 同一字典类型下，数据标签与数据值均不能重复。
    private void validateDictDataUnique(String dictTypeCode, String dictDataLabel, String dictDataValue, String dictDataId) {
        DictData labelExists = checkDictDataLabelUnique(dictTypeCode, dictDataLabel);
        if (labelExists != null && (dictDataId == null || !labelExists.getDictDataId().equals(dictDataId))) {
            throw new BusinessException("同一字典类型下数据标签已存在");
        }
        DictData valueExists = checkDictDataValueUnique(dictTypeCode, dictDataValue);
        if (valueExists != null && (dictDataId == null || !valueExists.getDictDataId().equals(dictDataId))) {
            throw new BusinessException("同一字典类型下数据值已存在");
        }
        if (dictDataId != null && dictDataMapper.selectById(dictDataId) == null) {
            throw new BusinessException("字典数据不存在");
        }
    }

    @Transactional
    public void updateDefault(DictDataDto.UpdateDefault updateDefault) {
        DictData oldDictData = dictDataMapper.selectById(updateDefault.dictDataId());
        if (oldDictData == null) {
            throw new BusinessException("字典数据不存在");
        }
        if ("1".equals(updateDefault.isDefault())) {
            // 同一字典类型只允许一个默认项，设置当前项前先清空同类型其它默认项。
            dictDataMapper.update(null, new LambdaUpdateWrapper<DictData>()
                    .set(DictData::getIsDefault, "0")
                    .eq(DictData::getDictTypeCode, oldDictData.getDictTypeCode())
                    .ne(DictData::getDictDataId, oldDictData.getDictDataId()));
        }
        oldDictData.setIsDefault(updateDefault.isDefault());
        int rows = dictDataMapper.updateById(oldDictData);
        if (rows <= 0) {
            throw new BusinessException("修改字典默认状态失败");
        }
        dictRuntimeService.refreshCache(oldDictData.getDictTypeCode());
    }
}
