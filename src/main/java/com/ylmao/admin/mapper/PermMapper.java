package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ylmao.admin.entity.Perm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermMapper extends BaseMapper<Perm> {

    List<Perm> queryPermCheckArrByRoleId(String roleId);

    List<Perm> selectPermChildCount(@Param(Constants.WRAPPER) Wrapper<Perm> queryWrapper);

    List<String> queryPermsListByRoleId(String roleId);

    /** 多角色合并查询启用权限，菜单与鉴权共用。 */
    List<Perm> selectPermsByRoleIds(@Param("roleIds") List<String> roleIds);
}
