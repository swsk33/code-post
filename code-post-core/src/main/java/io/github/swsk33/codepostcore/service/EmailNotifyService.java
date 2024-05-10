package io.github.swsk33.codepostcore.service;

import java.util.Map;

/**
 * 邮件通知服务
 */
public interface EmailNotifyService {

	/**
	 * 发送单个邮件通知
	 *
	 * @param title    通知邮件标题
	 * @param template 通知邮件的模板文件名（模板文件所在文件夹以配置的为准）
	 * @param models   模板中的变量和值列表（模板中没有变量可以传入null）
	 * @param receiver 接收者
	 */
	void sendTemplateNotify(String title, String template, Map<String, Object> models, String receiver);

	/**
	 * 批量发送邮件通知
	 *
	 * @param title     通知邮件标题
	 * @param template  通知邮件的模板文件名（模板文件所在文件夹以配置的为准）
	 * @param models    模板中的变量和值列表（模板中没有变量可以传入null）
	 * @param receivers 接收者列表
	 */
	void sendTemplateNotify(String title, String template, Map<String, Object> models, String[] receivers);

	/**
	 * 异步发送单个邮件通知
	 *
	 * @param title    通知邮件标题
	 * @param template 通知邮件的模板文件名（模板文件所在文件夹以配置的为准）
	 * @param models   模板中的变量和值列表（模板中没有变量可以传入null）
	 * @param receiver 接收者
	 */
	void sendTemplateNotifyAsync(String title, String template, Map<String, Object> models, String receiver);

	/**
	 * 异步批量发送邮件通知
	 *
	 * @param title     通知邮件标题
	 * @param template  通知邮件的模板文件名（模板文件所在文件夹以配置的为准）
	 * @param models    模板中的变量和值列表（模板中没有变量可以传入null）
	 * @param receivers 接收者列表
	 */
	void sendTemplateNotifyAsync(String title, String template, Map<String, Object> models, String[] receivers);

}