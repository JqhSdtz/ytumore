<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/25
  Time: 下午 6:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String requestSource = request.getHeader("x-requested-with");
	if ("cn.ytumore.ym".equals(requestSource))
		request.setAttribute("isAndroid", true);
	response.setHeader("Cache-Control", "max-age=2592000");
	response.addHeader("Cache-Control", "private");
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
		<link href="${requestScope.staticFilePath}/css/spin_kit.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.eot"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.svg"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.ttf"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.woff"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/FontAwesome.otf"/>
		<title>不及格成绩</title>
	</head>
	<style>
		.grade {
			margin: 2rem 1rem;
		}
		.divLabel {
			font-size: 2rem;
			font-weight: bold;
			text-align: center;
		}
		#updateButton {
			width: 80%;
			margin-left: 10%;
			margin-top: 1rem;
		}
		<c:choose >
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
                title: "不及格成绩",
                backHref: getIndexUrl(),
                moreLan: "更多",
                moreOpt: [
                    {
                        title: "本学期成绩",
                        url: getTermGradeUrl()
                    },
                    {
                        title: "全部及格成绩",
                        url: getAllGradeUrl()
                    }
                ]
            });
		</script>
		<div id="main">
			<button id="updateButton" class="btn btn-outline-dark">更新成绩</button>
			<p class="divLabel" style="margin-top: 1rem">尚不及格</p>
			<c:forEach var="grade" items="${requestScope.stillFailingList}">
				<div class="grade alert alert-danger">
					课程：${grade.course.cName}(${grade.course.cCredit}学分)(${grade.course.cAttr})
					&nbsp;&nbsp;成绩：${grade.courseGrade}&nbsp;考试时间：${grade.examTime}
				</div>
			</c:forEach>
			<p class="divLabel">曾不及格</p>
			<c:forEach var="grade" items="${requestScope.passedList}">
				<div class="grade alert alert-success">
					课程：${grade.course.cName}(${grade.course.cCredit}学分)(${grade.course.cAttr})
					&nbsp;&nbsp;成绩：${grade.courseGrade}&nbsp;考试时间：${grade.examTime}
				</div>
			</c:forEach>
		</div>
		<div id="loadingDiv"></div>
	</body>
	<script>
        //更新成绩操作
        $('#updateButton').click(function () {
            showLoading();
            $(this).attr('disabled', 'disabled');
            window.location.href = getNewGradeUrl('failed');
        });
	</script>
</html>
