package io.github.swsk33.codepostspringbootstarter.autoconfigure;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.model.config.RedisStandaloneConfig;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import io.github.swsk33.codepostcore.service.impl.EmailVerifyCodeServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MailServiceAutoConfigurationTest {

	@Test
	void localBeanUsesLocalConstructor() throws Exception {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.codeStorage(CodeStorageMethod.LOCAL_THREAD_POOL)
			.build();

		EmailVerifyCodeService service = new MailServiceAutoConfiguration().localVerifyCodeService(config);

		Field field = EmailVerifyCodeServiceImpl.class.getDeclaredField("emailCodeStrategy");
		field.setAccessible(true);
		assertEquals("ThreadPoolCodeStrategy", field.get(service).getClass().getSimpleName());
	}

	@Test
	void redisBeanUsesRedisConstructor() throws Exception {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.codeStorage(CodeStorageMethod.REDIS)
			.build();
		RedisStandaloneConfig redis = RedisStandaloneConfig.builder().host("127.0.0.1").port(6379).build();

		EmailVerifyCodeService service = new MailServiceAutoConfiguration().redisVerifyCodeService(config, redis);

		Field field = EmailVerifyCodeServiceImpl.class.getDeclaredField("emailCodeStrategy");
		field.setAccessible(true);
		assertEquals("RedisCodeStrategy", field.get(service).getClass().getSimpleName());
	}
}
