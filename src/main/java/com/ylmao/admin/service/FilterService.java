package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.common.FilterCodes;
import com.ylmao.admin.common.SecurityConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.FilterDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.Filter;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.FilterMapper;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.vo.FilterVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterService {

    private static final Logger log = LoggerFactory.getLogger(FilterService.class);

    private final FilterMapper filterMapper;
    private final UserMapper userMapper;
    private final ConfigRuntimeService configRuntimeService;

    public IPage<FilterVo.FilterListVo> selectPage(PageQuery pageQuery, FilterDto.FilterList query) {
        Page<Filter> page = pageQuery.toMpPage();
        page.addOrder(OrderItem.desc("create_time"));
        LambdaQueryWrapper<Filter> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StrUtil.isNotBlank(query.filterType())) {
                wrapper.eq(Filter::getFilterType, query.filterType());
            }
            if (StrUtil.isNotBlank(query.filterValue())) {
                wrapper.like(Filter::getFilterValue, query.filterValue());
            }
            if (StrUtil.isNotBlank(query.filterSource())) {
                wrapper.eq(Filter::getFilterSource, query.filterSource());
            }
            if (StrUtil.isNotBlank(query.policyMode())) {
                wrapper.eq(Filter::getPolicyMode, query.policyMode());
            }
            if (query.isEnabled() != null) {
                wrapper.eq(Filter::getIsEnabled, query.isEnabled());
            }
        }
        filterMapper.selectPage(page, wrapper);
        Map<String, String> userLabelMap = loadUserLabels(page.getRecords());
        Page<FilterVo.FilterListVo> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream()
                .map(row -> FilterVo.FilterListVo.from(row, resolveValueLabel(row, userLabelMap)))
                .toList());
        return voPage;
    }

    @Transactional
    public void insert(FilterDto.FilterInsert dto) {
        validateTypeValueMode(dto.filterType(), dto.filterValue(), dto.policyMode());
        if (findActiveByTypeAndValue(dto.filterType(), dto.filterValue()) != null) {
            throw new BusinessException("该访问控制记录已存在");
        }
        Filter row = new Filter(dto);
        if (row.getExpireTime() == null) {
            row.setExpireTime(FilterCodes.PERMANENT_EXPIRE);
        }
        int rows = filterMapper.insert(row);
        if (rows <= 0) {
            throw new BusinessException("新增访问控制失败");
        }
    }

    @Transactional
    public void update(FilterDto.FilterUpdate dto) {
        validateTypeValueMode(dto.filterType(), dto.filterValue(), dto.policyMode());
        Filter old = filterMapper.selectById(dto.filterId());
        if (old == null) {
            throw new BusinessException("访问控制不存在");
        }
        Filter conflict = findActiveByTypeAndValue(dto.filterType(), dto.filterValue());
        if (conflict != null && !conflict.getFilterId().equals(dto.filterId())) {
            throw new BusinessException("该访问控制记录已存在");
        }
        Filter row = new Filter(dto);
        // 修改不改来源，保留原人工/自动标记。
        row.setFilterSource(old.getFilterSource());
        int rows = filterMapper.updateById(row);
        if (rows <= 0) {
            throw new BusinessException("修改访问控制失败");
        }
    }

    @Transactional
    public void updateEnabled(FilterDto.UpdateEnabled dto) {
        Filter old = filterMapper.selectById(dto.filterId());
        if (old == null) {
            throw new BusinessException("访问控制不存在");
        }
        old.setIsEnabled(dto.isEnabled());
        int rows = filterMapper.updateById(old);
        if (rows <= 0) {
            throw new BusinessException("修改访问控制状态失败");
        }
    }

    @Transactional
    public void deleteByIds(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的访问控制");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        int rows = filterMapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("访问控制不存在或删除失败");
        }
    }

    /** 登录拦截：是否存在生效中的黑名单。 */
    public boolean isBlocked(String filterType, String filterValue) {
        return existsActive(FilterCodes.MODE_BLACK, filterType, filterValue);
    }

    /** IP 白名单：仅跳过 IP 失败计数与自动拉黑。 */
    public boolean isIpWhitelisted(String ip) {
        if (StrUtil.isBlank(ip)) {
            return false;
        }
        return existsActive(FilterCodes.MODE_WHITE, FilterCodes.TYPE_IP, ip);
    }

    /**
     * IP 登录失败达阈值整数倍时自动拉黑；
     * 续期在未过期原时间上再加配置分钟数（已过期则从当前起算）。
     * autoBanMin=0 时不自动拉黑。
     */
    @Transactional
    public void banIpAuto(String ip, int ipFailCount, String failScene) {
        if (StrUtil.isBlank(ip)) {
            log.warn("登录失败自动拉黑跳过：IP 为空");
            return;
        }
        if (isIpWhitelisted(ip)) {
            log.info("登录失败自动拉黑跳过：IP[{}]在白名单", ip);
            return;
        }
        int ipLimit = configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.IP_FAIL_LIMIT);
        if (ipFailCount <= 0 || ipLimit <= 0 || ipFailCount % ipLimit != 0) {
            log.warn("登录失败自动拉黑跳过：IP[{}]次数={} 阈值={}", ip, ipFailCount, ipLimit);
            return;
        }
        // autoBanMin=0：关闭自动拉黑。
        int banMinutes = configRuntimeService.requireNonNegativeInt(SecurityConfigCodes.AUTO_BAN_MINUTES);
        if (banMinutes == 0) {
            log.info("登录失败自动拉黑跳过：autoBanMin=0 ip={} count={}", ip, ipFailCount);
            return;
        }
        String filterDesc = buildIpAutoBanReason(ipFailCount, ipLimit, failScene, false);
        LocalDateTime now = LocalDateTime.now();
        Filter existing = findActiveByTypeAndValue(FilterCodes.TYPE_IP, ip);
        if (existing != null) {
            if (FilterCodes.MODE_WHITE.equals(existing.getPolicyMode())) {
                log.info("登录失败自动拉黑跳过：IP[{}]记录为白名单", ip);
                return;
            }
            LocalDateTime base = existing.getExpireTime() != null && existing.getExpireTime().isAfter(now)
                    ? existing.getExpireTime()
                    : now;
            existing.setExpireTime(base.plusMinutes(banMinutes));
            existing.setIsEnabled(1);
            existing.setPolicyMode(FilterCodes.MODE_BLACK);
            if (StrUtil.isBlank(existing.getFilterSource())) {
                existing.setFilterSource(FilterCodes.SOURCE_AUTO);
            }
            existing.setFilterDesc(filterDesc);
            filterMapper.updateById(existing);
            return;
        }
        Filter row = new Filter();
        row.setFilterType(FilterCodes.TYPE_IP);
        row.setFilterValue(ip);
        row.setFilterSource(FilterCodes.SOURCE_AUTO);
        row.setPolicyMode(FilterCodes.MODE_BLACK);
        row.setFilterDesc(buildIpAutoBanReason(ipFailCount, ipLimit, failScene, true));
        row.setExpireTime(now.plusMinutes(banMinutes));
        row.setIsEnabled(1);
        filterMapper.insert(row);
    }

    private static String buildIpAutoBanReason(int ipFailCount, int ipLimit, String failScene, boolean newRecord) {
        String ipPart;
        if (newRecord) {
            ipPart = "IP鉴权失败累计" + ipFailCount + "次，首次拉黑";
        } else {
            int extendTimes = Math.max(1, ipFailCount / Math.max(ipLimit, 1) - 1);
            ipPart = "IP鉴权失败累计" + ipFailCount + "次，第" + extendTimes + "次延长";
        }
        if (StrUtil.isBlank(failScene)) {
            return ipPart;
        }
        return failScene.trim() + "；" + ipPart;
    }

    @Transactional
    public int cleanExpiredForLogin(String ip, String userId) {
        if (StrUtil.isBlank(ip) && StrUtil.isBlank(userId)) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Filter> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Filter::getExpireTime, now)
                .and(w -> {
                    boolean appended = false;
                    if (StrUtil.isNotBlank(ip)) {
                        w.nested(n -> n.eq(Filter::getFilterType, FilterCodes.TYPE_IP)
                                .eq(Filter::getFilterValue, ip));
                        appended = true;
                    }
                    if (StrUtil.isNotBlank(userId)) {
                        if (appended) {
                            w.or();
                        }
                        w.nested(n -> n.eq(Filter::getFilterType, FilterCodes.TYPE_USER_ID)
                                .eq(Filter::getFilterValue, userId));
                    }
                });
        return filterMapper.delete(wrapper);
    }

    private boolean existsActive(String policyMode, String filterType, String filterValue) {
        if (StrUtil.isBlank(policyMode) || StrUtil.isBlank(filterType) || StrUtil.isBlank(filterValue)) {
            return false;
        }
        LambdaQueryWrapper<Filter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Filter::getPolicyMode, policyMode)
                .eq(Filter::getFilterType, filterType)
                .eq(Filter::getFilterValue, filterValue)
                .eq(Filter::getIsEnabled, 1)
                .gt(Filter::getExpireTime, LocalDateTime.now())
                .orderByDesc(Filter::getCreateTime)
                .last("limit 1");
        return filterMapper.selectOne(wrapper) != null;
    }

    private Filter findActiveByTypeAndValue(String type, String value) {
        LambdaQueryWrapper<Filter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Filter::getFilterType, type)
                .eq(Filter::getFilterValue, value)
                .orderByDesc(Filter::getCreateTime)
                .last("limit 1");
        return filterMapper.selectOne(wrapper);
    }

    private void validateTypeValueMode(String type, String value, String policyMode) {
        if (!FilterCodes.TYPE_IP.equals(type)
                && !FilterCodes.TYPE_USER_ID.equals(type)
                && !FilterCodes.TYPE_DEVICE.equals(type)) {
            throw new BusinessException("参数不合法");
        }
        if (!FilterCodes.MODE_BLACK.equals(policyMode) && !FilterCodes.MODE_WHITE.equals(policyMode)) {
            throw new BusinessException("参数不合法");
        }
        if (StrUtil.isBlank(value)) {
            throw new BusinessException("过滤值不能为空");
        }
        // 白名单仅开放 IP。
        if (FilterCodes.MODE_WHITE.equals(policyMode) && !FilterCodes.TYPE_IP.equals(type)) {
            throw new BusinessException("白名单仅支持 IP");
        }
        if (FilterCodes.TYPE_USER_ID.equals(type) && userMapper.selectById(value) == null) {
            throw new BusinessException("用户不存在");
        }
    }

    private Map<String, String> loadUserLabels(List<Filter> rows) {
        Set<String> userIds = rows.stream()
                .filter(r -> FilterCodes.TYPE_USER_ID.equals(r.getFilterType()))
                .map(Filter::getFilterValue)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Map.of();
        }
        Map<String, String> map = new HashMap<>();
        for (User user : userMapper.selectByIds(userIds)) {
            if (user != null) {
                String label = StrUtil.blankToDefault(user.getUserName(), "")
                        + "（" + StrUtil.blankToDefault(user.getUserAccount(), user.getUserId()) + "）";
                map.put(user.getUserId(), label.trim());
            }
        }
        return map;
    }

    private String resolveValueLabel(Filter row, Map<String, String> userLabelMap) {
        if (FilterCodes.TYPE_USER_ID.equals(row.getFilterType())) {
            return userLabelMap.getOrDefault(row.getFilterValue(), row.getFilterValue());
        }
        return row.getFilterValue();
    }
}
