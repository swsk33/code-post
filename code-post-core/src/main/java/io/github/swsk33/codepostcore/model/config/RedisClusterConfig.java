package io.github.swsk33.codepostcore.model.config;

import lombok.Data;

/**
 * 用于连接Redis Cluster集群的配置对象
 */
@Data
public class RedisClusterConfig extends RedisClientConfig {

	/**
	 * 获取Redis客户端配置唯一单例
	 *
	 * @return 唯一单例
	 */
	public static RedisClusterConfig getInstance() {
		// 双检锁延迟初始化
		if (INSTANCE == null) {
			synchronized (RedisClusterConfig.class) {
				if (INSTANCE == null) {
					INSTANCE = new RedisClusterConfig();
				}
			}
		}
		return (RedisClusterConfig) INSTANCE;
	}

	private RedisClusterConfig() {

	}

	/**
	 * Cluster集群中每个节点的地址列表，每个地址使用逗号隔开<br>
	 * 例如：127.0.0.1:8000,127.0.0.1:8001,127.0.0.1:8002
	 */
	private String nodes;

}