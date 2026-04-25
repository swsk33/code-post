package io.github.swsk33.codepostspringbootstarter.autoconfigure.config;

import cn.hutool.core.util.StrUtil;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.model.config.RedisClusterConfig;
import io.github.swsk33.codepostcore.model.config.RedisSentinelConfig;
import io.github.swsk33.codepostcore.model.config.RedisStandaloneConfig;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import io.github.swsk33.codepostspringbootstarter.autoconfigure.MailClientAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.List;

/**
 * 使用Redis管理验证码时，初始化Redis客户端的自动配置类
 */
@Slf4j
@AutoConfiguration(before = MailClientAutoConfiguration.class)
@ConditionalOnProperty(prefix = "io.github.swsk33.code-post.core", value = "code-storage", havingValue = CodeStorageMethod.REDIS)
public class RedisConfigAutoConfiguration {

	/**
	 * RedisProperties 自动配置属性全限定类名
	 */
	private static final String REDIS_PROPERTIES_CLASS_NAME = "org.springframework.boot.autoconfigure.data.redis.RedisProperties";

	/**
	 * 向下兼容Spring Boot 2.x - 3.x的Redis自动配置，使用内部类配合类加载条件进行
	 * 这里使用全限定类名，而不是import，因为在4.x环境下RedisProperties类型不存在，会导致无法加载类报错
	 */
	@AutoConfiguration
	@ConditionalOnClass(name = REDIS_PROPERTIES_CLASS_NAME)
	@EnableConfigurationProperties(org.springframework.boot.autoconfigure.data.redis.RedisProperties.class)
	static class Boot3RedisConfig {

		/**
		 * 读取Spring Boot 的 Redis 配置，自动构造Redis配置对象
		 * 连接模式通过对应配置非空来进行判断，优先级：Cluster > Sentinel > Standalone
		 */
		@Bean
		public RedisClientConfig readConfig(org.springframework.boot.autoconfigure.data.redis.RedisProperties redisProperties) {
			log.info("从 RedisProperties 读取配置");
			// 如果是 Cluster 模式
			if (redisProperties.getCluster() != null && redisProperties.getCluster().getNodes() != null && !redisProperties.getCluster().getNodes().isEmpty()) {
				log.info("使用 Redis Cluster 连接配置");
				return RedisClusterConfig.builder()
						.nodes(redisProperties.getCluster().getNodes())
						.password(redisProperties.getPassword())
						.build();
			}
			// 如果是 Sentinel 模式
			if (redisProperties.getSentinel() != null && redisProperties.getSentinel().getNodes() != null && !redisProperties.getSentinel().getNodes().isEmpty()) {
				log.info("使用 Redis Sentinel 连接配置");
				return RedisSentinelConfig.builder()
						.masterName(redisProperties.getSentinel().getMaster())
						.password(redisProperties.getPassword())
						.nodes(redisProperties.getSentinel().getNodes())
						.database(redisProperties.getDatabase())
						.build();
			}
			// 否则就是单机模式
			log.info("使用 Redis 单节点连接配置");
			// 优先读取 url 配置
			if (!StrUtil.isEmpty(redisProperties.getUrl())) {
				return RedisStandaloneConfig.builder().url(redisProperties.getUrl()).build();
			}
			return RedisStandaloneConfig.builder()
					.host(redisProperties.getHost())
					.port(redisProperties.getPort())
					.password(redisProperties.getPassword())
					.database(redisProperties.getDatabase())
					.build();
		}

	}

	/**
	 * 用于新的Spring Boot 4.x的加载内部类
	 */
	@AutoConfiguration
	@ConditionalOnMissingClass(REDIS_PROPERTIES_CLASS_NAME)
	static class Boot4RedisConfig {

		/**
		 * Spring Boot Redis配置前缀
		 */
		private static final String REDIS_CONFIG_PREFIX = "spring.data.redis";

		/**
		 * 自动配置 Redis 方案，适用于新的Spring Boot 4.x版本
		 * 连接模式通过对应配置非空来进行判断，优先级：Cluster > Sentinel > Standalone
		 *
		 * @param environment 环境变量对象
		 * @return Redis 配置
		 */
		@Bean
		public RedisClientConfig redisConfig(Environment environment) {
			log.info("从配置环境变量读取 Redis 配置");
			// 读取基本配置
			Binder binder = Binder.get(environment);
			String password = binder.bind(REDIS_CONFIG_PREFIX + ".password", String.class).orElse(null);
			int database = binder.bind(REDIS_CONFIG_PREFIX + ".database", Integer.class).orElse(0);
			// 如果是 Cluster 模式
			List<String> clusterNodes = binder.bind(REDIS_CONFIG_PREFIX + ".cluster.nodes", Bindable.listOf(String.class)).orElse(Collections.emptyList());
			if (!clusterNodes.isEmpty()) {
				log.info("使用 Redis Cluster 连接配置");
				return RedisClusterConfig.builder()
						.nodes(clusterNodes)
						.password(password)
						.build();
			}
			// 如果是 Sentinel 模式
			List<String> sentinelNodes = binder.bind(REDIS_CONFIG_PREFIX + ".sentinel.nodes", Bindable.listOf(String.class)).orElse(Collections.emptyList());
			if (!sentinelNodes.isEmpty()) {
				log.info("使用 Redis Sentinel 连接配置");
				String masterName = binder.bind(REDIS_CONFIG_PREFIX + ".sentinel.master", String.class).orElse(null);
				return RedisSentinelConfig.builder()
						.masterName(masterName)
						.password(password)
						.nodes(sentinelNodes)
						.database(database)
						.build();
			}
			// 否则就是单机模式
			log.info("使用 Redis 单节点连接配置");
			String url = binder.bind(REDIS_CONFIG_PREFIX + ".url", String.class).orElse(null);
			// 优先读 url 属性
			if (!StrUtil.isEmpty(url)) {
				return RedisStandaloneConfig.builder().url(url).build();
			}
			String host = binder.bind(REDIS_CONFIG_PREFIX + ".host", String.class).orElse("localhost");
			int port = binder.bind(REDIS_CONFIG_PREFIX + ".port", Integer.class).orElse(6379);
			return RedisStandaloneConfig.builder()
					.host(host)
					.port(port)
					.password(password)
					.database(database)
					.build();
		}

	}

}