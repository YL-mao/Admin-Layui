package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.RoleUser;
import com.ylmao.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface RoleUserMapper extends BaseMapper<RoleUser> {
}
