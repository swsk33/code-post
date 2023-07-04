package io.github.swsk33.codepostcore.strategy;

import java.util.concurrent.TimeUnit;

/**
 * 关于一些邮件验证码的操作策略
 */
public interface EmailCodeStrategy {

	/**
	 * 暂存验证码
	 *
	 * @param key      验证码的键（通常由用户的id等部分信息组成）
	 * @param code     要存放的验证码
	 * @param period   验证码过期时间
	 * @param timeUnit 验证码过期时间的时间单位
	 */
	void saveCode(String key, String code, long period, TimeUnit timeUnit);

	/**
	 * 校验验证码
	 * 校验成功后验证码立即失效
	 *
	 * @param key       验证码的键（通常由用户的id等部分信息组成）
	 * @param inputCode 传入用户输入的验证码
	 * @return 是否校验成功
	 */
	boolean verifyCode(String key, String inputCode);

}