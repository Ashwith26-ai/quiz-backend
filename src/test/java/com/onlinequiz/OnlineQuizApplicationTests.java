package com.onlinequiz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OnlineQuizApplicationTests {

    @Test
    void contextLoads() {
        // Confirms Spring Boot application context, entities, repositories, and security beans load cleanly
    }
}
