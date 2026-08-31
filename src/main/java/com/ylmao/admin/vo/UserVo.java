package com.ylmao.admin.vo;
import cn.hutool.core.util.StrUtil;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ylmao.admin.common.ExcelCellSafe;
import com.ylmao.admin.entity.User;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class UserVo {

    public record UserListVo(String userId, String userName, String userAccount, String userSex, String userSexName,
                             String userEmail,
                             String userPhone, String deptId, String deptName, String postId, String postName,
                             String roleIds, String roleNames, Integer isEnabled, Integer isLock, Integer online) {

        public static UserListVo from(User user,
                                      Map<String, String> deptMap,
                                      Map<String, String> postMap,
                                      Map<String, String> roleMap,
                                      Map<String, String> userRoleIdsMap,
                                      String userSexName,
                                      boolean online) {
            String roleIds = userRoleIdsMap.getOrDefault(user.getUserId(), "");
            return new UserListVo(
                    user.getUserId(),
                    user.getUserName(),
                    user.getUserAccount(),
                    user.getUserSex(),
                    userSexName,
                    user.getUserEmail(),
                    user.getUserPhone(),
                    user.getDeptId(),
                    deptMap.getOrDefault(user.getDeptId(), "无部门"),
                    user.getPostId(),
                    postMap.getOrDefault(user.getPostId(), "无岗位"),
                    roleIds,
                    toRoleNames(roleIds, roleMap),
                    user.getIsEnabled(),
                    user.getIsLock(),
                    online ? 1 : 0
            );
        }

        private static String toRoleNames(String roleIds, Map<String, String> roleMap) {
            if (StrUtil.isBlank(roleIds)) {
                return "无角色";
            }
            String names = Arrays.stream(roleIds.split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .map(roleMap::get)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.joining(","));
            return StrUtil.isBlank(names) ? "无角色" : names;
        }
    }

    /** 用户列表导出列，与列表页展示字段一致；EasyExcel 需标准 getter，不能用 record。 */
    @Data
    public static class UserExportVo {
        @ExcelProperty("姓名")
        private String userName;
        @ExcelProperty("登录账号")
        private String userAccount;
        @ExcelProperty("性别")
        private String userSexName;
        @ExcelProperty("邮箱")
        private String userEmail;
        @ExcelProperty("电话")
        private String userPhone;
        @ExcelProperty("部门")
        private String deptName;
        @ExcelProperty("岗位")
        private String postName;
        @ExcelProperty("角色")
        private String roleNames;
        @ExcelProperty("状态")
        private String isEnabledName;
        @ExcelProperty("锁定")
        private String isLockName;

        public static UserExportVo from(UserListVo vo) {
            UserExportVo exportVo = new UserExportVo();
            // 导出单元格转义公式前缀，避免 Excel 当公式执行。
            exportVo.setUserName(ExcelCellSafe.escape(vo.userName()));
            exportVo.setUserAccount(ExcelCellSafe.escape(vo.userAccount()));
            exportVo.setUserSexName(ExcelCellSafe.escape(vo.userSexName()));
            exportVo.setUserEmail(ExcelCellSafe.escape(vo.userEmail()));
            exportVo.setUserPhone(ExcelCellSafe.escape(vo.userPhone()));
            exportVo.setDeptName(ExcelCellSafe.escape(vo.deptName()));
            exportVo.setPostName(ExcelCellSafe.escape(vo.postName()));
            exportVo.setRoleNames(ExcelCellSafe.escape(vo.roleNames()));
            exportVo.setIsEnabledName(ExcelCellSafe.escape(
                    vo.isEnabled() != null && vo.isEnabled() == 1 ? "启用" : "停用"));
            exportVo.setIsLockName(ExcelCellSafe.escape(
                    vo.isLock() != null && vo.isLock() == 1 ? "锁定" : "正常"));
            return exportVo;
        }
    }

    /** 公告指定用户检索下拉选项。 */
    public record UserOptionVo(String userId, String userAccount, String userName, String label) {

        public static UserOptionVo from(User user) {
            String label = user.getUserName() + "（" + user.getUserAccount() + "）";
            return new UserOptionVo(user.getUserId(), user.getUserAccount(), user.getUserName(), label);
        }
    }

    /** 用户最终权限详情：角色标签 + 并集权限树平铺节点。 */
    public record UserPermDetailVo(
            String userId,
            String userName,
            String userAccount,
            List<RoleItem> roles,
            List<PermItem> perms
    ) {
        public record RoleItem(String roleId, String roleName) {
        }

        public record PermItem(String permId, String parentId, String permName) {
        }
    }
}
