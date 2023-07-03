<!DOCTYPE html>
<html lang="zh">

<head>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
	<title>验证码</title>
	<style>
		* {
			margin: 0;
			padding: 0;
		}

		body {
			background-color: rgb(224, 224, 224);
		}

		.background {
			position: absolute;
			width: 390px;
			height: 380px;
			background-color: white;
		}

		.background .head {
			position: relative;
			height: 10px;
			background-color: rgb(0, 153, 255);
		}


		.background .content .text {
			position: relative;
			margin-top: 4%;
			margin-left: 5%;
			width: 90%;
			padding: 8px;
			box-sizing: border-box;
			height: 330px;
			border: 1.5px blue solid;
			overflow: auto;
		}

		.background .content .text p {
			display: inline;
			border-bottom: 1px green dashed;
		}

		@media (max-width: 768px) {
			.background {
				width: 100%;
			}
		}
	</style>
</head>

<body>
<div class="background">
	<div class="head"></div>
	<div class="content">
		<div class="text">
			<p>您的${serviceName}重置验证码是：${code}，验证码将在${time}后过期！</p>
			<br/>
			<p>请妥善保管您的验证码，请勿泄露！</p>
		</div>
	</div>
</div>
</body>

</html>