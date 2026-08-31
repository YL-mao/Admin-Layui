package com.ylmao.admin.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.common.OnlineSessionKeys;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.dto.OnlineDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.vo.OnlineVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OnlineService {

    private static final int TOKEN_DISPLAY_HEAD = 8;
    private static final int TOKEN_DISPLAY_TAIL = 6;

    private final UserMapper userMapper;

    /**
     * 以 Sa-Token 有效 Token 为主集：先扫会话再补用户信息，再按账号/IP 过滤后分页。
     * 会话存 Redis，多实例下在线列表与强退全局可见。
     */
    public IPage<OnlineVo.OnlineListVo> selectPage(PageQuery pageQuery, OnlineDto.OnlineList query) {
        String accountFilter = query != null ? StrUtil.trim(query.userAccount()) : null;
        String ipFilter = query != null ? StrUtil.trim(query.loginIp()) : null;
        String currentToken = StpUtil.getTokenValue();
        List<TokenRow> rows = listTokenRows(ipFilter);

        Map<String, User> userById = loadUsers(rows.stream().map(TokenRow::userId).distinct().toList());
        List<OnlineVo.OnlineListVo> all = new ArrayList<>();
        for (TokenRow row : rows) {
            User user = userById.get(row.userId());
            String userAccount = user != null ? user.getUserAccount() : "";
            String userName = user != null ? user.getUserName() : "";
            if (StrUtil.isNotBlank(accountFilter)
                    && !StrUtil.containsIgnoreCase(userAccount, accountFilter)
                    && !StrUtil.containsIgnoreCase(StrUtil.blankToDefault(userName, ""), accountFilter)) {
                continue;
            }
            all.add(new OnlineVo.OnlineListVo(
                    row.tokenValue(),
                    truncateToken(row.tokenValue()),
                    row.userId(),
                    userAccount,
                    userName,
                    StrUtil.blankToDefault(row.loginIp(), ""),
                    StrUtil.blankToDefault(row.loginTime(), ""),
                    StrUtil.blankToDefault(row.browser(), ""),
                    StrUtil.blankToDefault(row.systemOs(), ""),
                    row.timeoutSeconds(),
                    formatTimeout(row.timeoutSeconds()),
                    Objects.equals(currentToken, row.tokenValue())
            ));
        }

        int page = pageQuery.getPage() == null ? 1 : pageQuery.getPage();
        int limit = pageQuery.getLimit() == null ? 10 : pageQuery.getLimit();
        int from = Math.max((page - 1) * limit, 0);
        int to = Math.min(from + limit, all.size());
        List<OnlineVo.OnlineListVo> pageRecords = from >= all.size() ? List.of() : all.subList(from, to);

        Page<OnlineVo.OnlineListVo> voPage = new Page<>(page, limit, all.size());
        voPage.setRecords(pageRecords);
        return voPage;
    }

    /** 按 Token 强退；禁止踢当前会话。 */
    public void kickByToken(OnlineDto.OnlineKick kick) {
        // DTO 已 @NotBlank；此处再 trim，避免首尾空白绕过强退目标。
        String tokenValue = StrUtil.trim(kick.tokenValue());
        String currentToken = StpUtil.getTokenValue();
        if (StrUtil.equals(tokenValue, currentToken)) {
            throw new BusinessException("不能强退当前登录会话");
        }
        Object loginId = StpUtil.getLoginIdByToken(tokenValue);
        if (loginId == null) {
            throw new BusinessException("会话不存在或已下线");
        }
        StpUtil.logoutByTokenValue(tokenValue);
    }

    /** 按用户 ID 强退全部会话；在线用户页与用户列表共用该能力。 */
    public void kickByUserId(OnlineDto.OnlineKickUser kickUser) {
        String userId = StrUtil.trim(kickUser.userId());
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException("用户不存在");
        }
        if (StrUtil.equals(userId, SaTokenUtil.getUserId())) {
            throw new BusinessException("不能踢出当前登录用户的全部会话");
        }
        if (!listOnlineUserIds().contains(userId)) {
            throw new BusinessException("用户已离线");
        }
        StpUtil.logout(userId);
    }

    /** 用户列表按开关展示在线状态时，只需要用户维度是否存在有效会话。 */
    public Set<String> listOnlineUserIds() {
        List<TokenRow> rows = listTokenRows(null);
        if (rows.isEmpty()) {
            return Set.of();
        }
        return rows.stream().map(TokenRow::userId).collect(java.util.stream.Collectors.toSet());
    }

    private Map<String, User> loadUsers(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        List<User> users = userMapper.selectByIds(userIds);
        if (users == null || users.isEmpty()) {
            return Map.of();
        }
        Map<String, User> map = new HashMap<>();
        for (User user : users) {
            if (user != null && StrUtil.isNotBlank(user.getUserId())) {
                map.put(user.getUserId(), user);
            }
        }
        return map;
    }

    private String toTokenValue(String tokenKey) {
        if (StrUtil.isBlank(tokenKey)) {
            return "";
        }
        String prefix = StpUtil.getStpLogic().splicingKeyTokenValue("");
        if (StrUtil.isNotBlank(prefix) && tokenKey.startsWith(prefix)) {
            return tokenKey.substring(prefix.length());
        }
        int idx = tokenKey.lastIndexOf(':');
        return idx >= 0 ? tokenKey.substring(idx + 1) : tokenKey;
    }

    private List<TokenRow> listTokenRows(String ipFilter) {
        List<String> tokenKeys = StpUtil.searchTokenValue("", 0, -1, false);
        if (tokenKeys == null || tokenKeys.isEmpty()) {
            return List.of();
        }
        List<TokenRow> rows = new ArrayList<>();
        for (String tokenKey : tokenKeys) {
            String tokenValue = toTokenValue(tokenKey);
            if (StrUtil.isBlank(tokenValue)) {
                continue;
            }
            Object loginIdObj = StpUtil.getLoginIdByToken(tokenValue);
            if (loginIdObj == null) {
                continue;
            }
            String userId = String.valueOf(loginIdObj);
            // isCreate=false：列表查询不得顺带创建空 Token-Session。
            SaSession tokenSession = StpUtil.getStpLogic().getTokenSessionByToken(tokenValue, false);
            String loginIp = tokenSession != null ? tokenSession.getString(OnlineSessionKeys.IP) : null;
            if (StrUtil.isNotBlank(ipFilter) && !StrUtil.containsIgnoreCase(StrUtil.blankToDefault(loginIp, ""), ipFilter)) {
                continue;
            }
            rows.add(new TokenRow(
                    tokenValue,
                    userId,
                    loginIp,
                    tokenSession != null ? tokenSession.getString(OnlineSessionKeys.LOGIN_TIME) : null,
                    tokenSession != null ? tokenSession.getString(OnlineSessionKeys.BROWSER) : null,
                    tokenSession != null ? tokenSession.getString(OnlineSessionKeys.OS) : null,
                    StpUtil.getTokenTimeout(tokenValue)
            ));
        }
        return rows;
    }

    private static String truncateToken(String tokenValue) {
        if (StrUtil.isBlank(tokenValue)) {
            return "";
        }
        if (tokenValue.length() <= TOKEN_DISPLAY_HEAD + TOKEN_DISPLAY_TAIL + 3) {
            return tokenValue;
        }
        return tokenValue.substring(0, TOKEN_DISPLAY_HEAD)
                + "..."
                + tokenValue.substring(tokenValue.length() - TOKEN_DISPLAY_TAIL);
    }

    private static String formatTimeout(long seconds) {
        if (seconds == -1) {
            return "永久";
        }
        if (seconds < 0) {
            return "—";
        }
        long day = seconds / 86400;
        long hour = (seconds % 86400) / 3600;
        long minute = (seconds % 3600) / 60;
        if (day > 0) {
            return day + "天" + (hour > 0 ? hour + "小时" : "");
        }
        if (hour > 0) {
            return hour + "小时" + (minute > 0 ? minute + "分" : "");
        }
        if (minute > 0) {
            return minute + "分";
        }
        return seconds + "秒";
    }

    private record TokenRow(
            String tokenValue,
            String userId,
            String loginIp,
            String loginTime,
            String browser,
            String systemOs,
            long timeoutSeconds
    ) {
    }
}
