package io.github.swsk33.codepostcore.model.config;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Redis客户端配置抽象类（使用Redis存放验证码时）
 */
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
public abstract class RedisClientConfig {

	/**
	 * Redis（主节点）密码
	 */
	private final String password;

}