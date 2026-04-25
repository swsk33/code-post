package io.github.swsk33.codepostspringbootstarter.autoconfigure;

import io.github.swsk33.codepostcore.client.FreeMarkerClient;
import io.github.swsk33.codepostcore.client.MailClient;
import io.github.swsk33.codepostcore.client.VerifyCodeClient;
import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.service.EmailNotifyService;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import io.github.swsk33.codepostcore.service.impl.EmailNotifyServiceImpl;
import io.github.swsk33.codepostcore.service.impl.EmailVerifyCodeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 验证码和通知邮件服务自动配置类
 */
@Slf4j
@AutoConfiguration
public class MailServiceAutoConfiguration {

	/**
	 * 自动配置邮件验证码服务类
	 */
	@Bean
	public EmailVerifyCodeService localVerifyCodeService(MailConfig mailConfig, MailClient mailClient, FreeMarkerClient freeMarkerClient, VerifyCodeClient verifyCodeClient) {
		return new EmailVerifyCodeServiceImpl(mailConfig, mailClient, freeMarkerClient, verifyCodeClient);
	}

	/**
	 * 自动配置邮件通知服务类
	 */
	@Bean
	public EmailNotifyService notifyService(MailConfig mailConfig, MailClient mailClient, FreeMarkerClient freeMarkerClient) {
		log.info("------- CodePost已完成自动配置─=≡Σ(((つ•̀ω•́)つ -------");
		return new EmailNotifyServiceImpl(mailConfig, mailClient, freeMarkerClient);
	}

}