package io.github.swsk33.codepost.util;

import freemarker.template.Template;
import io.github.swsk33.codepost.config.FreeMarkerLoaderConfig;
import io.github.swsk33.codepost.context.ServiceNameContext;
import io.github.swsk33.codepost.model.config.MailConfig;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * FreeMarker模板实用类
 */
public class TemplateUtils {

	/**
	 * 渲染模板
	 *
	 * @param variables        模板中的变量对应值的列表
	 * @param templateFileName 模板文件名（不是路径）
	 * @return 渲染后的结果字符串，渲染失败则返回null
	 */
	public static String renderTemplate(Map<String, Object> variables, String templateFileName) {
		String result = null;
		// 创建字符串写入器
		try (StringWriter stringWriter = new StringWriter()) {
			// 获取模板对象
			Template template = FreeMarkerLoaderConfig.getConfiguration().getTemplate(templateFileName);
			// 渲染模板，并通过写入器将结果写入流
			template.process(variables, stringWriter);
			// 获取流中的对象
			result = stringWriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 渲染一个验证码邮件模板
	 *
	 * @param siteName       网站名
	 * @param serviceNameKey 服务名的键
	 * @param code           验证码
	 * @return 渲染后的模板字符串
	 */
	public static String renderVerifyMailTemplate(String siteName, String serviceNameKey, String code) {
		Map<String, Object> vars = new HashMap<>();
		vars.put("site-name", siteName);
		vars.put("service-name", ServiceNameContext.getServiceName(serviceNameKey));
		vars.put("code", code);
		return renderTemplate(vars, MailConfig.getInstance().getTemplatePath() + File.separator + MailConfig.getInstance().getVerifyCodeTemplateName());
	}

}