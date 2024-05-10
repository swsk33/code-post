package io.github.swsk33.codepostcore.strategy.impl;

import io.github.swsk33.codepostcore.config.LettuceClientConfig;
import io.github.swsk33.codepostcore.strategy.RedisCommandStrategy;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * 在连接Redis单机和哨兵集群时，调用RedisCommands对象操作Redis的策略实现
 */
public class CommonRedisCommandStrategy implements RedisCommandStrategy {

	/**
	 * Redis命令对象
	 */
	private final RedisCommands<String, String> commands;

	/**
	 * 构造函数，初始化命令对象
	 */
	public CommonRedisCommandStrategy() {
		commands = (RedisCommands<String, String>) LettuceClientConfig.getCommands();
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