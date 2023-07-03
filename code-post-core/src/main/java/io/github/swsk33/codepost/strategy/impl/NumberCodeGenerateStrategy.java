package io.github.swsk33.codepost.strategy.impl;

import cn.hutool.core.util.RandomUtil;
import io.github.swsk33.codepost.strategy.CodeGenerateStrategy;

/**
 * 纯数字验证码生成策略
 */
public class NumberCodeGenerateStrategy implements CodeGenerateStrategy {

	@Override
	public String generateCode(int length) {
		return RandomUtil.randomNumbers(length);
	}

}