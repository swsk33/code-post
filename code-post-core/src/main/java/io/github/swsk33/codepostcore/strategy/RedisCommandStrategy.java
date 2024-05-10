package io.github.swsk33.codepostcore.strategy;

/**
 * 根据连接的Redis类型不同（单机、哨兵、Cluster），调用不同的RedisCommands对象操作Redis的抽象策略
 */
public interface RedisCommandStrategy {

	/**
	 * 设定字符串键值对
	 *
	 * @param key   键
	 * @param value 值
	 */
	void set(String key, String value);

	/**
	 * 获取字符串键值对的值
	 *
	 * @param key 键
	 * @return 获取的值
	 */
	String get(String key);

	/**
	 * 删除键
	 *
	 * @param key 要删除的键
	 */
	void del(String key);

	/**
	 * 设定键过期
	 *
	 * @param key     要设定过期的键
	 * @param seconds 过期时间（单位：秒）
	 */
	void expire(String key, long seconds);

}