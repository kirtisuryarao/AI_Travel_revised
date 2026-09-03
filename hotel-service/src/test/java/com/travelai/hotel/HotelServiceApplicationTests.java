package com.travelai.hotel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:hoteldb;DB_CLOSE_DELAY=-1"
})
class HotelServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
