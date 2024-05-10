package io.github.swsk33.codepostspringboottest.param;

/**
 * 代表邮件验证码服务的枚举（作为服务名的键）
 */
public enum EmailService {
	/**
	 * 用户登录
	 */
	USER_LOGIN,
	/**
	 * 密码重置
	 */
	PASSWORD_RESET,
	/**
	 * 用户注销
	 */
	USER_DELETE
}