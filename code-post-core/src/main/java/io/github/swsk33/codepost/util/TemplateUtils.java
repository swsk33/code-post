package io.github.swsk33.codepost.util;

import freemarker.template.Template;
import io.github.swsk33.codepost.config.FreeMarkerLoaderConfig;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
		// 存放渲染结果的输出流和流写入器
		try (OutputStream renderResultStream = new ByteArrayOutputStream(); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(renderResultStream)) {
			// 获取模板对象
			Template template = FreeMarkerLoaderConfig.getConfiguration().getTemplate(templateFileName);
			// 渲染模板，并通过写入器将结果写入流
			template.process(variables, outputStreamWriter);
			// 获取流中的对象
			result = renderResultStream.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}