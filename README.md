# CodePost-小型邮件验证码框架

<p align="center">
	<a target="_blank" href="https://central.sonatype.com/search?smo=true&q=io.github.swsk33.code-post">
		<img src="https://img.shields.io/maven-central/v/io.github.swsk33/code-post-parent
" />
	</a>
	<a target="_blank" href="https://www.gnu.org/licenses/old-licenses/gpl-2.0.html">
		<img alt="GitHub" src="https://img.shields.io/github/license/swsk33/code-post">
	</a>
	<a target="_blank" href="https://www.azul.com/downloads/#downloads-table-zulu">
		<img alt="Static Badge" src="https://img.shields.io/badge/1.8%2B-blue?label=JDK">
	</a>
</p>


## 1，介绍
CodePost是一款简单的Java邮件验证码框架，它对邮件验证码的生成、发送和校验等功能做了封装，使得开发者能够更加简单地完成邮件验证码功能。

### (1) 项目背景

一直以来，邮件验证码发送是许多Web后端系统的功能之一，虽然Spring Boot也有对应的邮件Starter，不过邮件验证码的生成和管理、邮件发送等逻辑仍然需要去自己完成。

该项目将邮件验证码相关业务以及邮件发送的业务逻辑抽离了出来，并封装了一些简单的邮件相关业务接口，配置完成后，只需一行代码，就可以完成邮件验证码发送和校验操作！

### (2) 功能介绍

该框架主要是简化Spring或者Spring Boot中邮件验证码、邮件通知等业务逻辑，其主要功能如下：

- 生成并发送邮件验证码
- 校验邮件验证码
- 验证码服务隔离
- 支持纯文本和HTML网页邮件
- 基于模板的邮件内容

其中，邮件验证码的储存以及过期管理支持下列两种方式：

- 本地线程池
- Redis（需要额外引入`lettuce-core`或者`spring-boot-starter-data-redis`依赖）

## 2，快速开始

### (1) 环境要求

无论是普通Java项目，还是Spring，以及Spring Boot都可以使用该框架，需要满足下列基本要求：

- JDK 1.8及其以上版本
- Spring Boot环境集成时，需要Spring Boot 2.x及其以上版本，建议使用2.7.x及其以上版本

### (2) 开启邮箱SMTP服务

> 如果你的邮箱以及开通了SMTP服务，并且知道授权码，就可以跳过这一步。

可以使用QQ或者163等邮箱，登录对应邮箱网站，在设置中开启IMAP/SMTP服务即可，这里以163邮箱为例，登录后在设置点击POP3/SMTP/IMAP选项：

![](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/3b0b8af4c4844c5285d687c69ceeb637~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp)

开启任意一个即可，然后按照指引操作，最后会得到一个**授权码**，建议复制到一个文本文档记下来，这个页面也可以看到SMTP地址，复制到配置里面即可：

![](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/137c06c1a51d41afa26e5837436a6a0e~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp)

### (3) 在Spring Boot环境集成

首先引入依赖：

```xml
<dependency>
	<groupId>io.github.swsk33</groupId>
	<artifactId>code-post-spring-boot-starter</artifactId>
	<version>1.2.0</version>
</dependency>
```

然后在Spring Boot配置文件`application.yml`中，加入下列配置：

```yaml
# 服务端配置
server:
  port: 8802

####### 以下是CodePost相关邮件配置 #######
io:
  github:
    swsk33:
      code-post:
        core:
          # 发件邮箱的SMTP服务器（这里以163邮箱为例）
          smtp-host: "smtp.163.com"
          # 用于发件的邮箱
          email: "你的邮箱"
          # 发件邮箱密码（或者是授权码）
          password: "你的邮箱密码（或者是授权码）"
          # 是否启用TLS安全加密
          enable-tls: true
          # 用于存放管理验证码过期的方式
          code-storage: "thread_pool"
          # 验证码格式
          code-format: "number"
          # 验证码长度
          code-length: 6
          # 你的网站名（会在验证码邮件标题显示）
          site-name: "xxx博客"
          # 是否使用HTML网页邮件
          enable-html: false
```

