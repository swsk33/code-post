package io.github.swsk33.springannotationtest;

import io.github.swsk33.codepostcore.service.EmailNotifyService;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class Main {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		EmailVerifyCodeService verifyCodeService = context.getBean(EmailVerifyCodeService.class);
		EmailNotifyService notifyService = context.getBean(EmailNotifyService.class);
		System.out.println(verifyCodeService != null);
		System.out.println(notifyService != null);
	}

}