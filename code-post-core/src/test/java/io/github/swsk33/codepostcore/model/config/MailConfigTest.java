package io.github.swsk33.codepostcore.model.config;

import io.github.swsk33.codepostcore.param.CodeGenerateMethod;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MailConfigTest {

	@Test
	void builderUsesCurrentDefaultValues() {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.build();

		assertEquals(CodeStorageMethod.LOCAL_THREAD_POOL, config.getCodeStorage());
		assertEquals(CodeGenerateMethod.NUMBER, config.getCodeFormat());
		assertEquals(6, config.getCodeLength());
		assertEquals("网站名", config.getSiteName());
		assertTrue(config.isEnableTls());
		assertFalse(config.isEnableHtml());
	}

	@Test
	void redisStandaloneBuilderKeepsExplicitValues() {
		RedisStandaloneConfig config = RedisStandaloneConfig.builder()
			.host("127.0.0.1")
			.port(6379)
			.password("redis-secret")
			.build();

		assertEquals("127.0.0.1", config.getHost());
		assertEquals(6379, config.getPort());
		assertEquals("redis-secret", config.getPassword());
	}

	@Test
	void singletonApiIsRemovedFromConfigObjects() {
		assertTrue(Arrays.stream(MailConfig.class.getDeclaredMethods()).noneMatch(method -> method.getName().equals("getInstance") || method.getName().equals("setInstance")));
		assertTrue(Arrays.stream(RedisClientConfig.class.getDeclaredMethods()).noneMatch(method -> method.getName().equals("getInstance") || method.getName().equals("setInstance")));
	}
}
