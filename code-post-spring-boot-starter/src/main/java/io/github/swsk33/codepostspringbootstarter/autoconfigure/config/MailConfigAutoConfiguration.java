package io.github.swsk33.codepostspringbootstarter.autoconfigure.config;

import io.github.swsk33.codepostcore.model.config.MailConfig;
import io.github.swsk33.codepostspringbootstarter.autoconfigure.MailClientAutoConfiguration;
import io.github.swsk33.codepostspringbootstarter.property.CoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 邮件核心配置类的初始化
 */
@Slf4j
@AutoConfiguration(before = MailClientAutoConfiguration.class)
@EnableConfigurationProperties(CoreProperties.class)
public class MailConfigAutoConfiguration {

	/**
	 * 自动配置邮件核心配置类
	 */
	@Bean
	public MailConfig mailConfig(CoreProperties coreProperties) {
		return coreProperties.toMailConfig();
	}

}