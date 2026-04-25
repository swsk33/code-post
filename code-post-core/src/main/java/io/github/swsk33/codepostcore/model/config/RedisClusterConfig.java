package io.github.swsk33.codepostcore.model.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 用于连接Redis Cluster集群的配置对象
 */
@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RedisClusterConfig extends RedisClientConfig {

	/**
	 * Cluster 集群中每个节点的地址列表<br>
	 * 列表中每个元素是一个集群节点的地址和端口，例如：127.0.0.1:8000
	 */
	private final List<String> nodes;

}