package io.github.swsk33.codepostcore.service.impl;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.service.EmailNotifyService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static io.github.swsk33.codepostcore.util.EmailSendUtils.sendEmail;
import static io.github.swsk33.codepostcore.util.TemplateUtils.renderTemplate;

/**
 * 邮件通知服务实现类
 */
@Slf4j
public class EmailNotifyServiceImpl implements EmailNotifyService {

	/**
	 * 配置对象（需自行注入）
	 */
	@Setter
	private MailConfig mailConfig;

	@Override
	public void sendTemplateNotify(String title, String template, Map<String, Object> models, String receiver) {
		sendTemplateNotify(title, template, models, new String[]{receiver});
		log.info("已向：" + receiver + "发送通知邮件！");
	}

	@Override
	public void sendTemplateNotify(String title, String template, Map<String, Object> models, String[] receivers) {
		// 渲染模板
		String templateContent = renderTemplate(models, template);
		// 发送邮件
		sendEmail(title, templateContent, receivers, mailConfig.isEnableHTML());
		log.info("已向" + receivers.length + "个用户发送通知邮件！");
	}

}