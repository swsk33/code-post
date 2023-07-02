package io.github.swsk33.codepost.model.config;

import lombok.Data;

/**
 * FreeMarker模板引擎配置
 */
@Data
public class FreeMarkerConfig {

	/**
	 * 唯一单例
	 */
	private static volatile FreeMarkerConfig INSTANCE;

	/**
	 * 获取FreeMarker模板引擎配置唯一单例
	 *
	 * @return 唯一单例
	 */
	public static FreeMarkerConfig getInstance() {
		// 双检锁延迟初始化
		if (INSTANCE == null) {
			synchronized (FreeMarkerConfig.class) {
				if (INSTANCE == null) {
					INSTANCE = new FreeMarkerConfig();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * 私有化构造器
	 */
	private FreeMarkerConfig() {

	}

	/**
	 * 模板文件是否位于classpath
	 */
	private boolean isClassPath = false;

	/**
	 * 模板文件所在目录
	 */
	private String templatePath;

}