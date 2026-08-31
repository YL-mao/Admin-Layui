package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostVo {

    public record PostListVo(String postId, String postCode, String postName, Integer postType, String postTypeName,
                             Integer orderNum, Integer isEnabled,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createTime) {

        public static PostListVo from(Post post, String postTypeName) {
            return new PostListVo(post.getPostId(), post.getPostCode(), post.getPostName(),
                    post.getPostType(), postTypeName, post.getOrderNum(), post.getIsEnabled(), post.getCreateTime());
        }
    }

    /** 岗位下拉选项，供用户 listView 使用。 */
    public record PostOptionVo(String postId, String postName) {

        public static PostOptionVo from(Post post) {
            return new PostOptionVo(post.getPostId(), post.getPostName());
        }
    }
}
