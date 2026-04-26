package io.github.swsk33.codepostspringboot4test;

import io.github.swsk33.codeposttestcommon.CodePostCommonTestApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CodePostCommonTestApplication.class)
public class CodePostSpringBootTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodePostSpringBootTestApplication.class, args);
	}

}