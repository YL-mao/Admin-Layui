package com.ylmao.admin.config.mybatis;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 登录自动拉黑等未登录写库场景不能调 getLoginId，否则 NotLoginException。
        this.setFieldValByName("createBy", currentOperatorOrEmpty(), metaObject);
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("isDel", 0, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateBy", currentOperatorOrEmpty(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }

    /** 已登录写操作人 ID；未登录（如登录态自动拉黑）写空串，与库默认一致。 */
    private static String currentOperatorOrEmpty() {
        return StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "";
    }
}
