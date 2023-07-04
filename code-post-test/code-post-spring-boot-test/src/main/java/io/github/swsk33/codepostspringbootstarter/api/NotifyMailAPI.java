package io.github.swsk33.codepostspringbootstarter.api;

import io.github.swsk33.codepost.service.EmailNotifyService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邮件通知群发接口
 */
@Slf4j
@RestController
@RequestMapping("/api/notify")
public class NotifyMailAPI {

	/**
	 * 自动装配邮件通知服务
	 */
	@Autowired
	private EmailNotifyService emailNotifyService;

	/**
	 * 用于渲染模板的变量列表
	 */
	private Map<String, Object> dataModels;

	/**
	 * 初始化模板变量列表
	 */
	@PostConstruct
	private void initDataModels() {
		dataModels = new HashMap<>();
		// 模板变量名：anthologyName，变量值：Redis深度历险
		dataModels.put("anthologyName", "Redis深度历险");
		List<String> articles = new ArrayList<>();
		articles.add("1.Redis的前世今生");
		articles.add("2.Redis的安装和配置");
		articles.add("3.Redis命令");
		// 模板变量名：newArticles，变量值：列表articles
		dataModels.put("newArticles", articles);
		log.info("已完成测试模板变量初始化！");
	}

	/**
	 * 发送单个通知邮件
	 *
	 * @param mail 接收通知的邮箱
	 * @return 结果
	 */
	@GetMapping("/single/{mail}")
	public String sendNotify(@PathVariable String mail) {
		emailNotifyService.sendTemplateNotify("专栏更新通知", "notify-template.txt", dataModels, mail);
		return "已发送通知！";
	}

	/**
	 * 批量发送邮件通知<br>
	 * 这里仅为示例，所以使用请求传递接收通知的邮箱列表，实际情况下建议从数据库获取用户信息并取得邮箱地址后，调用服务
	 *
	 * @param emails 接收通知的邮箱列表
	 * @return 结果
	 */
	@PostMapping("/batch")
	public String sendBatchNotify(@RequestBody String[] emails) {
		emailNotifyService.sendTemplateNotify("专栏更新通知", "notify-template.txt", dataModels, emails);
		return "已发送通知！";
	}

}