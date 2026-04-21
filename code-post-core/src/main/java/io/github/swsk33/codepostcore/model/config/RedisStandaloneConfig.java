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
	 * Redis地址，默认127.0.0.1
	 */
	@Builder.Default
	private final String host = "127.0.0.1";

	/**
	 * Redis端口，默认6379
	 */
	@Builder.Default
	private final int port = 6379;

}