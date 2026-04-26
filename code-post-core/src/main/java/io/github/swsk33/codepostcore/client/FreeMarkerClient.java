package io.github.swsk33.codepostcore.client;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.swsk33.codepostcore.context.ServiceNameContext;
import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostcore.util.ClassPathUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * FreeMarker 模板客户端
 */
@Slf4j
public class FreeMarkerClient {

	/**
	 * ClassPath 路径前缀
	 */
	private static final String CLASS_PATH_PREFIX = "classpath:";

	/**
	 * 本地文件路径前缀
	 */
	private static final String FILE_PATH_PREFIX = "file:";

	/**
	 * 默认模板路径
	 */
	private static final String DEFAULT_TEMPLATE_PATH = "classpath:/code-post-default";

	/**
	 * 默认网页模板文件名
	 */
	private static final String DEFAULT_HTML_TEMPLATE = "verify-code-template.ftlh";

	/**
	 * 默认文本模板文件名
	 */
	private static final String DEFAULT_TEXT_TEMPLATE = "verify-code-template.txt";

	/**
	 * 时间单位列表
	 */
	private static final Map<TimeUnit, String> TIME_UNIT_NAME_MAP = new HashMap<>();

	static {
		TIME_UNIT_NAME_MAP.put(TimeUnit.NANOSECONDS, "纳秒");
		TIME_UNIT_NAME_MAP.put(TimeUnit.MICROSECONDS, "微秒");
		TIME_UNIT_NAME_MAP.put(TimeUnit.MILLISECONDS, "毫秒");
		TIME_UNIT_NAME_MAP.put(TimeUnit.SECONDS, "秒");
		TIME_UNIT_NAME_MAP.put(TimeUnit.MINUTES, "分钟");
		TIME_UNIT_NAME_MAP.put(TimeUnit.HOURS, "小时");
		TIME_UNIT_NAME_MAP.put(TimeUnit.DAYS, "天");
	}

	/**
	 * FreeMarker 的配置对象
	 */
	@Getter
	private final Configuration configuration;

	/**
	 * 核心邮件配置对象
	 */
	private final MailConfig mailConfig;

	/**
	 * 验证码模板名
	 */
	private final String verifyTemplateName;

	/**
	 * 构造函数，使用邮件配置对象初始化
	 *
	 * @param mailConfig 邮件配置对象
	 */
	public FreeMarkerClient(MailConfig mailConfig) {
		this.mailConfig = Objects.requireNonNull(mailConfig, "mailConfig 不能为空！");
		this.configuration = createConfiguration(this.mailConfig);
		this.verifyTemplateName = resolveVerifyTemplateName();
	}

	/**
	 * 解析模板路径
	 *
	 * @param mailConfig 传入邮件配置对象
	 * @return 解析得到的模板文件路径，为file:xxx或classpath:xxx形式
	 */
	private String resolveTemplatePath(MailConfig mailConfig) {
		String templatePath = mailConfig.getTemplatePath();
		// 若没有配置模板路径，返回默认路径
		if (StrUtil.isEmpty(templatePath)) {
			return DEFAULT_TEMPLATE_PATH;
		}
		// 如果是 classpath 路径
		if (templatePath.startsWith(CLASS_PATH_PREFIX)) {
			String relativePath = templatePath.replace(CLASS_PATH_PREFIX, "");
			// 不存在返回默认值
			if (!ClassPathUtils.classpathFileExists(relativePath)) {
				log.warn("配置的 classpath 模板路径不存在！回退至默认值：{}", DEFAULT_TEMPLATE_PATH);
				return DEFAULT_TEMPLATE_PATH;
			}
			return CLASS_PATH_PREFIX + relativePath;
		}
		// 如果是本地文件系统路径
		if (templatePath.startsWith(FILE_PATH_PREFIX)) {
			String filePath = templatePath.replace(FILE_PATH_PREFIX, "");
			// 转换为绝对路径
			if (!Paths.get(filePath).isAbsolute()) {
				filePath = Paths.get("").toAbsolutePath() + File.separator + filePath;
			}
			// 不存在返回默认值
			if (!FileUtil.exist(filePath)) {
				log.warn("配置的本地文件模板路径不存在！回退至默认值：{}", DEFAULT_TEMPLATE_PATH);
				return DEFAULT_TEMPLATE_PATH;
			}
			return FILE_PATH_PREFIX + filePath;
		}
		throw new RuntimeException("模板路径格式错误！必须以classpath:或者file:开头！");
	}

