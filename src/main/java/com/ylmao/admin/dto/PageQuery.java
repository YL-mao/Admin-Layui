package com.ylmao.admin.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageQuery {
    @Min(value = 1, message = "页码参数不合法")
    private Integer page = 1;  // 默认为第 1 页
    @Min(value = 1, message = "分页条数参数不合法")
    @Max(value = 100, message = "分页条数不能超过100")
    private Integer limit = 10;

    // 提供一个快捷转换方法，一秒变成 MyBatis-Plus 需要的 Page 对象
    public <T> Page<T> toMpPage() {
        return new Page<>(this.page, this.limit);
    }
}
