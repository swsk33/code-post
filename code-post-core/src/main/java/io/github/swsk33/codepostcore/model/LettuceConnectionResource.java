package io.github.swsk33.codepostcore.model;

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
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Lettuce连接资源对象，用于存放和组合Lettuce相关客户端对象、连接对象及其命令对象的类
 */
@Slf4j
@Getter
public class LettuceConnectionResource {

	/**
	 * Redis客户端对象<br>
	 * 在使用不同的Redis集群模式时，该对象需要进行不同类型的向下转型操作
	 * <ul>
	 *     <li>当使用单机节点或者哨兵集群时，该对象实际上是{@link RedisClient}类型 </li>
	 *     <li>当使用Cluster分片集群时，该对象实际上是{@link RedisClusterClient}类型</li>
	 * </ul>
	 */
	private AbstractRedisClient client;

	/**
	 * Redis连接对象<br>
	 * 在使用不同的Redis集群模式时，该对象需要进行不同类型的向下转型操作
	 * <ul>
	 *     <li>单机节点：{@link StatefulRedisConnection}类型</li>
	 *     <li>哨兵集群：{@link StatefulRedisMasterReplicaConnection}类型</li>
	 *     <li>Cluster分片集群：{@link StatefulRedisClusterConnection}类型</li>
	 * </ul>
	 */
	private StatefulConnection<String, String> connection;

	/**
	 * 用于执行Redis命令的对象
	 * 在使用不同的Redis集群模式时，该对象需要进行不同类型的向下转型操作
	 * <ul>
	 *     <li>单机节点和哨兵集群：{@link RedisCommands}类型</li>
	 *     <li>Cluster分片集群：{@link RedisAdvancedClusterCommands}类型</li>
	 * </ul>
	 */
	private BaseRedisCommands<String, String> commands;

	/**
	 * 传入配置，创建一个Lettuce的连接资源对象
	 *
	 * @param config 配置
	 */
	public LettuceConnectionResource(RedisClientConfig config) {
		// 判断实例类型，来判断用户配置的是单节点、哨兵集群还是Cluster集群
		// 单节点
		if (config instanceof RedisStandaloneConfig) {
			RedisStandaloneConfig standaloneConfig = (RedisStandaloneConfig) config;
			StringBuilder redisURL = new StringBuilder("redis://");
			// 处理密码
			if (!StrUtil.isEmpty(config.getPassword())) {
				redisURL.append(URLEncodeUtils.percentEncode(config.getPassword())).append("@");
			}
			// 追加地址
			redisURL.append(standaloneConfig.getHost()).append(":").append(standaloneConfig.getPort());
			// 创建客户端
			RedisClient redisClient = RedisClient.create(redisURL.toString());
			this.client = redisClient;
			// 创建连接对象
			StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
			this.connection = redisConnection;
			// 创建命令对象
			this.commands = redisConnection.sync();
			log.info("已连接到Redis单节点服务端");
		}
		// Sentinel集群
		if (config instanceof RedisSentinelConfig) {
			RedisSentinelConfig sentinelConfig = (RedisSentinelConfig) config;
			StringBuilder redisURL = new StringBuilder("redis-sentinel://");
			// 处理密码
			if (!StrUtil.isEmpty(sentinelConfig.getPassword())) {
				redisURL.append(URLEncodeUtils.percentEncode(sentinelConfig.getPassword())).append("@");
			}
			// 追加地址
			redisURL.append(sentinelConfig.getNodes()).append("?sentinelMasterId=").append(sentinelConfig.getMasterName());
			RedisURI uri = RedisURI.create(redisURL.toString());
			// 创建客户端
			RedisClient redisClient = RedisClient.create();
			this.client = redisClient;
			// 创建连接对象
			StatefulRedisMasterReplicaConnection<String, String> redisConnection = MasterReplica.connect(redisClient, StringCodec.UTF8, uri);
			this.connection = redisConnection;
			// 创建命令对象
			this.commands = redisConnection.sync();
			log.info("已连接到Redis哨兵集群");
		}
		// Cluster集群
		if (config instanceof RedisClusterConfig) {
			RedisClusterConfig clusterConfig = (RedisClusterConfig) config;
			// 解析地址列表
			List<RedisURI> uriList = new ArrayList<>();
			String[] uriArray = clusterConfig.getNodes().split(",");
			String redisURLPrefix = !StrUtil.isEmpty(config.getPassword()) ? "redis://" + URLEncodeUtils.percentEncode(config.getPassword()) + "@" : "redis://";
			for (String uri : uriArray) {
				uriList.add(RedisURI.create(redisURLPrefix + uri));
			}
			// 创建客户端
			RedisClusterClient redisClient = RedisClusterClient.create(uriList);
			this.client = redisClient;
			// 创建连接对象
			StatefulRedisClusterConnection<String, String> redisConnection = redisClient.connect();
			this.connection = redisConnection;
			// 创建命令对象
			this.commands = redisConnection.sync();
			log.info("已连接到Redis Cluster分片集群");
		}
	}

}