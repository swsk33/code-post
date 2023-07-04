package io.github.swsk33.codepostcore.model.config;

import io.github.swsk33.codepostcore.param.CodeGenerateMethod;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 一些关于邮件发送的配置
 */
@Data
@Slf4j
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
	 * 是否启用TLS加密协议
	 */
	private boolean enableTLS = true;

	/**
	 * 验证码的保存方式
	 */
	private String codeStorage = CodeStorageMethod.LOCAL_THREAD_POOL;

	/**
	 * 验证码的格式
	 */
	private String codeFormat = CodeGenerateMethod.NUMBER;

	/**
	 * 验证码长度
	 */
	private int codeLength = 6;

	/**
	 * 网站名
	 */
	private String siteName = "网站名";

	/**
	 * 启用是否发送HTML邮件
	 */
	private boolean enableHTML = false;

	/**
	 * 模板文件所在目录
	 * 需要以file:或者classpath:开头，分别代表文件系统路径或者类路径
	 */
	private String templatePath;

	/**
	 * 用于邮件验证码内容的模板文件名<br>
	 * 该项只填写文件名！<br>
	 * 该项会和templatePath配置值拼接得到完整文件路径
	 */
	private String codeTemplateName;

	/**
	 * 重置模板文件夹和验证码模板文件名为内置默认值
	 */
	public void resetTemplatePath() {
		templatePath = "classpath:/code-post-default";
		codeTemplateName = enableHTML ? "verify-code-template.ftlh" : "verify-code-template.txt";
		log.warn("检测到未配置或者配置的验证码模板文件不存在！恢复为默认配置！");
	}

}