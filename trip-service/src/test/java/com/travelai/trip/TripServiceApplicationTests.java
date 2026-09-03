package com.travelai.trip;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:tripdb;DB_CLOSE_DELAY=-1",
    "app.jwt.secret=travel-planner-dev-secret-change-in-production-32chars"
})
class TripServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
