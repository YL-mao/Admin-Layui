package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.OperateLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface OperateLogMapper extends BaseMapper<OperateLog> {
}
