package io.github.swsk33.codepostspringbootstarter.property;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.param.CodeGenerateMethod;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 核心的一些配置
 */
@Data
@ConfigurationProperties(prefix = "io.github.swsk33.code-post.core")
public class CoreProperties {

	/**
	 * SMTP服务器地址
	 */
	private String smtpHost;

	/**
	 * 发送者邮箱
	 */
	private String email;

	/**
	 * 发送者密码（或者授权码）
	 */
	private String password;

	/**
	 * 是否开启TLS加密
	 */
	private boolean enableTls = true;

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
	private boolean enableHtml = false;

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
	 * 将读取到的配置设定到邮件配置对象中去
	 *
	 * @param mailConfig 传入邮件配置对象
	 */
	public void setMailConfig(MailConfig mailConfig) {
		mailConfig.setSMTPHost(smtpHost);
		mailConfig.setEmail(email);
		mailConfig.setPassword(password);
		mailConfig.setEnableTLS(enableTls);
		mailConfig.setCodeStorage(codeStorage);
		mailConfig.setCodeFormat(codeFormat);
		mailConfig.setCodeLength(codeLength);
		mailConfig.setSiteName(siteName);
		mailConfig.setEnableHTML(enableHtml);
		mailConfig.setTemplatePath(templatePath);
		mailConfig.setCodeTemplateName(codeTemplateName);
	}

}