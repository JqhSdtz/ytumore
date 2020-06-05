<%@ page import="util.AESUtil" %><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/10/04
  Time: 下午 7:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${sessionScope.monitorAccess != null}">
	<jsp:forward page="/pages/manage/monitor.jsp"/>
</c:if>
<%
	response.setHeader("Cache-Control", "no-cache");
	request.getSession(true).setAttribute("aesKey", AESUtil.getRandKey());
	request.setAttribute("staticFilePath", request.getAttribute("ymLocal") == null ? "https://www.ytumore.cn/static_file" : "/ytumore");
%>
<html>
	<head>
		<script src="https://cdn.staticfile.org/jquery/3.3.1/jquery.min.js"></script>
		<script src="https://cdn.staticfile.org/crypto-js/3.1.9-1/crypto-js.min.js"></script>
		<title>系统监控</title>
	</head>
	<style>
		#pwd {
			position: fixed;
			top: 25%;
			height: 20%;
			width: 100%;
			font-size: 5em;
		}
		#btn {
			position: fixed;
			top: 55%;
			height: 20%;
			width: 100%;
			font-size: 5em;
		}
	</style>
	<body>
		<form id="form" method="post" action="${pageContext.request.contextPath}/monitor.ym">
			<input type="hidden" name="login" value="1"/>
			<input type="password" name="pwd" id="pwd"/>
			<input type="button" value="登&nbsp;&nbsp;&nbsp;录" id="btn" />
		</form>
		<script>
			$('#btn').click(function () {
                let pwd = $('#pwd').val();
                if(pwd === '') {
                    alert('请输入密码');
                    return;
                }
                $('#pwd').val(aes_encrypt('${sessionScope.aesKey}', new Date().getTime() + pwd));
                $('#form').submit();
            });
            function aes_encrypt(key, data) {
                key = CryptoJS.enc.Utf8.parse(key);
                const res = CryptoJS.AES.encrypt(data, key, {
                    mode: CryptoJS.mode.ECB,
                    padding: CryptoJS.pad.Pkcs7
                }).toString();
                return res;
            }
		</script>
	</body>
</html>
