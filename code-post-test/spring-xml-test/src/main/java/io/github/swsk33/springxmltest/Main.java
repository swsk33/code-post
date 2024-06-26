package io.github.swsk33.springxmltest;

import io.github.swsk33.codepostcore.service.EmailNotifyService;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("/mail-service.xml", "/redis-cluster-config.xml");
		EmailVerifyCodeService verifyCodeService = context.getBean(EmailVerifyCodeService.class);
		EmailNotifyService notifyService = context.getBean(EmailNotifyService.class);
		System.out.println(verifyCodeService != null);
		System.out.println(notifyService != null);
	}

}