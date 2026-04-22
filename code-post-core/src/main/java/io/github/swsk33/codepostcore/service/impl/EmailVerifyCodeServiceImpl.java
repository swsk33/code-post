package io.github.swsk33.codepostcore.service.impl;

import io.github.swsk33.codepostcore.client.FreeMarkerClient;
import io.github.swsk33.codepostcore.client.MailClient;
import io.github.swsk33.codepostcore.context.ServiceNameContext;
import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import io.github.swsk33.codepostcore.client.VerifyCodeClient;
import io.github.swsk33.codepostcore.strategy.context.CodeGenerateContext;
import io.github.swsk33.codepostcore.client.impl.RedisVerifyCodeClient;
import io.github.swsk33.codepostcore.client.impl.ThreadPoolVerifyCodeClient;
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
	 * 邮件验证码生成操作客户端
	 */
	private final VerifyCodeClient verifyCodeClient;

	/**
	 * 邮件客户端对象
	 */
	private final MailClient mailClient;

	/**
	 * 模板渲染客户端
	 */
	private final FreeMarkerClient freeMarkerClient;

	/**
	 * 使用邮件配置对象初始化服务，适用于本地线程池验证码存储方案
	 *
	 * @param mailConfig 邮件配置
	 */
	public EmailVerifyCodeServiceImpl(MailConfig mailConfig) {
		if (mailConfig == null) {
			throw new IllegalArgumentException("mailConfig 不能为空！");
		}
		if (!CodeStorageMethod.LOCAL_THREAD_POOL.equals(mailConfig.getCodeStorage())) {
			throw new IllegalArgumentException("当未传入Redis配置时，验证码过期方案必须为 local_thread_pool");
		}
		this.mailConfig = mailConfig;
		this.verifyCodeClient = new ThreadPoolVerifyCodeClient();
		this.mailClient = new MailClient(mailConfig);
		this.freeMarkerClient = new FreeMarkerClient(mailConfig);
	}

	/**
	 * 使用邮件配置对象和 Redis 配置对象初始化，适用于 Redis 的验证码过期方案
	 *
	 * @param mailConfig        邮件配置
	 * @param redisClientConfig Redis 配置
	 */
	public EmailVerifyCodeServiceImpl(MailConfig mailConfig, RedisClientConfig redisClientConfig) {
		if (mailConfig == null) {
			throw new IllegalArgumentException("mailConfig 不能为空！");
		}
		if (redisClientConfig == null) {
			throw new IllegalArgumentException("redisClientConfig 不能为空！");
		}
		if (!CodeStorageMethod.REDIS.equals(mailConfig.getCodeStorage())) {
			throw new IllegalArgumentException("当传入 Redis 配置时，验证码过期方案也必须为 redis");
		}
		this.mailConfig = mailConfig;
		this.verifyCodeClient = new RedisVerifyCodeClient(redisClientConfig);
		this.mailClient = new MailClient(mailConfig);
		this.freeMarkerClient = new FreeMarkerClient(mailConfig);
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