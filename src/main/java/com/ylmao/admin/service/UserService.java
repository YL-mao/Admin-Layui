package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.config.saToken.StpInterfaceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.constant.DictTypeCode;
import com.ylmao.admin.dto.OnlineDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.UserDto;
import com.ylmao.admin.vo.UserVo;
import com.ylmao.admin.entity.*;
import com.ylmao.admin.mapper.*;
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RoleUserMapper roleUserMapper;
    private final DeptMapper deptMapper;
    private final RoleMapper roleMapper;
    private final PostMapper postMapper;
    private final PermMapper permMapper;
    private final DictRuntimeService dictRuntimeService;
    private final PasswordService passwordService;
    private final PasswordPolicyService passwordPolicyService;
    private final LoginFailService loginFailService;
    private final OnlineService onlineService;

    public IPage<UserVo.UserListVo> selectPage(PageQuery pageQuery, UserDto.UserList query) {
        Page<User> pageUser = pageQuery.toMpPage();
        pageUser.addOrder(OrderItem.desc("create_time"));
        userMapper.selectPage(pageUser, buildUserListWrapper(query));
        Page<UserVo.UserListVo> voPage = new Page<>(pageUser.getCurrent(), pageUser.getSize(), pageUser.getTotal());
        voPage.setRecords(toUserListVos(pageUser.getRecords()));
        return voPage;
    }

    /** 按列表相同筛选条件导出全部用户，不分页。 */
    public void exportUserList(UserDto.UserList query, HttpServletResponse response) throws IOException {
        List<UserVo.UserExportVo> rows = toUserListVos(userMapper.selectList(
                buildUserListWrapper(query).orderByDesc(User::getCreateTime))).stream()
                .map(UserVo.UserExportVo::from)
                .toList();
        String fileName = "用户列表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + encodedName);
        EasyExcel.write(response.getOutputStream(), UserVo.UserExportVo.class)
                .sheet("用户列表")
                .doWrite(rows);
    }

    private LambdaQueryWrapper<User> buildUserListWrapper(UserDto.UserList query) {
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StrUtil.isNotBlank(query.userAccount())) {
                userQueryWrapper.like(User::getUserAccount, query.userAccount());
            }
            if (query.isEnabled() != null) {
                userQueryWrapper.eq(User::getIsEnabled, query.isEnabled());
            }
            if (query.isLock() != null) {
                userQueryWrapper.eq(User::getIsLock, query.isLock());
            }
        }
        return userQueryWrapper;
    }

    private List<UserVo.UserListVo> toUserListVos(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return List.of();
        }
        Set<String> userIds = userList.stream()
                .map(User::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<String, String> userRoleIdsMap = new HashMap<>();
        Set<String> roleIds = new HashSet<>();
        if (!userIds.isEmpty()) {
            List<RoleUser> roleUserList = roleUserMapper.selectList(
                    new LambdaQueryWrapper<RoleUser>().in(RoleUser::getUserId, userIds));
            for (RoleUser roleUser : roleUserList) {
                userRoleIdsMap.merge(roleUser.getUserId(), roleUser.getRoleId(), (existing, added) -> existing + "," + added);
                if (StrUtil.isNotBlank(roleUser.getRoleId())) {
                    roleIds.add(roleUser.getRoleId());
                }
            }
        }
        Set<String> deptIds = userList.stream()
                .map(User::getDeptId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        Set<String> postIds = userList.stream()
                .map(User::getPostId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        Map<String, String> deptMap = deptIds.isEmpty() ? Map.of()
                : deptMapper.selectList(new LambdaQueryWrapper<Dept>().in(Dept::getDeptId, deptIds)).stream()
                .collect(Collectors.toMap(Dept::getDeptId, Dept::getDeptName, (a, b) -> a));
        Map<String, String> postMap = postIds.isEmpty() ? Map.of()
                : postMapper.selectList(new LambdaQueryWrapper<Post>().in(Post::getPostId, postIds)).stream()
                .collect(Collectors.toMap(Post::getPostId, Post::getPostName, (a, b) -> a));
        Map<String, String> roleMap = roleIds.isEmpty() ? Map.of()
                : roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getRoleId, roleIds)).stream()
                .collect(Collectors.toMap(Role::getRoleId, Role::getRoleName, (a, b) -> a));
        Set<String> onlineUserIds = onlineService.listOnlineUserIds();
        return userList.stream()
                .map(user -> UserVo.UserListVo.from(user, deptMap, postMap, roleMap, userRoleIdsMap,
                        dictRuntimeService.getLabel(DictTypeCode.SYS_USER_SEX, user.getUserSex()),
                        onlineUserIds.contains(user.getUserId())))
                .toList();
    }



    @Transactional
    public void insertUserRoles(UserDto.UserInsert userInsert) {
        // 新增用户先校验关联数据合法性，再校验登录账号唯一性。
        validateUserRelations(userInsert.userSex(), userInsert.deptId(), userInsert.postId(), userInsert.roleIds());
        if (getUserByAccount(userInsert.userAccount()) != null) {
            throw new BusinessException("登录账号已存在");
        }
        User user = new User(userInsert);
        user.setIsEnabled(0);
        // 新增默认未锁定，与启停独立。
        user.setIsLock(0);
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException("新增用户失败");
        }
        syncUserRoles(user.getUserId(), userInsert.roleIds());
    }


    @Transactional
    public void userUpdate(UserDto.UserUpdate userUpdate) {
        // 修改用户先校验关联数据合法性，再校验账号不能被其它用户占用。
        validateUserRelations(userUpdate.userSex(), userUpdate.deptId(), userUpdate.postId(), userUpdate.roleIds());
        User oldUser = getUserByAccount(userUpdate.userAccount());
        if (oldUser != null && !oldUser.getUserId().equals(userUpdate.userId())) {
            throw new BusinessException("登录账号已存在");
        }
        User user = new User(userUpdate);
        int rows = userMapper.updateById(user);
        if (rows <= 0) {
            throw new BusinessException("用户不存在或修改失败");
        }
        syncUserRoles(user.getUserId(), userUpdate.roleIds());
    }

    private void syncUserRoles(String userId, String roleIds) {
        // 角色中间表以本次提交的角色集合为准，先清空再重建。
        roleUserMapper.delete(new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getUserId, userId));
        if (StrUtil.isBlank(roleIds)) {
            return;
        }
        // 关系表唯一键约束用户和角色只绑定一次，入库前保序去重。
        Set<String> uniqueRoleIds = new LinkedHashSet<>(StrUtil.splitTrim(roleIds, ','));
        for (String roleId : uniqueRoleIds) {
            if (StrUtil.isBlank(roleId)) {
                continue;
            }
            RoleUser roleUser = new RoleUser();
            roleUser.setUserId(userId);
            roleUser.setRoleId(roleId);
            int rows = roleUserMapper.insert(roleUser);
            if (rows <= 0) {
                throw new BusinessException("用户角色保存失败");
            }
        }
    }

    public User selectById(String userId) {
        return userMapper.selectById(userId);
    }

    @Transactional
    public void updatePwd(UserDto.UpdatePwd updatePwd){
        // 管理员重置密码走固定高复杂度策略。
        passwordPolicyService.validateNewPassword(updatePwd.userPassword());
        User oldUser=userMapper.selectById(updatePwd.userId());
        if (oldUser == null) {
            throw new BusinessException("用户不存在");
        }
        oldUser.setUserPassword(passwordService.encode(updatePwd.userPassword()));
        int rows = userMapper.updateById(oldUser);
        if (rows <= 0) {
            throw new BusinessException("修改用户密码失败");
        }
        // 重置密码后踢掉该用户会话，强制用新密码重新登录。
        StpUtil.logout(updatePwd.userId());
    }

    @Transactional
    public void updateUserEnabled(UserDto.UpdateEnabled updateEnabled) {
        User oldUser = userMapper.selectById(updateEnabled.userId());
        if (oldUser == null) {
            throw new BusinessException("用户不存在");
        }
        if (updateEnabled.isEnabled() != null && updateEnabled.isEnabled() == 0
                && StrUtil.equals(updateEnabled.userId(), SaTokenUtil.getUserId())) {
            throw new BusinessException("不能停用当前登录用户");
        }
        if (updateEnabled.isEnabled() != null && updateEnabled.isEnabled() == 1
                && StrUtil.isBlank(oldUser.getUserPassword())) {
            throw new BusinessException("请先设置密码后再启用");
        }
        oldUser.setIsEnabled(updateEnabled.isEnabled());
        int rows = userMapper.updateById(oldUser);
        if (rows <= 0) {
            throw new BusinessException("修改用户状态失败");
        }
        // 停用后踢掉该用户全部登录会话，避免已登录账号继续访问。
        if (updateEnabled.isEnabled() != null && updateEnabled.isEnabled() == 0) {
            StpUtil.logout(updateEnabled.userId());
        }
    }

    /** 管理端解锁：清 is_lock、清单机失败计数，并踢掉该用户全部登录会话。 */
    @Transactional
    public void unlockUser(String userId) {
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException("用户不存在");
        }
        oldUser.setIsLock(0);
        int rows = userMapper.updateById(oldUser);
        if (rows <= 0) {
            throw new BusinessException("解锁用户失败");
        }
        loginFailService.clearAccountFail(oldUser.getUserAccount());
        // 解锁后强制重新登录，避免旧会话继续有效。
        StpUtil.logout(userId);
    }

    /** 用户列表在线开关仅用于会话治理：在线时确认后按用户 ID 踢全部会话。 */
    public void kickUserSessions(String userId) {
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException("用户不存在");
        }
        if (StrUtil.equals(userId, SaTokenUtil.getUserId())) {
            throw new BusinessException("不能踢出当前登录用户的全部会话");
        }
        onlineService.kickByUserId(new OnlineDto.OnlineKickUser(userId));
    }


    @Transactional
    public void deleteRoleUser(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的用户");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        int rows = userMapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("用户不存在或删除失败");
        }
        // 删除用户成功后清理角色中间表，避免残留无效关系。
        roleUserMapper.delete(new LambdaQueryWrapper<RoleUser>().in(RoleUser::getUserId, idList));
        // 删除后踢掉会话，避免已登录 token 继续访问。
        for (String userId : idList) {
            if (StrUtil.isNotBlank(userId)) {
                StpUtil.logout(userId);
            }
        }
    }

    public User getUserByAccount(String userAccount) {
        if (StrUtil.isBlank(userAccount)) {
            return null;
        }
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getUserAccount, userAccount);
        return userMapper.selectOne(userQueryWrapper);
    }

    private void validateUserRelations(String userSex, String deptId, String postId, String roleIds) {
        // 用户基础资料只强制账号和姓名；部门、岗位、角色允许为空，但提交时必须存在。
        if (StrUtil.isNotBlank(userSex)) {
            dictRuntimeService.validateValue(DictTypeCode.SYS_USER_SEX, userSex, "性别");
        }
        if (StrUtil.isNotBlank(deptId) && deptMapper.selectById(deptId) == null) {
            throw new BusinessException("部门不存在");
        }
        if (StrUtil.isNotBlank(postId) && postMapper.selectById(postId) == null) {
            throw new BusinessException("岗位不存在");
        }
        validateRoleIds(roleIds);
    }

    private void validateRoleIds(String roleIds) {
        if (StrUtil.isBlank(roleIds)) {
            return;
        }
        for (String roleId : StrUtil.splitTrim(roleIds, ',')) {
            if (StrUtil.isBlank(roleId) || roleMapper.selectById(roleId) == null) {
                throw new BusinessException("角色不存在");
            }
        }
    }

    /** 公告指定用户检索：支持用户 ID、账号、姓名模糊匹配。 */
    public List<UserVo.UserOptionVo> searchForNotice(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return List.of();
        }
        String trimmed = keyword.trim();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIsEnabled, 1)
                .and(w -> w.eq(User::getUserId, trimmed)
                        .or()
                        .like(User::getUserAccount, trimmed)
                        .or()
                        .like(User::getUserName, trimmed))
                .orderByDesc(User::getCreateTime)
                .last("limit 20");
        return userMapper.selectList(wrapper).stream()
                .map(UserVo.UserOptionVo::from)
                .toList();
    }

    /** 用户最终权限：多角色并集权限树 + 角色列表。 */
    public UserVo.UserPermDetailVo getUserPermDetail(String userId) {
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户ID不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        List<String> roleIds = roleMapper.queryRoleByUserId(userId);
        List<UserVo.UserPermDetailVo.RoleItem> roles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<Role> roleList = roleMapper.selectByIds(roleIds);
            Map<String, String> roleNameMap = roleList.stream()
                    .collect(Collectors.toMap(Role::getRoleId, Role::getRoleName, (a, b) -> a));
            for (String roleId : roleIds) {
                String roleName = roleNameMap.get(roleId);
                if (StrUtil.isNotBlank(roleName)) {
                    roles.add(new UserVo.UserPermDetailVo.RoleItem(roleId, roleName));
                }
            }
        }
        List<UserVo.UserPermDetailVo.PermItem> perms = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<Perm> permList = permMapper.selectPermsByRoleIds(roleIds);
            for (Perm perm : permList) {
                perms.add(new UserVo.UserPermDetailVo.PermItem(
                        perm.getPermId(),
                        perm.getParentId(),
                        perm.getPermName()
                ));
            }
        }
        return new UserVo.UserPermDetailVo(
                user.getUserId(),
                user.getUserName(),
                user.getUserAccount(),
                roles,
                perms
        );
    }

}
