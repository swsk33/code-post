package io.github.swsk33.codepostspringbootstarter.autoconfigure;

import io.github.swsk33.codepostspringbootstarter.property.CoreProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码邮件服务自动配置类
 */
@Configuration
@EnableConfigurationProperties(CoreProperties.class)
public class CodeMailServiceAutoConfiguration {
}