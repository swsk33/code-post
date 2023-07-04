package io.github.swsk33.codepostcore.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.util.ClassPathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;

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
	 * 如果模板文件位于文件系统上，则该变量记录模板绝对路径，否则该变量不使用
	 */
	private static String templateFileSystemAbsolutePath;

	/**
	 * classpath配置路径前缀
	 */
	private static final String CLASS_PATH_PREFIX = "classpath:";

	/**
	 * 文件系统配置路径前缀
	 */
	private static final String FILE_PATH_PREFIX = "file:";

	/**
	 * 检查配置中的模板是否存在，若不存在则使用默认内置模板
	 */
	private static void checkTemplateExists() {
		// 获取配置中的路径
		MailConfig config = MailConfig.getInstance();
		// 未配置模板文件名视为不存在
		if (StrUtil.isEmpty(config.getTemplatePath()) || StrUtil.isEmpty(config.getCodeTemplateName())) {
			config.resetTemplatePath();
			return;
		}
		String templatePath = config.getTemplatePath();
		// 如果是classpath路径
		if (templatePath.startsWith(CLASS_PATH_PREFIX)) {
			templatePath = templatePath.replace(CLASS_PATH_PREFIX, "");
			if (!ClassPathUtils.classpathFileExists(templatePath + "/" + config.getCodeTemplateName())) {
				config.resetTemplatePath();
			}
		} else if (templatePath.startsWith(FILE_PATH_PREFIX)) {            // 如果是文件系统路径
			templatePath = templatePath.replace(FILE_PATH_PREFIX, "");
			// 计算模板绝对路径
			if (!Paths.get(templatePath).isAbsolute()) {
				templatePath = Paths.get("").toAbsolutePath() + File.separator + templatePath;
			}
			templateFileSystemAbsolutePath = templatePath;
			if (!FileUtil.exist(templateFileSystemAbsolutePath + File.separator + config.getCodeTemplateName())) {
				config.resetTemplatePath();
			}
		}
	}

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
						// 检查模板文件是否存在
						checkTemplateExists();
						// 根据配置设定模板加载器
						MailConfig config = MailConfig.getInstance();
						TemplateLoader loader;
						if (config.getTemplatePath().startsWith(CLASS_PATH_PREFIX)) {
							String templateClassPath = config.getTemplatePath().replace(CLASS_PATH_PREFIX, "");
							loader = new ClassTemplateLoader(FreeMarkerLoaderConfig.class, ClassPathUtils.toAbsolutePath(templateClassPath));
							log.info("模板文件目录位于classpath:{}", templateClassPath);
						} else if (config.getTemplatePath().startsWith(FILE_PATH_PREFIX)) {
							loader = new FileTemplateLoader(FileUtil.file(templateFileSystemAbsolutePath));
							log.info("模板文件目录位于file:{}", templateFileSystemAbsolutePath);
						} else {
							throw new RuntimeException("模板路径格式错误！必须以classpath:或者file:开头！");
						}
						// 设定FreeMarker配置
						configuration = new Configuration(Configuration.VERSION_2_3_32);
						configuration.setDefaultEncoding("UTF-8");
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