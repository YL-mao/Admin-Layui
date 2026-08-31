package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_notice_user")
@EqualsAndHashCode(callSuper = false)
public final class NoticeUser {

    @TableId(value = "user_notice_id", type = IdType.ASSIGN_ID)
    private String noticeUserId;
    private String userId;
    private String noticeId;
    /** Java 仍使用 readState 语义，数据库字段用 is_read 表示是否已读。 */
    @TableField("is_read")
    private Integer readState;
    private LocalDateTime readTime;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDel;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
