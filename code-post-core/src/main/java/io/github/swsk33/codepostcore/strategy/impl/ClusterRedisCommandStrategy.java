package io.github.swsk33.codepostcore.strategy.impl;

import io.github.swsk33.codepostcore.strategy.RedisCommandStrategy;
import io.lettuce.core.api.sync.BaseRedisCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

/**
 * 在连接Redis Cluster分片集群时，调用RedisClusterCommands操作Redis的策略实现类
 */
public class ClusterRedisCommandStrategy implements RedisCommandStrategy {

	private final RedisClusterCommands<String, String> commands;

	public ClusterRedisCommandStrategy(BaseRedisCommands<String, String> commands) {
		this.commands = (RedisClusterCommands<String, String>) commands;
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