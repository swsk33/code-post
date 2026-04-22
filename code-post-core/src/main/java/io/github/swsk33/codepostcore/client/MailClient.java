package io.github.swsk33.codepostcore.client;

import cn.hutool.core.util.ArrayUtil;
import io.github.swsk33.codepostcore.model.config.MailConfig;
import jakarta.activation.DataHandler;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 邮件客户端
 */
@Slf4j
public class MailClient {

	/**
	 * 邮件会话对象
	 */
	private final Session session;

	/**
	 * 邮件连接对象
	 */
	private volatile Transport transport;

	/**
	 * 邮件配置对象
	 */
	private final MailConfig mailConfig;

	/**
	 * 构造函数，使用邮件配置初始化
	 *
	 * @param mailConfig 邮件配置对象
	 */
	public MailClient(MailConfig mailConfig) {
		this.mailConfig = Objects.requireNonNull(mailConfig, "mailConfig 不能为空！");
		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.host", mailConfig.getSmtpHost());
		mailProperties.put("mail.smtp.ssl.enable", mailConfig.isEnableTls());
		mailProperties.put("mail.smtp.starttls.enable", mailConfig.isEnableTls());
		this.session = Session.getInstance(mailProperties);
		log.info("邮件会话已创建！");
	}

	/**
	 * 获取连接对象
	 *
	 * @return 连接对象
	 */
	protected synchronized Transport getTransport() {
		if (transport == null) {
			try {
				transport = session.getTransport();
				transport.connect(mailConfig.getEmail(), mailConfig.getPassword());
				log.info("邮件服务器连接已建立！");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (!transport.isConnected()) {
			try {
				transport.connect(mailConfig.getEmail(), mailConfig.getPassword());
				log.info("邮件服务器已重连！");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return transport;
	}

	/**
	 * 发送邮件
	 *
	 * @param from      发件人
	 * @param title     邮件标题
	 * @param content   邮件内容
	 * @param receivers 收件人列表
	 * @param html      是否是网页邮件
	 */
	public void sendEmail(String from, String title, String content, String[] receivers, boolean html) {
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(from));
			List<Address> addressList = new ArrayList<>();
			for (String receiver : receivers) {
				addressList.add(new InternetAddress(receiver));
			}
			message.setRecipients(Message.RecipientType.TO, ArrayUtil.toArray(addressList, Address.class));
			message.setSubject(title);
			if (html) {
				message.setDataHandler(new DataHandler(new ByteArrayDataSource(content, "text/html")));
			} else {
				message.setText(content, StandardCharsets.UTF_8.toString());
			}
			getTransport().sendMessage(message, message.getAllRecipients());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}