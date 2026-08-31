package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.NoticeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_notice")
@EqualsAndHashCode(callSuper = false)
public final class Notice {

    @TableId(type = IdType.ASSIGN_ID)
    private String noticeId;
    private String noticeTitle;
    private String noticeContent;
    private Integer noticeType;
    private Integer receiverType;
    /** 接收目标 ID，逗号分隔：角色 / 部门 / 用户。草稿期仅存主表，发布时再写入中间表。 */
    private String receiverIds;
    private String noticeDesc;
    private Integer isSend;
    private Integer orderNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
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

    public Notice(NoticeDto.NoticeInsert noticeInsert) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.noticeTitle = noticeInsert.noticeTitle();
        this.noticeContent = noticeInsert.noticeContent();
        this.noticeType = noticeInsert.noticeType();
        this.receiverType = noticeInsert.receiverType();
        this.receiverIds = noticeInsert.receiverIds();
        this.noticeDesc = noticeInsert.noticeDesc();
        this.isSend = noticeInsert.isSend();
        this.orderNum = noticeInsert.orderNum();
        this.expireTime = noticeInsert.expireTime();
    }

    public Notice(NoticeDto.NoticeUpdate noticeUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.noticeId = noticeUpdate.noticeId();
        this.noticeTitle = noticeUpdate.noticeTitle();
        this.noticeContent = noticeUpdate.noticeContent();
        this.noticeType = noticeUpdate.noticeType();
        this.receiverType = noticeUpdate.receiverType();
        this.receiverIds = noticeUpdate.receiverIds();
        this.noticeDesc = noticeUpdate.noticeDesc();
        this.isSend = noticeUpdate.isSend();
        this.orderNum = noticeUpdate.orderNum();
        this.expireTime = noticeUpdate.expireTime();
    }
}
