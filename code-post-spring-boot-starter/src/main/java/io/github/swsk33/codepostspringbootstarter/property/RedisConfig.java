package io.github.swsk33.codepostspringbootstarter.property;

import io.github.swsk33.codepost.model.config.RedisClientConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConfig {

	/**
	 * Redis服务器地址
	 */
	private String host;

	/**
	 * Redis服务器端口
	 */
	private int port = 6379;

	/**
	 * Redis连接密码
	 */
	private String password;

	/**
	 * 将读取到的配置设定到核心Redis配置中去
	 *
	 * @param redisConfig Redis配置对象
	 */
	public void setRedisConfig(RedisClientConfig redisConfig) {
		redisConfig.setHost(host);
		redisConfig.setPort(port);
		redisConfig.setPassword(password);
	}

}