package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.DictDataDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_dict_data")
@EqualsAndHashCode(callSuper = false)
public final class DictData {

    @TableId(type = IdType.ASSIGN_ID)
    private String dictDataId;
    private String dictTypeCode;
    private String dictDataLabel;
    private String dictDataValue;
    private Integer orderNum;
    private String isDefault;
    private Integer isEnabled;
    private String dictDataDesc;
    /** 审计字段由 MyBatis-Plus 自动填充创建人。 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 审计字段由 MyBatis-Plus 自动填充更新人。 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDel;

    public DictData(DictDataDto.DictDataInsert dictDataInsert) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.dictTypeCode = dictDataInsert.dictTypeCode();
        this.dictDataLabel = dictDataInsert.dictDataLabel();
        this.dictDataValue = dictDataInsert.dictDataValue();
        this.orderNum = dictDataInsert.orderNum();
        this.isEnabled = dictDataInsert.isEnabled();
        this.dictDataDesc = dictDataInsert.dictDataDesc();
    }

    public DictData(DictDataDto.DictDataUpdate dictDataUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.dictDataId = dictDataUpdate.dictDataId();
        this.dictTypeCode = dictDataUpdate.dictTypeCode();
        this.dictDataLabel = dictDataUpdate.dictDataLabel();
        this.dictDataValue = dictDataUpdate.dictDataValue();
        this.orderNum = dictDataUpdate.orderNum();
        this.isEnabled = dictDataUpdate.isEnabled();
        this.dictDataDesc = dictDataUpdate.dictDataDesc();
    }
}
