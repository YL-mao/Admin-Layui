package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.Role;
import com.ylmao.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<String> queryRoleByUserId(String userId);
}
