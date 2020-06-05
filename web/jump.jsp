<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/10/03
  Time: 下午 10:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String requestSource = request.getHeader("x-requested-with");
	if ("cn.ytumore.ym".equals(requestSource))
		request.setAttribute("isAndroid", true);
	response.setHeader("Cache-Control", "max-age=2592000");
	response.addHeader("Cache-Control", "public");
	request.setAttribute("staticFilePath", request.getAttribute("ymLocal") == null ? "https://www.ytumore.cn/static_file" : "/ytumore");
%>
<c:if test="${requestScope.isAndroid}">
	<script>const fromApp = true;</script>
</c:if>
<html>
	<head>
		<title>正在跳转，请稍候</title>
		<script src="https://cdn.staticfile.org/jquery/3.3.1/jquery.min.js"></script>
		<script src="${requestScope.staticFilePath}/js/getScript.js"></script>
		<script>getScriptSync('${pageContext.request.contextPath}/js_tpl/args_simp.jsp?' + new Date().getTime());</script>
		<script>
			getScriptSync('${requestScope.staticFilePath}/js/fileSeq.js?v=' + fileSeqVersion);
			getScriptWithSeq('common.js', false);
			const ref = getParamOfUrl('ref');
			if(ref === '')
                window.location.href = getIndexUrl() + '&env=' + env;
			else if(ref === '1')
			    window.location.href = getTermGradeUrl() + '&env=' + env;
			else if(ref === '2')
			    window.location.href = getTimeTableUrl() + '&env=' + env;
			else if(ref === '3')
			    window.location.href = getRoomQueryUrl() + '&env=' + env;
			else if(ref === '4')
				window.location.href = getExamScheduleUrl() + '&env=' + env;
			else if(ref === '5')
			    window.location.href = getTeacherInfoQueryUrl() + '&env=' + env;
		</script>
	</head>
</html>
