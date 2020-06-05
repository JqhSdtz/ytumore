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
		<script src="https://cdn.staticfile.org/jquery/3.3.1/jquery.min.js"></script>
		<script src="${requestScope.staticFilePath}/js/getScript.js"></script>
		<script>
            if(localStorage.getItem('ymfromlogin') === '1')
                getScriptSync('${pageContext.request.contextPath}/js_tpl/args_login.jsp?' + new Date().getTime());
            else
                getScriptSync('${pageContext.request.contextPath}/js_tpl/args_simp.jsp?' + new Date().getTime());
            getScriptSync('${requestScope.staticFilePath}/js/fileSeq.js?v=' + fileSeqVersion);
            getScriptWithSeq('common.js', false);
		</script>
		<script src="https://cdn.staticfile.org/popper.js/1.14.7/umd/popper.min.js"></script>
		<script src="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>
		<link href="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/css/font-awesome.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.eot"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.svg"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.ttf"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.woff"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/FontAwesome.otf"/>
		<title>提示</title>
	</head>
	<style>
		body {
			--fontSize: 16px;
		}

		#main {
			width: 90%;
			margin-left: 5%;
		}

		td {
			word-wrap: break-word;
			word-break: break-all;
		}

		<c:choose >
			<c:when test="${requestScope.isAndroid}">
				#main {
					margin-top: 7.5rem;
					min-height: 100%;
				}
			</c:when>
			<c:otherwise>
				#main {
					margin-top: 4rem
				}
			</c:otherwise>
		</c:choose>
	</style>
	<body>
		<script>
            getScriptWithSeq('setRem.js', false);
            <c:if test="${requestScope.isAndroid}">
                getScriptWithSeq('statusBar.js', false);
            </c:if>
            getScriptWithSeq('topBar.js', false);
            setTopBar({
                title: "提示",
                backHref: getIndexUrl(),
            });
		</script>
		<div id="main">
			<h1>URP教务系统暂时关闭了课表，无法获取数据，请稍候再试</h1>
			<h2>一般这种情况不会持续很久，也就一两天，或者半天，总之不是常规现象</h2>
		</div>
	</body>
</html>
