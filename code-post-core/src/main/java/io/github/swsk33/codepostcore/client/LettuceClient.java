package io.github.swsk33.codepostcore.client;

import cn.hutool.core.util.StrUtil;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.model.config.RedisClusterConfig;
import io.github.swsk33.codepostcore.model.config.RedisSentinelConfig;
import io.github.swsk33.codepostcore.model.config.RedisStandaloneConfig;
import io.github.swsk33.codepostcore.util.URLEncodeUtils;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.BaseRedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lettuce Redis 客户端
 */
@Getter
public class LettuceClient {

	/**
	 * Redis 客户端对象，对于不同的Redis集群部署其实际类型会不同
	 */
	private final AbstractRedisClient client;

	/**
	 * Redis 连接对象
	 */
	private final StatefulConnection<String, String> connection;

	/**
	 * Redis 命令对象
	 */
	private final BaseRedisCommands<String, String> commands;

	/**
	 * 通过 Redis 配置构造客户端对象
	 *
	 * @param redisClientConfig Redis 配置对象
	 */
	public LettuceClient(RedisClientConfig redisClientConfig) {
		RedisClientConfig config = Objects.requireNonNull(redisClientConfig, "redisClientConfig 不能为空！");
		// 根据配置对象不同类型判断初始化方式及其对象
		// 单节点 Redis 配置
		if (config instanceof RedisStandaloneConfig) {
			RedisStandaloneConfig standaloneConfig = (RedisStandaloneConfig) config;
			StringBuilder redisUrl = new StringBuilder("redis://");
			if (!StrUtil.isEmpty(config.getPassword())) {
				redisUrl.append(URLEncodeUtils.percentEncode(config.getPassword())).append("@");
			}
			redisUrl.append(standaloneConfig.getHost()).append(":").append(standaloneConfig.getPort());
			RedisClient redisClient = RedisClient.create(redisUrl.toString());
			StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
			this.client = redisClient;
			this.connection = redisConnection;
			this.commands = redisConnection.sync();
			return;
		}
		// Redis 哨兵集群
		if (config instanceof RedisSentinelConfig) {
			RedisSentinelConfig sentinelConfig = (RedisSentinelConfig) config;
			StringBuilder redisUrl = new StringBuilder("redis-sentinel://");
			if (!StrUtil.isEmpty(sentinelConfig.getPassword())) {
				redisUrl.append(URLEncodeUtils.percentEncode(sentinelConfig.getPassword())).append("@");
			}
			redisUrl.append(sentinelConfig.getNodes()).append("?sentinelMasterId=").append(sentinelConfig.getMasterName());
			RedisURI uri = RedisURI.create(redisUrl.toString());
			RedisClient redisClient = RedisClient.create();
			StatefulRedisMasterReplicaConnection<String, String> redisConnection = MasterReplica.connect(redisClient, StringCodec.UTF8, uri);
			this.client = redisClient;
			this.connection = redisConnection;
			this.commands = redisConnection.sync();
			return;
		}
		// Redis Cluster 集群
		if (config instanceof RedisClusterConfig) {
			RedisClusterConfig clusterConfig = (RedisClusterConfig) config;
			List<RedisURI> uriList = new ArrayList<>();
			String[] uriArray = clusterConfig.getNodes().split(",");
			String redisUrlPrefix = !StrUtil.isEmpty(config.getPassword()) ? "redis://" + URLEncodeUtils.percentEncode(config.getPassword()) + "@" : "redis://";
			for (String uri : uriArray) {
				uriList.add(RedisURI.create(redisUrlPrefix + uri));
			}
			RedisClusterClient redisClient = RedisClusterClient.create(uriList);
			StatefulRedisClusterConnection<String, String> redisConnection = redisClient.connect();
			this.client = redisClient;
			this.connection = redisConnection;
			this.commands = redisConnection.sync();
			return;
		}
		throw new IllegalArgumentException("不支持的 Redis 配置类型：" + config.getClass().getName());
	}

}