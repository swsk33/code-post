package io.github.swsk33.codepostspringbootstarter.autoconfigure.config;

import io.github.swsk33.codepost.model.config.MailConfig;
import io.github.swsk33.codepost.param.CodeStorageMethod;
import io.github.swsk33.codepostspringbootstarter.autoconfigure.MailServiceAutoConfiguration;
import io.github.swsk33.codepostspringbootstarter.property.CoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件核心配置类的初始化
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CoreProperties.class)
@AutoConfigureBefore(MailServiceAutoConfiguration.class)
public class MailConfigAutoConfiguration {

	/**
	 * 自动配置邮件核心配置类
	 */
	@Bean
	public MailConfig mailConfig(CoreProperties coreProperties) {
		// 获取配置单例并设定
		MailConfig mailConfig = MailConfig.getInstance();
		coreProperties.setMailConfig(mailConfig);
		if (mailConfig.getCodeStorage().equals(CodeStorageMethod.LOCAL_THREAD_POOL)) {
			log.info("使用基于本地线程池的验证码管理方案");
		}
		return mailConfig;
	}

}