package io.github.swsk33.codepostcore.strategy.impl;

import cn.hutool.core.util.RandomUtil;
import io.github.swsk33.codepostcore.strategy.CodeGenerateStrategy;

/**
 * 数字和字母验证码生成策略
 */
public class NumberCharCodeGenerateStrategy implements CodeGenerateStrategy {

	@Override
	public String generateCode(int length) {
		return RandomUtil.randomString(length);
	}

}