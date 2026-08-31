package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylmao.admin.entity.JobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobLogMapper extends BaseMapper<JobLog> {

    /** 每个任务最近一条有效执行（SUCCESS / FAILED，不含 SKIPPED）。 */
    List<JobLog> selectLatestEffectiveByJobIds(@Param("jobIds") List<String> jobIds);
}
