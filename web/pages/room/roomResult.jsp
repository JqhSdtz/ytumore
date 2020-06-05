<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/29
  Time: 上午 11:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String requestSource = request.getHeader("x-requested-with");
	if ("cn.ytumore.ym".equals(requestSource))
		request.setAttribute("isAndroid", true);
	response.setHeader("Cache-Control", "max-age=2592000");
	response.addHeader("Cache-Control", "public");
	request.setAttribute("staticFilePath", request.getAttribute("ymLocal") == null ? "https://www.ytumore.cn/static_file" : "/ytumore");
	String[] charset = {"一","二","三","四","五","六",};
	int cIdx = (Integer.valueOf((String)request.getAttribute("secNum")) + 1) / 2;
	cIdx = (cIdx > 6 || cIdx < 0) ? 5 : cIdx - 1;
	request.setAttribute("secNum", charset[cIdx]);
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
		<script src="https://cdn.staticfile.org/popper.js/1.14.7/popper.min.js"></script>
		<script src="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>
		<link href="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/css/font-awesome.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.eot"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.svg"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.ttf"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.woff"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/FontAwesome.otf"/>
		<title>空教室查询结果</title>
	</head>
	<style>
		#main {
			width: 75%;
			margin-left: 12.5%;
		}
		<c:choose>
			<c:when test="${requestScope.isAndroid}">
				#main{margin-top:7.5rem;min-height:100%;}
			</c:when>
			<c:otherwise>
				#main{margin-top:4rem}
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
                title: "${requestScope.buildingName} 第${requestScope.secNum}大节",
                backHref: getRoomQueryUrl()
            });
		</script>
		<div id="main">
			<c:choose>
				<c:when test="${fn:length(requestScope.roomList) == 0}">
					<div style="width: 90%;margin-left: 5%" class="alert alert-info">
						${requestScope.buildingName} 第${requestScope.secNum}大节 竟然一个空教室都没有！
					</div>
				</c:when>
				<c:otherwise>
					<ul class="list-group">
						<c:forEach var="room" items="${requestScope.roomList}">
							<li class="list-group-item d-flex justify-content-between align-items-center">
								<span style="font-weight:bold">${room.roomNo}</span>&nbsp;&nbsp;${room.roomType}&nbsp;&nbsp;
								<span class="badge badge-secondary badge-pill">${room.seatNum}座</span>
							</li>
						</c:forEach>
					</ul>
				</c:otherwise>
			</c:choose>
		</div>
	</body>
</html>
