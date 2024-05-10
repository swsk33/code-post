package io.github.swsk33.codepostcore.config;

import io.github.swsk33.codepostcore.model.LettuceConnectionResource;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.lettuce.core.api.sync.BaseRedisCommands;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis连接客户端（Lettuce客户端）的配置
 */
@Slf4j
public class LettuceClientConfig {

	/**
	 * Lettuce连接资源对象
	 */
	private static volatile LettuceConnectionResource connectionResource;

	/**
	 * 获取Redis连接资源对象
	 *
	 * @return 连接资源对象
	 */
	private static LettuceConnectionResource getConnection() {
		// 双检锁延迟初始化
		if (connectionResource == null) {
			synchronized (LettuceClientConfig.class) {
				if (connectionResource == null) {
					connectionResource = new LettuceConnectionResource(RedisClientConfig.getInstance());
				}
			}
		}
		return connectionResource;
	}

	/**
	 * 获取Redis命令对象
	 *
	 * @return Redis命令对象
	 */
	public static BaseRedisCommands<String, String> getCommands() {
		return getConnection().getCommands();
	}

}