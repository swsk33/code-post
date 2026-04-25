package io.github.swsk33.codepostcore.client;

import cn.hutool.core.util.StrUtil;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.model.config.RedisClusterConfig;
import io.github.swsk33.codepostcore.model.config.RedisSentinelConfig;
import io.github.swsk33.codepostcore.model.config.RedisStandaloneConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lettuce Redis 客户端
 */
@Slf4j
public class LettuceClient {

	/**
	 * Redis 命令对象封装
	 */
	@Getter
	private final AbstractRedisCommandsWrapper commands;

	/**
	 * 通过 Redis 配置构造客户端对象
	 *
	 * @param redisClientConfig Redis 配置对象
	 */
	public LettuceClient(RedisClientConfig redisClientConfig) {
		RedisClientConfig redisConfig = Objects.requireNonNull(redisClientConfig, "redisClientConfig 不能为空！");
		// 根据配置对象不同类型判断初始化方式及其对象
		// 单节点 Redis 配置
		if (redisConfig instanceof RedisStandaloneConfig) {
			// 解析配置
			RedisStandaloneConfig config = (RedisStandaloneConfig) redisConfig;
			// 构建 RedisURI 对象
			RedisURI redisUri;
			// 优先采用 url 配置
			if (!StrUtil.isEmpty(config.getUrl())) {
				redisUri = RedisURI.create(config.getUrl());
			} else {
				// 否则，逐一构建
				RedisURI.Builder uriBuilder = RedisURI.Builder
						.redis(config.getHost(), config.getPort())
						.withDatabase(config.getDatabase());
				if (!StrUtil.isEmpty(config.getPassword())) {
					uriBuilder.withPassword(config.getPassword());
				}
				redisUri = uriBuilder.build();
			}
			// 创建客户端
			StatefulRedisConnection<String, String> redisConnection = RedisClient.create(redisUri).connect();
			this.commands = new SingleRedisCommandsWrapper(redisConnection.sync());
			return;
		}
		// Redis 哨兵集群
		if (redisConfig instanceof RedisSentinelConfig) {
			// 解析配置
			RedisSentinelConfig config = (RedisSentinelConfig) redisConfig;
			// 构建 RedisURI 对象
			RedisURI redisUri;
			// 优先使用 url
			if (!StrUtil.isEmpty(config.getUrl())) {
				redisUri = RedisURI.create(config.getUrl());
			} else {
				// 否则，逐一构建
				RedisURI.Builder builder = RedisURI.builder();
				// 构建哨兵地址
				for (String node : config.getNodes()) {
					String[] split = node.split(":");
					builder.withSentinel(split[0], Integer.parseInt(split[1]));
				}
				// 构建密码
				if (!StrUtil.isEmpty(config.getPassword())) {
					builder.withPassword(config.getPassword());
				}
				// 构建其它属性
				redisUri = builder.withDatabase(config.getDatabase())
						.withSentinelMasterId(config.getMasterName())
						.build();
			}
			// 创建客户端
			StatefulRedisConnection<String, String> redisConnection = RedisClient.create(redisUri).connect();
			this.commands = new SingleRedisCommandsWrapper(redisConnection.sync());
			return;
		}
		// Redis Cluster 集群
		if (redisConfig instanceof RedisClusterConfig) {
			// 解析配置
			RedisClusterConfig config = (RedisClusterConfig) redisConfig;
			// 解析节点，创建集群配置
			List<RedisURI> clusterNodeUris = new ArrayList<>();
			for (String node : config.getNodes()) {
				String[] split = node.split(":");
				RedisURI.Builder builder = RedisURI.Builder.redis(split[0], Integer.parseInt(split[1]));
				if (!StrUtil.isEmpty(config.getPassword())) {
					builder.withPassword(config.getPassword());
				}
				clusterNodeUris.add(builder.build());
			}
			// 创建客户端
			StatefulRedisClusterConnection<String, String> redisConnection = RedisClusterClient.create(clusterNodeUris).connect();
			this.commands = new ClusterRedisCommandsWrapper(redisConnection.sync());
			return;
		}
		throw new IllegalArgumentException("不支持的 Redis 配置类型：" + redisConfig.getClass().getName());
	}

	/**
	 * 面对不同类型RedisCommands的统一抽象接口，包括常用操作封装
	 */
	public interface AbstractRedisCommandsWrapper {

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

	/**
	 * 适用于单节点或 Sentinel 集群的 Redis 连接
	 */
	private static class SingleRedisCommandsWrapper implements AbstractRedisCommandsWrapper {

		/**
		 * Redis 命令对象
		 */
		private final RedisCommands<String, String> commands;

		/**
		 * 构造函数，通过命令对象创建
		 *
		 * @param commands Redis 命令对象
		 */
		private SingleRedisCommandsWrapper(RedisCommands<String, String> commands) {
			this.commands = commands;
			log.info("已初始化适用于单节点或 Sentinel 集群的 Redis 命令对象");
		}

		@Override
		public void set(String key, String value) {
			commands.set(key, value);
		}

		@Override
		public String get(String key) {
			return commands.get(key);
		}

		@Override
		public void del(String key) {
			commands.del(key);
		}

		@Override
		public void expire(String key, long seconds) {
			commands.expire(key, seconds);
		}

	}

	/**
	 * 适用于单节点或 Cluster 集群的 Redis 连接
	 */
	private static class ClusterRedisCommandsWrapper implements AbstractRedisCommandsWrapper {

		/**
		 * Redis 命令对象
		 */
		private final RedisClusterCommands<String, String> commands;

		/**
		 * 构造函数，通过命令对象创建
		 *
		 * @param commands Redis 命令对象
		 */
		private ClusterRedisCommandsWrapper(RedisClusterCommands<String, String> commands) {
			this.commands = commands;
			log.info("已初始化适用于 Cluster 集群的 Redis 命令对象");
		}

		@Override
		public void set(String key, String value) {
			commands.set(key, value);
		}

		@Override
		public String get(String key) {
			return commands.get(key);
		}

		@Override
		public void del(String key) {
			commands.del(key);
		}

		@Override
		public void expire(String key, long seconds) {
			commands.expire(key, seconds);
		}

	}

}