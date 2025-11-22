package com.canse.slave;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Contexte Spring désactivé temporairement le temps de stabiliser la config de test")
class SlaveryHomeChallengeApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
