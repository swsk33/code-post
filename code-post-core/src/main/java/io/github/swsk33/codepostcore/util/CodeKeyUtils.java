package io.github.swsk33.codepostcore.util;

/**
 * 用于生成验证码键的实用类
 */
public class CodeKeyUtils {

	/**
	 * 生成一个验证码键
	 *
	 * @param serviceNameKey 验证码对应的服务名
	 * @param userId         验证码对应的用户id
	 * @return 验证码键
	 */
	public static String generateCodeKey(String serviceNameKey, Object userId) {
		return "code-post:" + serviceNameKey + ":" + (userId != null ? userId.toString() : "null");
	}

}