package io.github.swsk33.codepostspringbootstarter.api;

import io.github.swsk33.codepost.context.ServiceNameContext;
import io.github.swsk33.codepost.service.EmailVerifyCodeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多服务的邮件验证码发送校验
 */
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

	}

}