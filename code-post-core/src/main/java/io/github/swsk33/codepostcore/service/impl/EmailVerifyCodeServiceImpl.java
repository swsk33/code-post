package io.github.swsk33.codepostcore.service.impl;

import io.github.swsk33.codepostcore.client.FreeMarkerClient;
import io.github.swsk33.codepostcore.client.LettuceClient;
import io.github.swsk33.codepostcore.client.MailClient;
import io.github.swsk33.codepostcore.client.VerifyCodeClient;
import io.github.swsk33.codepostcore.client.impl.RedisVerifyCodeClient;
import io.github.swsk33.codepostcore.client.impl.ThreadPoolVerifyCodeClient;
import io.github.swsk33.codepostcore.context.ServiceNameContext;
import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import io.github.swsk33.codepostcore.strategy.context.CodeGenerateContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static io.github.swsk33.codepostcore.context.SendThreadPoolContext.submitTask;
import static io.github.swsk33.codepostcore.context.ServiceNameContext.DEFAULT_SERVICE_KEY;
import static io.github.swsk33.codepostcore.util.CodeKeyUtils.generateCodeKey;

/**
 * 邮件验证码接口服务实现类
 */
@Slf4j
public class EmailVerifyCodeServiceImpl implements EmailVerifyCodeService {

	/**
	 * 邮件配置对象
	 */
	private final MailConfig mailConfig;

	/**
	 * 邮件客户端对象
	 */
	private final MailClient mailClient;

	/**
	 * 模板渲染客户端
	 */
	private final FreeMarkerClient freeMarkerClient;

	/**
	 * 邮件验证码生成操作客户端
	 */
	private final VerifyCodeClient verifyCodeClient;

	/**
	 * 使用邮箱核心配置构造邮件验证码服务实例，将会自动创建所需客户端，仅适用于本地线程池管理验证码的场景
	 *
	 * @param mailConfig 邮箱核心配置
	 */
	public EmailVerifyCodeServiceImpl(MailConfig mailConfig) {
		this(mailConfig, new MailClient(mailConfig), new FreeMarkerClient(mailConfig), new ThreadPoolVerifyCodeClient());
	}

	/**
	 * 使用邮箱核心配置和Redis配置构造邮件验证码服务实例，将会自动创建所需客户端，仅适用于Redis管理验证码的场景
	 *
	 * @param mailConfig  邮箱核心配置
	 * @param redisConfig Redis 配置
	 */
	public EmailVerifyCodeServiceImpl(MailConfig mailConfig, RedisClientConfig redisConfig) {
		this(mailConfig, new MailClient(mailConfig), new FreeMarkerClient(mailConfig), new RedisVerifyCodeClient(new LettuceClient(redisConfig)));
	}

	/**
	 * 手动构造邮件验证码服务实例
	 *
	 * @param mailConfig       邮箱核心配置
	 * @param mailClient       邮箱客户端
	 * @param freeMarkerClient 渲染引擎客户端
	 * @param verifyCodeClient 验证码客户端，根据不同配置传递不同实例：
	 *                         <ul>
	 *                         <li>使用本地线程池管理验证码，则传入{@link io.github.swsk33.codepostcore.client.impl.ThreadPoolVerifyCodeClient}实例</li>
	 *                         <li>使用 Redis 管理验证码：则传入{@link io.github.swsk33.codepostcore.client.impl.RedisVerifyCodeClient}</li>
	 *                         </ul>
	 */
	public EmailVerifyCodeServiceImpl(MailConfig mailConfig, MailClient mailClient, FreeMarkerClient freeMarkerClient, VerifyCodeClient verifyCodeClient) {
		this.mailConfig = mailConfig;
		this.mailClient = mailClient;
		this.freeMarkerClient = freeMarkerClient;
		this.verifyCodeClient = verifyCodeClient;
	}

	@Override
	public void sendCode(Object userId, String receiverEmail, long period, TimeUnit timeUnit) {
		sendCode(DEFAULT_SERVICE_KEY, userId, receiverEmail, period, timeUnit);
	}

	@Override
	public void sendCode(String serviceNameKey, Object userId, String receiverEmail, long period, TimeUnit timeUnit) {
		String code = CodeGenerateContext.generateCode(mailConfig.getCodeFormat(), mailConfig.getCodeLength());
		String mailContent = freeMarkerClient.renderVerifyMailTemplate(serviceNameKey, code, period, timeUnit);
		mailClient.sendEmail(mailConfig.getEmail(), mailConfig.getSiteName() + " - " + ServiceNameContext.getServiceName(serviceNameKey), mailContent, new String[]{receiverEmail}, mailConfig.isEnableHtml());
		verifyCodeClient.saveCode(generateCodeKey(serviceNameKey, userId), code, period, timeUnit);
		log.info("已向{}发送验证码邮件！", receiverEmail);
	}

	@Override
	public void sendCode(Enum<?> serviceNameKey, Object userId, String receiverEmail, long period, TimeUnit timeUnit) {
		sendCode(serviceNameKey.toString(), userId, receiverEmail, period, timeUnit);
	}

	@Override
	public void sendCodeAsync(Object userId, String receiverEmail, long period, TimeUnit timeUnit) {
		submitTask(() -> sendCode(userId, receiverEmail, period, timeUnit));
	}

	@Override
	public void sendCodeAsync(String serviceNameKey, Object userId, String receiverEmail, long period, TimeUnit timeUnit) {
		submitTask(() -> sendCode(serviceNameKey, userId, receiverEmail, period, timeUnit));
	}

	@Override
	public void sendCodeAsync(Enum<?> serviceNameKey, Object userId, String receiverEmail, long period, TimeUnit timeUnit) {
		submitTask(() -> sendCode(serviceNameKey, userId, receiverEmail, period, timeUnit));
	}

	@Override
	public boolean verifyCode(Object userId, String inputCode) {
		return verifyCode(DEFAULT_SERVICE_KEY, userId, inputCode);
	}

	@Override
	public boolean verifyCode(String serviceNameKey, Object userId, String inputCode) {
		return verifyCodeClient.verifyCode(generateCodeKey(serviceNameKey, userId), inputCode);
	}

	@Override
	public boolean verifyCode(Enum<?> serviceNameKey, Object userId, String inputCode) {
		return verifyCode(serviceNameKey.toString(), userId, inputCode);
	}

}