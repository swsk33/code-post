package io.github.swsk33.codepostcore.strategy.impl;

import io.github.swsk33.codepostcore.client.LettuceClient;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.strategy.EmailCodeStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 使用 Redis 管理验证码的策略
 */
@Slf4j
public class RedisCodeStrategy implements EmailCodeStrategy {

	private final LettuceClient lettuceClient;

	public RedisCodeStrategy(RedisClientConfig redisClientConfig) {
		this.lettuceClient = new LettuceClient(Objects.requireNonNull(redisClientConfig, "redisClientConfig must not be null"));
	}

	@Override
	public void saveCode(String key, String code, long period, TimeUnit timeUnit) {
		lettuceClient.getCommands().set(key, code);
		lettuceClient.getCommands().expire(key, timeUnit.toSeconds(period));
		log.info("验证码键：{}已保存！", key);
	}

	@Override
	public boolean verifyCode(String key, String inputCode) {
		String code = lettuceClient.getCommands().get(key);
		if (inputCode.equals(code)) {
			lettuceClient.getCommands().del(key);
			log.info("验证码键：{} 校验成功！", key);
			return true;
		}
		return false;
	}

}