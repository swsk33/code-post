package io.github.swsk33.codepostcore.model.config;

import io.github.swsk33.codepostcore.param.CodeGenerateMethod;
import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import lombok.Builder;
import lombok.Value;

/**
 * 一些关于邮件发送的配置
 */
@Value
@Builder(toBuilder = true)
public class MailConfig {

	/**
	 * SMTP 服务器地址
	 */
	String smtpHost;

	/**
	 * 发送者邮箱
	 */
	String email;

	/**
	 * 发送者密码（或者授权码）
	 */
	String password;

	/**
	 * 是否启用 TLS 加密协议
	 */
	@Builder.Default
	boolean enableTls = true;

	/**
	 * 验证码的保存方式
	 */
	@Builder.Default
	String codeStorage = CodeStorageMethod.LOCAL_THREAD_POOL;

	/**
	 * 验证码的格式
	 */
	@Builder.Default
	String codeFormat = CodeGenerateMethod.NUMBER;

	/**
	 * 验证码长度
	 */
	@Builder.Default
	int codeLength = 6;

	/**
	 * 网站名
	 */
	@Builder.Default
	String siteName = "网站名";

	/**
	 * 启用是否发送 HTML 邮件
	 */
	@Builder.Default
	boolean enableHtml = false;

	/**
	 * 模板文件所在目录
	 * 需要以file:或者classpath:开头，分别代表文件系统路径或者类路径
	 */
	String templatePath;

	/**
	 * 用于邮件验证码内容的模板文件名<br>
	 * 该项只填写文件名！<br>
	 * 该项会和 templatePath 配置值拼接得到完整文件路径
	 */
	String codeTemplateName;

}