package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.LogConfigCodes;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.OperateLogDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.OperateLog;
import com.ylmao.admin.mapper.OperateLogMapper;
import com.ylmao.admin.vo.OperateLogVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperateLogService {

    private static final Logger log = LoggerFactory.getLogger(OperateLogService.class);

    private final OperateLogMapper operateLogMapper;
    private final ConfigRuntimeService configRuntimeService;

    /**
     * 获取最新10条日志
     * @return List<TsysOperLog>
     */
    public List<OperateLog> getNEW(){
        LambdaQueryWrapper<OperateLog> operateLogQueryWrapper = new LambdaQueryWrapper<>();
        operateLogQueryWrapper.orderByDesc(OperateLog::getOperateTime);
        // MySQL：limit 10 取最新 10 条；limit 1,10 会跳过最新 1 条。
        operateLogQueryWrapper.last("limit 10");
        return operateLogMapper.selectList(operateLogQueryWrapper);
    }

    public IPage<OperateLogVo.OperateLogListVo> selectPageList(PageQuery pageQuery, OperateLogDto.OperateLogList operateLogList){
        LambdaQueryWrapper<OperateLog> operateLogQueryWrapper = new LambdaQueryWrapper<>();
        // 按页面筛选条件组合查询，空条件不参与 SQL。
        if (StrUtil.isNotBlank(operateLogList.operateTitle())) {
            // 配置变更页签需要精确标题；其它页签仍按模糊搜模块名。
            if (Boolean.TRUE.equals(operateLogList.operateTitleExact())) {
                operateLogQueryWrapper.eq(OperateLog::getOperateTitle, operateLogList.operateTitle());
            } else {
                operateLogQueryWrapper.like(OperateLog::getOperateTitle, operateLogList.operateTitle());
            }
        }
        operateLogQueryWrapper.eq(StrUtil.isNotBlank(operateLogList.loggingType()), OperateLog::getLoggingType, operateLogList.loggingType());
        operateLogQueryWrapper.eq(operateLogList.isSuccess() != null, OperateLog::getIsSuccess, operateLogList.isSuccess());
        operateLogQueryWrapper.ge(StrUtil.isNotBlank(operateLogList.startTime()), OperateLog::getOperateTime, operateLogList.startTime());
        operateLogQueryWrapper.le(StrUtil.isNotBlank(operateLogList.endTime()), OperateLog::getOperateTime, operateLogList.endTime());
        operateLogQueryWrapper.like(StrUtil.isNotBlank(operateLogList.operateName()), OperateLog::getOperateName, operateLogList.operateName());
        operateLogQueryWrapper.eq(StrUtil.isNotBlank(operateLogList.businessType()), OperateLog::getBusinessType, operateLogList.businessType());
        operateLogQueryWrapper.like(StrUtil.isNotBlank(operateLogList.requestUri()), OperateLog::getRequestUri, operateLogList.requestUri());
        operateLogQueryWrapper.like(StrUtil.isNotBlank(operateLogList.operateIp()), OperateLog::getOperateIp, operateLogList.operateIp());
        operateLogQueryWrapper.orderByDesc(OperateLog::getOperateTime);
        return operateLogMapper.selectPage(pageQuery.toMpPage(),operateLogQueryWrapper).convert(OperateLogVo.OperateLogListVo::from);
    }


    @Transactional
    public void insertOperateLog(OperateLog operateLog){
        operateLogMapper.insert(operateLog);
    }

    /**
     * 按 log.retainDays 清理过期日志。
     * 缺失/停用或值为 0：不清理；大于 0：删除 operate_time 早于截止时间的记录。
     * @return 实际删除条数（未执行清理时为 0）
     */
    @Transactional
    public int cleanExpiredByRetentionConfig() {
        Integer days = resolveRetentionDaysOrNull();
        if (days == null || days <= 0) {
            return 0;
        }
        int deleted = deleteExpiredBeforeDays(days);
        if (deleted > 0) {
            log.info("清理过期操作日志 retentionDays={} deleted={}", days, deleted);
        }
        return deleted;
    }

    /**
     * 手动按保留天数清理：0 / 缺失提示且不删；大于 0 删除过期记录并返回条数。
     */
    @Transactional
    public int cleanExpiredByRetentionManual() {
        Integer days = resolveRetentionDaysOrNull();
        if (days == null) {
            throw new BusinessException("请先配置日志保留天数");
        }
        if (days <= 0) {
            throw new BusinessException("保留天数为 0，不执行删除");
        }
        int deleted = deleteExpiredBeforeDays(days);
        log.info("手动清理过期操作日志 retentionDays={} deleted={}", days, deleted);
        return deleted;
    }

    /** 读取保留天数；缺失/非整数返回 null。 */
    private Integer resolveRetentionDaysOrNull() {
        Optional<BigDecimal> daysOpt = configRuntimeService.getNumber(LogConfigCodes.RETENTION_DAYS);
        if (daysOpt.isEmpty()) {
            return null;
        }
        try {
            return daysOpt.get().intValueExact();
        } catch (ArithmeticException ex) {
            log.warn("配置读取失败 configCode={} reason=保留天数须为整数 value={}",
                    LogConfigCodes.RETENTION_DAYS, daysOpt.get());
            return null;
        }
    }

    private int deleteExpiredBeforeDays(int days) {
        LocalDateTime deadline = LocalDateTime.now().minusDays(days);
        return operateLogMapper.delete(new LambdaQueryWrapper<OperateLog>()
                .lt(OperateLog::getOperateTime, deadline));
    }
}
