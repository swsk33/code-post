package io.github.swsk33.codepostcore.strategy.context;

import io.github.swsk33.codepostcore.param.CodeGenerateMethod;
import io.github.swsk33.codepostcore.strategy.CodeGenerateStrategy;
import io.github.swsk33.codepostcore.strategy.impl.CharCodeGenerateStrategy;
import io.github.swsk33.codepostcore.strategy.impl.NumberCharCodeGenerateStrategy;
import io.github.swsk33.codepostcore.strategy.impl.NumberCodeGenerateStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码生成策略上下文
 */
public class CodeGenerateContext {

	/**
	 * 存放所有验证码生成策略的哈希表
	 */
	private static final Map<String, CodeGenerateStrategy> CODE_GENERATE_STRATEGY_MAP = new HashMap<>();

	// 初始化所有策略
	static {
		CODE_GENERATE_STRATEGY_MAP.put(CodeGenerateMethod.NUMBER, new NumberCodeGenerateStrategy());
		CODE_GENERATE_STRATEGY_MAP.put(CodeGenerateMethod.CHAR, new CharCodeGenerateStrategy());
		CODE_GENERATE_STRATEGY_MAP.put(CodeGenerateMethod.NUMBER_CHAR, new NumberCharCodeGenerateStrategy());
	}

	/**
	 * 调用策略生成验证码
	 *
	 * @param method 验证码生成方法
	 * @param length 生成验证码长度
	 * @return 生成的验证码
	 */
	public static String generateCode(String method, int length) {
		if (!CODE_GENERATE_STRATEGY_MAP.containsKey(method)) {
			return CODE_GENERATE_STRATEGY_MAP.get(CodeGenerateMethod.NUMBER).generateCode(length);
		}
		return CODE_GENERATE_STRATEGY_MAP.get(method).generateCode(length);
	}

}