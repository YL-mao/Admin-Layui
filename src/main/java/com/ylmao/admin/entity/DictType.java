package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.DictTypeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_dict_type")
@EqualsAndHashCode(callSuper = false)
public final class DictType {

    @TableId(type = IdType.ASSIGN_ID)
    private String dictTypeId;
    private String dictTypeName;
    private String dictTypeCode;
    private Integer orderNum;
    private Integer isEnabled;
    private String dictTypeDesc;
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

    public DictType(DictTypeDto.DictTypeInsert dictTypeInsert) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.dictTypeName = dictTypeInsert.dictTypeName();
        this.dictTypeCode = dictTypeInsert.dictTypeCode();
        this.orderNum = dictTypeInsert.orderNum();
        this.isEnabled = dictTypeInsert.isEnabled();
        this.dictTypeDesc = dictTypeInsert.dictTypeDesc();
    }

    public DictType(DictTypeDto.DictTypeUpdate dictTypeUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.dictTypeId = dictTypeUpdate.dictTypeId();
        this.dictTypeName = dictTypeUpdate.dictTypeName();
        this.dictTypeCode = dictTypeUpdate.dictTypeCode();
        this.orderNum = dictTypeUpdate.orderNum();
        this.isEnabled = dictTypeUpdate.isEnabled();
        this.dictTypeDesc = dictTypeUpdate.dictTypeDesc();
    }
}
