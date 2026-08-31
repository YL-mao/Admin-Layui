package com.ylmao.admin.config.base;

import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.common.R;


public class BaseController
{
    protected R<Void> success()
    {
        return R.ok();
    }

    protected R<Void> error()
    {
        return R.fail("操作失败");
    }

    protected R<Void> success(String message)
    {
        return R.ok(message);
    }

    protected R<Void> error(String message)
    {
        return R.fail(message);
    }

    protected R<Void> error(int code, String message)
    {
        return R.fail(code, message);
    }

    protected <T> R<T> okData(T data)
    {
        return R.ok(data);
    }

    protected <T> R<T> pageData(T data, long count)
    {
        return R.page(data, count);
    }

    public String redirect(String url)
    {
    	return StrUtil.format("redirect:{}", url);
    }
}
