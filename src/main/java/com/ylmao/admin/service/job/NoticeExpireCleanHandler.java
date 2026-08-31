package com.ylmao.admin.service.job;

import com.ylmao.admin.common.JobCodes;
import com.ylmao.admin.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeExpireCleanHandler implements JobHandler {

    private final NoticeService noticeService;

    @Override
    public String jobCode() {
        return JobCodes.NOTICE_EXPIRE_CLEAN;
    }

    @Override
    public String execute() {
        int deleted = noticeService.cleanExpiredNotices();
        return "清理过期公告 " + deleted + " 条";
    }
}
