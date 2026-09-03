package com.travelai.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "groq.api.key="
})
class AiPlannerServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
