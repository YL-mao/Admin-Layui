package com.ylmao.admin.config.mybatis;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 调试辅助：在升级 MyBatis-Plus / MyBatis 相关依赖后，用于验证指定的 Mapper SQL 语句是否已被注册。
 *
 * 注意：该检查不会访问数据库，只检查 MyBatis 的 statement 是否存在。
 */
@Component
@ConditionalOnProperty(name = "app.mybatis.verify", havingValue = "true")
public class MappedStatementVerifier implements ApplicationListener<ApplicationReadyEvent> {

    private final SqlSessionFactory sqlSessionFactory;

    public MappedStatementVerifier(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        String statementId = "com.ylmao.admin.mapper.UserMapper.selectList";
        boolean has = configuration.hasStatement(statementId, false);
        System.out.println("[MyBatis-Verify] hasStatement=" + has + ", id=" + statementId);
    }
}

