package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.ConfigDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_config")
@EqualsAndHashCode(callSuper = false)
public final class Config {

    @TableId(type = IdType.ASSIGN_ID)
    private String configId;
    private String configName;
    private String configCode;
    private String configValue;
    private String configGroup;
    private String valueType;
    private Integer isBuiltin;
    private Integer isEnabled;
    private Integer orderNum;
    private String configDesc;
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

    public Config(ConfigDto.ConfigInsert configInsert) {
        // DTO 只承接页面提交字段，PO 负责映射系统配置表字段。
        this.configName = configInsert.configName();
        this.configCode = configInsert.configCode();
        this.configValue = configInsert.configValue();
        this.configGroup = configInsert.configGroup();
        this.valueType = configInsert.valueType();
        this.isBuiltin = configInsert.isBuiltin();
        this.isEnabled = configInsert.isEnabled();
        this.orderNum = configInsert.orderNum();
        this.configDesc = configInsert.configDesc();
    }

    public Config(ConfigDto.ConfigUpdate configUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射系统配置表字段。
        this.configId = configUpdate.configId();
        this.configName = configUpdate.configName();
        this.configCode = configUpdate.configCode();
        this.configValue = configUpdate.configValue();
        this.configGroup = configUpdate.configGroup();
        this.valueType = configUpdate.valueType();
        this.isBuiltin = configUpdate.isBuiltin();
        this.isEnabled = configUpdate.isEnabled();
        this.orderNum = configUpdate.orderNum();
        this.configDesc = configUpdate.configDesc();
    }
}
