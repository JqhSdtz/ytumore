<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/31
  Time: 上午 11:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%request.setAttribute("staticFilePath", request.getAttribute("ymLocal") == null ? "https://www.ytumore.cn/static_file" : "/ytumore");%>
<html>
	<head>
		<script src="https://cdn.staticfile.org/jquery/3.3.1/jquery.min.js"></script>
		<script src="https://cdn.staticfile.org/crypto-js/3.1.9-1/crypto-js.min.js"></script>
		<title>系统管理</title>
	</head>
	<style>
		#main {
			width: 70%;
			margin-left: 15%;
		}

		#cmd {
			width: 80%;
		}

		#result {
			width: 100%;
			min-height: 20rem;
			margin-top: 1rem;
			overflow: scroll;
		}
	</style>
	<body>
		<div id="main">
			<input id="pageContext" type="hidden" value="${pageContext.request.contextPath}"/>
			<form autocomplete="off">
				<p>管理员密码：<input type="password" id="pwd"/></p>
				<p>命令：<input type="text" id="cmd"/></p>
				<input type="hidden" name="set" value="1"/>
				<input type="hidden" name="data" id="data"/>
				<input type="hidden" id="managerId" value="${requestScope.managerId}"/>
			</form>
			<button id="submitButton">提交</button>
			<br/>
			<textarea id="result">
		</textarea>
		</div>
	</body>
	<script src="${requestScope.staticFilePath}/js/manage.js"></script>
	<script>
		const str = '更新应用之前需要停止刷新线程并等待几秒后再更新\n' +
			'设置http的timeout后需要停止刷新线程并隔几秒后再次启动';
		console.log(str);
	</script>
</html>
