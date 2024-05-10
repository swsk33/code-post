package io.github.swsk33.codepostcore.strategy.impl;

import io.github.swsk33.codepostcore.strategy.EmailCodeStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static io.github.swsk33.codepostcore.strategy.context.RedisCommandContext.*;

/**
 * 使用Redis管理验证码的策略
 */
@Slf4j
public class RedisCodeStrategy implements EmailCodeStrategy {

	@Override
	public void saveCode(String key, String code, long period, TimeUnit timeUnit) {
		// 保存验证码到Redis
		redisSet(key, code);
		// 设定过期时间
		redisExpire(key, timeUnit.toSeconds(period));
		log.info("验证码键：" + key + "已保存！");
	}

	@Override
	public boolean verifyCode(String key, String inputCode) {
		// 校验成功则移除验证码
		String code = redisGet(key);
		// 校验成功则移除验证码
		if (inputCode.equals(code)) {
			redisDelete(key);
			log.info("验证码键：" + key + " 校验成功！");
			return true;
		}
		// 否则不执行任何操作
		return false;
	}

}