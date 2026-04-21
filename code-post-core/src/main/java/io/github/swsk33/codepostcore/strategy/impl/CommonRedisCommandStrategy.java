package io.github.swsk33.codepostcore.strategy.impl;

import io.github.swsk33.codepostcore.strategy.RedisCommandStrategy;
import io.lettuce.core.api.sync.BaseRedisCommands;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * 在连接Redis单机和哨兵集群时，调用RedisCommands对象操作Redis的策略实现
 */
public class CommonRedisCommandStrategy implements RedisCommandStrategy {

	private final RedisCommands<String, String> commands;

	public CommonRedisCommandStrategy(BaseRedisCommands<String, String> commands) {
		this.commands = (RedisCommands<String, String>) commands;
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