package io.github.swsk33.codepostspringboottest.api;

import io.github.swsk33.codepostcore.context.ServiceNameContext;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import io.github.swsk33.codepostspringboottest.param.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 多服务的邮件验证码发送校验
 */
@Slf4j
@RestController
@RequestMapping("/api/code/multi-service")
public class MultiServiceCodeAPI {

	/**
	 * 自动装配邮件验证码服务
	 */
	@Autowired
	private EmailVerifyCodeService emailVerifyCodeService;

	/**
	 * 注册你的服务
	 */
	@PostConstruct
	private void initService() {
		// 调用ServiceNameContext类的静态方法即可完成服务名注册
		ServiceNameContext.register(EmailService.USER_LOGIN, "用户登录");
		ServiceNameContext.register(EmailService.PASSWORD_RESET, "密码重置");
		ServiceNameContext.register(EmailService.USER_DELETE, "用户注销");
		log.info("所有邮件验证码服务注册完成！");
	}

	/**
	 * 发送用户登录验证码
	 *
	 * @param mail   用户邮箱
	 * @param userId 用户id
	 * @return 结果
	 */
	@GetMapping("/login-code-send/mail/{mail}/user-id/{userId}")
	public String loginCodeSend(@PathVariable("mail") String mail, @PathVariable("userId") int userId) {
		emailVerifyCodeService.sendCodeAsync(EmailService.USER_LOGIN, userId, mail, 1, TimeUnit.MINUTES);
		return "用户登录验证码已发送！";
	}

	/**
	 * 发送密码重置验证码
	 *
	 * @param mail   用户邮箱
	 * @param userId 用户id
	 * @return 结果
	 */
	@GetMapping("/reset-code-send/mail/{mail}/user-id/{userId}")
	public String passwordResetCodeSend(@PathVariable("mail") String mail, @PathVariable("userId") int userId) {
		emailVerifyCodeService.sendCodeAsync(EmailService.PASSWORD_RESET, userId, mail, 1, TimeUnit.MINUTES);
		return "用户密码重置验证码已发送！";
	}

	/**
	 * 校验用户登录验证码
	 *
	 * @param userId    验证码对应的用户id
	 * @param inputCode 用户传入的验证码（用于校验）
	 * @return 结果
	 */
	@GetMapping("/login-code-verify/user-id/{userId}/code/{inputCode}")
	public String verifyLoginCode(@PathVariable("userId") int userId, @PathVariable("inputCode") String inputCode) {
		boolean result = emailVerifyCodeService.verifyCode(EmailService.USER_LOGIN, userId, inputCode);
		return result ? "用户登录验证码校验成功！" : "验证码错误或者不存在！";
	}

	/**
	 * 校验用户密码重置验证码
	 *
	 * @param userId    验证码对应的用户id
	 * @param inputCode 用户传入的验证码（用于校验）
	 * @return 结果
	 */
	@GetMapping("/reset-code-verify/user-id/{userId}/code/{inputCode}")
	public String verifyResetCode(@PathVariable("userId") int userId, @PathVariable("inputCode") String inputCode) {
		boolean result = emailVerifyCodeService.verifyCode(EmailService.PASSWORD_RESET, userId, inputCode);
		return result ? "用户密码重置验证码校验成功！" : "验证码错误或者不存在！";
	}

}