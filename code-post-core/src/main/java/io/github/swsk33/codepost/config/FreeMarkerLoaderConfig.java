package io.github.swsk33.codepost.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import io.github.swsk33.codepost.model.config.MailConfig;
import io.github.swsk33.codepost.util.ClassPathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

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
	 * classpath配置路径前缀
	 */
	private static final String CLASS_PATH_PREFIX = "classpath:";

	/**
	 * 文件系统配置路径前缀
	 */
	private static final String FILE_PATH_PREFIX = "file:";

	/**
	 * 检查配置中的验证码邮件模板文件是否存在
	 *
	 * @return 是否存在
	 */
	private static boolean codeTemplateExists() {
		// 获取配置中的路径
		MailConfig config = MailConfig.getInstance();
		// 未配置模板文件名视为不存在
		if (StrUtil.isEmpty(config.getTemplatePath()) || StrUtil.isEmpty(config.getCodeTemplateName())) {
			return false;
		}
		String templatePath = config.getTemplatePath();
		if (templatePath.startsWith(CLASS_PATH_PREFIX)) {
			templatePath = templatePath.replace(CLASS_PATH_PREFIX, "");
			return ClassPathUtils.classpathFileExists(templatePath + "/" + config.getCodeTemplateName());
		}
		if (templatePath.startsWith(FILE_PATH_PREFIX)) {
			templatePath = templatePath.replace(FILE_PATH_PREFIX, "");
			return FileUtil.exist(templatePath + File.separator + config.getCodeTemplateName());
		}
		// 错误前缀视为不存在
		return false;
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
						configuration = new Configuration(Configuration.VERSION_2_3_32);
						configuration.setDefaultEncoding("UTF-8");
						// 根据配置设定模板加载器
						MailConfig config = MailConfig.getInstance();
						// 如果验证码模板文件不存在，则恢复为默认配置
						if (!codeTemplateExists()) {
							config.setTemplatePath("classpath:/code-post-default");
							config.setCodeTemplateName(config.isEnableHTML() ? "verify-code-template.ftlh" : "verify-code-template.txt");
							log.warn("检测到未配置或者配置的验证码模板文件不存在！恢复为默认配置！");
						}
						TemplateLoader loader;
						String templatePath;
						if (config.getTemplatePath().startsWith(CLASS_PATH_PREFIX)) {
							templatePath = config.getTemplatePath().replace(CLASS_PATH_PREFIX, "");
							loader = new ClassTemplateLoader(FreeMarkerLoaderConfig.class, ClassPathUtils.toAbsolutePath(templatePath));
						} else if (config.getTemplatePath().startsWith(FILE_PATH_PREFIX)) {
							templatePath = config.getTemplatePath().replace(FILE_PATH_PREFIX, "");
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