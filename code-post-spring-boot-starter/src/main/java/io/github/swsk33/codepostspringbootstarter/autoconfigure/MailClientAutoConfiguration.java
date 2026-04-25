package io.github.swsk33.codepostspringbootstarter.autoconfigure;

import io.github.swsk33.codepostcore.client.FreeMarkerClient;
import io.github.swsk33.codepostcore.client.LettuceClient;
import io.github.swsk33.codepostcore.client.MailClient;
import io.github.swsk33.codepostcore.client.VerifyCodeClient;
import io.github.swsk33.codepostcore.client.impl.RedisVerifyCodeClient;
import io.github.swsk33.codepostcore.client.impl.ThreadPoolVerifyCodeClient;
import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 各类依赖的底层客户端对象自动配置
 */
@Slf4j
@AutoConfiguration(before = MailServiceAutoConfiguration.class)
public class MailClientAutoConfiguration {

	/**
	 * 自动配置邮件 FreeMarker 模板渲染引擎客户端对象
	 *
	 * @param mailConfig 邮件核心配置
	 * @return FreeMarker 模板渲染引擎客户端对象
	 */
	@Bean
	public FreeMarkerClient freeMarkerClient(MailConfig mailConfig) {
		return new FreeMarkerClient(mailConfig);
	}

	/**
	 * 自动配置邮件核心操作客户端对象
	 *
	 * @param mailConfig 邮件核心配置
	 * @return 邮件客户端对象
	 */
	@Bean
	public MailClient mailClient(MailConfig mailConfig) {
		return new MailClient(mailConfig);
	}

	/**
	 * 使用本地线程池方案时，自动初始化基于本地线程池的邮件验证码客户端对象
	 *
	 * @return 邮件验证码客户端对象
	 */
	@Bean
	@ConditionalOnProperty(prefix = "io.github.swsk33.code-post.core", value = "code-storage", havingValue = CodeStorageMethod.LOCAL_THREAD_POOL, matchIfMissing = true)
	public VerifyCodeClient threadPoolVerifyCodeClient() {
		log.info("使用基于本地线程池的验证码管理方案");
		return new ThreadPoolVerifyCodeClient();
	}

	/**
	 * 使用 Redis 验证码管理方案时，自动初始化 Redis Lettuce 客户端对象
	 *
	 * @param redisConfig Redis 配置
	 * @return 邮件验证码客户端对象
	 */
	@Bean
	@ConditionalOnProperty(prefix = "io.github.swsk33.code-post.core", value = "code-storage", havingValue = CodeStorageMethod.REDIS)
	public VerifyCodeClient redisVerifyCodeClient(RedisClientConfig redisConfig) {
		log.info("使用基于 Redis 的邮件验证码管理方案");
		return new RedisVerifyCodeClient(new LettuceClient(redisConfig));
	}

}