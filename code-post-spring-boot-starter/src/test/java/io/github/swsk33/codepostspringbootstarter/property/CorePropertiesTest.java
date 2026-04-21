package io.github.swsk33.codepostspringbootstarter.property;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorePropertiesTest {

	@Test
	void toMailConfigCopiesBoundProperties() {
		CoreProperties properties = new CoreProperties();
		properties.setSmtpHost("smtp.163.com");
		properties.setEmail("demo@example.com");
		properties.setPassword("secret");
		properties.setEnableTls(false);
		properties.setEnableHtml(true);

		MailConfig config = properties.toMailConfig();

		assertEquals("smtp.163.com", config.getSmtpHost());
		assertEquals("demo@example.com", config.getEmail());
		assertEquals("secret", config.getPassword());
		assertFalse(config.isEnableTls());
		assertTrue(config.isEnableHtml());
	}
}
