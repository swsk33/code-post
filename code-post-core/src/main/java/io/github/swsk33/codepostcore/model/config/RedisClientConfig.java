package io.github.swsk33.codepostcore.model.config;

import lombok.Data;

/**
 * Redis客户端配置抽象类（使用Redis存放验证码时）
 */
@Data
public abstract class RedisClientConfig {

	/**
	 * 唯一单例
	 * 延迟初始化函数位于子类，第一次配置时请先通过对应子类进行调用
	 */
	protected static volatile RedisClientConfig INSTANCE;

	/**
	 * 获取Redis客户端配置唯一单例
	 *
	 * @return 唯一单例
	 */
	public static RedisClientConfig getInstance() {
		// 双检锁延迟初始化
		if (INSTANCE == null) {
			throw new RuntimeException("请先调用任意一子类的getInstance初始化单例！");
		}
		return INSTANCE;
	}

	/**
	 * Redis（主节点）密码
	 */
	private String password;

}