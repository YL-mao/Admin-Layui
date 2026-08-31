package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.common.NoticeConfigCodes;
import com.ylmao.admin.common.TextSafeUtils;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.constant.DictTypeCode;
import com.ylmao.admin.dto.NoticeDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.Notice;
import com.ylmao.admin.entity.NoticeUser;
import com.ylmao.admin.entity.Dept;
import com.ylmao.admin.entity.Role;
import com.ylmao.admin.entity.RoleUser;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.NoticeMapper;
import com.ylmao.admin.mapper.NoticeUserMapper;
import com.ylmao.admin.mapper.DeptMapper;
import com.ylmao.admin.mapper.RoleMapper;
import com.ylmao.admin.mapper.RoleUserMapper;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.vo.DictVo;
import com.ylmao.admin.vo.NoticeVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private static final Logger log = LoggerFactory.getLogger(NoticeService.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final NoticeMapper noticeMapper;
    private final NoticeUserMapper noticeUserMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final RoleMapper roleMapper;
    private final RoleUserMapper roleUserMapper;
    private final DictRuntimeService dictRuntimeService;
    private final ConfigRuntimeService configRuntimeService;

    public IPage<NoticeVo.NoticeListVo> selectPageList(PageQuery pageQuery, NoticeDto.NoticeList noticeList) {
        Page<Notice> noticePage = pageQuery.toMpPage();
        LambdaQueryWrapper<Notice> noticeQueryWrapper = buildNoticeListWrapper(noticeList);
        noticeQueryWrapper.orderByAsc(Notice::getOrderNum).orderByDesc(Notice::getCreateTime);
        noticeMapper.selectPage(noticePage, noticeQueryWrapper);

        List<Notice> notices = noticePage.getRecords();
        Set<String> deliveredNoticeIds = findDeliveredNoticeIds(
                notices.stream().map(Notice::getNoticeId).toList());
        Page<NoticeVo.NoticeListVo> voPage = new Page<>(noticePage.getCurrent(), noticePage.getSize(), noticePage.getTotal());
        voPage.setRecords(notices.stream()
                .map(notice -> NoticeVo.NoticeListVo.from(
                        notice,
                        deliveredNoticeIds.contains(notice.getNoticeId()),
                        dictRuntimeService.getLabel(DictTypeCode.SYS_NOTICE_TYPE, notice.getNoticeType()),
                        dictRuntimeService.getLabel(DictTypeCode.SYS_NOTICE_RECEIVER_TYPE, notice.getReceiverType())))
                .toList());
        return voPage;
    }

    public List<Notice> getUserNoticeNotRead(int state) {
        String userId = currentUserIdOrNull();
        if (userId == null) {
            return new ArrayList<>();
        }
        List<String> ids = getUserNoticeIds(userId, state);
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Notice> noticeQueryWrapper = new LambdaQueryWrapper<>();
        noticeQueryWrapper.in(Notice::getNoticeId, ids)
                .eq(Notice::getIsSend, 1)
                .and(w -> w.isNull(Notice::getExpireTime).or().gt(Notice::getExpireTime, LocalDateTime.now()))
                .orderByAsc(Notice::getOrderNum)
                .orderByDesc(Notice::getSendTime);
        return noticeMapper.selectList(noticeQueryWrapper);
    }

    /** 个人管理收件箱分页：仅当前登录用户、已发布且未过期公告。 */
    public IPage<NoticeVo.UserInboxVo> selectUserInboxPageList(PageQuery pageQuery, NoticeDto.UserNoticeList userNoticeList) {
        String userId = currentUserIdOrNull();
        if (userId == null) {
            return new Page<>(pageQuery.getPage(), pageQuery.getLimit(), 0);
        }
        String noticeTitle = userNoticeList != null ? userNoticeList.noticeTitle() : null;
        Integer noticeType = userNoticeList != null ? userNoticeList.noticeType() : null;
        Integer readState = userNoticeList != null ? userNoticeList.readState() : null;
        Page<NoticeVo.UserInboxQueryVo> inboxPage = pageQuery.toMpPage();
        IPage<NoticeVo.UserInboxQueryVo> queryPage = noticeUserMapper.selectUserInboxPage(
                inboxPage, userId, noticeTitle, noticeType, readState);
        Page<NoticeVo.UserInboxVo> voPage = new Page<>(queryPage.getCurrent(), queryPage.getSize(), queryPage.getTotal());
        voPage.setRecords(queryPage.getRecords().stream()
                .map(query -> NoticeVo.UserInboxVo.from(
                        query,
                        dictRuntimeService.getLabel(DictTypeCode.SYS_NOTICE_TYPE, query.getNoticeType())))
                .toList());
        return voPage;
    }

    /** 顶部铃铛：按公告类型分 Tab，每类最多 limitPerTab 条未读。 */
    public List<NoticeVo.HeaderMessageTabVo> buildUserNoticeHeader(int limitPerTab) {
        List<DictVo.DictOptionVo> noticeTypeOptions = dictRuntimeService.getOptions(DictTypeCode.SYS_NOTICE_TYPE);
        int typeCount = Math.max(noticeTypeOptions.size(), 1);

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(1);
        // 一次拉取足够未读，再按类型分组截断。
        pageQuery.setLimit(Math.max(limitPerTab * typeCount, limitPerTab));
        NoticeDto.UserNoticeList userNoticeList = new NoticeDto.UserNoticeList(null, null, 0);
        IPage<NoticeVo.UserInboxVo> inboxPage = selectUserInboxPageList(pageQuery, userNoticeList);

        Map<Integer, List<NoticeVo.HeaderMessageItemVo>> grouped = new LinkedHashMap<>();
        for (DictVo.DictOptionVo option : noticeTypeOptions) {
            grouped.put(Integer.valueOf(option.dictDataValue()), new ArrayList<>());
        }
        for (NoticeVo.UserInboxVo inbox : inboxPage.getRecords()) {
            if (inbox.noticeType() == null) {
                continue;
            }
            List<NoticeVo.HeaderMessageItemVo> typeItems = grouped.get(inbox.noticeType());
            if (typeItems != null && typeItems.size() < limitPerTab) {
                typeItems.add(toHeaderMessageItem(inbox));
            }
        }

        return noticeTypeOptions.stream()
                .map(option -> new NoticeVo.HeaderMessageTabVo(
                        Integer.valueOf(option.dictDataValue()),
                        option.dictDataLabel(),
                        grouped.getOrDefault(Integer.valueOf(option.dictDataValue()), List.of())))
                .toList();
    }

    /** 无权限时返回空的公告类型 Tab。 */
    public List<NoticeVo.HeaderMessageTabVo> buildEmptyNoticeHeaderTabs() {
        return dictRuntimeService.getOptions(DictTypeCode.SYS_NOTICE_TYPE).stream()
                .map(option -> new NoticeVo.HeaderMessageTabVo(
                        Integer.valueOf(option.dictDataValue()),
                        option.dictDataLabel(),
                        List.of()))
                .toList();
    }

    private NoticeVo.HeaderMessageItemVo toHeaderMessageItem(NoticeVo.UserInboxVo inbox) {
        // 头部消息由前端拼 HTML；此处返回规范化后的纯文本，展示侧再转义。
        return new NoticeVo.HeaderMessageItemVo(
                inbox.noticeId(),
                NoticeVo.noticeTypeIcon(inbox.noticeType()),
                inbox.noticeType(),
                nullToEmpty(inbox.noticeTitle()),
                truncateNoticeContent(inbox.noticeContent()),
                nullToEmpty(inbox.noticeTypeName()),
                inbox.sendTime() != null
                        ? inbox.sendTime().format(DATE_TIME_FORMATTER)
                        : ""
        );
    }

    private String truncateNoticeContent(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 80 ? normalized : normalized.substring(0, 80) + "...";
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /** 公告标题 / 正文 / 备注按纯文本规范化后入库。 */
    private void normalizeNoticePlainText(Notice notice) {
        notice.setNoticeTitle(TextSafeUtils.normalizePlainText(notice.getNoticeTitle()));
        notice.setNoticeContent(TextSafeUtils.normalizePlainText(notice.getNoticeContent()));
        notice.setNoticeDesc(TextSafeUtils.normalizePlainText(notice.getNoticeDesc()));
    }

    /** 获取最新 8 条已发布公告，供后台工作台展示。 */
    public List<Notice> getNEW() {
        LambdaQueryWrapper<Notice> noticeQueryWrapper = new LambdaQueryWrapper<>();
        noticeQueryWrapper.eq(Notice::getIsSend, 1)
                .and(w -> w.isNull(Notice::getExpireTime).or().gt(Notice::getExpireTime, LocalDateTime.now()))
                .orderByDesc(Notice::getSendTime)
                .last("limit 8");
        return noticeMapper.selectList(noticeQueryWrapper);
    }

    @Transactional
    public void insert(NoticeDto.NoticeInsert noticeInsert) {
        validateNoticeFields(noticeInsert.noticeType(), noticeInsert.receiverType(), noticeInsert.receiverIds());
        Notice notice = new Notice(noticeInsert);
        normalizeNoticePlainText(notice);
        normalizeReceiverIds(notice);
        applyPublishTime(notice);
        int rows = noticeMapper.insert(notice);
        if (rows <= 0) {
            throw new BusinessException("新增公告失败");
        }
        deliverIfPublished(notice);
    }

    @Transactional
    public void updateById(NoticeDto.NoticeUpdate noticeUpdate) {
        validateNoticeFields(noticeUpdate.noticeType(), noticeUpdate.receiverType(), noticeUpdate.receiverIds());
        Notice oldNotice = noticeMapper.selectById(noticeUpdate.noticeId());
        if (oldNotice == null) {
            throw new BusinessException("公告不存在");
        }
        if (oldNotice.getIsSend() != null && oldNotice.getIsSend() == 1) {
            throw new BusinessException("已发布公告不允许修改");
        }
        validateSendStateChange(oldNotice, noticeUpdate.isSend());
        Notice notice = new Notice(noticeUpdate);
        normalizeNoticePlainText(notice);
        normalizeReceiverIds(notice);
        if (oldNotice.getIsSend() != null && oldNotice.getIsSend() == 1 && notice.getIsSend() != null && notice.getIsSend() == 1) {
            notice.setSendTime(oldNotice.getSendTime());
        } else {
            applyPublishTime(notice);
        }
        int rows = noticeMapper.updateById(notice);
        if (rows <= 0) {
            throw new BusinessException("修改公告失败");
        }
        if (notice.getIsSend() != null && notice.getIsSend() == 1) {
            deliverIfPublished(notice);
        }
    }

    @Transactional
    public void updateEnabled(NoticeDto.UpdateEnabled updateEnabled) {
        Notice oldNotice = noticeMapper.selectById(updateEnabled.noticeId());
        if (oldNotice == null) {
            throw new BusinessException("公告不存在");
        }
        if (updateEnabled.isSend() == 1 && oldNotice.getIsSend() != null && oldNotice.getIsSend() == 1) {
            return;
        }
        validateSendStateChange(oldNotice, updateEnabled.isSend());
        oldNotice.setIsSend(updateEnabled.isSend());
        if (updateEnabled.isSend() == 1 && oldNotice.getSendTime() == null) {
            oldNotice.setSendTime(LocalDateTime.now());
        }
        int rows = noticeMapper.updateById(oldNotice);
        if (rows <= 0) {
            throw new BusinessException("修改公告发布状态失败");
        }
        if (updateEnabled.isSend() == 1) {
            deliverIfPublished(oldNotice);
        }
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的公告");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        int rows = noticeMapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("公告不存在或删除失败");
        }
        // 删除公告后同步隐藏用户收件箱记录。
        noticeUserMapper.update(null, new LambdaUpdateWrapper<NoticeUser>()
                .set(NoticeUser::getIsDel, 1)
                .in(NoticeUser::getNoticeId, idList));
    }

    /**
     * 软删除已过期公告（expire_time &lt; now），并同步软删收件箱关联。
     * @return 清理的公告条数
     */
    @Transactional
    public int cleanExpiredNotices() {
        LocalDateTime now = LocalDateTime.now();
        List<Notice> expired = noticeMapper.selectList(new LambdaQueryWrapper<Notice>()
                .isNotNull(Notice::getExpireTime)
                .lt(Notice::getExpireTime, now)
                .select(Notice::getNoticeId));
        if (expired.isEmpty()) {
            return 0;
        }
        List<String> idList = expired.stream().map(Notice::getNoticeId).toList();
        int rows = noticeMapper.deleteByIds(idList);
        noticeUserMapper.update(null, new LambdaUpdateWrapper<NoticeUser>()
                .set(NoticeUser::getIsDel, 1)
                .in(NoticeUser::getNoticeId, idList)
                .eq(NoticeUser::getIsDel, 0));
        return rows;
    }

    /**
     * 按 notice.readDays 软删收件箱已读记录。
     * 缺失/停用或值为 0：不清理；大于 0：按阅读时间（无则投递时间）早于截止时间清理。
     */
    @Transactional
    public int cleanReadInboxByRetentionConfig() {
        Integer days = resolveRetentionDaysOrNull(NoticeConfigCodes.READ_DAYS);
        if (days == null || days <= 0) {
            return 0;
        }
        LocalDateTime deadline = LocalDateTime.now().minusDays(days);
        int deleted = noticeUserMapper.softDeleteReadBefore(deadline);
        if (deleted > 0) {
            log.info("清理已读收件箱 readDays={} deleted={}", days, deleted);
        }
        return deleted;
    }

    /**
     * 按 notice.unreadDays 软删收件箱未读记录。
     * 缺失/停用或值为 0：不清理；大于 0：按投递时间早于截止时间清理。
     */
    @Transactional
    public int cleanUnreadInboxByRetentionConfig() {
        Integer days = resolveRetentionDaysOrNull(NoticeConfigCodes.UNREAD_DAYS);
        if (days == null || days <= 0) {
            return 0;
        }
        LocalDateTime deadline = LocalDateTime.now().minusDays(days);
        int deleted = noticeUserMapper.softDeleteUnreadBefore(deadline);
        if (deleted > 0) {
            log.info("清理未读收件箱 unreadDays={} deleted={}", days, deleted);
        }
        return deleted;
    }

    /** 读取保留天数；缺失/非整数返回 null。 */
    private Integer resolveRetentionDaysOrNull(String configCode) {
        Optional<BigDecimal> daysOpt = configRuntimeService.getNumber(configCode);
        if (daysOpt.isEmpty()) {
            return null;
        }
        try {
            return daysOpt.get().intValueExact();
        } catch (ArithmeticException ex) {
            log.warn("配置读取失败 configCode={} reason=保留天数须为整数 value={}",
                    configCode, daysOpt.get());
            return null;
        }
    }

    public Notice selectById(String noticeId) {
        return noticeMapper.selectById(noticeId);
    }

    private LambdaQueryWrapper<Notice> buildNoticeListWrapper(NoticeDto.NoticeList noticeList) {
        LambdaQueryWrapper<Notice> noticeQueryWrapper = new LambdaQueryWrapper<>();
        if (noticeList == null) {
            return noticeQueryWrapper;
        }
        if (StrUtil.isNotBlank(noticeList.noticeTitle())) {
            noticeQueryWrapper.like(Notice::getNoticeTitle, noticeList.noticeTitle());
        }
        if (noticeList.noticeType() != null) {
            noticeQueryWrapper.eq(Notice::getNoticeType, noticeList.noticeType());
        }
        if (noticeList.isSend() != null) {
            noticeQueryWrapper.eq(Notice::getIsSend, noticeList.isSend());
        }
        return noticeQueryWrapper;
    }

    private void validateNoticeFields(Integer noticeType, Integer receiverType, String receiverIds) {
        dictRuntimeService.validateValue(DictTypeCode.SYS_NOTICE_TYPE, noticeType, "公告类型");
        dictRuntimeService.validateValue(DictTypeCode.SYS_NOTICE_RECEIVER_TYPE, receiverType, "接收者类型");
        if (receiverType == null || receiverType == 1) {
            return;
        }
        if (StrUtil.isBlank(receiverIds)) {
            throw new BusinessException("请选择接收对象");
        }
        List<String> targetIds = StrUtil.splitTrim(receiverIds, ',');
        if (targetIds.isEmpty()) {
            throw new BusinessException("请选择接收对象");
        }
        if (receiverType == 4 && targetIds.size() != 1) {
            throw new BusinessException("指定用户只能选择一名用户");
        }
    }

    private void normalizeReceiverIds(Notice notice) {
        if (notice.getReceiverType() != null && notice.getReceiverType() == 1) {
            notice.setReceiverIds(null);
        }
    }

    private void applyPublishTime(Notice notice) {
        if (notice.getIsSend() != null && notice.getIsSend() == 1) {
            notice.setSendTime(LocalDateTime.now());
        } else {
            notice.setSendTime(null);
        }
    }

    private void deliverIfPublished(Notice notice) {
        if (notice.getIsSend() == null || notice.getIsSend() != 1 || notice.getReceiverType() == null) {
            return;
        }
        List<String> userIds = resolveTargetUserIds(notice);
        if (userIds.isEmpty()) {
            throw new BusinessException("未找到可投递的目标用户");
        }
        List<NoticeUser> noticeUserList = new ArrayList<>();
        for (String userId : userIds) {
            if (existsUserNotice(userId, notice.getNoticeId())) {
                continue;
            }
            NoticeUser noticeUser = new NoticeUser();
            noticeUser.setUserId(userId);
            noticeUser.setNoticeId(notice.getNoticeId());
            noticeUser.setReadState(0);
            noticeUserList.add(noticeUser);
        }
        if (!noticeUserList.isEmpty()) {
            noticeUserMapper.insertBatch(noticeUserList);
        }
    }

    /** 按接收者类型解析目标用户 ID，均从主表 receiver_ids 读取。 */
    private List<String> resolveTargetUserIds(Notice notice) {
        Integer receiverType = notice.getReceiverType();
        if (receiverType == null || receiverType == 1) {
            return listEnabledUserIds(null);
        }
        String receiverIds = notice.getReceiverIds();
        if (StrUtil.isBlank(receiverIds)) {
            return List.of();
        }
        List<String> targetIds = StrUtil.splitTrim(receiverIds, ',');
        if (targetIds.isEmpty()) {
            return List.of();
        }
        if (receiverType == 2) {
            List<RoleUser> roleUsers = roleUserMapper.selectList(new LambdaQueryWrapper<RoleUser>()
                    .in(RoleUser::getRoleId, targetIds));
            if (roleUsers.isEmpty()) {
                return List.of();
            }
            List<String> userIds = roleUsers.stream()
                    .map(RoleUser::getUserId)
                    .distinct()
                    .toList();
            return listEnabledUserIds(userIds);
        }
        if (receiverType == 3) {
            return listEnabledUserIdsByDeptIds(targetIds);
        }
        if (receiverType == 4) {
            return listEnabledUserIds(targetIds);
        }
        return List.of();
    }

    private List<String> listEnabledUserIds(List<String> userIds) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIsEnabled, 1);
        if (userIds != null) {
            if (userIds.isEmpty()) {
                return List.of();
            }
            wrapper.in(User::getUserId, userIds);
        }
        return userMapper.selectList(wrapper).stream()
                .map(User::getUserId)
                .toList();
    }

    private List<String> listEnabledUserIdsByDeptIds(List<String> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return List.of();
        }
        return userMapper.selectList(new LambdaQueryWrapper<User>()
                        .eq(User::getIsEnabled, 1)
                        .in(User::getDeptId, deptIds))
                .stream()
                .map(User::getUserId)
                .distinct()
                .toList();
    }

    private boolean existsUserNotice(String userId, String noticeId) {
        return noticeUserMapper.selectCount(new LambdaQueryWrapper<NoticeUser>()
                .eq(NoticeUser::getUserId, userId)
                .eq(NoticeUser::getNoticeId, noticeId)) > 0;
    }

    private boolean hasNoticeUserRecords(String noticeId) {
        return noticeUserMapper.selectCount(new LambdaQueryWrapper<NoticeUser>()
                .eq(NoticeUser::getNoticeId, noticeId)) > 0;
    }

    /** 批量查询已投递到用户关联表的公告 ID。 */
    private Set<String> findDeliveredNoticeIds(List<String> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return Set.of();
        }
        List<Object> deliveredIds = noticeUserMapper.selectObjs(new LambdaQueryWrapper<NoticeUser>()
                .select(NoticeUser::getNoticeId)
                .in(NoticeUser::getNoticeId, noticeIds)
                .groupBy(NoticeUser::getNoticeId));
        if (deliveredIds == null || deliveredIds.isEmpty()) {
            return Set.of();
        }
        return deliveredIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /** 发布状态只允许草稿发布；已发布或已投递时不允许改回草稿。 */
    private void validateSendStateChange(Notice oldNotice, Integer newIsSend) {
        if (newIsSend == null || newIsSend.equals(oldNotice.getIsSend())) {
            return;
        }
        if (newIsSend != 1) {
            if (oldNotice.getIsSend() != null && oldNotice.getIsSend() == 1) {
                throw new BusinessException("已发布公告不允许改为草稿");
            }
            if (hasNoticeUserRecords(oldNotice.getNoticeId())) {
                throw new BusinessException("公告已投递用户，不允许改为草稿");
            }
        }
    }

    private List<String> getUserNoticeIds(String userId, Integer readState) {
        LambdaQueryWrapper<NoticeUser> noticeUserQueryWrapper = new LambdaQueryWrapper<>();
        noticeUserQueryWrapper.eq(NoticeUser::getUserId, userId)
                .orderByDesc(NoticeUser::getCreateTime);
        if (readState != null) {
            noticeUserQueryWrapper.eq(NoticeUser::getReadState, readState);
        }
        return noticeUserMapper.selectList(noticeUserQueryWrapper).stream()
                .map(NoticeUser::getNoticeId)
                .toList();
    }

    /** 收件箱查询用：未登录返回 null；写操作请用 NoticeUserService.requireCurrentUserId 模式抛错。 */
    private String currentUserIdOrNull() {
        String userId = SaTokenUtil.getUserId();
        return StrUtil.isBlank(userId) ? null : userId;
    }

    /** 公告控制台统计：仅已发布公告可查看。 */
    public NoticeVo.ConsoleStatsVo getConsoleStats(String noticeId) {
        Notice notice = requirePublishedNotice(noticeId);
        NoticeVo.ConsoleCountQueryVo counts = noticeUserMapper.selectConsoleCounts(noticeId);
        long totalCount = nvl(counts == null ? null : counts.getTotalCount());
        long readCount = nvl(counts == null ? null : counts.getReadCount());
        long unreadCount = nvl(counts == null ? null : counts.getUnreadCount());
        return new NoticeVo.ConsoleStatsVo(
                notice.getNoticeId(),
                notice.getNoticeTitle(),
                notice.getNoticeType(),
                dictRuntimeService.getLabel(DictTypeCode.SYS_NOTICE_TYPE, notice.getNoticeType()),
                notice.getReceiverType(),
                dictRuntimeService.getLabel(DictTypeCode.SYS_NOTICE_RECEIVER_TYPE, notice.getReceiverType()),
                resolveReceiverTargetNames(notice),
                notice.getSendTime(),
                notice.getExpireTime(),
                totalCount,
                readCount,
                unreadCount,
                formatReadRate(totalCount, readCount)
        );
    }

    /**
     * 解析控制台展示用的接收目标名称：仅指定角色 / 指定部门有值。
     * 全体用户与指定个人不返回目标列表（个人看接收人明细即可）。
     */
    private List<String> resolveReceiverTargetNames(Notice notice) {
        Integer receiverType = notice.getReceiverType();
        if (receiverType == null || receiverType == 1 || receiverType == 4) {
            return List.of();
        }
        if (StrUtil.isBlank(notice.getReceiverIds())) {
            return List.of();
        }
        List<String> targetIds = StrUtil.splitTrim(notice.getReceiverIds(), ',');
        if (targetIds.isEmpty()) {
            return List.of();
        }
        if (receiverType == 2) {
            Map<String, String> roleNameMap = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                            .in(Role::getRoleId, targetIds))
                    .stream()
                    .collect(Collectors.toMap(Role::getRoleId, Role::getRoleName, (a, b) -> a));
            // 按 receiver_ids 顺序输出，已删角色回退显示 ID。
            return targetIds.stream()
                    .map(id -> roleNameMap.getOrDefault(id, id))
                    .toList();
        }
        if (receiverType == 3) {
            Map<String, String> deptNameMap = deptMapper.selectList(new LambdaQueryWrapper<Dept>()
                            .in(Dept::getDeptId, targetIds))
                    .stream()
                    .collect(Collectors.toMap(Dept::getDeptId, Dept::getDeptName, (a, b) -> a));
            return targetIds.stream()
                    .map(id -> deptNameMap.getOrDefault(id, id))
                    .toList();
        }
        return List.of();
    }

    /** 公告控制台接收人分页：仅已发布公告可查看。 */
    public IPage<NoticeVo.ConsoleReceiverVo> selectConsoleReceiverPage(
            PageQuery pageQuery,
            NoticeDto.ConsoleReceiverList consoleReceiverList
    ) {
        requirePublishedNotice(consoleReceiverList.noticeId());
        Page<NoticeVo.ConsoleReceiverQueryVo> page = pageQuery.toMpPage();
        IPage<NoticeVo.ConsoleReceiverQueryVo> queryPage = noticeUserMapper.selectConsoleReceiverPage(
                page,
                consoleReceiverList.noticeId(),
                consoleReceiverList.readState());
        // 部门表与用户表排序规则不一致，避免 SQL JOIN，改为内存补部门名。
        Map<String, String> deptNameMap = loadDeptNameMap(queryPage.getRecords());
        Page<NoticeVo.ConsoleReceiverVo> voPage = new Page<>(queryPage.getCurrent(), queryPage.getSize(), queryPage.getTotal());
        voPage.setRecords(queryPage.getRecords().stream()
                .map(row -> NoticeVo.ConsoleReceiverVo.from(row, deptNameMap.get(row.getDeptId())))
                .toList());
        return voPage;
    }

    /** 批量解析接收人部门名称。 */
    private Map<String, String> loadDeptNameMap(List<NoticeVo.ConsoleReceiverQueryVo> rows) {
        Set<String> deptIds = rows.stream()
                .map(NoticeVo.ConsoleReceiverQueryVo::getDeptId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toCollection(HashSet::new));
        if (deptIds.isEmpty()) {
            return Map.of();
        }
        return deptMapper.selectList(new LambdaQueryWrapper<Dept>().in(Dept::getDeptId, deptIds)).stream()
                .collect(Collectors.toMap(Dept::getDeptId, Dept::getDeptName, (a, b) -> a));
    }

    /** 控制台只允许查看已发布公告。 */
    private Notice requirePublishedNotice(String noticeId) {
        if (StrUtil.isBlank(noticeId)) {
            throw new BusinessException("公告ID不能为空");
        }
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        if (notice.getIsSend() == null || notice.getIsSend() != 1) {
            throw new BusinessException("仅已发布公告可查看控制台");
        }
        return notice;
    }

    private static String formatReadRate(long totalCount, long readCount) {
        if (totalCount <= 0L) {
            return "0%";
        }
        return Math.round(readCount * 100.0d / totalCount) + "%";
    }

    private static long nvl(Long value) {
        return value != null ? value : 0L;
    }
}
