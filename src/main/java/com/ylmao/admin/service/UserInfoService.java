package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.constant.DictTypeCode;
import com.ylmao.admin.dto.UserInfoDto;
import com.ylmao.admin.entity.Dept;
import com.ylmao.admin.entity.OperateLog;
import com.ylmao.admin.entity.Post;
import com.ylmao.admin.entity.Role;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.DeptMapper;
import com.ylmao.admin.mapper.OperateLogMapper;
import com.ylmao.admin.mapper.PostMapper;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.vo.UserInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private static final int LOGIN_LOG_LIMIT = 10;

    private final UserMapper userMapper;
    private final DictRuntimeService dictRuntimeService;
    private final DeptMapper deptMapper;
    private final PostMapper postMapper;
    private final OperateLogMapper operateLogMapper;
    private final RoleService roleService;
    private final PasswordService passwordService;
    private final PasswordPolicyService passwordPolicyService;

    public UserInfoVo.ProfileDetailVo getCurrentProfileDetail() {
        User user = requireCurrentUser();
        String deptName = resolveDeptName(user.getDeptId());
        String postName = resolvePostName(user.getPostId());
        List<Role> roles = roleService.getUserIsRole(user.getUserId());
        return UserInfoVo.ProfileDetailVo.from(user, deptName, postName, roles, getLatestLoginTime(user.getUserId()));
    }

    public List<UserInfoVo.LoginLogVo> getCurrentLoginLogs() {
        String userId = SaTokenUtil.getUserId();
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户未登录");
        }
        LambdaQueryWrapper<OperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperateLog::getUserId, userId)
                .eq(OperateLog::getLoggingType, "LOGIN")
                .eq(OperateLog::getBusinessType, "LOGIN")
                .eq(OperateLog::getIsSuccess, 1)
                .orderByDesc(OperateLog::getOperateTime)
                .last("limit " + LOGIN_LOG_LIMIT);
        List<OperateLog> logs = operateLogMapper.selectList(wrapper);
        return java.util.stream.IntStream.range(0, logs.size())
                .mapToObj(index -> UserInfoVo.LoginLogVo.from(logs.get(index), index == 0))
                .toList();
    }

    @Transactional
    public void updateCurrentProfile(UserInfoDto.ProfileSave profileSave) {
        dictRuntimeService.validateValue(DictTypeCode.SYS_USER_SEX, profileSave.userSex(), "性别");
        String userId = requireCurrentUserId();
        // 个人资料保存不接受 userId，仅更新当前登录用户允许自助修改的字段。
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUserId, userId)
                .set(User::getUserName, profileSave.userName().trim())
                .set(User::getUserSex, profileSave.userSex())
                .set(User::getUserEmail, normalizeOptionalText(profileSave.userEmail()))
                .set(User::getUserPhone, normalizeOptionalText(profileSave.userPhone()));
        int rows = userMapper.update(null, updateWrapper);
        if (rows <= 0) {
            throw new BusinessException("资料保存失败");
        }
        // 同步 Session，避免顶部用户名等仍显示旧值。
        User refreshed = userMapper.selectById(userId);
        SaTokenUtil.setUser(refreshed);
    }

    @Transactional
    public void updateCurrentPassword(UserInfoDto.UpdatePwd updatePwd) {
        validatePasswordSave(updatePwd);
        // 个人改密与管理员重置共用固定高复杂度策略。
        passwordPolicyService.validateNewPassword(updatePwd.newPassword());
        String userId = requireCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 个人改密不接受 userId 参数，仅允许修改当前登录账号密码。
        if (StrUtil.isBlank(user.getUserPassword())) {
            throw new BusinessException("账号尚未设置密码，请联系管理员");
        }
        if (!passwordService.matches(updatePwd.oldPassword(), user.getUserPassword())) {
            throw new BusinessException("原密码不正确");
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUserId, userId)
                .set(User::getUserPassword, passwordService.encode(updatePwd.newPassword()));
        int rows = userMapper.update(null, updateWrapper);
        if (rows <= 0) {
            throw new BusinessException("密码修改失败");
        }
        // 改密后强制重新登录。
        StpUtil.logout();
    }

    /** 更新当前用户头像地址；请求体不含 userId。 */
    @Transactional
    public void updateCurrentAvatar(UserInfoDto.UpdateAvatar updateAvatar) {
        String userId = requireCurrentUserId();
        String avatar = updateAvatar.userAvatar() == null ? "" : updateAvatar.userAvatar().trim();
        if (StrUtil.isBlank(avatar)) {
            throw new BusinessException("头像地址不能为空");
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUserId, userId).set(User::getUserAvatar, avatar);
        int rows = userMapper.update(null, updateWrapper);
        if (rows <= 0) {
            throw new BusinessException("头像更新失败");
        }
        User refreshed = userMapper.selectById(userId);
        SaTokenUtil.setUser(refreshed);
    }

    /** 仅返回当前登录用户 ID，写操作一律以此为准，忽略请求体中的任何用户标识。 */
    private String requireCurrentUserId() {
        String userId = SaTokenUtil.getUserId();
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户未登录");
        }
        return userId;
    }

    private User requireCurrentUser() {
        User user = userMapper.selectById(requireCurrentUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private String resolveDeptName(String deptId) {
        if (StrUtil.isBlank(deptId)) {
            return null;
        }
        Dept dept = deptMapper.selectById(deptId);
        return dept != null ? dept.getDeptName() : null;
    }

    private String resolvePostName(String postId) {
        if (StrUtil.isBlank(postId)) {
            return null;
        }
        Post post = postMapper.selectById(postId);
        return post != null ? post.getPostName() : null;
    }

    private LocalDateTime getLatestLoginTime(String userId) {
        LambdaQueryWrapper<OperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperateLog::getUserId, userId)
                .eq(OperateLog::getLoggingType, "LOGIN")
                .eq(OperateLog::getBusinessType, "LOGIN")
                .eq(OperateLog::getIsSuccess, 1)
                .orderByDesc(OperateLog::getOperateTime)
                .last("limit 1");
        OperateLog latest = operateLogMapper.selectOne(wrapper);
        return latest != null ? latest.getOperateTime() : null;
    }

    private void validatePasswordSave(UserInfoDto.UpdatePwd updatePwd) {
        if (updatePwd.oldPassword().equals(updatePwd.newPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
