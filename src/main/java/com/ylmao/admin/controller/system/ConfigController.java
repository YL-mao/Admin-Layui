package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.ConfigAuditCodes;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.ConfigDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.ConfigService;
import com.ylmao.admin.vo.ConfigVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController extends BaseController {

    private static final String CONFIG_VIEW = "system/config";

    private final ConfigService configService;

    @Log(title = "系统配置页面", businessType = "QUERY")
    @SaCheckPermission("system:config:view")
    @GetMapping("/listView")
    public String configListView(ModelMap model) {
        return CONFIG_VIEW;
    }

    @Log(title = "系统配置分页查询", businessType = "QUERY")
    @SaCheckPermission("system:config:maintain")
    @GetMapping("/list")
    @ResponseBody
    public R<?> configList(@Valid PageQuery pageQuery, @Valid ConfigDto.ConfigList configList) {
        IPage<ConfigVo.ConfigListVo> configPage = configService.selectPage(pageQuery, configList);
        return pageData(configPage.getRecords(), configPage.getTotal());
    }

    @Log(title = "系统配置分组查询", businessType = "QUERY")
    @SaCheckPermission("system:config:maintain")
    @GetMapping("/groups")
    @ResponseBody
    public R<?> configGroups(@Valid ConfigDto.GroupList groupList) {
        String configGroup = groupList == null ? null : groupList.configGroup();
        List<ConfigVo.ConfigGroupVo> groups = configService.selectGroupList(configGroup);
        return pageData(groups, groups.size());
    }

    @Log(title = "系统配置分组明细", businessType = "QUERY")
    // 上传配置入口已迁到文件页，允许仅持有分组权限（无需 system:config:view）访问对应分组。
    @SaCheckPermission(value = {"system:config:system", "system:config:upload", "system:config:log", "system:config:security", "system:config:job", "system:config:notice"}, mode = SaMode.OR)
    @GetMapping("/group")
    @ResponseBody
    public R<?> configGroup(String configGroup) {
        checkGroupPermission(configGroup);
        return okData(configService.selectByGroup(configGroup));
    }

    @Log(title = ConfigAuditCodes.OPERATE_TITLE, businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission(value = {"system:config:system", "system:config:upload", "system:config:log", "system:config:security", "system:config:job", "system:config:notice"}, mode = SaMode.OR)
    @PutMapping("/updateGroup")
    @ResponseBody
    public R<?> updateConfigGroup(@Valid @RequestBody ConfigDto.GroupUpdate groupUpdate) {
        checkGroupPermission(groupUpdate == null ? null : groupUpdate.configGroup());
        configService.updateGroup(groupUpdate);
        return success();
    }

    @Log(title = ConfigAuditCodes.OPERATE_TITLE, businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:config:insert")
    @PostMapping("/add")
    @ResponseBody
    public R<?> configInsert(@Valid @RequestBody ConfigDto.ConfigInsert configInsert) {
        configService.insert(configInsert);
        return success();
    }

    @Log(title = ConfigAuditCodes.OPERATE_TITLE, businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:config:update")
    @PutMapping("/update")
    @ResponseBody
    public R<?> configUpdate(@Valid @RequestBody ConfigDto.ConfigUpdate configUpdate) {
        configService.updateById(configUpdate);
        return success();
    }

    @Log(title = ConfigAuditCodes.OPERATE_TITLE, businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:config:delete")
    @DeleteMapping("/delete")
    @ResponseBody
    public R<?> configDelete(String ids) {
        configService.deleteById(ids);
        return success();
    }

    @Log(title = ConfigAuditCodes.OPERATE_TITLE, businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:config:updateEnabled")
    @PatchMapping("/updateEnabled")
    @ResponseBody
    public R<?> updateConfigEnabled(@Valid @RequestBody ConfigDto.UpdateEnabled updateEnabled) {
        configService.updateEnabled(updateEnabled);
        return success();
    }

    @Log(title = "查询系统配置编码是否唯一", businessType = "QUERY")
    @SaCheckPermission(value = {"system:config:insert", "system:config:update"}, mode = SaMode.OR)
    @GetMapping("/checkCode")
    @ResponseBody
    public R<Boolean> checkConfigCodeUnique(String configCode) {
        return R.ok(configService.checkConfigCodeUnique(configCode) == null);
    }

    private void checkGroupPermission(String configGroup) {
        // 固定配置页按分组权限控制 Tab，同时后端再次校验分组访问权限。
        if (configGroup == null || configGroup.trim().isEmpty()) {
            throw new BusinessException("配置分组不能为空");
        }
        StpUtil.checkPermission("system:config:" + configGroup);
    }
}