	/**
	 * 解析验证码模板名称
	 *
	 * @return 验证码模板名称，不存在返回默认值
	 */
	private String resolveVerifyTemplateName() {
		String defaultTemplateName = mailConfig.isEnableHtml() ? DEFAULT_HTML_TEMPLATE : DEFAULT_TEXT_TEMPLATE;
		if (StrUtil.isEmpty(mailConfig.getCodeTemplateName())) {
			return defaultTemplateName;
		}
		try {
			configuration.getTemplate(mailConfig.getCodeTemplateName());
			return mailConfig.getCodeTemplateName();
		} catch (Exception e) {
			log.warn("配置的验证码模板不存在！回退至默认值：{}", defaultTemplateName);
			return defaultTemplateName;
		}
	}

	/**
	 * 创建 FreeMarker 配置
	 *
	 * @param mailConfig 邮件配置对象
	 * @return FreeMarker 配置对象
	 */
	private Configuration createConfiguration(MailConfig mailConfig) {
		try {
			// 解析路径
			String resolvedTemplatePath = resolveTemplatePath(mailConfig);
			// 创建模板加载器
			TemplateLoader loader;
			// 根据不同类型路径解析模板文件
			if (resolvedTemplatePath.startsWith(CLASS_PATH_PREFIX)) {
				String templateClassPath = resolvedTemplatePath.replace(CLASS_PATH_PREFIX, "");
				loader = new ClassTemplateLoader(FreeMarkerClient.class, ClassPathUtils.toAbsolutePath(templateClassPath));
				log.info("模板文件目录位于classpath:{}", templateClassPath);
			} else {
				String templateFilePath = resolvedTemplatePath.replace(FILE_PATH_PREFIX, "");
				loader = new FileTemplateLoader(FileUtil.file(templateFilePath));
				log.info("模板文件目录位于file:{}", templateFilePath);
			}
			Configuration resolved = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			resolved.setDefaultEncoding("UTF-8");
			resolved.setTemplateLoader(loader);
			log.info("模板引擎已完成初始化！");
			return resolved;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 渲染通用模板
	 *
	 * @param variables        变量列表
	 * @param templateFileName 模板名
	 * @return 渲染后的内容
	 */
	public String renderTemplate(Map<String, Object> variables, String templateFileName) {
		Map<String, Object> resolvedVariables = variables == null ? new HashMap<>() : variables;
		try (StringWriter stringWriter = new StringWriter()) {
			Template template = configuration.getTemplate(templateFileName);
			template.process(resolvedVariables, stringWriter);
			return stringWriter.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 渲染验证码模板
	 *
	 * @param serviceNameKey 服务名
	 * @param code           验证码
	 * @param period         过期时间
	 * @param timeUnit       时间单位
	 * @return 渲染后内容
	 */
	public String renderVerifyMailTemplate(String serviceNameKey, String code, long period, TimeUnit timeUnit) {
		Map<String, Object> vars = new HashMap<>();
		vars.put("siteName", this.mailConfig.getSiteName());
		vars.put("serviceName", ServiceNameContext.getServiceName(serviceNameKey));
		vars.put("code", code);
		vars.put("time", period + TIME_UNIT_NAME_MAP.get(timeUnit));
		return renderTemplate(vars, verifyTemplateName);
	}

}