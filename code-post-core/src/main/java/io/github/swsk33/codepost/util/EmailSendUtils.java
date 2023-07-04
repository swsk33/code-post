package io.github.swsk33.codepost.util;

import cn.hutool.core.util.ArrayUtil;
import io.github.swsk33.codepost.config.MailClientConfig;
import io.github.swsk33.codepost.model.config.MailConfig;
import jakarta.activation.DataHandler;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 发送邮件的实用类
 */
public class EmailSendUtils {

	/**
	 * 发送邮件
	 *
	 * @param title     邮件标题
	 * @param content   邮件内容
	 * @param receivers 收件人列表
	 * @param html      是否为HTML邮件
	 */
	public static void sendEmail(String title, String content, String[] receivers, boolean html) {
		// 创建消息对象
		MimeMessage message = new MimeMessage(MailClientConfig.getSession());
		try {
			// 设定发件人
			message.setFrom(MailConfig.getInstance().getEmail());
			// 收件人列表
			List<Address> addressList = Arrays.stream(receivers).map(receiver -> {
				Address address;
				try {
					address = new InternetAddress(receiver);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return address;
			}).toList();
			// 设定收件人列表
			message.setRecipients(Message.RecipientType.TO, ArrayUtil.toArray(addressList, Address.class));
			// 设定邮件标题和内容
			message.setSubject(title);
			if (html) {
				message.setDataHandler(new DataHandler(new ByteArrayDataSource(content, "text/html")));
			} else {
				message.setText(content, StandardCharsets.UTF_8.toString());
			}
			// 发送邮件
			MailClientConfig.getTransport().sendMessage(message, message.getAllRecipients());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}