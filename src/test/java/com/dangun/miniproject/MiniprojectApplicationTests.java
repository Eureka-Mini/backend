package com.dangun.miniproject;

import com.dangun.miniproject.common.config.JpaAuditingConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(JpaAuditingConfig.class)
class MiniprojectApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        Assertions.assertDoesNotThrow(() -> MiniprojectApplication.main(new String[]{}));
    }
}
