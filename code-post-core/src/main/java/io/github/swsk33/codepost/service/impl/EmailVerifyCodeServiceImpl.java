package io.github.swsk33.codepost.service.impl;

import io.github.swsk33.codepost.context.ServiceNameContext;
import io.github.swsk33.codepost.model.config.MailConfig;
import io.github.swsk33.codepost.service.EmailVerifyCodeService;
import io.github.swsk33.codepost.strategy.context.CodeGenerateContext;
import io.github.swsk33.codepost.strategy.context.EmailCodeContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static io.github.swsk33.codepost.context.ServiceNameContext.DEFAULT_SERVICE_KEY;
import static io.github.swsk33.codepost.util.CodeKeyUtils.generateCodeKey;
import static io.github.swsk33.codepost.util.EmailSendUtils.sendEmail;
import static io.github.swsk33.codepost.util.TemplateUtils.renderVerifyMailTemplate;

/**
 * 邮件验证码接口服务实现类
 */
@Slf4j
public class EmailVerifyCodeServiceImpl implements EmailVerifyCodeService {

	/**
	 * 邮件配置对象（需自己注入）
	 */
	@Setter
	private MailConfig mailConfig;

	@Override
	public void sendCode(Object userId, String receiverEmail, long period, TimeUnit timeUnit) {
		sendCode(DEFAULT_SERVICE_KEY, userId, receiverEmail, period, timeUnit);
	}

	@Override
	public void sendCode(String serviceNameKey, Object userId, String receiverEmail, long period, TimeUnit timeUnit) {
		// 生成验证码
		String code = CodeGenerateContext.generateCode(mailConfig.getCodeFormat(), mailConfig.getCodeLength());
		// 暂存验证码
		EmailCodeContext.saveCode(mailConfig.getCodeStorage(), generateCodeKey(serviceNameKey, userId), code, period, timeUnit);
		// 渲染验证码模板
		String mailContent = renderVerifyMailTemplate(serviceNameKey, code, period, timeUnit);
		// 发送邮件
		sendEmail(mailConfig.getSiteName() + " - " + ServiceNameContext.getServiceName(serviceNameKey), mailContent, new String[]{receiverEmail}, mailConfig.isEnableHTML());
		log.info("已向" + receiverEmail + "发送验证码邮件！");
	}

	@Override
	public boolean verifyCode(Object userId, String inputCode) {
		return verifyCode(DEFAULT_SERVICE_KEY, userId, inputCode);
	}

	@Override
	public boolean verifyCode(String serviceNameKey, Object userId, String inputCode) {
		return EmailCodeContext.verifyCode(mailConfig.getCodeStorage(), generateCodeKey(serviceNameKey, userId), inputCode);
	}

}