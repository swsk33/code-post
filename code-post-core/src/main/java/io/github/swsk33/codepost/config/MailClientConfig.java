package io.github.swsk33.codepost.config;

import io.github.swsk33.codepost.model.config.MailConfig;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * 存放邮件客户端连接对象
 */
@Slf4j
public class MailClientConfig {

	/**
	 * 邮件会话对象
	 */
	private static volatile Session session;

	/**
	 * 邮件发送对象单例
	 */
	private static Transport transport;

	/**
	 * 获取邮件会话对象
	 *
	 * @return 会话对象
	 */
	public static Session getSession() {
		// 双重检查锁
		if (session == null) {
			synchronized (MailClientConfig.class) {
				if (session == null) {
					// 获取邮件配置
					MailConfig config = MailConfig.getInstance();
					// 构建配置对象
					Properties mailProperties = new Properties();
					mailProperties.put("mail.smtp.host", config.getSMTPHost());
					mailProperties.put("mail.smtp.ssl.enable", config.isEnableSSL());
					session = Session.getInstance(mailProperties);
					log.info("邮件会话已创建！");
				}
			}
		}
		return session;
	}

	/**
	 * 获取邮件发送对象单例
	 *
	 * @return 邮件发送对象
	 */
	public static Transport getTransport() {
		if (transport == null) {
			try {
				// 获取邮件会话并连接
				transport = getSession().getTransport();
				// 登录邮件用户
				transport.connect(MailConfig.getInstance().getEmail(), MailConfig.getInstance().getPassword());
				log.info("邮件服务器连接已建立！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return transport;
	}

}