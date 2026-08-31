package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.config.saToken.StpInterfaceImpl;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.PermDto;
import com.ylmao.admin.entity.Perm;
import com.ylmao.admin.entity.PermRole;
import com.ylmao.admin.entity.RoleUser;
import com.ylmao.admin.mapper.PermMapper;
import com.ylmao.admin.mapper.PermRoleMapper;
import com.ylmao.admin.mapper.RoleUserMapper;
import com.ylmao.admin.model.Menu;
import com.ylmao.admin.vo.PermVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermService {

    private final PermMapper permMapper;
    private final PermRoleMapper permRoleMapper;
    private final RoleUserMapper roleUserMapper;

    public List<Menu> getUserPermMenu(String userId) {
        LambdaQueryWrapper<RoleUser> roleUserQueryWrapper = new LambdaQueryWrapper<>();
        roleUserQueryWrapper.eq(RoleUser::getUserId, userId);
        List<RoleUser> roleUserList = roleUserMapper.selectList(roleUserQueryWrapper);
        List<String> roleIds = new ArrayList<>();
        for (RoleUser item : roleUserList) {
            roleIds.add(item.getRoleId());
        }
        List<Perm> permList = getPermByRole(roleIds);
        return getMenuList(permList, "0");
    }

    /** 按角色 ID 列表一次查出启用权限，供侧边栏菜单组装使用。 */
    public List<Perm> getPermByRole(List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return permMapper.selectPermsByRoleIds(roleIds);
    }

    public List<Menu> getMenuList(List<Perm> permList, String parentId) {
        List<Menu> menuList = new ArrayList<>();
        for (Perm item : permList) {
            if (item.getParentId().equals(parentId)) {
                List<Menu> childMenu = getMenuList(permList, item.getPermId());
                Menu menu = new Menu();
                menu.setId(item.getPermId());
                menu.setParentId(item.getParentId());
                menu.setTitle(item.getPermName());
                menu.setType(item.getPermType());
                menu.setIsBlank(normalizeIsBlank(item.getIsBlank()));
                menu.setIcon(item.getPermIcon());
                menu.setHref(item.getPermUrl());
                if (!childMenu.isEmpty()) {
                    menu.setChildren(childMenu);
                }
                menuList.add(menu);
            }
        }
        return menuList;
    }

    public List<Perm> queryPermCheckArrByRoleId(String roleId) {
        return permMapper.queryPermCheckArrByRoleId(roleId);
    }

    /** 角色授权树接口出口，转换为 VO 避免暴露 PO。 */
    public List<PermVo.PermCheckVo> queryPermCheckVoByRoleId(String roleId) {
        return permMapper.queryPermCheckArrByRoleId(roleId).stream()
                .map(PermVo.PermCheckVo::from)
                .toList();
    }

    /** 上级权限下拉树，附带顶级节点供表单选择。 */
    public List<PermVo.PermParentVo> selectParentVoList() {
        List<PermVo.PermParentVo> list = selectList().stream()
                .map(PermVo.PermParentVo::from)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        list.add(new PermVo.PermParentVo("0", "-1", "顶级权限", null));
        return list;
    }

    @Transactional
    public void updateRolePerm(String roleId, String permIds) {
        LambdaQueryWrapper<PermRole> permRoleQueryWrapper = new LambdaQueryWrapper<>();
        permRoleQueryWrapper.eq(PermRole::getRoleId, roleId);
        permRoleMapper.delete(permRoleQueryWrapper);
        int rows = 0;
        // 清空授权时 permIds 为空，删除关联后即可视为保存成功。
        if (!StrUtil.isBlank(permIds)) {
            // 角色权限关系有唯一键，保存前保序去重，避免重复勾选导致唯一键异常。
            Set<String> permList = new LinkedHashSet<>(StrUtil.splitTrim(permIds, ','));
            int validPermCount = 0;

            for (String permId : permList) {
                if (StrUtil.isBlank(permId)) {
                    continue;
                }
                validPermCount++;
                PermRole permRole = new PermRole();
                permRole.setRoleId(roleId);
                permRole.setPermId(permId);
                rows = rows + permRoleMapper.insert(permRole);
            }
            if (validPermCount > 0 && rows <= 0) {
                throw new BusinessException("授权角色权限失败");
            }
        }
        // 授权保存后让在线用户下次鉴权重新加载权限码。
        clearPermCacheByRole(roleId);
    }

    /** 角色授权变更后，清理持有该角色用户的权限码缓存。 */
    private void clearPermCacheByRole(String roleId) {
        List<RoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getRoleId, roleId));
        for (RoleUser roleUser : roleUsers) {
            SaSession session = StpUtil.getSessionByLoginId(roleUser.getUserId(), false);
            if (session != null) {
                session.delete(StpInterfaceImpl.PERM_LIST);
            }
        }
    }

    public List<Perm> selectList() {
        LambdaQueryWrapper<Perm> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Perm::getOrderNum);
        return permMapper.selectList(wrapper);
    }

    public List<PermVo.PermListVo> selectList(PermDto.PermList permList) {
        LambdaQueryWrapper<Perm> wrapper = new LambdaQueryWrapper<>();
        if (permList != null) {
            if (StrUtil.isNotBlank(permList.permName())) {
                wrapper.like(Perm::getPermName, permList.permName());
            }
            if (StrUtil.isNotBlank(permList.permCode())) {
                wrapper.like(Perm::getPermCode, permList.permCode());
            }
            if (StrUtil.isNotBlank(permList.permUrl())) {
                wrapper.like(Perm::getPermUrl, permList.permUrl());
            }
        }
        wrapper.orderByAsc(Perm::getOrderNum);
        return permMapper.selectList(wrapper).stream().map(PermVo.PermListVo::from).toList();
    }

    public Perm selectById(String permId) {
        return permMapper.selectById(permId);
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的权限");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        Long roleCount = permRoleMapper.selectCount(new LambdaQueryWrapper<PermRole>().in(PermRole::getPermId, idList));
        if (roleCount != null && roleCount > 0) {
            throw new BusinessException("权限已分配给角色，不能删除");
        }
        Long childCount = permMapper.selectCount(new LambdaQueryWrapper<Perm>().in(Perm::getParentId, idList));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("权限包含下级节点，不能删除");
        }
        int rows = permMapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("权限不存在或删除失败");
        }
    }

    @Transactional
    public void insert(PermDto.PermInsert permInsert) {
        // 菜单/按钮必须有权限标识；目录允许空标识。
        validatePermCodeByType(permInsert.permType(), permInsert.permCode());
        // 同级权限名称与权限标识不能重复。
        checkPermUnique(permInsert.parentId(), permInsert.permName(), permInsert.permCode(), null);
        Perm perm = new Perm(permInsert);
        fillPermPath(perm);
        perm.setIsBlank(normalizeIsBlank(perm.getIsBlank()));
        int rows = permMapper.insert(perm);
        if (rows <= 0) {
            throw new BusinessException("新增权限失败");
        }
    }

    @Transactional
    public void updateById(PermDto.PermUpdate permUpdate) {
        // 修改权限校验层级关系，并约束同级名称与标识唯一。
        Perm oldPerm = permMapper.selectById(permUpdate.permId());
        if (oldPerm == null) {
            throw new BusinessException("权限不存在");
        }
        if (permUpdate.parentId().equals(permUpdate.permId()) || isChildPerm(permUpdate.permId(), permUpdate.parentId())) {
            throw new BusinessException("上级权限不能选择自己或下级权限");
        }
        validatePermCodeByType(permUpdate.permType(), permUpdate.permCode());
        checkPermUnique(permUpdate.parentId(), permUpdate.permName(), permUpdate.permCode(), permUpdate.permId());
        Perm perm = new Perm(permUpdate);
        fillPermPath(perm);
        perm.setIsBlank(normalizeIsBlank(perm.getIsBlank()));
        int rows = permMapper.updateById(perm);
        if (rows <= 0) {
            throw new BusinessException("修改权限失败");
        }
    }

    public Perm checkPermNameUnique(String parentId, String permName) {
        LambdaQueryWrapper<Perm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Perm::getParentId, normalizeParentId(parentId));
        wrapper.eq(Perm::getPermName, permName);
        return permMapper.selectOne(wrapper);
    }

    public Perm checkPermCodeUnique(String permCode) {
        if (StrUtil.isBlank(permCode)) {
            return null;
        }
        LambdaQueryWrapper<Perm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Perm::getPermCode, permCode);
        return permMapper.selectOne(wrapper);
    }

    @Transactional
    public void updatePermEnabled(PermDto.UpdateEnabled updateEnabled) {
        Perm oldPerm = permMapper.selectById(updateEnabled.permId());
        if (oldPerm == null) {
            throw new BusinessException("权限不存在");
        }
        oldPerm.setIsEnabled(updateEnabled.isEnabled());
        int rows = permMapper.updateById(oldPerm);
        if (rows <= 0) {
            throw new BusinessException("修改权限状态失败");
        }
    }

    private void checkPermUnique(String parentId, String permName, String permCode, String excludePermId) {
        Perm oldNamePerm = checkPermNameUnique(parentId, permName);
        if (oldNamePerm != null && (excludePermId == null || !oldNamePerm.getPermId().equals(excludePermId))) {
            throw new BusinessException("同级权限名称已存在");
        }
        Perm oldCodePerm = checkPermCodeUnique(permCode);
        if (oldCodePerm != null && (excludePermId == null || !oldCodePerm.getPermId().equals(excludePermId))) {
            throw new BusinessException("权限标识已存在");
        }
    }

    /** 目录可空码；菜单与按钮必须有非空权限标识。 */
    private void validatePermCodeByType(Integer permType, String permCode) {
        if (permType != null && permType != 0 && StrUtil.isBlank(permCode)) {
            throw new BusinessException("菜单或按钮权限标识不能为空");
        }
    }

    private void fillPermPath(Perm perm) {
        String parentId = normalizeParentId(perm.getParentId());
        perm.setParentId(parentId);
        if ("0".equals(parentId)) {
            perm.setPermPath("0");
            return;
        }
        Perm parent = permMapper.selectById(parentId);
        if (parent == null) {
            throw new BusinessException("上级权限不存在");
        }
        perm.setPermPath(parent.getPermPath() + "," + parentId);
    }

    private boolean isChildPerm(String permId, String parentId) {
        if (StrUtil.isBlank(parentId) || "0".equals(parentId)) {
            return false;
        }
        Perm parent = permMapper.selectById(parentId);
        return parent != null && parent.getPermPath() != null
                && (parent.getPermPath() + ",").contains(permId + ",");
    }

    private String normalizeParentId(String parentId) {
        return StrUtil.isBlank(parentId) ? "0" : parentId;
    }

    private Integer normalizeIsBlank(Integer isBlank) {
        return isBlank != null && isBlank == 1 ? 1 : 0;
    }
}
