package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    @Select("""
            SELECT config_group AS configGroup, COUNT(1) AS configCount
            FROM sys_config
            WHERE is_del = 0
              AND (#{configGroup} IS NULL OR #{configGroup} = '' OR config_group LIKE CONCAT('%', #{configGroup}, '%'))
            GROUP BY config_group
            ORDER BY MIN(order_num), config_group
            """)
    List<Map<String, Object>> selectGroupList(@Param("configGroup") String configGroup);
}
