package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.Dept;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeptVo {

    public record DeptListVo(String deptId, String parentId, String deptPath, String deptName,
                             Integer orderNum, String deptLeader, String leaderPhone,
                             String leaderEmail, Integer isEnabled,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createTime) {

        public static DeptListVo from(Dept dept) {
            return new DeptListVo(dept.getDeptId(), dept.getParentId(), dept.getDeptPath(), dept.getDeptName(),
                    dept.getOrderNum(), dept.getDeptLeader(), dept.getLeaderPhone(), dept.getLeaderEmail(),
                    dept.getIsEnabled(), dept.getCreateTime());
        }
    }

    /** 部门下拉/树选项，供 listView 与 /dept/tree 使用。 */
    public record DeptOptionVo(String deptId, String parentId, String deptName) {

        public static DeptOptionVo from(Dept dept) {
            return new DeptOptionVo(dept.getDeptId(), dept.getParentId(), dept.getDeptName());
        }
    }
}
