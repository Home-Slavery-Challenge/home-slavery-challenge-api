package com.canse.slave;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class SlaveryHomeChallengeApiApplicationTests {

	@Test
	void contextLoads() {
        int result = 1 + 1;
        assertEquals(2, result);
	}

}
