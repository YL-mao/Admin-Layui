package com.ylmao.admin.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aizuda.monitor.DiskInfo;
import com.aizuda.monitor.OshiMonitor;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.entity.Notice;
import com.ylmao.admin.entity.OperateLog;
import com.ylmao.admin.service.NoticeService;
import com.ylmao.admin.service.OperateLogService;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * 后台首页：工作台监控、最新公告与最近操作日志。
 */
@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private static final DateTimeFormatter START_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 后台首页模板。 */
    private static final String HOME_VIEW = "admin/home";

    private final NoticeService noticeService;
    private final OperateLogService operateLogService;
    private final OshiMonitor oshiMonitor;
    private final DataSource dataSource;
    private final ZoneId appZoneId;

    @Log(title = "后台首页", businessType = "QUERY")
    @SaCheckPermission("home:view")
    @GetMapping("/view")
    public String homeView(ModelMap model) {
        // 业务数据与监控数据分开组装，监控失败时页面仍可打开。
        fillWorkbenchData(model);
        fillMonitorData(model);
        fillJvmRuntimeData(model);
        fillDataSourceData(model);
        return HOME_VIEW;
    }

    private void fillWorkbenchData(ModelMap model) {
        List<Notice> noticeList;
        List<OperateLog> operateLogList;
        try {
            noticeList = noticeService.getNEW();
        } catch (Exception e) {
            log.warn("加载最新公告失败", e);
            noticeList = Collections.emptyList();
        }
        try {
            operateLogList = operateLogService.getNEW();
        } catch (Exception e) {
            log.warn("加载最近操作日志失败", e);
            operateLogList = Collections.emptyList();
        }
        model.addAttribute("sysNotices", noticeList != null ? noticeList : Collections.emptyList());
        model.addAttribute("sysOperLog", operateLogList != null ? operateLogList : Collections.emptyList());
    }

    private void fillMonitorData(ModelMap model) {
        // 默认占位，避免模板空指针；读取失败时页面降级展示。
        model.addAttribute("monitorOk", false);
        model.addAttribute("runtime", "-");
        model.addAttribute("cpuUsePercent", "-");
        model.addAttribute("jvmUsePercent", "-");
        model.addAttribute("memUsePercent", "-");
        model.addAttribute("jvmUsedMemory", "-");
        model.addAttribute("jvmTotalMemory", "-");
        model.addAttribute("memUsed", "-");
        model.addAttribute("memTotal", "-");
        model.addAttribute("osName", "-");
        model.addAttribute("osIp", "-");
        model.addAttribute("jdkName", "-");
        model.addAttribute("jdkVersion", "-");
        model.addAttribute("diskInfos", Collections.emptyList());
        model.addAttribute("diskUsePercent", "-");

        try {
            var jvm = oshiMonitor.getJvmInfo();
            var cpu = oshiMonitor.getCpuInfo();
            var mem = oshiMonitor.getMemoryInfo();
            var sys = oshiMonitor.getSysInfo();
            List<DiskInfo> diskInfos = oshiMonitor.getDiskInfos();

            model.addAttribute("runtime", formatUptime(jvm.getUptime()));
            model.addAttribute("cpuUsePercent", formatPercent(cpu.getUsePercent()));
            model.addAttribute("jvmUsePercent", formatPercent(jvm.getUsePercent()));
            model.addAttribute("memUsePercent", formatPercent(mem.getUsePercent()));
            model.addAttribute("jvmUsedMemory", nullToDash(jvm.getUsedMemory()));
            model.addAttribute("jvmTotalMemory", nullToDash(jvm.getJvmTotalMemory()));
            model.addAttribute("memUsed", nullToDash(mem.getUsed()));
            model.addAttribute("memTotal", nullToDash(mem.getTotal()));
            model.addAttribute("osName", nullToDash(sys.getOsName()));
            model.addAttribute("osIp", nullToDash(sys.getIp()));
            model.addAttribute("jdkName", nullToDash(jvm.getJdkName()));
            model.addAttribute("jdkVersion", nullToDash(jvm.getJdkVersion()));
            model.addAttribute("diskInfos", diskInfos != null ? diskInfos : Collections.emptyList());
            model.addAttribute("diskUsePercent", calcDiskUsePercent(diskInfos));
            model.addAttribute("monitorOk", true);
        } catch (Exception e) {
            log.warn("加载系统监控失败，首页降级展示", e);
        }
    }

    /** 应用启动时间与线程信息，独立于 Oshi，避免互相拖累。 */
    private void fillJvmRuntimeData(ModelMap model) {
        model.addAttribute("appStartTime", "-");
        model.addAttribute("threadCount", "-");
        model.addAttribute("daemonThreadCount", "-");
        model.addAttribute("peakThreadCount", "-");
        try {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            LocalDateTime startTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(runtimeMXBean.getStartTime()), appZoneId);
            model.addAttribute("appStartTime", START_TIME_FMT.format(startTime));

            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            model.addAttribute("threadCount", String.valueOf(threadMXBean.getThreadCount()));
            model.addAttribute("daemonThreadCount", String.valueOf(threadMXBean.getDaemonThreadCount()));
            model.addAttribute("peakThreadCount", String.valueOf(threadMXBean.getPeakThreadCount()));
        } catch (Exception e) {
            log.warn("加载 JVM 运行时信息失败", e);
        }
    }

    /** Hikari 连接池状态；非 Hikari 数据源时降级显示。 */
    private void fillDataSourceData(ModelMap model) {
        model.addAttribute("dsPoolOk", false);
        model.addAttribute("dsActive", "-");
        model.addAttribute("dsIdle", "-");
        model.addAttribute("dsTotal", "-");
        model.addAttribute("dsWaiting", "-");
        model.addAttribute("dsMaxPoolSize", "-");
        try {
            if (!(dataSource instanceof HikariDataSource hikari)) {
                model.addAttribute("dsPoolOk", false);
                return;
            }
            HikariPoolMXBean pool = hikari.getHikariPoolMXBean();
            if (pool == null) {
                return;
            }
            model.addAttribute("dsActive", String.valueOf(pool.getActiveConnections()));
            model.addAttribute("dsIdle", String.valueOf(pool.getIdleConnections()));
            model.addAttribute("dsTotal", String.valueOf(pool.getTotalConnections()));
            model.addAttribute("dsWaiting", String.valueOf(pool.getThreadsAwaitingConnection()));
            model.addAttribute("dsMaxPoolSize", String.valueOf(hikari.getMaximumPoolSize()));
            model.addAttribute("dsPoolOk", true);
        } catch (Exception e) {
            log.warn("加载数据源连接池状态失败", e);
        }
    }

    private String formatUptime(long uptimeMs) {
        long nd = 1000L * 24 * 60 * 60;
        long nh = 1000L * 60 * 60;
        long nm = 1000L * 60;
        long day = uptimeMs / nd;
        long hour = uptimeMs % nd / nh;
        long min = uptimeMs % nd % nh / nm;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    private String formatPercent(Double value) {
        if (value == null) {
            return "-";
        }
        return String.format("%.1f", value);
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String calcDiskUsePercent(List<DiskInfo> diskInfos) {
        if (diskInfos == null || diskInfos.isEmpty()) {
            return "-";
        }
        long usableSpace = 0;
        long totalSpace = 0;
        for (DiskInfo diskInfo : diskInfos) {
            usableSpace += diskInfo.getUsableSpace();
            totalSpace += diskInfo.getTotalSpace();
        }
        if (totalSpace <= 0) {
            return "-";
        }
        double usedSize = totalSpace - usableSpace;
        return formatPercent(oshiMonitor.formatDouble(usedSize / totalSpace * 100));
    }
}
