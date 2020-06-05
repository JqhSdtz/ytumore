<%@ page import="java.util.ArrayList" %>
<%@ page import="bean.ExamBean" %><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/12/11
  Time: 下午 9:57
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
	((ArrayList<ExamBean>)request.getAttribute("examList"))
			.sort((o1, o2) -> o1.geteWeekNum() == o2.geteWeekNum() ? o1.geteWeekDay() - o2.geteWeekDay() : o1.geteWeekNum() - o2.geteWeekNum());
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
		<title>考试安排</title>
	</head>
	<style>
		.exam, #updateBtnTip {
			margin: 2rem 1rem;
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
                title: "考试安排",
                backHref: getIndexUrl()
            });
		</script>
		<div id="main">
			<div id="updateBtnTip" style="display:none" class="alert alert-info">
				提示:考试安排一般都是一次出完，如果确定还有新的安排，可以点击页面下方更新按钮(点击此处不再提示)
			</div>
			<button id="updateButton" class="btn btn-outline-dark">更新考试安排</button>
			<script>
                if(localStorage.getItem('ymexupdatebtntipclicked') != '1') {
                    $('#updateBtnTip').show().click(function () {
                        $(this).hide();
                        localStorage.setItem('ymexupdatebtntipclicked', '1');
                    });
                }
			</script>
			<c:forEach var="exam" items="${requestScope.examList}">
				<div class="exam alert alert-success">
					科目:${exam.eName}&nbsp;&nbsp;地点:${exam.eBuilding}&nbsp;${exam.eRoom}</br>
					时间:第${exam.eWeekNum}周&nbsp;星期<span class="weekNum" data-value="${exam.eWeekDay}"></span>&nbsp;${exam.eTime}
				</div>
			</c:forEach>
		</div>
		<div id="loadingDiv"></div>
	</body>
	<script>
		/*设置星期为汉字*/
		const charset = ['一','二','三','四','五','六','日'];
		$('.weekNum').each(function () {
			$(this).text(charset[parseInt($(this).attr('data-value')) - 1]);
        })
        /*更新考试安排操作*/
        $('#updateButton').click(function () {
            showLoading();
            $(this).attr('disabled', 'disabled');
            window.location.href = getNewExamUrl();
        });
	</script>
</html>
