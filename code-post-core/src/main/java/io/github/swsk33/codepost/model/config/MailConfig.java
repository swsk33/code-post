package io.github.swsk33.codepost.model.config;

import io.github.swsk33.codepost.param.CodeGenerateMethod;
import io.github.swsk33.codepost.param.CodeStorageMethod;
import lombok.Data;

/**
 * 一些关于邮件发送的配置
 */
@Data
public class MailConfig {

	/**
	 * 唯一单例
	 */
	private static volatile MailConfig INSTANCE;

	/**
	 * 获取邮件配置唯一单例
	 *
	 * @return 唯一单例
	 */
	public static MailConfig getInstance() {
		// 双检锁延迟初始化
		if (INSTANCE == null) {
			synchronized (MailConfig.class) {
				if (INSTANCE == null) {
					INSTANCE = new MailConfig();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * 私有化构造器
	 */
	private MailConfig() {

	}

	/**
	 * SMTP服务器地址
	 */
	private String SMTPHost;

	/**
	 * 发送者邮箱
	 */
	private String email;

	/**
	 * 发送者密码（或者授权码）
	 */
	private String password;

	/**
	 * 是否开启SSL加密
	 */
	private boolean enableSSL = true;

	/**
	 * 网站名（用于放入验证码邮件标题）
	 */
	private String siteName;

	/**
	 * 验证码的保存方式
	 */
	private String codeStorage = CodeStorageMethod.LOCAL_THREAD_POOL;

	/**
	 * 验证码的格式
	 */
	private String codeFormat = CodeGenerateMethod.NUMBER;

	/**
	 * 启用模板引擎发送HTML邮件（使用FreeMarker）
	 */
	private boolean enableTemplateEngine;

}