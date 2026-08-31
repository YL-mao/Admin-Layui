package com.ylmao.admin.vo;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.Role;
import com.ylmao.admin.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class UserInfoVo {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 个人资料页左侧概览与表单初始值。 */
    public record ProfileDetailVo(
            String userAccount,
            String userName,
            String userSex,
            String userEmail,
            String userPhone,
            String userAvatar,
            String deptName,
            String postName,
            List<String> roleNames,
            Integer isEnabled,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime createTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime lastLoginTime
    ) {

        public static ProfileDetailVo from(User user,
                                           String deptName,
                                           String postName,
                                           List<Role> roles,
                                           LocalDateTime lastLoginTime) {
            List<String> roleNames = roles == null ? List.of()
                    : roles.stream()
                    .map(Role::getRoleName)
                    .filter(StrUtil::isNotBlank)
                    .toList();
            return new ProfileDetailVo(
                    user.getUserAccount(),
                    user.getUserName(),
                    user.getUserSex(),
                    user.getUserEmail(),
                    user.getUserPhone(),
                    user.getUserAvatar(),
                    StrUtil.isNotBlank(deptName) ? deptName : "无部门",
                    StrUtil.isNotBlank(postName) ? postName : "无岗位",
                    roleNames,
                    user.getIsEnabled(),
                    user.getCreateTime(),
                    lastLoginTime
            );
        }
    }

    /** 个人资料页最近登录记录。 */
    public record LoginLogVo(
            String loginTime,
            String loginIp,
            String browser,
            String systemOs,
            Boolean current
    ) {

        public static LoginLogVo from(com.ylmao.admin.entity.OperateLog operateLog, boolean current) {
            LocalDateTime operateTime = operateLog.getOperateTime();
            return new LoginLogVo(
                    operateTime != null ? operateTime.format(DATE_TIME_FORMATTER) : null,
                    operateLog.getOperateIp(),
                    operateLog.getBrowser(),
                    operateLog.getSystemOs(),
                    current
            );
        }
    }
}
