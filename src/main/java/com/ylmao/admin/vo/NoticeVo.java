package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ylmao.admin.entity.Notice;
import com.ylmao.admin.vo.NoticeVo.ConsoleReceiverQueryVo;
import com.ylmao.admin.vo.NoticeVo.UserInboxQueryVo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class NoticeVo {

    public NoticeVo() {
    }

    public record NoticeListVo(
            String noticeId,
            String noticeTitle,
            String noticeContent,
            Integer noticeType,
            String noticeTypeName,
            Integer receiverType,
            String receiverTypeName,
            String receiverIds,
            String noticeDesc,
            Integer isSend,
            String isSendName,
            Integer orderNum,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime sendTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime expireTime,
            String createBy,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime createTime,
            /** 是否已写入用户公告关联表，有记录时不允许改回草稿。 */
            Boolean delivered
    ) {

        public static NoticeListVo from (Notice notice,boolean delivered, String noticeTypeName, String receiverTypeName)
        {
            return new NoticeListVo(
                    notice.getNoticeId(),
                    notice.getNoticeTitle(),
                    notice.getNoticeContent(),
                    notice.getNoticeType(),
                    noticeTypeName,
                    notice.getReceiverType(),
                    receiverTypeName,
                    notice.getReceiverIds(),
                    notice.getNoticeDesc(),
                    notice.getIsSend(),
                    notice.getIsSend() != null && notice.getIsSend() == 1 ? "已发布" : "草稿",
                    notice.getOrderNum(),
                    notice.getSendTime(),
                    notice.getExpireTime(),
                    notice.getCreateBy(),
                    notice.getCreateTime(),
                    delivered
            );
        }
    }

    /**
     * 个人管理收件箱列表项（接口返回）。
     */
    public record UserInboxVo(
            String noticeId,
            String noticeTitle,
            String noticeContent,
            Integer noticeType,
            String noticeTypeName,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime sendTime,
            Integer readState,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime readTime
    ) {

        public static UserInboxVo from (UserInboxQueryVo query, String noticeTypeName){
            return new UserInboxVo(
                    query.getNoticeId(),
                    query.getNoticeTitle(),
                    query.getNoticeContent(),
                    query.getNoticeType(),
                    noticeTypeName,
                    query.getSendTime(),
                    query.getReadState(),
                    query.getReadTime()
            );
        }
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NoticeVo)) return false;
        final NoticeVo other = (NoticeVo) o;
        if (!other.canEqual((Object) this)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NoticeVo;
    }

    public int hashCode() {
        int result = 1;
        return result;
    }

    public String toString() {
        return "NoticeVo()";
    }

    /**
     * 收件箱联表查询映射（MyBatis），不含 noticeTypeName。
     */
    public static class UserInboxQueryVo {
        private String noticeId;
        private String noticeTitle;
        private String noticeContent;
        private Integer noticeType;
        private LocalDateTime sendTime;
        private Integer readState;
        private LocalDateTime readTime;

        public UserInboxQueryVo() {
        }

        public String getNoticeId() {
            return this.noticeId;
        }

        public String getNoticeTitle() {
            return this.noticeTitle;
        }

        public String getNoticeContent() {
            return this.noticeContent;
        }

        public Integer getNoticeType() {
            return this.noticeType;
        }

        public LocalDateTime getSendTime() {
            return this.sendTime;
        }

        public Integer getReadState() {
            return this.readState;
        }

        public LocalDateTime getReadTime() {
            return this.readTime;
        }

        public void setNoticeId(String noticeId) {
            this.noticeId = noticeId;
        }

        public void setNoticeTitle(String noticeTitle) {
            this.noticeTitle = noticeTitle;
        }

        public void setNoticeContent(String noticeContent) {
            this.noticeContent = noticeContent;
        }

        public void setNoticeType(Integer noticeType) {
            this.noticeType = noticeType;
        }

        public void setSendTime(LocalDateTime sendTime) {
            this.sendTime = sendTime;
        }

        public void setReadState(Integer readState) {
            this.readState = readState;
        }

        public void setReadTime(LocalDateTime readTime) {
            this.readTime = readTime;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof UserInboxQueryVo)) return false;
            final UserInboxQueryVo other = (UserInboxQueryVo) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$noticeId = this.getNoticeId();
            final Object other$noticeId = other.getNoticeId();
            if (this$noticeId == null ? other$noticeId != null : !this$noticeId.equals(other$noticeId)) return false;
            final Object this$noticeTitle = this.getNoticeTitle();
            final Object other$noticeTitle = other.getNoticeTitle();
            if (this$noticeTitle == null ? other$noticeTitle != null : !this$noticeTitle.equals(other$noticeTitle))
                return false;
            final Object this$noticeContent = this.getNoticeContent();
            final Object other$noticeContent = other.getNoticeContent();
            if (this$noticeContent == null ? other$noticeContent != null : !this$noticeContent.equals(other$noticeContent))
                return false;
            final Object this$noticeType = this.getNoticeType();
            final Object other$noticeType = other.getNoticeType();
            if (this$noticeType == null ? other$noticeType != null : !this$noticeType.equals(other$noticeType))
                return false;
            final Object this$sendTime = this.getSendTime();
            final Object other$sendTime = other.getSendTime();
            if (this$sendTime == null ? other$sendTime != null : !this$sendTime.equals(other$sendTime)) return false;
            final Object this$readState = this.getReadState();
            final Object other$readState = other.getReadState();
            if (this$readState == null ? other$readState != null : !this$readState.equals(other$readState))
                return false;
            final Object this$readTime = this.getReadTime();
            final Object other$readTime = other.getReadTime();
            if (this$readTime == null ? other$readTime != null : !this$readTime.equals(other$readTime)) return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof UserInboxQueryVo;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $noticeId = this.getNoticeId();
            result = result * PRIME + ($noticeId == null ? 43 : $noticeId.hashCode());
            final Object $noticeTitle = this.getNoticeTitle();
            result = result * PRIME + ($noticeTitle == null ? 43 : $noticeTitle.hashCode());
            final Object $noticeContent = this.getNoticeContent();
            result = result * PRIME + ($noticeContent == null ? 43 : $noticeContent.hashCode());
            final Object $noticeType = this.getNoticeType();
            result = result * PRIME + ($noticeType == null ? 43 : $noticeType.hashCode());
            final Object $sendTime = this.getSendTime();
            result = result * PRIME + ($sendTime == null ? 43 : $sendTime.hashCode());
            final Object $readState = this.getReadState();
            result = result * PRIME + ($readState == null ? 43 : $readState.hashCode());
            final Object $readTime = this.getReadTime();
            result = result * PRIME + ($readTime == null ? 43 : $readTime.hashCode());
            return result;
        }

        public String toString() {
            return "NoticeVo.UserInboxQueryVo(noticeId=" + this.getNoticeId() + ", noticeTitle=" + this.getNoticeTitle() + ", noticeContent=" + this.getNoticeContent() + ", noticeType=" + this.getNoticeType() + ", sendTime=" + this.getSendTime() + ", readState=" + this.getReadState() + ", readTime=" + this.getReadTime() + ")";
        }
    }

    /**
     * Pear 头部消息组件：单条未读公告。
     */
    public record HeaderMessageItemVo(
            String id,
            /** Layui 图标类名，如 layui-icon-notice。 */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String icon,
            /** 公告类型，用于图标底色样式。 */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Integer noticeType,
            String title,
            String context,
            String form,
            String time
    ) {
    }

    /**
     * Pear 头部消息组件：Tab 分组（按公告类型）。
     */
    public record HeaderMessageTabVo(
            Integer id,
            String title,
            List<HeaderMessageItemVo> children
    ) {
    }

    /**
     * 公告控制台：阅读统计与公告摘要。
     */
    public record ConsoleStatsVo(
            String noticeId,
            String noticeTitle,
            Integer noticeType,
            String noticeTypeName,
            Integer receiverType,
            String receiverTypeName,
            /** 指定角色/部门时的目标名称列表；全体与指定个人为空。 */
            List<String> receiverTargets,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime sendTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime expireTime,
            long totalCount,
            long readCount,
            long unreadCount,
            /** 阅读率百分比文本，如 75%。 */
            String readRate
    ) {
    }

    /**
     * 控制台接收人列表项。
     */
    public record ConsoleReceiverVo(
            String userId,
            String userAccount,
            String userName,
            String deptName,
            Integer readState,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime readTime
    ) {
        public static ConsoleReceiverVo from (ConsoleReceiverQueryVo query, String deptName){
            return new ConsoleReceiverVo(
                    query.getUserId(),
                    query.getUserAccount(),
                    query.getUserName(),
                    deptName != null && !deptName.isBlank() ? deptName : "无部门",
                    query.getReadState(),
                    query.getReadTime()
            );
        }
    }

    /**
     * 控制台接收人联表查询映射（MyBatis）。
     */
    public static class ConsoleReceiverQueryVo {
        private String userId;
        private String userAccount;
        private String userName;
        private String deptId;
        private Integer readState;
        private LocalDateTime readTime;

        public ConsoleReceiverQueryVo() {
        }

        public String getUserId() {
            return this.userId;
        }

        public String getUserAccount() {
            return this.userAccount;
        }

        public String getUserName() {
            return this.userName;
        }

        public String getDeptId() {
            return this.deptId;
        }

        public Integer getReadState() {
            return this.readState;
        }

        public LocalDateTime getReadTime() {
            return this.readTime;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setUserAccount(String userAccount) {
            this.userAccount = userAccount;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public void setReadState(Integer readState) {
            this.readState = readState;
        }

        public void setReadTime(LocalDateTime readTime) {
            this.readTime = readTime;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof ConsoleReceiverQueryVo)) return false;
            final ConsoleReceiverQueryVo other = (ConsoleReceiverQueryVo) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$userId = this.getUserId();
            final Object other$userId = other.getUserId();
            if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
            final Object this$userAccount = this.getUserAccount();
            final Object other$userAccount = other.getUserAccount();
            if (this$userAccount == null ? other$userAccount != null : !this$userAccount.equals(other$userAccount))
                return false;
            final Object this$userName = this.getUserName();
            final Object other$userName = other.getUserName();
            if (this$userName == null ? other$userName != null : !this$userName.equals(other$userName)) return false;
            final Object this$deptId = this.getDeptId();
            final Object other$deptId = other.getDeptId();
            if (this$deptId == null ? other$deptId != null : !this$deptId.equals(other$deptId)) return false;
            final Object this$readState = this.getReadState();
            final Object other$readState = other.getReadState();
            if (this$readState == null ? other$readState != null : !this$readState.equals(other$readState))
                return false;
            final Object this$readTime = this.getReadTime();
            final Object other$readTime = other.getReadTime();
            if (this$readTime == null ? other$readTime != null : !this$readTime.equals(other$readTime)) return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof ConsoleReceiverQueryVo;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $userId = this.getUserId();
            result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
            final Object $userAccount = this.getUserAccount();
            result = result * PRIME + ($userAccount == null ? 43 : $userAccount.hashCode());
            final Object $userName = this.getUserName();
            result = result * PRIME + ($userName == null ? 43 : $userName.hashCode());
            final Object $deptId = this.getDeptId();
            result = result * PRIME + ($deptId == null ? 43 : $deptId.hashCode());
            final Object $readState = this.getReadState();
            result = result * PRIME + ($readState == null ? 43 : $readState.hashCode());
            final Object $readTime = this.getReadTime();
            result = result * PRIME + ($readTime == null ? 43 : $readTime.hashCode());
            return result;
        }

        public String toString() {
            return "NoticeVo.ConsoleReceiverQueryVo(userId=" + this.getUserId() + ", userAccount=" + this.getUserAccount() + ", userName=" + this.getUserName() + ", deptId=" + this.getDeptId() + ", readState=" + this.getReadState() + ", readTime=" + this.getReadTime() + ")";
        }
    }

    /**
     * 控制台阅读计数查询映射（MyBatis）。
     */
    public static class ConsoleCountQueryVo {
        private Long totalCount;
        private Long readCount;
        private Long unreadCount;

        public ConsoleCountQueryVo() {
        }

        public Long getTotalCount() {
            return this.totalCount;
        }

        public Long getReadCount() {
            return this.readCount;
        }

        public Long getUnreadCount() {
            return this.unreadCount;
        }

        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }

        public void setReadCount(Long readCount) {
            this.readCount = readCount;
        }

        public void setUnreadCount(Long unreadCount) {
            this.unreadCount = unreadCount;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof ConsoleCountQueryVo)) return false;
            final ConsoleCountQueryVo other = (ConsoleCountQueryVo) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$totalCount = this.getTotalCount();
            final Object other$totalCount = other.getTotalCount();
            if (this$totalCount == null ? other$totalCount != null : !this$totalCount.equals(other$totalCount))
                return false;
            final Object this$readCount = this.getReadCount();
            final Object other$readCount = other.getReadCount();
            if (this$readCount == null ? other$readCount != null : !this$readCount.equals(other$readCount))
                return false;
            final Object this$unreadCount = this.getUnreadCount();
            final Object other$unreadCount = other.getUnreadCount();
            if (this$unreadCount == null ? other$unreadCount != null : !this$unreadCount.equals(other$unreadCount))
                return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof ConsoleCountQueryVo;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $totalCount = this.getTotalCount();
            result = result * PRIME + ($totalCount == null ? 43 : $totalCount.hashCode());
            final Object $readCount = this.getReadCount();
            result = result * PRIME + ($readCount == null ? 43 : $readCount.hashCode());
            final Object $unreadCount = this.getUnreadCount();
            result = result * PRIME + ($unreadCount == null ? 43 : $unreadCount.hashCode());
            return result;
        }

        public String toString() {
            return "NoticeVo.ConsoleCountQueryVo(totalCount=" + this.getTotalCount() + ", readCount=" + this.getReadCount() + ", unreadCount=" + this.getUnreadCount() + ")";
        }
    }

    /**
     * 公告类型对应 Layui 图标。
     */
    private static final Map<Integer, String> NOTICE_TYPE_ICONS = Map.of(
            1, "layui-icon-notice",
            2, "layui-icon-fire",
            3, "layui-icon-speaker",
            4, "layui-icon-email"
    );

    public static String noticeTypeIcon(Integer noticeType) {
        return NOTICE_TYPE_ICONS.getOrDefault(noticeType, NOTICE_TYPE_ICONS.get(1));
    }
}
