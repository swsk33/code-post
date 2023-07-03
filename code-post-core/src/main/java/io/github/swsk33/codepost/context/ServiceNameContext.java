package io.github.swsk33.codepost.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有验证码相关服务名管理上下文<br>
 * 建议使用枚举或者字符串常量作为服务名的键
 */
public class ServiceNameContext {

	/**
	 * 存放所有服务名的哈希表
	 */
	private static final Map<String, String> SERVICE_NAME_MAP = new HashMap<>();

	/**
	 * 默认服务名(default)的键
	 */
	public static final String DEFAULT_SERVICE_KEY = "DEFAULT";

	// 初始化默认服务
	static {
		SERVICE_NAME_MAP.put(DEFAULT_SERVICE_KEY, "default");
	}

	/**
	 * 注册服务名
	 *
	 * @param serviceNameKey 服务名对应的键
	 * @param serviceName    服务名
	 */
	public static void register(String serviceNameKey, String serviceName) {
		SERVICE_NAME_MAP.put(serviceNameKey, serviceName);
	}

	/**
	 * 注册服务名
	 *
	 * @param serviceNameKey 服务名对应的键（枚举形式）
	 * @param serviceName    服务名
	 */
	public static void register(Enum<?> serviceNameKey, String serviceName) {
		register(serviceNameKey.toString(), serviceName);
	}

	/**
	 * 获取一个服务名
	 *
	 * @param serviceNameKey 服务名的键
	 * @return 服务名，不存在返回null
	 */
	public static String getServiceName(String serviceNameKey) {
		return SERVICE_NAME_MAP.get(serviceNameKey);
	}

	/**
	 * 获取一个服务名
	 *
	 * @param serviceNameKey 服务名的键（枚举形式）
	 * @return 服务名，不存在返回null
	 */
	public static String getServiceName(Enum<?> serviceNameKey) {
		return getServiceName(serviceNameKey.toString());
	}

	/**
	 * 删除一个服务名
	 *
	 * @param serviceNameKey 要删除的服务名的键
	 */
	public static void unregister(String serviceNameKey) {
		SERVICE_NAME_MAP.remove(serviceNameKey);
	}

	/**
	 * 删除一个服务名
	 *
	 * @param serviceNameKey 要删除的服务名的键（枚举形式）
	 */
	public static void unregister(Enum<?> serviceNameKey) {
		unregister(serviceNameKey.toString());
	}

}