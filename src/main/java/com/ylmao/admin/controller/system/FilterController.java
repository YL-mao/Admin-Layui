package com.ylmao.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ylmao.admin.common.R;
import com.ylmao.admin.config.base.BaseController;
import com.ylmao.admin.config.log.Log;
import com.ylmao.admin.dto.FilterDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.service.FilterService;
import com.ylmao.admin.vo.FilterVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController extends BaseController {

    private static final String FILTER_VIEW = "system/filter";

    private final FilterService filterService;

    @Log(title = "访问控制页面跳转", businessType = "QUERY")
    @SaCheckPermission("system:filter:view")
    @GetMapping("/listView")
    public String filterListView(ModelMap model) {
        return FILTER_VIEW;
    }

    @Log(title = "访问控制分页查询", businessType = "QUERY")
    @SaCheckPermission("system:filter:select")
    @GetMapping("/list")
    @ResponseBody
    public R<?> filterList(@Valid PageQuery pageQuery, @Valid FilterDto.FilterList filterList) {
        IPage<FilterVo.FilterListVo> page = filterService.selectPage(pageQuery, filterList);
        return pageData(page.getRecords(), page.getTotal());
    }

    @Log(title = "新增访问控制", businessType = "ADD", isSaveResponseData = true)
    @SaCheckPermission("system:filter:insert")
    @PostMapping("/add")
    @ResponseBody
    public R<?> filterInsert(@Valid @RequestBody FilterDto.FilterInsert filterInsert) {
        filterService.insert(filterInsert);
        return success();
    }

    @Log(title = "修改访问控制", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:filter:update")
    @PutMapping("/update")
    @ResponseBody
    public R<?> filterUpdate(@Valid @RequestBody FilterDto.FilterUpdate filterUpdate) {
        filterService.update(filterUpdate);
        return success();
    }

    @Log(title = "删除访问控制", businessType = "DELETE", isSaveResponseData = true)
    @SaCheckPermission("system:filter:delete")
    @DeleteMapping("/delete")
    @ResponseBody
    public R<?> filterDelete(String ids) {
        filterService.deleteByIds(ids);
        return success();
    }

    @Log(title = "修改访问控制状态", businessType = "UPDATE", isSaveResponseData = true)
    @SaCheckPermission("system:filter:updateEnabled")
    @PatchMapping("/updateEnabled")
    @ResponseBody
    public R<?> updateFilterEnabled(@Valid @RequestBody FilterDto.UpdateEnabled updateEnabled) {
        filterService.updateEnabled(updateEnabled);
        return success();
    }
}
