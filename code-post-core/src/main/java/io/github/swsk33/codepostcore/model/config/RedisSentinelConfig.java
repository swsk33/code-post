package io.github.swsk33.codepostcore.model.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 用于连接 Redis 哨兵集群的配置对象
 */
@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RedisSentinelConfig extends RedisClientConfig {

	/**
	 * 完整连接url，若该属性不为空，则会采用该url进行连接，并忽略其它配置
	 */
	private final String url;

	/**
	 * 哨兵监控的主节点名称
	 */
	private final String masterName;

	/**
	 * 指定全部哨兵节点的地址和端口<br>
	 * 列表中每个元素是一个哨兵节点地址和端口，例如：127.0.0.1:7000
	 */
	private final List<String> nodes;

	/**
	 * Redis数据库编号，默认：0
	 */
	@Builder.Default
	private final int database = 0;

}