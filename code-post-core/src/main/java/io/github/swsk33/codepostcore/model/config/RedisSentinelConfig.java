package io.github.swsk33.codepostcore.model.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * 用于连接 Redis 哨兵集群的配置对象
 */
@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RedisSentinelConfig extends RedisClientConfig {

	/**
	 * 哨兵监控的主节点名称
	 */
	private final String masterName;

	/**
	 * 指定全部哨兵节点的地址和端口，每个地址端口之间使用逗号隔开<br>
	 * 例如：127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002
	 */
	private final String nodes;

	/**
	 * Redis数据库编号，默认：0
	 */
	@Builder.Default
	private final int database = 0;

}