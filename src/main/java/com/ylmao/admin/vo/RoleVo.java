package com.ylmao.admin.vo;

import com.ylmao.admin.entity.Role;
import lombok.Data;

@Data
public class RoleVo {

    public record RoleListVo(String roleId, String roleName, String roleCode, Integer orderNum, Integer isEnabled) {

        public static RoleListVo from(Role role) {
            // 角色列表只输出页面需要展示和编辑的字段，不暴露持久化对象。
            return new RoleListVo(
                    role.getRoleId(),
                    role.getRoleName(),
                    role.getRoleCode(),
                    role.getOrderNum(),
                    role.getIsEnabled()
            );
        }
    }

    /** 角色下拉选项，供用户/公告 listView 使用。 */
    public record RoleOptionVo(String roleId, String roleName) {

        public static RoleOptionVo from(Role role) {
            return new RoleOptionVo(role.getRoleId(), role.getRoleName());
        }
    }
}
