package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.common.FilterCodes;
import com.ylmao.admin.dto.FilterDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_filter")
@EqualsAndHashCode(callSuper = false)
public final class Filter {

    @TableId(type = IdType.ASSIGN_ID)
    private String filterId;
    private String filterType;
    private String filterValue;
    private String filterSource;
    private String filterDesc;
    /** WHITE / BLACK */
    private String policyMode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
    private Integer isEnabled;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDel;

    public Filter(FilterDto.FilterInsert dto) {
        this.filterType = dto.filterType();
        this.filterValue = dto.filterValue();
        this.filterDesc = dto.filterDesc();
        this.policyMode = dto.policyMode();
        this.expireTime = dto.expireTime();
        this.isEnabled = dto.isEnabled();
        this.filterSource = FilterCodes.SOURCE_MANUAL;
    }

    public Filter(FilterDto.FilterUpdate dto) {
        this.filterId = dto.filterId();
        this.filterType = dto.filterType();
        this.filterValue = dto.filterValue();
        this.filterDesc = dto.filterDesc();
        this.policyMode = dto.policyMode();
        this.expireTime = dto.expireTime();
        this.isEnabled = dto.isEnabled();
    }
}
