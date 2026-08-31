package com.ylmao.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.entity.NoticeUser;
import com.ylmao.admin.vo.NoticeVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeUserMapper extends BaseMapper<NoticeUser> {

     Integer insertBatch(@Param("noticeUserList") List<NoticeUser> noticeUserList);

     /** 当前用户收件箱分页：仅已发布且未过期的公告。 */
     IPage<NoticeVo.UserInboxQueryVo> selectUserInboxPage(
             Page<NoticeVo.UserInboxQueryVo> page,
             @Param("userId") String userId,
             @Param("noticeTitle") String noticeTitle,
             @Param("noticeType") Integer noticeType,
             @Param("readState") Integer readState
     );

     /** 将当前用户可见未读公告全部标为已读。 */
     Integer readAllVisibleUnread(@Param("userId") String userId);

     /** 单条标记已读，仅更新当前用户可见收件箱记录。 */
     Integer markReadByNoticeId(@Param("userId") String userId, @Param("noticeId") String noticeId);

     /** 判断公告是否仍在当前用户可见收件箱中。 */
     Integer countVisibleInboxNotice(@Param("userId") String userId, @Param("noticeId") String noticeId);

     /** 公告控制台：按 noticeId 汇总投递 / 已读 / 未读数量。 */
     NoticeVo.ConsoleCountQueryVo selectConsoleCounts(@Param("noticeId") String noticeId);

     /** 公告控制台：接收人分页，可按阅读状态筛选。 */
     IPage<NoticeVo.ConsoleReceiverQueryVo> selectConsoleReceiverPage(
             Page<NoticeVo.ConsoleReceiverQueryVo> page,
             @Param("noticeId") String noticeId,
             @Param("readState") Integer readState
     );

     /** 软删截止时间之前的已读收件箱记录（无阅读时间时回退投递时间）。 */
     int softDeleteReadBefore(@Param("deadline") java.time.LocalDateTime deadline);

     /** 软删截止时间之前的未读收件箱记录（按投递时间）。 */
     int softDeleteUnreadBefore(@Param("deadline") java.time.LocalDateTime deadline);
}
