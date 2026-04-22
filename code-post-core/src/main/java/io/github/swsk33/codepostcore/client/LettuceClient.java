package io.github.swsk33.codepostcore.client;

import cn.hutool.core.util.StrUtil;
import io.github.swsk33.codepostcore.model.config.RedisClientConfig;
import io.github.swsk33.codepostcore.model.config.RedisClusterConfig;
import io.github.swsk33.codepostcore.model.config.RedisSentinelConfig;
import io.github.swsk33.codepostcore.model.config.RedisStandaloneConfig;
import io.github.swsk33.codepostcore.util.URLEncodeUtils;
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
		RedisClientConfig config = Objects.requireNonNull(redisClientConfig, "redisClientConfig 不能为空！");
		// 根据配置对象不同类型判断初始化方式及其对象
		// 单节点 Redis 配置
		if (config instanceof RedisStandaloneConfig) {
			// 解析配置
			RedisStandaloneConfig standaloneConfig = (RedisStandaloneConfig) config;
			StringBuilder redisUrl = new StringBuilder("redis://");
			if (!StrUtil.isEmpty(config.getPassword())) {
				redisUrl.append(URLEncodeUtils.percentEncode(config.getPassword())).append("@");
			}
			redisUrl.append(standaloneConfig.getHost()).append(":").append(standaloneConfig.getPort());
			// 创建客户端
			RedisClient redisClient = RedisClient.create(redisUrl.toString());
			StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
			this.commands = new SingleRedisCommandsWrapper(redisConnection.sync());
			return;
		}
		// Redis 哨兵集群
		if (config instanceof RedisSentinelConfig) {
			// 解析配置
			RedisSentinelConfig sentinelConfig = (RedisSentinelConfig) config;
			StringBuilder redisUrl = new StringBuilder("redis-sentinel://");
			if (!StrUtil.isEmpty(sentinelConfig.getPassword())) {
				redisUrl.append(URLEncodeUtils.percentEncode(sentinelConfig.getPassword())).append("@");
			}
			redisUrl.append(sentinelConfig.getNodes()).append("?sentinelMasterId=").append(sentinelConfig.getMasterName());
			// 创建客户端
			RedisURI uri = RedisURI.create(redisUrl.toString());
			RedisClient redisClient = RedisClient.create(uri);
			StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
			this.commands = new SingleRedisCommandsWrapper(redisConnection.sync());
			return;
		}
		// Redis Cluster 集群
		if (config instanceof RedisClusterConfig) {
			// 解析配置
			RedisClusterConfig clusterConfig = (RedisClusterConfig) config;
			List<RedisURI> uriList = new ArrayList<>();
			String[] uriArray = clusterConfig.getNodes().split(",");
			String redisUrlPrefix = !StrUtil.isEmpty(config.getPassword()) ? "redis://" + URLEncodeUtils.percentEncode(config.getPassword()) + "@" : "redis://";
			for (String uri : uriArray) {
				uriList.add(RedisURI.create(redisUrlPrefix + uri));
			}
			// 创建客户端
			RedisClusterClient redisClient = RedisClusterClient.create(uriList);
			StatefulRedisClusterConnection<String, String> redisConnection = redisClient.connect();
			this.commands = new ClusterRedisCommandsWrapper(redisConnection.sync());
			return;
		}
		throw new IllegalArgumentException("不支持的 Redis 配置类型：" + config.getClass().getName());
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