上述配置中邮箱、授权码替换成第(2)步得到的，这里使用的是`YAML`格式的配置，使用`properties`同理。

### (4) 编写API测试

现在，写一个`RestController`调用邮箱验证码服务即可：

```java
package io.github.swsk33.codepostspringboottest.api;

import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 简单验证码发送和校验
 */
@RestController
@RequestMapping("/api/code/simple")
public class SimpleEmailCodeAPI {

	/**
	 * 自动装配邮件验证码服务
	 */
	@Autowired
	private EmailVerifyCodeService emailVerifyCodeService;

	/**
	 * 发送验证码
	 *
	 * @param email  接收用户邮件
	 * @param userId 用户id
	 * @return 消息
	 */
	@GetMapping("/send/mail/{email}/user-id/{userId}")
	public String sendCode(@PathVariable("email") String email, @PathVariable("userId") int userId) {
		// 调用sendCodeAsync方法即可一键完成验证码生成发送操作
		// 参数分别是：邮箱对应的用户id、用户邮箱、验证码有效时长、验证码有效时长单位
		emailVerifyCodeService.sendCodeAsync(userId, email, 1, TimeUnit.MINUTES);
		return "已发送验证码！";
	}

	/**
	 * 校验验证码
	 *
	 * @param userId    验证码对应的用户id
	 * @param inputCode 传入验证码用于校验（用户输入的验证码）
	 * @return 消息
	 */
	@GetMapping("/verify/user-id/{userId}/code/{inputCode}")
	public String verifyCode(@PathVariable("userId") int userId, @PathVariable("inputCode") String inputCode) {
		// 调用verifyCode方法即可一键完成验证码校验操作
		// 参数分别是：邮箱对应的用户id、用户传入的验证码（用于校验）
		// 校验成功返回true，并且验证码也会立即失效
		boolean result = emailVerifyCodeService.verifyCode(userId, inputCode);
		return result ? "校验成功！" : "验证码错误或者不存在！";
	}

}
```

运行项目，访问对应API即可完成邮件验证码发送和校验工作。

### (5) 访问API

首先访问API路径`/api/code/simple/send/mail/{email}/user-id/{userId}`，发送邮件（`{email}`和`{userId}`部分换成自己的，`{userId}`随便填），不一会成功收到邮件：

![image-20230705182602610](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/undefinedimage-20230705182602610.png)

然后访问API路径`/api/code/simple/verify/user-id/{userId}/code/{inputCode}`，完成验证码校验操作：

![image-20230705182727804](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/undefinedimage-20230705182727804.png)

通过仅仅几行代码，我们就完成了验证码的发送、校验操作了！在这背后，框架自动地完成了验证码生成、管理、邮件渲染发送等操作。

## 3，常见问题排查

### (1) 发送富文本邮件时抛出异常：`java.lang.NoClassDefFoundError: jakarta/activation/DataHandler`

该问题通常出现在JDK 1.8 + Spring Boot 2.x的环境下，这是由于Spring Boot 2.x使用的Java EE规范和Angus Mail的Jakarta EE规范不一致导致，在项目中加入最新的`jakarta.activation-api`依赖即可，而不是继承Spring Boot中的版本：

```xml
<!-- 使用Spring Boot 2.x还需手动添加最新版jakarta.activation-api -->
<dependency>
	<groupId>jakarta.activation</groupId>
	<artifactId>jakarta.activation-api</artifactId>
	<version>2.1.3</version>
</dependency>
```

## 4，文档

关于该框架详细功能以及API，请参考：

- 详细文档：[传送门](./docs/主要文档.md)
- 配置参考：[传送门](./docs/配置参考.md)
- 一些示例程序：[传送门](https://github.com/swsk33/code-post/tree/master/code-post-test)