package io.github.swsk33.codepost.model.config;

import lombok.Data;

/**
 * Redis客户端配置（使用Redis存放验证码时）
 */
@Data
public class RedisClientConfig {

	/**
	 * 唯一单例
	 */
	private static volatile RedisClientConfig INSTANCE;

	/**
	 * 获取Redis客户端配置唯一单例
	 *
	 * @return 唯一单例
	 */
	public static RedisClientConfig getInstance() {
		// 双检锁延迟初始化
		if (INSTANCE == null) {
			synchronized (RedisClientConfig.class) {
				if (INSTANCE == null) {
					INSTANCE = new RedisClientConfig();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * 私有化构造器
	 */
	private RedisClientConfig() {

	}

	/**
	 * Redis地址
	 */
	private String host;

	/**
	 * Redis端口
	 */
	private int port = 6379;

	/**
	 * Redis密码
	 */
	private String password;

}