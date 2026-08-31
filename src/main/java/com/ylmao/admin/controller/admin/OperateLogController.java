package com.ylmao.admin.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.OperateLogDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.OperateLogService;
import com.ylmao.admin.vo.OperateLogVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/operateLog")
@RequiredArgsConstructor
public class OperateLogController extends BaseController {

    private static final String LOG_VIEW = "system/log";

    private final OperateLogService operateLogService;

    @Log(title = "日志页面跳转", businessType = "QUERY")
    @SaCheckPermission("system:log:view")
    @GetMapping("/listView")
    public String listView(ModelMap model) {
        return LOG_VIEW;
    }

    @Log(title = "日志分页查询", businessType = "QUERY")
    @SaCheckPermission("system:log:select")
    @GetMapping("/list")
    @ResponseBody
    public R<?> operateLogList(@Valid PageQuery pageQuery, @Valid OperateLogDto.OperateLogList operateLogList) {
        // Controller 只接收分页和 DTO 入参，列表出口统一返回 VO。
        IPage<OperateLogVo.OperateLogListVo> operateLogPage = operateLogService.selectPageList(pageQuery, operateLogList);
        return pageData(operateLogPage.getRecords(), operateLogPage.getTotal());
    }

    /** 按已保存的保留天数清理过期日志；0 天由 Service 提示且不删。 */
    @Log(title = "按保留天数清理日志", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:config:log")
    @DeleteMapping("/cleanByRetention")
    @ResponseBody
    public R<?> operateLogCleanByRetention() {
        int deleted = operateLogService.cleanExpiredByRetentionManual();
        return R.ok("已删除 " + deleted + " 条过期日志", deleted);
    }
}
