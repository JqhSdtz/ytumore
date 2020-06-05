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
		<title>全部及格成绩</title>
	</head>
	<style>
		.grade, #updateBtnTip {
			margin: 2rem 1rem;
		}
		.termDivLabel {
			font-size: 1.25rem;
			font-weight: bold;
			text-align: center;
		}
		#updateButton {
			width: 80%;
			margin-left: 10%;
			margin-bottom: 10%;
		}
		#creditDiv {
			min-height: 2.6rem;
			margin: 1rem 2rem;
			font-size: 1.3rem;
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
				title: "全部及格成绩",
                backHref: getIndexUrl(),
				moreLan: "更多",
				moreOpt: [
					{
					    title: "本学期成绩",
						url: getTermGradeUrl()
					},
					{
					    title: "不及格成绩",
						url: getFailGradeUrl()
					}
				]
			});
		</script>
		<div id="main">
			<div id="creditDiv" class="text-center">
				<strong class="text-center" style="font-size: 1.3rem;">课程属性学分统计</strong></br>
			</div>
			<div id="updateBtnTip" style="display:none" class="alert alert-info">
				提示:滑到页面底部有更新成绩按钮，新出的成绩在本学期成绩中显示，全部成绩一学期更新一次就行(点击此处不再提示)
			</div>
			<script>
                if(localStorage.getItem('ymgdupdatebtntipclicked') != '1') {
                    $('#updateBtnTip').show().click(function () {
                        $(this).hide();
                        localStorage.setItem('ymgdupdatebtntipclicked', '1');
                    });
                }
			</script>
			<c:forEach var="term" items="${requestScope.termList}" varStatus="status">
				<p class="termDivLabel">${requestScope.termTitleList[status.count -1]}</p>
				<c:forEach var="grade" items="${term}">
					<div class="grade alert alert-success" data-attr="${grade.course.cAttr}" data-credit="${grade.course.cCredit}">
						课程：${grade.course.cName}(${grade.course.cCredit}学分)(${grade.course.cAttr})&nbsp;&nbsp;成绩：${grade.courseGrade}
					</div>
				</c:forEach>
			</c:forEach>
		</div>
		<button id="updateButton" class="btn btn-outline-dark">更新成绩</button>
		<div id="loadingDiv"></div>
	</body>
	<script>
		/*统计课程属性成绩*/
        const creditMap = new Map();
        $('.grade').each(function () {
            const attr = $(this).attr('data-attr');
            const credit = $(this).attr('data-credit');
	        if(!isNaN(credit)) {
	            if(creditMap.get(attr) === undefined)
                    creditMap.set(attr, parseFloat(credit));
	            else {
                    const newCredit = creditMap.get(attr) + parseFloat(credit);
                    creditMap.set(attr, newCredit);
	            }
	        }
        });
        creditMap.forEach(function (credit, attr) {
            //console.log(attr + ' ' + credit);
            $('#creditDiv').append($('<strong class="text-center" style="margin:0 1rem">' + attr + ': ' + credit +'</strong>'));
        });

        /*更新成绩操作*/
        $('#updateButton').click(function () {
            showLoading();
            $(this).attr('disabled', 'disabled');
            window.location.href = getNewGradeUrl('all');
        });
	</script>
</html>
