package io.github.swsk33.codepostcore.model.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * 用于连接Redis Cluster集群的配置对象
 */
@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RedisClusterConfig extends RedisClientConfig {

	/**
	 * Cluster集群中每个节点的地址列表，每个地址使用逗号隔开<br>
	 * 例如：127.0.0.1:8000,127.0.0.1:8001,127.0.0.1:8002
	 */
	private final String nodes;

}