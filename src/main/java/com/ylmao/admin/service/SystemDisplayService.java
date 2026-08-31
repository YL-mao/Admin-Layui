package com.ylmao.admin.service;

import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.SystemConfigCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

/**
 * 将 system.* 展示配置写入页面 Model；缺失或停用时不写入默认值（字段为空）。
 */
@Service
@RequiredArgsConstructor
public class SystemDisplayService {

    private final ConfigRuntimeService configRuntimeService;

    public void fillModel(ModelMap model) {
        String websiteHref = toExternalHref(stringOrEmpty(SystemConfigCodes.WEBSITE));
        model.addAttribute("systemName", stringOrEmpty(SystemConfigCodes.NAME));
        model.addAttribute("systemShortName", stringOrEmpty(SystemConfigCodes.SHORT_NAME));
        model.addAttribute("systemLogo", stringOrEmpty(SystemConfigCodes.LOGO));
        model.addAttribute("systemFavicon", stringOrEmpty(SystemConfigCodes.FAVICON));
        model.addAttribute("systemCopyright", stringOrEmpty(SystemConfigCodes.COPYRIGHT));
        model.addAttribute("systemAdminEmail", stringOrEmpty(SystemConfigCodes.ADMIN_EMAIL));
        model.addAttribute("systemVersion", stringOrEmpty(SystemConfigCodes.VERSION));
        // href 仅做展示用绝对化：已有协议原样保留，缺协议时才补 https://。
        model.addAttribute("systemWebsite", websiteHref);
        model.addAttribute("systemWebsiteLinkable", StrUtil.isNotBlank(websiteHref));
        model.addAttribute("systemIcp", stringOrEmpty(SystemConfigCodes.ICP));
        model.addAttribute("systemPoliceIcp", stringOrEmpty(SystemConfigCodes.POLICE_ICP));
    }

    private String stringOrEmpty(String configCode) {
        return configRuntimeService.getString(configCode).orElse("");
    }

    /**
     * 转为可外跳的绝对地址：http/https/协议相对原样保留；无协议域名补 https://，避免被当成站内相对路径。
     */
    private String toExternalHref(String raw) {
        if (StrUtil.isBlank(raw)) {
            return "";
        }
        String url = raw.trim();
        String lower = url.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("//")) {
            return url;
        }
        return "https://" + url;
    }
}
