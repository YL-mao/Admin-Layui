package com.ylmao.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeDto {

    public record NoticeList(
            @Size(max = 255, message = "公告标题参数不合法") String noticeTitle,
            Integer noticeType,
            @Min(value = 0, message = "发布状态参数不合法") @Max(value = 1, message = "发布状态参数不合法")
            Integer isSend
    ) {
    }

    public record NoticeInsert(
            @NotBlank(message = "公告标题不能为空") String noticeTitle,
            @NotBlank(message = "公告内容不能为空") String noticeContent,
            @NotNull(message = "公告类型参数不合法") Integer noticeType,
            @NotNull(message = "接收人类型参数不合法") Integer receiverType,
            String receiverIds,
            String noticeDesc,
            @NotNull(message = "发布状态参数不合法") @Min(value = 0, message = "发布状态参数不合法") @Max(value = 1, message = "发布状态参数不合法")
            Integer isSend,
            @NotNull(message = "公告排序不能为空") @Min(value = 0, message = "公告排序号不能为空且不能小于 0")
            Integer orderNum,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime expireTime
    ) {
    }

    public record NoticeUpdate(
            @NotBlank(message = "公告ID不能为空") String noticeId,
            @NotBlank(message = "公告标题不能为空") String noticeTitle,
            @NotBlank(message = "公告内容不能为空") String noticeContent,
            @NotNull(message = "公告类型参数不合法") Integer noticeType,
            @NotNull(message = "接收人类型参数不合法") Integer receiverType,
            String receiverIds,
            String noticeDesc,
            @NotNull(message = "发布状态参数不合法") @Min(value = 0, message = "发布状态参数不合法") @Max(value = 1, message = "发布状态参数不合法")
            Integer isSend,
            @NotNull(message = "公告排序不能为空") @Min(value = 0, message = "公告排序号不能为空且不能小于 0")
            Integer orderNum,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime expireTime
    ) {
    }

    public record UpdateEnabled(
            @NotBlank(message = "公告ID不能为空") String noticeId,
            @NotNull(message = "发布状态参数不合法") @Min(value = 0, message = "发布状态参数不合法") @Max(value = 1, message = "发布状态参数不合法")
            Integer isSend
    ) {
    }

    /** 个人收件箱列表查询条件（不含 userId，后端仅查当前登录用户）。 */
    public record UserNoticeList(
            @Size(max = 255, message = "公告标题参数不合法") String noticeTitle,
            Integer noticeType,
            @Min(value = 0, message = "阅读状态参数不合法") @Max(value = 1, message = "阅读状态参数不合法")
            Integer readState
    ) {
    }

    /** 单条公告标记已读（不含 userId，后端仅更新当前登录用户的收件箱记录）。 */
    public record UpdateRead(
            @NotBlank(message = "公告ID不能为空") String noticeId
    ) {
    }

    /** 公告控制台接收人分页：可按阅读状态筛选。 */
    public record ConsoleReceiverList(
            @NotBlank(message = "公告ID不能为空") String noticeId,
            @Min(value = 0, message = "阅读状态参数不合法") @Max(value = 1, message = "阅读状态参数不合法")
            Integer readState
    ) {
    }
}
