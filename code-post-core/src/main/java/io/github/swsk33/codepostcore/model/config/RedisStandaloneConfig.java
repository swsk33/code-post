package io.github.swsk33.codepostcore.model.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * 用于连接 Redis 单机模式的配置对象
 */
@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RedisStandaloneConfig extends RedisClientConfig {

	/**
	 * 完整连接url，若该属性不为空，则会采用该url进行连接，并忽略其它配置
	 */
	private final String url;

	/**
	 * Redis地址，默认：127.0.0.1
	 */
	@Builder.Default
	private final String host = "127.0.0.1";

	/**
	 * Redis端口，默认：6379
	 */
	@Builder.Default
	private final int port = 6379;

	/**
	 * Redis数据库编号，默认：0
	 */
	@Builder.Default
	private final int database = 0;

}