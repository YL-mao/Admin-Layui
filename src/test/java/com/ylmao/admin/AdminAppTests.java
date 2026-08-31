package com.ylmao.admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 全量上下文冒烟；依赖本机 MySQL/Redis，默认不随单元测试强制执行。
 * 需要时去掉 @Disabled 或单独：mvnw test -Dtest=AdminAppTests
 */
@SpringBootTest
@org.junit.jupiter.api.Disabled("依赖本机 MySQL/Redis，脚手架日常用 Service 单测即可")
class AdminAppTests {

    @Test
    void contextLoads() {
    }

}
