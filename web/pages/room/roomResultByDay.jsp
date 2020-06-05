<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/04/07
  Time: 上午 10:43
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
			width: 90%;
			margin-left: 5%;
			position: relative;
		}

		td {
			word-wrap: break-word;
			word-break: break-all;
		}

		table.table tr td.avl {
			border: #233333 0.025rem solid;
		}

		.table {
			table-layout:fixed;
		}

		<c:choose>
			<c:when test="${requestScope.isAndroid}">
				#main{margin-top:7.5rem;min-height:100%;}
			</c:when>
			<c:otherwise>
				#main{margin-top:4rem}
				table {
					font-size: 1rem;
				}
			</c:otherwise>
		</c:choose>
		.tNum {
			margin-left: -0.25rem;
		}
		.tNum2 {
			margin-left: -0.55rem;
		}
		#roomTable {
			position: absolute;
			top: 2rem;
		}
		#avIntro {
			position: absolute;
			width:1rem;
			height:1rem;
			margin-top: 0.25rem;
			border-radius:0.25rem;
		}

		#na1, #na2 {
			position: absolute;
			width: 1rem;
			height: 0.5rem;
		}

		#na1 {
			margin-top: 0.25rem;
			background-color: #aceb8d;
			border-top-left-radius: 0.25rem;
			border-top-right-radius: 0.25rem;
		}

		#na2 {
			margin-top: 0.75rem;
			background-color: #90d0f0;
			border-bottom-left-radius: 0.25rem;
			border-bottom-right-radius: 0.25rem;
		}

		#avIntro {
			border: 0.1rem solid #233333;
			background-color: white;
		}
		#naBar {
			position: absolute;
			left: 20%;
		}
		#avBar {
			position: absolute;
			left: 65%;
		}
	</style>
	<body>
		<script>
            getScriptWithSeq('setRem.js', false);
            <c:if test="${requestScope.isAndroid}">
                getScriptWithSeq('statusBar.js', false);
            </c:if>
            getScriptWithSeq('topBar.js', false);
            setTopBar({
                title: "${requestScope.buildingName}",
                backHref: getRoomQueryUrl()
            });
		</script>
		<div id="main">
			<div id="introBar"><span id="naBar"><span id="na1"></span><span id="na2"></span>
				<span style="margin-left:1.5rem;">非空闲</span></span>
				<span id="avBar"><span id="avIntro"></span><span style="margin-left:1.5rem;">空闲</span></span></div>
			<table id="roomTable" class="table table-bordered" cellpadding="0">
				<col width="28%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<col width="6%"/>
				<thead>
					<tr>
						<th>教室</th>
						<th><span class="tNum">1</span></th>
						<th><span class="tNum">2</span></th>
						<th><span class="tNum">3</span></th>
						<th><span class="tNum">4</span></th>
						<th><span class="tNum">5</span></th>
						<th><span class="tNum">6</span></th>
						<th><span class="tNum">7</span></th>
						<th><span class="tNum">8</span></th>
						<th><span class="tNum">9</span></th>
						<th><span class="tNum2">10</span></th>
						<th><span class="tNum2">11</span></th>
						<th><span class="tNum2">12</span></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="room" items="${requestScope.roomList}">
						<tr>
							<th>${room.roomNo}<br/>${room.roomType}<br/>${room.seatNum}座</th>
							<c:forEach var="i" begin="1" end="12" step="1">
								<c:choose>
									<c:when test="${room.available[i]}">
										<td class="nAvl"></td>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${i < 5 || i > 8}">
												<td class="avl" style="background-color:#d4edda"></td>
											</c:when>
											<c:otherwise>
												<td class="avl" style="background-color:#d1ecf1"></td>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</body>
</html>
