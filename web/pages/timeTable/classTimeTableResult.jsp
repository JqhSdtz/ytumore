<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/30
  Time: 下午 10:09
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
		<script src="https://cdn.staticfile.org/popper.js/1.14.7/umd/popper.min.js"></script>
		<script src="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>
		<link href="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/css/font-awesome.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.eot"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.svg"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.ttf"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.woff"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/FontAwesome.otf"/>
		<title>班级课表</title>
	</head>
	<style>

		.midBar {
			font-weight: bold;
			text-align: center;
		}

		td {
			word-wrap:break-word;
			word-break:break-all;
			width: 30%;
		}

		#tableOuterDiv {
			width: 100%;
			overflow-x: scroll;
		}

		#tableDiv {
			width: 210%;
		}

		.table {
			table-layout:fixed;
		}

		table {
			font-size: 0.75rem;
		}
		.course {
			font-size: 0.75rem;
		}
		.normalCourse {
			font-size: 0.75rem;
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
                title: "${requestScope.className}",
                backHref: getClassTimeTableQueryUrl(),
                moreLan: "更多",
                moreOpt: [
                    {
                        title: "个人课表",
                        url: getTimeTableUrl()
                    }
                ]
            });
		</script>
		<div id="main">
			<div id="tableOuterDiv">
				<div id="tableDiv">
					<table class="table table-bordered table-condensed">
						<thead>
						<tr>
							<th>星期一</th>
							<th>星期二</th>
							<th>星期三</th>
							<th>星期四</th>
							<th>星期五</th>
							<th>星期六</th>
							<th>星期日</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach var="i" begin="0" end="11" step="1">
							<c:choose>
								<c:when test="${i == 4}">
									<tr class="midBar">
										<td colspan="7">午&nbsp;&nbsp;&nbsp;&nbsp;休</td>
									</tr>
								</c:when>
								<c:when test="${i == 8}">
									<tr class="midBar">
										<td colspan="7">晚&nbsp;&nbsp;&nbsp;&nbsp;饭</td>
									</tr>
								</c:when>
							</c:choose>
							<tr>
								<c:forEach var="j" begin="0" end="6" step="1">
									<c:choose>
										<c:when test="${requestScope.timeTable[i][j][1] != null}">
											<td class="course"
											    data-content="${requestScope.timeTable[i][j][1]}">
													${requestScope.timeTable[i][j][0]}<span class="text-info">&nbsp;&nbsp;点击查看详情</span>
											</td>
										</c:when>
										<c:otherwise>
											<td class="normalCourse">${requestScope.timeTable[i][j][0]}</td>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</body>
	<script>
        function initPopover() {
            $(".course").popover({
                placement: 'right',
                html: true,
                container: "body",
                trigger: " manual" //手动触发
            }).on('show.bs.popover', function () {
                $(this).addClass("popover_open");
            }).on('hide.bs.popover', function () {
                $(this).removeClass("popover_open");
            }).click(function (ev) {
                if ($(this).hasClass("popover_open")) {
                    $(this).popover("hide")
                } else {
                    $(".popover_open").popover("hide");
                    $(this).popover("show");
                }
                const e = ev || window.event || arguments.callee.caller.arguments[0];
                e.stopPropagation();
            });
            $(document).click(function () {
                $(".course").popover("hide");
            });
        }
        $(document).ready(function () {
            initPopover();
        });
	</script>
</html>
