<!DOCTYPE html>
<html lang="zh">
<head>
	<meta charset="utf-8"/>
	<title>${siteName}验证码服务</title>
	<style>
		div {
			font-size: 24px;
		}

		.code {
			font-weight: bold;
		}

		img {
			max-width: 360px;
		}
	</style>
</head>
<body>
<div>您的${serviceName}服务验证码：<span class="code">${code}</span></div>
<div>验证码有效期限：${time}</div>
<img src="https://img1.imgtp.com/2023/07/05/H8cDhSYW.jpg" alt="无法显示"/>
</body>
</html>