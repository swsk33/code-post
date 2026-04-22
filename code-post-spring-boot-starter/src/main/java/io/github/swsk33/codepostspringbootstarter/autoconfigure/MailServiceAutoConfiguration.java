package io.github.swsk33.codepostspringbootstarter.autoconfigure;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import io.github.swsk33.codepostcore.service.EmailNotifyService;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import io.github.swsk33.codepostcore.service.impl.EmailNotifyServiceImpl;
import io.github.swsk33.codepostcore.service.impl.EmailVerifyCodeServiceImpl;
import io.github.swsk33.codepostspringbootstarter.property.CoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
	 * 自动配置基于本地线程池的邮件验证码服务类
	 */
	@Bean
	@ConditionalOnProperty(prefix = "io.github.swsk33.code-post.core", value = "code-storage", havingValue = CodeStorageMethod.LOCAL_THREAD_POOL, matchIfMissing = true)
	public EmailVerifyCodeService localVerifyCodeService(MailConfig mailConfig) {
		return new EmailVerifyCodeServiceImpl(mailConfig);
	}

	/**
	 * 自动配置基于 Redis 的邮件验证码服务类
	 */
	@Bean
	@ConditionalOnProperty(prefix = "io.github.swsk33.code-post.core", value = "code-storage", havingValue = CodeStorageMethod.REDIS)
	public EmailVerifyCodeService redisVerifyCodeService(MailConfig mailConfig, RedisClientConfig redisClientConfig) {
		return new EmailVerifyCodeServiceImpl(mailConfig, redisClientConfig);
	}

	/**
	 * 自动配置邮件通知服务类
	 */
	@Bean
	public EmailNotifyService notifyService(MailConfig mailConfig) {
		log.info("------- CodePost已完成自动配置─=≡Σ(((つ•̀ω•́)つ -------");
		return new EmailNotifyServiceImpl(mailConfig);
	}

}