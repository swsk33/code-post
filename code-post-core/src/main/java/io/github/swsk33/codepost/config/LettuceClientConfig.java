package io.github.swsk33.codepost.config;

import cn.hutool.core.util.StrUtil;
import io.github.swsk33.codepost.model.config.RedisClientConfig;
import io.github.swsk33.codepost.util.URLEncodeUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis连接客户端（Lettuce客户端）的配置
 */
@Slf4j
public class LettuceClientConfig {

	/**
	 * Redis客户端对象
	 */
	private static volatile RedisClient client;

	/**
	 * 用于执行Redis命令的对象
	 */
	private static RedisAsyncCommands<String, String> commands;

	/**
	 * 获取Redis连接客户端
	 *
	 * @return 连接客户端对象
	 */
	private static RedisClient getClient() {
		// 双检锁延迟初始化
		if (client == null) {
			synchronized (LettuceClientConfig.class) {
				if (client == null) {
					// 读取配置的连接地址
					RedisClientConfig config = RedisClientConfig.getInstance();
					StringBuilder redisURL = new StringBuilder("redis://");
					if (!StrUtil.isEmpty(config.getPassword())) {
						redisURL.append(URLEncodeUtils.percentEncode(config.getPassword())).append("@");
					}
					redisURL.append(config.getHost()).append(":").append(config.getPort());
					// 创建客户端
					client = RedisClient.create(redisURL.toString());
				}
			}
		}
		return client;
	}

	/**
	 * 获取Redis命令对象
	 *
	 * @return Redis命令对象
	 */
	public static RedisAsyncCommands<String, String> getCommands() {
		if (commands == null) {
			commands = getClient().connect().async();
			log.info("Redis连接已建立！");
		}
		return commands;
	}

}