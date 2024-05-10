package io.github.swsk33.codepostcore.model.config;

import lombok.Data;

/**
 * 用于连接Redis单机模式的配置对象
 */
@Data
public class RedisStandaloneConfig extends RedisClientConfig {

	/**
	 * 获取Redis客户端配置唯一单例
	 *
	 * @return 唯一单例
	 */
	public static RedisStandaloneConfig getInstance() {
		// 双检锁延迟初始化
		if (INSTANCE == null) {
			synchronized (RedisStandaloneConfig.class) {
				if (INSTANCE == null) {
					INSTANCE = new RedisStandaloneConfig();
				}
			}
		}
		return (RedisStandaloneConfig) INSTANCE;
	}

	private RedisStandaloneConfig() {

	}

	/**
	 * Redis地址，默认127.0.0.1
	 */
	private String host = "127.0.0.1";

	/**
	 * Redis端口，默认6379
	 */
	private int port = 6379;

}