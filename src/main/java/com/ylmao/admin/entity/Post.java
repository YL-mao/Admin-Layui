package com.ylmao.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.dto.PostDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_post")
@EqualsAndHashCode(callSuper = false)
public final class Post {
    @TableId(type = IdType.ASSIGN_ID)
    private String postId;
    private String postCode;
    private String postName;
    private Integer postType;
    private Integer orderNum;
    private Integer isEnabled;
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

    public Post(PostDto.PostInsert postInsert) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.postCode = postInsert.postCode();
        this.postName = postInsert.postName();
        this.postType = postInsert.postType();
        this.orderNum = postInsert.orderNum();
        this.isEnabled = postInsert.isEnabled();
    }

    public Post(PostDto.PostUpdate postUpdate) {
        // DTO 只承接页面提交字段，PO 负责映射数据库字段。
        this.postId = postUpdate.postId();
        this.postCode = postUpdate.postCode();
        this.postName = postUpdate.postName();
        this.postType = postUpdate.postType();
        this.orderNum = postUpdate.orderNum();
        this.isEnabled = postUpdate.isEnabled();
    }
}
