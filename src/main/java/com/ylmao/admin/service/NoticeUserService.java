package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.mapper.NoticeUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeUserService {

    private final NoticeUserMapper noticeUserMapper;

    @Transactional
    public void updateUserNoticeRead(String noticeId) {
        if (StrUtil.isBlank(noticeId)) {
            throw new BusinessException("公告 ID 不能为空");
        }
        String userId = requireCurrentUserId();
        Integer rows = noticeUserMapper.markReadByNoticeId(userId, noticeId);
        if (rows != null && rows > 0) {
            return;
        }
        // 已读重复提交视为成功，非法 ID 才报错。
        Integer visibleCount = noticeUserMapper.countVisibleInboxNotice(userId, noticeId);
        if (visibleCount == null || visibleCount <= 0) {
            throw new BusinessException("公告不存在或无权查看");
        }
    }

    @Transactional
    public void readAllUserNotices() {
        noticeUserMapper.readAllVisibleUnread(requireCurrentUserId());
    }

    /** 仅返回当前登录用户 ID，写操作一律以此为准，忽略请求体中的任何用户标识。 */
    private String requireCurrentUserId() {
        String userId = SaTokenUtil.getUserId();
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户未登录");
        }
        return userId;
    }
}
