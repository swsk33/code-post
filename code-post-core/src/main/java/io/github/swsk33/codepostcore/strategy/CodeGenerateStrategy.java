package io.github.swsk33.codepostcore.strategy;

/**
 * 验证码生成策略
 */
public interface CodeGenerateStrategy {

	/**
	 * 生成一个随机验证码
	 *
	 * @param length 验证码长度
	 * @return 得到的验证码
	 */
	String generateCode(int length);

}