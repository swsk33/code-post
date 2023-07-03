package io.github.swsk33.codepost.config;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import io.github.swsk33.codepost.model.config.MailConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeMarker模板引擎的加载器配置
 */
@Slf4j
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
						MailConfig config = MailConfig.getInstance();
						TemplateLoader loader;
						final String classPathPrefix = "classpath:";
						final String filePathPrefix = "file:";
						String templatePath;
						if (config.getTemplatePath().startsWith(classPathPrefix)) {
							// 若为classpath时，处理相对路径
							templatePath = config.getTemplatePath().replace(classPathPrefix, "");
							if (templatePath.startsWith("./")) {
								templatePath = templatePath.substring(1);
							} else if (!templatePath.startsWith("/")) {
								templatePath = "/" + templatePath;
							}
							loader = new ClassTemplateLoader(FreeMarkerLoaderConfig.class, templatePath);
						} else if (config.getTemplatePath().startsWith(filePathPrefix)) {
							templatePath = config.getTemplatePath().replace(filePathPrefix, "");
							loader = new FileTemplateLoader(FileUtil.file(templatePath));
						} else {
							throw new RuntimeException("模板路径格式错误！必须以classpath:或者file:开头！");
						}
						configuration.setTemplateLoader(loader);
						log.info("模板引擎已完成初始化！");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return configuration;
	}

}