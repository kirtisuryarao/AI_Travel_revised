package com.travelai.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:userdb;DB_CLOSE_DELAY=-1",
    "app.jwt.secret=travel-planner-dev-secret-change-in-production-32chars"
})
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
