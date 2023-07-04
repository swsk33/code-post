package io.github.swsk33.codepostcore.strategy.context;

import io.github.swsk33.codepostcore.param.CodeStorageMethod;
import io.github.swsk33.codepostcore.strategy.EmailCodeStrategy;
import io.github.swsk33.codepostcore.strategy.impl.RedisCodeStrategy;
import io.github.swsk33.codepostcore.strategy.impl.ThreadPoolCodeStrategy;
import io.github.swsk33.codepostcore.util.ConstantClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 执行邮件验证码操作策略的上下文
 */
public class EmailCodeContext {

	/**
	 * 存放邮件策略的哈希表
	 */
	private static volatile Map<String, EmailCodeStrategy> emailCodeStrategyMap = new ConcurrentHashMap<>();

	/**
	 * 存放策略名称对应策略类的哈希表，用于延迟初始化
	 */
	private static Map<String, Class<?>> strategyClassMap = new ConcurrentHashMap<>();

	// 初始化策略名称对应的策略类列表
	static {
		strategyClassMap.put(CodeStorageMethod.LOCAL_THREAD_POOL, ThreadPoolCodeStrategy.class);
		strategyClassMap.put(CodeStorageMethod.REDIS, RedisCodeStrategy.class);
	}

	/**
	 * 获取对应的邮件验证码操作策略对象
	 *
	 * @param method 策略名
	 * @return 策略对象
	 */
	private static EmailCodeStrategy getStrategy(String method) {
		// 如果传入方法名位于常量列表但是不在策略哈希表中，说明需要初始化
		// 使用双检锁延迟初始化
		if (ConstantClassUtils.contains(CodeStorageMethod.class, method) && !emailCodeStrategyMap.containsKey(method)) {
			synchronized (EmailCodeContext.class) {
				if (!emailCodeStrategyMap.containsKey(method)) {
					try {
						emailCodeStrategyMap.put(method, (EmailCodeStrategy) strategyClassMap.get(method).getConstructor().newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		// 否则，就是用户传入了错误配置，执行默认策略
		if (!emailCodeStrategyMap.containsKey(method)) {
			return emailCodeStrategyMap.get(CodeStorageMethod.LOCAL_THREAD_POOL);
		}
		return emailCodeStrategyMap.get(method);
	}

	/**
	 * 暂存验证码
	 *
	 * @param method   存放方式
	 * @param key      验证码的键（通常包含用户id等信息）
	 * @param code     验证码
	 * @param period   有效时间
	 * @param timeUnit 有效时间的时间单位
	 */
	public static void saveCode(String method, String key, String code, long period, TimeUnit timeUnit) {
		getStrategy(method).saveCode(key, code, period, timeUnit);
	}

	/**
	 * 校验验证码
	 *
	 * @param method    存放方式
	 * @param key       验证码的键（通常包含用户id等信息）
	 * @param inputCode 用户传入验证码（用于校验）
	 * @return 是否校验成功
	 */
	public static boolean verifyCode(String method, String key, String inputCode) {
		return getStrategy(method).verifyCode(key, inputCode);
	}

}