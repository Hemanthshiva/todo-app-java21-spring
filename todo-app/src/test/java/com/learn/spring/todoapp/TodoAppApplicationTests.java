package com.learn.spring.todoapp;

import com.learn.spring.todoapp.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class TodoAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
