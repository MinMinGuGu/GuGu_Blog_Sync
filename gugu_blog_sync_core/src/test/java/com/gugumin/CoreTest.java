package com.gugumin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试基类 其他测试应基于此类使用继承的方式去测试
 *
 * @author minmin
 * @date 2023 /03/19
 */
@SpringBootApplication
@SpringBootTest
public class CoreTest {
    @Test
    public void testStart() {

    }
}
