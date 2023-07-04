package io.github.swsk33.codepostcore.util;

import freemarker.template.Template;
import io.github.swsk33.codepostcore.config.FreeMarkerLoaderConfig;
import io.github.swsk33.codepostcore.context.ServiceNameContext;
import io.github.swsk33.codepostcore.model.config.MailConfig;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * FreeMarker模板实用类
 */
public class TemplateUtils {

	/**
	 * 存放每个时间单位的名称
	 */
	private static final Map<TimeUnit, String> TIME_UNIT_NAME_MAP = new HashMap<>();

	// 初始化事件单位名称以及模板配置
	static {
		TIME_UNIT_NAME_MAP.put(TimeUnit.NANOSECONDS, "纳秒");
		TIME_UNIT_NAME_MAP.put(TimeUnit.MICROSECONDS, "微秒");
		TIME_UNIT_NAME_MAP.put(TimeUnit.MILLISECONDS, "毫秒");
		TIME_UNIT_NAME_MAP.put(TimeUnit.SECONDS, "秒");
		TIME_UNIT_NAME_MAP.put(TimeUnit.MINUTES, "分钟");
		TIME_UNIT_NAME_MAP.put(TimeUnit.HOURS, "小时");
		TIME_UNIT_NAME_MAP.put(TimeUnit.DAYS, "天");
		// 先获取一次配置完成初始化，防止模板不存在造成异常
		FreeMarkerLoaderConfig.getConfiguration();
	}

	/**
	 * 渲染模板
	 *
	 * @param variables        模板中的变量对应值的列表（没有变量可以传入null）
	 * @param templateFileName 模板文件名（不是路径）
	 * @return 渲染后的结果字符串，渲染失败则返回null
	 */
	public static String renderTemplate(Map<String, Object> variables, String templateFileName) {
		if (variables == null) {
			variables = new HashMap<>();
		}
		// 创建字符串写入器
		try (StringWriter stringWriter = new StringWriter()) {
			// 获取模板对象
			Template template = FreeMarkerLoaderConfig.getConfiguration().getTemplate(templateFileName);
			// 渲染模板，并通过写入器将结果写入流
			template.process(variables, stringWriter);
			// 获取结果并返回
			return stringWriter.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 渲染一个验证码邮件模板
	 *
	 * @param serviceNameKey 服务名的键
	 * @param code           验证码
	 * @param period         验证码有效时长
	 * @param timeUnit       验证码有效时长的时间单位
	 * @return 渲染后的模板字符串
	 */
	public static String renderVerifyMailTemplate(String serviceNameKey, String code, long period, TimeUnit timeUnit) {
		Map<String, Object> vars = new HashMap<>();
		MailConfig mailConfig = MailConfig.getInstance();
		vars.put("siteName", mailConfig.getSiteName());
		vars.put("serviceName", ServiceNameContext.getServiceName(serviceNameKey));
		vars.put("code", code);
		vars.put("time", period + TIME_UNIT_NAME_MAP.get(timeUnit));
		return renderTemplate(vars, mailConfig.getCodeTemplateName());
	}

}