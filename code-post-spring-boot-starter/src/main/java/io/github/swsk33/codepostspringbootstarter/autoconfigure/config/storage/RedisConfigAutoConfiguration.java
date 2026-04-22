package io.github.swsk33.codepostspringbootstarter.autoconfigure.config.storage;

import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.model.config.RedisClusterConfig;
import io.github.swsk33.codepostcore.model.config.RedisSentinelConfig;
import io.github.swsk33.codepostcore.model.config.RedisStandaloneConfig;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import io.github.swsk33.codepostspringbootstarter.autoconfigure.MailServiceAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用Redis管理验证码时，初始化Redis客户端的自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@AutoConfigureBefore(MailServiceAutoConfiguration.class)
@ConditionalOnProperty(prefix = "io.github.swsk33.code-post.core", value = "code-storage", havingValue = CodeStorageMethod.REDIS)
public class RedisConfigAutoConfiguration {

	/**
	 * 读取Spring Boot 的Redis配置，自动构造Redis配置对象
	 * 连接模式通过对应配置非空来进行判断，优先级：Cluster > Sentinel > Standalone
	 */
	@Bean
	public RedisClientConfig readConfig(RedisProperties redisProperties) {
		log.info("使用基于 Redis 的邮件验证码管理方案");
		// 如果是 Cluster 模式
		if (redisProperties.getCluster() != null && redisProperties.getCluster().getNodes() != null && !redisProperties.getCluster().getNodes().isEmpty()) {
			return RedisClusterConfig.builder()
					.nodes(String.join(",", redisProperties.getCluster().getNodes()))
					.password(redisProperties.getPassword())
					.build();
		}
		// 如果是 Sentinel 模式
		if (redisProperties.getSentinel() != null && redisProperties.getSentinel().getNodes() != null && !redisProperties.getSentinel().getNodes().isEmpty()) {
			return RedisSentinelConfig.builder()
					.masterName(redisProperties.getSentinel().getMaster())
					.password(redisProperties.getPassword())
					.nodes(String.join(",", redisProperties.getSentinel().getNodes()))
					.build();
		}
		// 否则就是单机模式
		return RedisStandaloneConfig.builder()
				.host(redisProperties.getHost())
				.port(redisProperties.getPort())
				.password(redisProperties.getPassword())
				.build();
	}

}