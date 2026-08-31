package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.RoleDto;
import com.ylmao.admin.entity.PermRole;
import com.ylmao.admin.entity.Role;
import com.ylmao.admin.entity.RoleUser;
import com.ylmao.admin.mapper.PermRoleMapper;
import com.ylmao.admin.mapper.RoleMapper;
import com.ylmao.admin.mapper.RoleUserMapper;
import com.ylmao.admin.vo.RoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final RoleUserMapper roleUserMapper;
    private final PermRoleMapper permRoleMapper;

    public List<RoleVo.RoleOptionVo> listOptions() {
        LambdaQueryWrapper<Role> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.orderByAsc(Role::getOrderNum);
        return roleMapper.selectList(roleQueryWrapper).stream().map(RoleVo.RoleOptionVo::from).toList();
    }

    /**
     * 获取所有权限 并且增加是否有权限字段
     *
     * @return
     */
    public List<Role> getUserIsRole(String userid) {
        // 查询当前用户已绑定的角色 ID，用于回显勾选
        Set<String> myRoleIds = roleUserMapper.selectList(
                        new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getUserId, userid))
                .stream()
                .map(RoleUser::getRoleId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toCollection(HashSet::new));

        List<Role> roleList = roleMapper.selectList(new LambdaQueryWrapper<>());
        for (Role item : roleList) {
            if (myRoleIds.contains(item.getRoleId())) {
                item.setIsCheck(true);
            }
        }
        return roleList;
    }

    public Role selectById(String roleID){
        return roleMapper.selectById(roleID);
    }

    public IPage<RoleVo.RoleListVo> selectRolePageList(PageQuery pageQuery, String roleName){
        Page<Role> rolePage=pageQuery.toMpPage();
        LambdaQueryWrapper<Role> roleQueryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(roleName)){
            roleQueryWrapper.like(Role::getRoleName,roleName);
        }
        IPage<Role> roleIPage = roleMapper.selectPage(rolePage,roleQueryWrapper);
        Page<RoleVo.RoleListVo> roleVoPage = new Page<>(roleIPage.getCurrent(), roleIPage.getSize(), roleIPage.getTotal());
        // 将持久化对象转换成列表 VO，避免 Controller 出口暴露 PO。
        roleVoPage.setRecords(roleIPage.getRecords().stream()
                .map(RoleVo.RoleListVo::from)
                .collect(Collectors.toList()));
        return roleVoPage;
    }

    @Transactional
    public void insert(RoleDto.RoleInsert roleInsert){
        // 新增角色校验名称和编码唯一性。
        if (checkRoleNameUnique(roleInsert.roleName()) != null) {
            throw new BusinessException("角色名称已存在");
        }
        if (checkRoleCodeUnique(roleInsert.roleCode()) != null) {
            throw new BusinessException("角色编码已存在");
        }
        Role role = new Role(roleInsert);
        int rows = roleMapper.insert(role);
        if (rows <= 0) {
            throw new BusinessException("新增角色失败");
        }
    }

    @Transactional
    public void updateById(RoleDto.RoleUpdate roleUpdate){
        // 修改角色校验名称和编码不能被其它角色占用。
        Role oldRole = checkRoleNameUnique(roleUpdate.roleName());
        if (oldRole != null && !oldRole.getRoleId().equals(roleUpdate.roleId())) {
            throw new BusinessException("角色名称已存在");
        }
        Role oldRoleCode = checkRoleCodeUnique(roleUpdate.roleCode());
        if (oldRoleCode != null && !oldRoleCode.getRoleId().equals(roleUpdate.roleId())) {
            throw new BusinessException("角色编码已存在");
        }
        Role role = new Role(roleUpdate);
        int rows = roleMapper.updateById(role);
        if (rows <= 0) {
            throw new BusinessException("角色不存在或修改失败");
        }
    }

    @Transactional
    public void deleteById(String roleIds){
        if (StrUtil.isBlank(roleIds)) {
            throw new BusinessException("请选择要删除的角色");
        }
        List<String> idList= StrUtil.splitTrim(roleIds, ',');
        Long userCount = roleUserMapper.selectCount(new LambdaQueryWrapper<RoleUser>().in(RoleUser::getRoleId, idList));
        if (userCount != null && userCount > 0) {
            throw new BusinessException("角色已分配给用户，不能删除");
        }
        // 角色权限属于角色自身配置，删除角色时同步清理授权关系。
        permRoleMapper.delete(new LambdaQueryWrapper<PermRole>().in(PermRole::getRoleId, idList));
        LambdaQueryWrapper<Role> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.in(Role::getRoleId,idList);
        int rows = roleMapper.delete(roleQueryWrapper);
        if (rows <= 0) {
            throw new BusinessException("角色不存在或删除失败");
        }
    }

    @Transactional
    public void updateEnabled(RoleDto.UpdateEnabled updateEnabled) {
        Role oldRole = roleMapper.selectById(updateEnabled.roleId());
        if (oldRole == null) {
            throw new BusinessException("角色不存在");
        }
        oldRole.setIsEnabled(updateEnabled.isEnabled());
        int rows = roleMapper.updateById(oldRole);
        if (rows <= 0) {
            throw new BusinessException("修改角色状态失败");
        }
    }

    public Role checkRoleNameUnique(String roleName){
        if (StrUtil.isBlank(roleName)) {
            return null;
        }
        LambdaQueryWrapper<Role> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.eq(Role::getRoleName,roleName);
        return roleMapper.selectOne(roleQueryWrapper);
    }

    public Role checkRoleCodeUnique(String roleCode) {
        if (StrUtil.isBlank(roleCode)) {
            return null;
        }
        LambdaQueryWrapper<Role> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.eq(Role::getRoleCode, roleCode);
        return roleMapper.selectOne(roleQueryWrapper);
    }
}
