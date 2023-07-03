package io.github.swsk33.codepost.strategy.impl;

import io.github.swsk33.codepost.config.LettuceClientConfig;
import io.github.swsk33.codepost.strategy.EmailCodeStrategy;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 使用Redis管理验证码的策略
 */
@Slf4j
public class RedisCodeStrategy implements EmailCodeStrategy {

	/**
	 * Redis命令执行对象
	 */
	RedisAsyncCommands<String, String> commands;

	/**
	 * 构造器：初始化命令执行对象
	 */
	public RedisCodeStrategy() {
		commands = LettuceClientConfig.getCommands();
	}

	@Override
	public void saveCode(String key, String code, long period, TimeUnit timeUnit) {
		// 保存验证码到Redis
		commands.set(key, code);
		// 设定过期时间
		commands.expire(key, timeUnit.toSeconds(period));
		log.info("验证码键：" + key + "已保存！");
	}

	@Override
	public boolean verifyCode(String key, String inputCode) {
		// 校验成功则移除验证码
		String code = null;
		try {
			code = commands.get(key).get(2, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 校验成功则移除验证码
		if (inputCode.equals(code)) {
			commands.del(key);
			log.info("验证码键：" + key + " 校验成功！");
			return true;
		}
		// 否则不执行任何操作
		return false;
	}

}