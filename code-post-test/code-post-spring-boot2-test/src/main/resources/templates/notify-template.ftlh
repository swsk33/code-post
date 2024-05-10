<!DOCTYPE html>
<html lang="zh">
<head>
	<meta charset="utf-8"/>
	<title>验证码服务</title>
</head>
<body>
<h3>您的专栏：《${anthologyName}》今天更新了！</h3>
<h5>新增文章：</h5>
<ul>
    <#list newArticles as item>
		<li>${item}</li>
    </#list>
</ul>
</body>
</html>