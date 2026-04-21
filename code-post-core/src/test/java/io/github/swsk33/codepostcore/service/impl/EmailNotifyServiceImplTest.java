package io.github.swsk33.codepostcore.service.impl;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailNotifyServiceImplTest {

	@Test
	void constructorStoresMailConfig() throws Exception {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.build();

		EmailNotifyServiceImpl service = new EmailNotifyServiceImpl(config);

		Field field = EmailNotifyServiceImpl.class.getDeclaredField("mailConfig");
		field.setAccessible(true);
		assertSame(config, field.get(service));
	}

	@Test
	void constructorRejectsNullMailConfig() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new EmailNotifyServiceImpl(null));
		assertEquals("mailConfig must not be null", ex.getMessage());
	}
}
