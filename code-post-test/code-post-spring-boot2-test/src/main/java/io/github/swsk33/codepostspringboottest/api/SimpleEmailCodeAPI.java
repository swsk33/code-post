package io.github.swsk33.codepostspringboottest.api;

import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 简单验证码发送和校验
 */
@RestController
@RequestMapping("/api/code/simple")
public class SimpleEmailCodeAPI {

	/**
	 * 自动装配邮件验证码服务
	 */
	@Autowired
	private EmailVerifyCodeService emailVerifyCodeService;

	/**
	 * 发送验证码
	 *
	 * @param email  接收用户邮件
	 * @param userId 用户id
	 * @return 消息
	 */
	@GetMapping("/send/mail/{email}/user-id/{userId}")
	public String sendCode(@PathVariable("email") String email, @PathVariable("userId") int userId) {
		// 调用sendCodeAsync方法即可一键完成验证码生成发送操作
		// 参数分别是：邮箱对应的用户id、用户邮箱、验证码有效时长、验证码有效时长单位
		emailVerifyCodeService.sendCodeAsync(userId, email, 1, TimeUnit.MINUTES);
		return "已发送验证码！";
	}

	/**
	 * 校验验证码
	 *
	 * @param userId    验证码对应的用户id
	 * @param inputCode 传入验证码用于校验（用户输入的验证码）
	 * @return 消息
	 */
	@GetMapping("/verify/user-id/{userId}/code/{inputCode}")
	public String verifyCode(@PathVariable("userId") int userId, @PathVariable("inputCode") String inputCode) {
		// 调用verifyCode方法即可一键完成验证码校验操作
		// 参数分别是：邮箱对应的用户id、用户传入的验证码（用于校验）
		// 校验成功返回true，并且验证码也会立即失效
		boolean result = emailVerifyCodeService.verifyCode(userId, inputCode);
		return result ? "校验成功！" : "验证码错误或者不存在！";
	}

}