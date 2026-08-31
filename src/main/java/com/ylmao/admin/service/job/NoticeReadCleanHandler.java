package com.ylmao.admin.service.job;

import com.ylmao.admin.common.JobCodes;
import com.ylmao.admin.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 按 notice.readDays 软删收件箱已读记录。 */
@Component
@RequiredArgsConstructor
public class NoticeReadCleanHandler implements JobHandler {

    private final NoticeService noticeService;

    @Override
    public String jobCode() {
        return JobCodes.NOTICE_READ_CLEAN;
    }

    @Override
    public String execute() {
        int deleted = noticeService.cleanReadInboxByRetentionConfig();
        return "清理已读收件箱 " + deleted + " 条";
    }
}
