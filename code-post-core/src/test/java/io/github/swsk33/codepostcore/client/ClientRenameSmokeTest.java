package io.github.swsk33.codepostcore.client;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.URLName;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientRenameSmokeTest {

	@Test
	void mailClientCanBeConstructed() {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.build();

		assertNotNull(new MailClient(config));
	}

	@Test
	void freeMarkerClientCanBeConstructed() {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.build();

		assertNotNull(new FreeMarkerClient(config));
	}

	@Test
	void mailClientCanSendPlainTextMessage() throws Exception {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.build();

		CapturingTransport transport = new CapturingTransport(Session.getInstance(System.getProperties()));
		MailClient client = new MailClient(config) {
			@Override
			public synchronized Transport getTransport() {
				return transport;
			}
		};

		client.sendEmail("noreply@example.com", "Test Title", "plain content", new String[]{"user@example.com"}, false);

		assertNotNull(transport.lastMessage);
		assertTrue(transport.lastMessage.getFrom()[0] instanceof InternetAddress);
		assertTrue(transport.lastMessage.getRecipients(Message.RecipientType.TO)[0] instanceof InternetAddress);
		assertTrue(transport.lastMessage.getContent().toString().contains("plain content"));
		assertTrue(transport.lastMessage.getSubject().contains("Test Title"));
	}

	@Test
	void freeMarkerClientCanRenderDefaultVerifyTemplate() {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.siteName("示例站点")
			.build();

		FreeMarkerClient client = new FreeMarkerClient(config);
		String content = client.renderVerifyMailTemplate("DEFAULT", "123456", 5, TimeUnit.MINUTES);

		assertTrue(content.contains("123456"));
		assertTrue(content.contains("5分钟"));
		assertTrue(content.contains("服务"));
	}

	@Test
	void freeMarkerClientFallsBackWhenCustomVerifyTemplateMissing() {
		MailConfig config = MailConfig.builder()
			.smtpHost("smtp.example.com")
			.email("noreply@example.com")
			.password("secret")
			.codeTemplateName("missing-template.txt")
			.build();

		FreeMarkerClient client = new FreeMarkerClient(config);
		String content = client.renderVerifyMailTemplate("DEFAULT", "654321", 3, TimeUnit.MINUTES);

		assertTrue(content.contains("654321"));
		assertTrue(content.contains("3分钟"));
		assertTrue(content.contains("服务"));
	}

	@Test
	void lettuceClientExposesRedisConfigConstructor() throws Exception {
		assertNotNull(LettuceClient.class.getDeclaredConstructor(RedisClientConfig.class));
	}

	private static final class CapturingTransport extends Transport {

		private MimeMessage lastMessage;

		private CapturingTransport(Session session) {
			super(session, (URLName) null);
		}

		@Override
		public void sendMessage(Message message, jakarta.mail.Address[] addresses) {
			this.lastMessage = (MimeMessage) message;
		}

		@Override
		protected boolean protocolConnect(String host, int port, String user, String password) {
			return true;
		}
	}
}
