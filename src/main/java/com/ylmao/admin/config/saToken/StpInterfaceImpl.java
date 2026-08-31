package com.ylmao.admin.config.saToken;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.ylmao.admin.entity.Perm;
import com.ylmao.admin.mapper.PermMapper;
import com.ylmao.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    /** 用户 Session 中缓存的权限码列表 */
    public static final String PERM_LIST = "Perm_List";

    private final RoleMapper roleMapper;
    private final PermMapper permMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> roleIds = getRoleList(loginId, loginType);
        if (CollectionUtils.isEmpty(roleIds)) {
            return List.of();
        }
        // 多角色合并查权限码，结果缓存在用户 Session，避免每次鉴权都打库。
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        return session.get(PERM_LIST, () -> loadPermCodes(roleIds));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        return session.get("Role_List", () -> roleMapper.queryRoleByUserId(String.valueOf(loginId)));
    }

    /** 从合并查询结果提取去重后的 perm_code，目录节点空串不入库到鉴权列表。 */
    private List<String> loadPermCodes(List<String> roleIds) {
        List<Perm> perms = permMapper.selectPermsByRoleIds(roleIds);
        if (CollectionUtils.isEmpty(perms)) {
            return List.of();
        }
        Set<String> permCodes = new LinkedHashSet<>();
        for (Perm perm : perms) {
            if (StrUtil.isNotBlank(perm.getPermCode())) {
                permCodes.add(perm.getPermCode());
            }
        }
        return new ArrayList<>(permCodes);
    }
}
