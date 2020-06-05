<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 20/01/03
  Time: 下午 12:07
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
            if (localStorage.getItem('ymfromlogin') === '1')
                getScriptSync('${pageContext.request.contextPath}/js_tpl/args_full.jsp?' + new Date().getTime());
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
		<title>教师信息查询</title>
	</head>
	<style>
		#main {
			width: 75%;
			margin-left: 12.5%;
		}

		#submitButton {
			width: 100%;
			margin-top: 3rem;
		}

		.info {
			margin-top: 1rem;
		}

		.info span {
			margin-left: 0.75rem;
		}

		.form-control {
			display: inline;
			width: 60%;
			right: 5%;
		}

		<c:choose>
		<c:when test="${requestScope.isAndroid}">
		#main {margin-top: 9.5rem;min-height: 100%;}
		</c:when>
		<c:otherwise>
		#main {margin-top: 6rem}
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
                title: "教师信息查询",
                backHref: getIndexUrl(),
            });
		</script>
		<div id="main">
			<form id="form">
				<p><bold>教师姓名：</bold><input id="teacherName" class="form-control" type="text"/></p>
				<div id="infoDiv">
					<div id="info0" class="teacherInfo">
						<p class="info"><bold style="margin-left: 2rem">院系：</bold><span class="school"></span></p>
						<p class="info"><bold style="margin-left: 2rem">电话：</bold><span class="tel"></span></p>
						<p class="info"><bold style="margin-left: 2rem">邮箱：</bold><span class="email"></span></p>
					</div>
				</div>
			</form>
			<button id="submitButton" class="btn btn-secondary">查&nbsp;&nbsp;询</button>
			<p class="text-secondary text-center" style="margin-top: 2rem">注：仅包含部分教师信息</p>
		</div>
	</body>
	<script>
		for(let j = 1; j < 10; ++j)
            $('#infoDiv').append('<div style="display: none; margin-top: 1rem;" class="teacherInfo border-top" id="info' + j + '">' + $('#info0').html() + '</div>');
		$('#submitButton').click(function () {
			$.get(contextPath + '/teacherInfo.ym?result=1&teacherName=' + $('#teacherName').val(), function (result) {
				const resultJson = JSON.parse(result);
                $('.teacherInfo').hide();
				if(resultJson.state === 0) {
				    $('#info0').show();
				    $('#info0 .school').text('暂无');
				    $('#info0 .tel').text('暂无');
				    $('#info0 .email').text('暂无');
				    $('#info0+.teacherInfo').hide();
				} else if(resultJson.state === 1) {
				    const info = resultJson.info;
				    for(let i = 0; i < info.length; ++i) {
				        $('#info' + i).show();
                        $('#info' + i + ' .school').text(info[i].schoolName);
                        $('#info' + i + ' .tel').text((info[i].tel === '' || info[i].tel === 'null') ? '暂无' : info[i].tel);
                        $('#info' + i + ' .email').text((info[i].email === '' || info[i].email === 'null') ? '暂无' : info[i].email);
				    }
				}
            });
        });
	</script>
</html>
