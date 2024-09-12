package com.dangun.miniproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MiniprojectApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        Assertions.assertDoesNotThrow(() -> MiniprojectApplication.main(new String[]{}));
    }
}
