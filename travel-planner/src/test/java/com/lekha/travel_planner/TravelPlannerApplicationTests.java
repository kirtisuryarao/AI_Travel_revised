package com.lekha.travel_planner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"groq.api.key=test-key-for-context-load",
	"groq.api.url=https://api.groq.com/openai/v1/chat/completions",
	"groq.api.model=openai/gpt-oss-120b",
	"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
class TravelPlannerApplicationTests {

	@Test
	void contextLoads() {
	}

}
