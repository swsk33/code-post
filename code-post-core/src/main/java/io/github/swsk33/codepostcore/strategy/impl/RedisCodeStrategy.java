package io.github.swsk33.codepostcore.strategy.impl;

import io.github.swsk33.codepostcore.client.LettuceClient;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.model.config.RedisClusterConfig;
import io.github.swsk33.codepostcore.strategy.EmailCodeStrategy;
import io.github.swsk33.codepostcore.strategy.RedisCommandStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 使用Redis管理验证码的策略
 */
@Slf4j
public class RedisCodeStrategy implements EmailCodeStrategy {

	private final RedisClientConfig redisClientConfig;

	private volatile RedisCommandStrategy redisCommandStrategy;

	public RedisCodeStrategy(RedisClientConfig redisClientConfig) {
		this.redisClientConfig = Objects.requireNonNull(redisClientConfig, "redisClientConfig must not be null");
	}

	private RedisCommandStrategy getRedisCommandStrategy() {
		if (redisCommandStrategy == null) {
			synchronized (this) {
				if (redisCommandStrategy == null) {
					LettuceClient lettuceClient = new LettuceClient(redisClientConfig);
					if (redisClientConfig instanceof RedisClusterConfig) {
						redisCommandStrategy = new ClusterRedisCommandStrategy(lettuceClient.getCommands());
					} else {
						redisCommandStrategy = new CommonRedisCommandStrategy(lettuceClient.getCommands());
					}
				}
			}
		}
		return redisCommandStrategy;
	}

	@Override
	public void saveCode(String key, String code, long period, TimeUnit timeUnit) {
		getRedisCommandStrategy().set(key, code);
		getRedisCommandStrategy().expire(key, timeUnit.toSeconds(period));
		log.info("验证码键：" + key + "已保存！");
	}

	@Override
	public boolean verifyCode(String key, String inputCode) {
		String code = getRedisCommandStrategy().get(key);
		if (inputCode.equals(code)) {
			getRedisCommandStrategy().del(key);
			log.info("验证码键：" + key + " 校验成功！");
			return true;
		}
		return false;
	}

}