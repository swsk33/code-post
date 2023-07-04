package io.github.swsk33.springannotationtest.config;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.param.CodeGenerateMethod;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import io.github.swsk33.codepostcore.service.EmailNotifyService;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import io.github.swsk33.codepostcore.service.impl.EmailNotifyServiceImpl;
import io.github.swsk33.codepostcore.service.impl.EmailVerifyCodeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件核心配置
 */
@Configuration
public class MailCoreConfig {

	/**
	 * 定义邮件发送核心配置Bean
	 */
	@Bean
	public MailConfig mailConfig() {
		MailConfig mailConfig = MailConfig.getInstance();
		mailConfig.setSMTPHost("邮件SMTP服务器");
		mailConfig.setEmail("发件邮箱");
		mailConfig.setPassword("密码");
		mailConfig.setEnableTLS(true);
		mailConfig.setCodeStorage(CodeStorageMethod.LOCAL_THREAD_POOL);
		mailConfig.setCodeFormat(CodeGenerateMethod.NUMBER);
		mailConfig.setCodeLength(6);
		mailConfig.setSiteName("网站名");
		mailConfig.setEnableHTML(false);
		mailConfig.setTemplatePath("classpath:/templates");
		mailConfig.setCodeTemplateName("template.ftlh");
		return mailConfig;
	}

	/**
	 * 若要使用Redis的储存方案，请配置Redis客户端
	 */
	@Bean
	public RedisClientConfig redisClientConfig() {
		RedisClientConfig config = RedisClientConfig.getInstance();
		config.setHost("127.0.0.1");
		config.setPort(6379);
		config.setPassword("123456");
		return config;
	}

	/**
	 * 配置邮件验证码服务类
	 */
	@Bean
	public EmailVerifyCodeService verifyCodeService(MailConfig mailConfig) {
		EmailVerifyCodeServiceImpl verifyCodeService = new EmailVerifyCodeServiceImpl();
		verifyCodeService.setMailConfig(mailConfig);
		return verifyCodeService;
	}

	/**
	 * 配置邮件通知服务类
	 */
	@Bean
	public EmailNotifyService notifyService(MailConfig mailConfig) {
		EmailNotifyServiceImpl notifyService = new EmailNotifyServiceImpl();
		notifyService.setMailConfig(mailConfig);
		return notifyService;
	}

}