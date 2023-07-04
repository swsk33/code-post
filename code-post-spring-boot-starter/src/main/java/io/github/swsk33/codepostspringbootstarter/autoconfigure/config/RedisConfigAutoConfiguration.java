package io.github.swsk33.codepostspringbootstarter.autoconfigure.config;

import io.github.swsk33.codepost.model.config.RedisClientConfig;
import io.github.swsk33.codepost.param.CodeStorageMethod;
import io.github.swsk33.codepostspringbootstarter.autoconfigure.CodeMailServiceAutoConfiguration;
import io.github.swsk33.codepostspringbootstarter.property.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用Redis管理验证码时，初始化Redis客户端的自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisConfig.class)
@AutoConfigureBefore(CodeMailServiceAutoConfiguration.class)
@ConditionalOnProperty(prefix = "io.github.swsk33.code-post.core", value = "code-storage", havingValue = CodeStorageMethod.REDIS)
public class RedisConfigAutoConfiguration {

	/**
	 * 自动配置核心Redis配置对象
	 */
	@Bean
	public RedisClientConfig redisClientConfig(RedisConfig redisConfig) {
		RedisClientConfig config = RedisClientConfig.getInstance();
		redisConfig.setRedisConfig(config);
		log.info("使用基于Redis的邮件验证码管理方案");
		return config;
	}

}