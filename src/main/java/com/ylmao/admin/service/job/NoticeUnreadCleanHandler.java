package com.ylmao.admin.service.job;

import com.ylmao.admin.common.JobCodes;
import com.ylmao.admin.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 按 notice.unreadDays 软删收件箱未读记录。 */
@Component
@RequiredArgsConstructor
public class NoticeUnreadCleanHandler implements JobHandler {

    private final NoticeService noticeService;

    @Override
    public String jobCode() {
        return JobCodes.NOTICE_UNREAD_CLEAN;
    }

    @Override
    public String execute() {
        int deleted = noticeService.cleanUnreadInboxByRetentionConfig();
        return "清理未读收件箱 " + deleted + " 条";
    }
}
