package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.DictData;
import com.ylmao.admin.entity.DictType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DictVo {

    public record DictTypeListVo(
            String dictTypeId,
            String dictTypeName,
            String dictTypeCode,
            Integer orderNum,
            Integer isEnabled,
            String dictTypeDesc,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime createTime
    ) {
        public static DictTypeListVo from(DictType dictType) {
            return new DictTypeListVo(
                    dictType.getDictTypeId(),
                    dictType.getDictTypeName(),
                    dictType.getDictTypeCode(),
                    dictType.getOrderNum(),
                    dictType.getIsEnabled(),
                    dictType.getDictTypeDesc(),
                    dictType.getCreateTime()
            );
        }
    }

    public record DictDataListVo(
            String dictDataId,
            String dictTypeCode,
            String dictDataLabel,
            String dictDataValue,
            Integer orderNum,
            String isDefault,
            Integer isEnabled,
            String dictDataDesc,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime createTime
    ) {
        public static DictDataListVo from(DictData dictData) {
            return new DictDataListVo(
                    dictData.getDictDataId(),
                    dictData.getDictTypeCode(),
                    dictData.getDictDataLabel(),
                    dictData.getDictDataValue(),
                    dictData.getOrderNum(),
                    dictData.getIsDefault(),
                    dictData.getIsEnabled(),
                    dictData.getDictDataDesc(),
                    dictData.getCreateTime()
            );
        }
    }

    /** 字典运行时选项，供下拉/单选与标签翻译使用。 */
    public record DictOptionVo(
            String dictDataLabel,
            String dictDataValue,
            String isDefault,
            Integer orderNum
    ) {
        public static DictOptionVo from(DictData dictData) {
            return new DictOptionVo(
                    dictData.getDictDataLabel(),
                    dictData.getDictDataValue(),
                    dictData.getIsDefault(),
                    dictData.getOrderNum()
            );
        }
    }
}
