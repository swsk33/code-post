package io.github.swsk33.codepostcore.strategy.context;

import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.model.config.RedisClusterConfig;
import io.github.swsk33.codepostcore.model.config.RedisSentinelConfig;
import io.github.swsk33.codepostcore.model.config.RedisStandaloneConfig;
import io.github.swsk33.codepostcore.strategy.RedisCommandStrategy;
import io.github.swsk33.codepostcore.strategy.impl.ClusterRedisCommandStrategy;
import io.github.swsk33.codepostcore.strategy.impl.CommonRedisCommandStrategy;

/**
 * 操作Redis策略的上下文
 */
public class RedisCommandContext {

	/**
	 * 当前使用的Redis命令策略
	 */
	private static volatile RedisCommandStrategy currentStrategy;

	/**
	 * 获取当前策略对象（懒加载）
	 *
	 * @return 当前所使用的策略对象
	 */
	private static RedisCommandStrategy getCurrentStrategy() {
		if (currentStrategy == null) {
			synchronized (RedisCommandContext.class) {
				if (currentStrategy == null) {
					// 根据配置类型判断
					RedisClientConfig config = RedisClientConfig.getInstance();
					if (config instanceof RedisStandaloneConfig || config instanceof RedisSentinelConfig) {
						currentStrategy = new CommonRedisCommandStrategy();
					} else if (config instanceof RedisClusterConfig) {
						currentStrategy = new ClusterRedisCommandStrategy();
					}
				}
			}
		}
		return currentStrategy;
	}

	/**
	 * 设定字符串键值对
	 *
	 * @param key   键
	 * @param value 值
	 */
	public static void redisSet(String key, String value) {
		getCurrentStrategy().set(key, value);
	}

	/**
	 * 获取字符串键值对的值
	 *
	 * @param key 键
	 * @return 获取的值
	 */
	public static String redisGet(String key) {
		return getCurrentStrategy().get(key);
	}

	/**
	 * 删除键
	 *
	 * @param key 要删除的键
	 */
	public static void redisDelete(String key) {
		getCurrentStrategy().del(key);
	}

	/**
	 * 设定键过期
	 *
	 * @param key     要设定过期的键
	 * @param seconds 过期时间（单位：秒）
	 */
	public static void redisExpire(String key, long seconds) {
		getCurrentStrategy().expire(key, seconds);
	}

}