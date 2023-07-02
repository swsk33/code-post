package io.github.swsk33.codepost.config;

import io.github.swsk33.codepost.model.config.MailConfig;
import jakarta.mail.Session;
import jakarta.mail.Transport;

import java.util.Properties;

/**
 * 存放邮件客户端连接对象
 */
public class MailClientConfig {

	/**
	 * 邮件发送对象单例
	 */
	private static volatile Transport transport;

	/**
	 * 获取邮件发送对象单例
	 *
	 * @return 邮件发送对象
	 */
	public static Transport getTransport() {
		// 双检锁延迟初始化
		if (transport == null) {
			synchronized (MailClientConfig.class) {
				if (transport == null) {
					// 获取邮件配置
					MailConfig config = MailConfig.getInstance();
					// 构建配置对象
					Properties mailProperties = new Properties();
					mailProperties.put("mail.smtp.host", config.getSMTPHost());
					mailProperties.put("mail.smtp.ssl.enable", config.isEnableSSL());
					try {
						// 获取邮件会话并连接
						transport = Session.getInstance(mailProperties).getTransport();
						// 登录邮件用户
						transport.connect(config.getEmail(), config.getPassword());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return transport;
	}

}