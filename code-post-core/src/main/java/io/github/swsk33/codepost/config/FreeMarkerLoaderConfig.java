package io.github.swsk33.codepost.config;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import io.github.swsk33.codepost.model.config.FreeMarkerConfig;

/**
 * FreeMarker模板引擎的加载器配置
 */
public class FreeMarkerLoaderConfig {

	/**
	 * FreeMarker配置唯一单例
	 */
	private static volatile Configuration configuration;

	/**
	 * 获取FreeMarker配置对象
	 *
	 * @return 配置对象
	 */
	public static Configuration getConfiguration() {
		// 双检锁延迟初始化
		if (configuration == null) {
			synchronized (FreeMarkerLoaderConfig.class) {
				if (configuration == null) {
					try {
						configuration = new Configuration(Configuration.VERSION_2_3_32);
						configuration.setDefaultEncoding("UTF-8");
						// 根据配置设定模板加载器
						FreeMarkerConfig freeMarkerConfig = FreeMarkerConfig.getInstance();
						TemplateLoader loader;
						if (freeMarkerConfig.isClassPath()) {
							// 若为classpath时，处理相对路径
							String path = freeMarkerConfig.getTemplatePath();
							if (path.startsWith("./")) {
								path = path.substring(1);
							} else if (!path.startsWith("/")) {
								path = "/" + path;
							}
							loader = new ClassTemplateLoader(FreeMarkerLoaderConfig.class, path);
						} else {
							loader = new FileTemplateLoader(FileUtil.file(freeMarkerConfig.getTemplatePath()));
						}
						configuration.setTemplateLoader(loader);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return configuration;
	}

}