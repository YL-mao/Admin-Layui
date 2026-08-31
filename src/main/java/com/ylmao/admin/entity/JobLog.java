package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_job_log")
@EqualsAndHashCode(callSuper = false)
public final class JobLog {

    @TableId(type = IdType.ASSIGN_ID)
    private String jobLogId;
    private String jobId;
    private String jobCode;
    /** 触发方式：1-定时，2-手动。 */
    private Integer triggerType;
    /** 执行结果：SUCCESS / FAILED / SKIPPED。 */
    private String runStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private Long costMs;
    private String message;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
