package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.Perm;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermVo {

    public record PermListVo(String permId, String parentId, String permPath, String permName, String permDesc,
                             String permUrl, Integer isBlank, String permCode, Integer permType, String permIcon,
                             Integer orderNum, Integer isEnabled,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createTime) {

        public static PermListVo from(Perm perm) {
            return new PermListVo(perm.getPermId(), perm.getParentId(), perm.getPermPath(), perm.getPermName(),
                    perm.getPermDesc(), perm.getPermUrl(), perm.getIsBlank(), perm.getPermCode(),
                    perm.getPermType(), perm.getPermIcon(), perm.getOrderNum(), perm.getIsEnabled(),
                    perm.getCreateTime());
        }
    }

    /** 角色授权树平铺节点，含勾选回显字段 checkArr。 */
    public record PermCheckVo(String permId, String parentId, String permName, String checkArr) {

        public static PermCheckVo from(Perm perm) {
            return new PermCheckVo(
                    perm.getPermId(),
                    perm.getParentId(),
                    perm.getPermName(),
                    perm.getCheckArr()
            );
        }
    }

    /** 上级权限下拉树节点。 */
    public record PermParentVo(String permId, String parentId, String permName, String permPath) {

        public static PermParentVo from(Perm perm) {
            return new PermParentVo(
                    perm.getPermId(),
                    perm.getParentId(),
                    perm.getPermName(),
                    perm.getPermPath()
            );
        }
    }
}
