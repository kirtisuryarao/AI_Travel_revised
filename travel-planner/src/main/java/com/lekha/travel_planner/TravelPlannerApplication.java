package com.lekha.travel_planner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TravelPlannerApplication {

	private static final Logger logger = LoggerFactory.getLogger(TravelPlannerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TravelPlannerApplication.class, args);
		logger.info("═══════════════════════════════════════════════════");
		logger.info("  ✈️  AI Travel Planner started successfully");
		logger.info("  Powered by Groq API");
		logger.info("═══════════════════════════════════════════════════");
	}

}
