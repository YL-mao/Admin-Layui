package com.ylmao.admin.service.job;

/** 内置任务执行器：jobCode 与 sys_job.job_code 对应。 */
public interface JobHandler {

    String jobCode();

    /** 执行业务并返回结果摘要（写入执行日志）。 */
    String execute();
}
