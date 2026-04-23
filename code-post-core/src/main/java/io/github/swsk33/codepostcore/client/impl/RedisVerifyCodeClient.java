package io.github.swsk33.codepostcore.client.impl;

import io.github.swsk33.codepostcore.client.LettuceClient;
import io.github.swsk33.codepostcore.client.VerifyCodeClient;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 使用 Redis 管理验证码的客户端实现
 */
@Slf4j
public class RedisVerifyCodeClient implements VerifyCodeClient {

	/**
	 * Redis 客户端
	 */
	private final LettuceClient lettuceClient;

	/**
	 * 构造函数，使用 Redis 客户端对象初始化
	 *
	 * @param lettuceClient Lettuce Redis 客户端对象
	 */
	public RedisVerifyCodeClient(LettuceClient lettuceClient) {
		this.lettuceClient = lettuceClient;
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