package io.github.swsk33.codepostcore.strategy.impl;

import io.github.swsk33.codepostcore.config.LettuceClientConfig;
import io.github.swsk33.codepostcore.strategy.RedisCommandStrategy;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

/**
 * 在连接Redis Cluster分片集群时，调用RedisClusterCommands操作Redis的策略实现类
 */
public class ClusterRedisCommandStrategy implements RedisCommandStrategy {

	/**
	 * Redis命令对象
	 */
	private final RedisClusterCommands<String, String> commands;

	/**
	 * 构造函数，用于初始化Redis命令对象
	 */
	public ClusterRedisCommandStrategy() {
		commands = (RedisClusterCommands<String, String>) LettuceClientConfig.getCommands();
	}

	@Override
	public void set(String key, String value) {
		commands.set(key, value);
	}

	@Override
	public String get(String key) {
		return commands.get(key);
	}

	@Override
	public void del(String key) {
		commands.del(key);
	}

	@Override
	public void expire(String key, long seconds) {
		commands.expire(key, seconds);
	}

}