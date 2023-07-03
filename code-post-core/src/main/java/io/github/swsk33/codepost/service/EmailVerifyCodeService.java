package io.github.swsk33.codepost.service;

import java.util.concurrent.TimeUnit;

/**
 * 邮件验证码相关的服务接口
 */
public interface EmailVerifyCodeService {

	/**
	 * 发送验证码，使用默认服务名default
	 *
	 * @param userId        验证码对应的用户id
	 * @param receiverEmail 验证码接收者邮箱
	 * @param period        验证码有效时长
	 * @param timeUnit      验证码有效时长的时间单位
	 */
	void sendCode(Object userId, String receiverEmail, long period, TimeUnit timeUnit);

	/**
	 * 发送验证码，使用自定义服务名
	 *
	 * @param serviceNameKey 验证码对应服务名的键
	 * @param userId         验证码对应的用户id
	 * @param receiverEmail  验证码接收者邮箱
	 * @param period         验证码有效时长
	 * @param timeUnit       验证码有效时长的时间单位
	 */
	void sendCode(String serviceNameKey, Object userId, String receiverEmail, long period, TimeUnit timeUnit);

	/**
	 * 校验验证码，使用默认服务名default
	 *
	 * @param userId    验证码对应的用户id
	 * @param inputCode 用户传入的验证码，用于校验
	 * @return 验证码是否正确
	 */
	boolean verifyCode(Object userId, String inputCode);

	/**
	 * 校验验证码，使用自定义服务名
	 *
	 * @param serviceNameKey 验证码对应服务名的键
	 * @param userId         验证码对应的用户id
	 * @param inputCode      用户传入的验证码，用于校验
	 * @return 验证码是否正确
	 */
	boolean verifyCode(String serviceNameKey, Object userId, String inputCode);

}