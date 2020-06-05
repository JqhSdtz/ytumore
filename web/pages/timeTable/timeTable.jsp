<%@ page import="bean.CourseScheduleBean" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="util.CalendarUtil" %>
<%@ page import="info.SystemInfo" %><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/28
  Time: 下午 5:01
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
            if (localStorage.getItem('ymfromlogin') === '1')
                getScriptSync('${pageContext.request.contextPath}/js_tpl/args_full.jsp?' + new Date().getTime());
            else
                getScriptSync('${pageContext.request.contextPath}/js_tpl/args_timeTable.jsp?' + new Date().getTime());
            getScriptSync('${requestScope.staticFilePath}/js/fileSeq.js?v=' + fileSeqVersion);
            getScriptWithSeq('common.js', false);
		</script>
		<script src="https://cdn.staticfile.org/popper.js/1.14.7/umd/popper.min.js"></script>
		<script src="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>
		<script>getScriptWithSeq('timeTable.js', false);</script>
		<link href="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/css/font-awesome.min.css" rel="stylesheet"/>
		<link href="${requestScope.staticFilePath}/css/spin_kit.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.eot"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.svg"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.ttf"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.woff"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/FontAwesome.otf"/>
		<title>个人课表</title>
	</head>
	<style>

		.midBar {
			font-weight: bold;
			text-align: center;
		}

		#updateButton, #customButton, #resetButton, #slideModeButton {
			width: 80%;
			margin-left: 10%;
			margin-top: 1rem;
		}

		#resetButton {
			margin-bottom: 1rem;
		}

		#updateButton {
			margin-bottom: 1rem;
			display: none;
		}

		#customButton {
			display: none;
		}

		#main {
			display: none;
		}

		#tableOuterDiv {
			width: 100%;
			overflow-x: scroll;
		}

		#tableDiv.slideMode {
			width: 210%;
		}

		.table {
			table-layout:fixed;
		}

		.table td {
			word-wrap: break-word;
			word-break: break-all;
			border: none;
		}

		.table th {
			text-align: center;
			border: none;
		}

		td.slideMode {
			width: 30%;
		}

		table {
			font-size: 0.77rem;
		}
		.coursePanel {
			font-size: 0.77rem;
			padding: 0.4rem;
			border-radius: 0.35rem;
		}

		.isThisWeek {
			font-size: 0.7rem;
		}

		.inputLabel {
			font-weight: bold;
		}

		.modal-dialog {
			min-width: 80%;
			margin-left: 10%;
		}

		#confirmModal .modal-dialog {
			min-width: 90%;
			margin-left: 5%;
			margin-top: 20%;
		}

		.inputInline {
			position: relative;
			margin: 1.5rem 0;
		}

		.inputInline .form-control {
			position: absolute;
			display: inline;
			width: auto;
			right: 5%;
		}

		<c:choose >
			<c:when test="${requestScope.isAndroid}">
				#main {
					margin-top: 7.5rem;
				}
			</c:when>
			<c:otherwise>
				#main {
					margin-top: 4rem
				}
			</c:otherwise>
		</c:choose>

		.popover {
			z-index: 8;
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
                title: "个人课表",
                backHref: getIndexUrl(),
                moreLan: "更多",
                moreOpt: [
                    {
                        title: "班级课表",
                        url: getClassTimeTableQueryUrl()
                    }
                ]
            });
		</script>
		<div id="main">
			<div id="updateBtnTip" style="display:none" class="alert alert-info">
				提示:滑到页面底部有更新课表按钮，以及<span class="text-danger">点击课程名称可以查看课程详情</span>(点击此处不再提示)
			</div>
			<div id="errTip" style="display:none" class="alert alert-warning"></div>
			<button id="resetButton" style="display: none" class="btn btn-outline-dark">恢复默认课表</button>
			<script>
                if (localStorage.getItem('ymttupdatebtntipclicked') != '1') {
                    $('#updateBtnTip').show().click(function () {
                        $(this).hide();
                        localStorage.setItem('ymttupdatebtntipclicked', '1');
                    });
                }
                $('#errTip').click(function () {
	                $(this).hide();
                });
			</script>
			<div id="tableOuterDiv">
				<div id="tableDiv">
					<table class="table table-bordered table-condensed">
						<thead>
						<tr>
							<th id="weekday1">一</th>
							<th id="weekday2">二</th>
							<th id="weekday3">三</th>
							<th id="weekday4">四</th>
							<th id="weekday5">五</th>
							<th id="weekday6">六</th>
							<th id="weekday0">日</th>
							<script>
                                const wdSelector = '#weekday' + new Date().getDay().toString();
                                $(wdSelector).css("background-color", "#233333")
                                    .css("color", "white");
							</script>
						</tr>
						</thead>
						<tbody>
						<%boolean first = true;%>
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
										<c:when test="${requestScope.timeTable[i][j][0].secNum != -1}">
											<td class="cpTd" id="cpTd${(i + 1) * 10 + j + 1}" rowspan="${requestScope.timeTable[i][j][0].secNum}">
												<c:forEach var="s" begin="0" end="4" step="1">
													<c:if test="${requestScope.timeTable[i][j][s] != null}">
														<c:set var="cpIdx" value="${(i + 1) * 100 + (j + 1) * 10 + s + 1}"/>
														<input id="secNum${cpIdx}" type="hidden" value="${requestScope.timeTable[i][j][0].secNum}"/>
														<div id='cpContent${cpIdx}' class="cpContent" style='display: none'>
															<p>课程号：<span id='cNo${cpIdx}'>${requestScope.timeTable[i][j][s].course.cNo}</span></p>
															<p>教师：<span id='cTeacher${cpIdx}'>${requestScope.timeTable[i][j][s].course.cTeacher}</span></p>
															<p>周次：<span id='weeks${cpIdx}'>${requestScope.timeTable[i][j][s].weeks}</span></p>
															<p>课程属性：<span id='cAttr${cpIdx}'>${requestScope.timeTable[i][j][s].course.cAttr}</span></p>
															<p>学分：<span id='cCredit${cpIdx}'>${requestScope.timeTable[i][j][s].course.cCredit}</span></p>
														</div>
														<div id="cp${cpIdx}" class="coursePanel" style="width:100%;"
														     title="${requestScope.timeTable[i][j][s].course.cName}">
															<span id="cName${cpIdx}">${requestScope.timeTable[i][j][s].course.cName}</span></br>
															<span id="room${cpIdx}">${requestScope.timeTable[i][j][s].building}&nbsp;${requestScope.timeTable[i][j][s].room}</span></br>
															<span id="isThisWeek${cpIdx}" class="isThisWeek"></span>
															<span id="cNote${cpIdx}"></span>
														</div>
													</c:if>
												</c:forEach>
											</td>
										</c:when>
										<c:otherwise>
											<td class="cpTd" id="cpTd${(i + 1) * 10 + j + 1}" style="display: none"></td>
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
		<button id="slideModeButton" class="btn btn-outline-dark"></button>
		<button id="customButton" class="btn btn-outline-dark">自定义课表</button>
		<button id="updateButton" class="btn btn-outline-dark">更新课表</button>
		<div class="modal fade" id="customModal" tabindex="-1" role="dialog" aria-labelledby="modalTitle"
		     aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="modalTitle"></h4>
						<button class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					</div>
					<div id="modalContent" class="modal-body">
						<form>
							<div class="inputLabel">课程名称：<input class="form-control" id="cName_modal" type="text"/></div>
							<div class="inputLabel inputInline">教师：<input class="form-control" id="cTeacher_modal" type="text"/></div>
							<div class="inputLabel">周次：<input class="form-control" id="weeks_modal"  type="text"/></div>
							<div class="inputLabel">上课教室：<input class="form-control" id="room_modal" type="text"/></div>
							<div class="inputLabel inputInline">课程时长：共<select id="secNum_modal" style="width: 2rem; margin:0 0.5rem">
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option></select>小节</div>
							<div class="inputLabel inputInline">课程属性：<input style="width: 60%" class="form-control" id="cAttr_modal"  type="text"/></div>
							<div class="inputLabel inputInline">学分：<input style="width: 60%" class="form-control" id="cCredit_modal" type="text"/></div>
							<div class="inputLabel">备注：<textarea class="form-control" id="cNote_modal" type="text"></textarea></div>
						</form>
					</div>
					<div id="modalFooter" class="modal-footer">
						<button class="btn btn-secondary" style="margin-left: 1rem;display: none"  data-toggle="modal"
						        data-target="#confirmModal" id="delCourseBtn">删除课程</button>
						<button class="btn btn-secondary" data-dismiss="modal">关闭</button>
						<button id="saveModBtn" class="btn btn-primary" data-dismiss="modal">保存</button>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="modalTitle"
		     aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" id="confirmTitle">请确认</h4>
						<button class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					</div>
					<div id="confirmContent" class="modal-body">
						<strong></strong>
					</div>
					<div id="confirmFooter" class="modal-footer">
						<button class="btn btn-secondary" data-dismiss="modal">取消</button>
						<button class="btn btn-primary" data-dismiss="modal" id="confirmBtn">确认</button>
					</div>
				</div>
			</div>
		</div>
		<div id="loadingDiv"></div>
	</body>
</html>
