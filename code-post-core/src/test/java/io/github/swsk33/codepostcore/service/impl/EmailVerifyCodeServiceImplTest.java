package io.github.swsk33.codepostcore.service.impl;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.model.config.RedisStandaloneConfig;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailVerifyCodeServiceImplTest {

	@Test
	void localConstructorRejectsRedisStorage() {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.codeStorage(CodeStorageMethod.REDIS)
			.build();

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new EmailVerifyCodeServiceImpl(config));
		assertEquals("mailConfig.codeStorage must be local_thread_pool when Redis config is absent", ex.getMessage());
	}

	@Test
	void redisConstructorRejectsLocalStorage() {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.codeStorage(CodeStorageMethod.LOCAL_THREAD_POOL)
			.build();

		RedisStandaloneConfig redis = RedisStandaloneConfig.builder()
			.host("127.0.0.1")
			.port(6379)
			.build();

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new EmailVerifyCodeServiceImpl(config, redis));
		assertEquals("mailConfig.codeStorage must be redis when Redis config is provided", ex.getMessage());
	}

	@Test
	void localConstructorCreatesIndependentStrategyInstances() throws Exception {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.build();

		EmailVerifyCodeServiceImpl first = new EmailVerifyCodeServiceImpl(config);
		EmailVerifyCodeServiceImpl second = new EmailVerifyCodeServiceImpl(config);

		Field field = EmailVerifyCodeServiceImpl.class.getDeclaredField("emailCodeStrategy");
		field.setAccessible(true);
		assertNotSame(field.get(first), field.get(second));
	}
}
