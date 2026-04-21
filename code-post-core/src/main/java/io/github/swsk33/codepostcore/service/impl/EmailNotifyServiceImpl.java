package io.github.swsk33.codepostcore.service.impl;

import io.github.swsk33.codepostcore.client.FreeMarkerClient;
import io.github.swsk33.codepostcore.client.MailClient;
import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.service.EmailNotifyService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static io.github.swsk33.codepostcore.context.SendThreadPoolContext.submitTask;

/**
 * 邮件通知服务实现类
 */
@Slf4j
public class EmailNotifyServiceImpl implements EmailNotifyService {

	/**
	 * 邮件配置对象
	 */
	private final MailConfig mailConfig;

	/**
	 * 邮件客户端对象
	 */
	private final MailClient mailClient;

	/**
	 * 渲染引擎客户端对象
	 */
	private final FreeMarkerClient freeMarkerClient;

	/**
	 * 构造函数，使用邮件配置对象初始化通知服务
	 *
	 * @param mailConfig 邮件配置对象
	 */
	public EmailNotifyServiceImpl(MailConfig mailConfig) {
		if (mailConfig == null) {
			throw new IllegalArgumentException("mailConfig 不能为空！");
		}
		this.mailConfig = mailConfig;
		this.mailClient = new MailClient(mailConfig);
		this.freeMarkerClient = new FreeMarkerClient(mailConfig);
	}

	@Override
	public void sendTemplateNotify(String title, String template, Map<String, Object> models, String receiver) {
		sendTemplateNotify(title, template, models, new String[]{receiver});
		log.info("已向：{}发送通知邮件！", receiver);
	}

	@Override
	public void sendTemplateNotify(String title, String template, Map<String, Object> models, String[] receivers) {
		String templateContent = freeMarkerClient.renderTemplate(models, template);
		mailClient.sendEmail(mailConfig.getEmail(), title, templateContent, receivers, mailConfig.isEnableHtml());
		log.info("已向{}个用户发送通知邮件！", receivers.length);
	}

	@Override
	public void sendTemplateNotifyAsync(String title, String template, Map<String, Object> models, String receiver) {
		submitTask(() -> sendTemplateNotify(title, template, models, receiver));
	}

	@Override
	public void sendTemplateNotifyAsync(String title, String template, Map<String, Object> models, String[] receivers) {
		submitTask(() -> sendTemplateNotify(title, template, models, receivers));
	}

}