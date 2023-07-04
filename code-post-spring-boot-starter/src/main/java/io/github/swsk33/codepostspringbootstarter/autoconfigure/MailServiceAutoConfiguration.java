package io.github.swsk33.codepostspringbootstarter.autoconfigure;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.service.EmailNotifyService;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import io.github.swsk33.codepostcore.service.impl.EmailNotifyServiceImpl;
import io.github.swsk33.codepostcore.service.impl.EmailVerifyCodeServiceImpl;
import io.github.swsk33.codepostspringbootstarter.property.CoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码邮件服务自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CoreProperties.class)
public class MailServiceAutoConfiguration {

	/**
	 * 自动配置邮件验证码服务类
	 */
	@Bean
	public EmailVerifyCodeService verifyCodeService(MailConfig mailConfig) {
		EmailVerifyCodeServiceImpl verifyCodeService = new EmailVerifyCodeServiceImpl();
		verifyCodeService.setMailConfig(mailConfig);
		return verifyCodeService;
	}

	/**
	 * 自动配置邮件通知服务类
	 */
	@Bean
	public EmailNotifyService notifyService(MailConfig mailConfig) {
		EmailNotifyServiceImpl notifyService = new EmailNotifyServiceImpl();
		notifyService.setMailConfig(mailConfig);
		log.info("------- CodePost已完成自动配置─=≡Σ(((つ•̀ω•́)つ -------");
		return notifyService;
	}

}