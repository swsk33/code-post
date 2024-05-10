package io.github.swsk33.codepostcore.model.config;

import lombok.Data;

/**
 * 用于连接Redis哨兵集群的配置对象
 */
@Data
public class RedisSentinelConfig extends RedisClientConfig {

	/**
	 * 获取Redis客户端配置唯一单例
	 *
	 * @return 唯一单例
	 */
	public static RedisSentinelConfig getInstance() {
		// 双检锁延迟初始化
		if (INSTANCE == null) {
			synchronized (RedisSentinelConfig.class) {
				if (INSTANCE == null) {
					INSTANCE = new RedisSentinelConfig();
				}
			}
		}
		return (RedisSentinelConfig) INSTANCE;
	}

	private RedisSentinelConfig() {

	}

	/**
	 * 哨兵监控的主节点名称
	 */
	private String masterName;

	/**
	 * 配置哨兵密码
	 * 若哨兵密码和主节点密码相同，则只配置主节点密码即可，此时该项可省略
	 */
	private String sentinelPassword;

	/**
	 * 指定全部哨兵节点的地址和端口，每个地址端口之间使用逗号隔开<br>
	 * 例如：127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002
	 */
	private String nodes;

}