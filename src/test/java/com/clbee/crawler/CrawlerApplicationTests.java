package com.clbee.crawler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrawlerApplicationTests {

	@Test
	@DisplayName("context Load")
	void contextLoads() {
		System.out.println("Context load test");
	}
}
