package com.travelai.flight;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:flightdb;DB_CLOSE_DELAY=-1"
})
class FlightServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
