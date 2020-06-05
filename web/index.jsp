<%@ page import="util.CalendarUtil" %><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/18
  Time: 下午 8:57
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
                getScriptSync('${pageContext.request.contextPath}/js_tpl/args_full.jsp?' + new Date().getTime());
            else
                getScriptSync('${pageContext.request.contextPath}/js_tpl/args_index.jsp?' + new Date().getTime());
            getScriptSync('${requestScope.staticFilePath}/js/fileSeq.js?v=' + fileSeqVersion);
		</script>
		<script>getScriptWithSeq('common.js', false);</script>
		<script src="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>
		<link href="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/css/font-awesome.min.css" rel="stylesheet"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.eot"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.svg"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.ttf"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/fontawesome-webfont.woff"/>
		<link href="https://cdn.staticfile.org/font-awesome/3.2.1/font/FontAwesome.otf"/>
		<link href="${requestScope.staticFilePath}/css/index.css" rel="stylesheet"/>
		<script src="${requestScope.staticFilePath}/js/dateFormat.js"></script>
		<meta http-equiv="Cache-Control" content="no-cache">
		<meta name="robots" content="all">
		<meta name="keywords" content="烟台大学,ytu,教务系统,查课表,查成绩,查空教室,URP,">
		<title>首页--ytumore</title>
	</head>
	<c:if test="${requestScope.isAndroid}">
		<style>
			.modal-dialog {
				margin-top: 40%;
			}
		</style>
	</c:if>
	<body>
		<script>getScriptWithSeq('setRem.js', false);</script>
		<c:if test="${requestScope.isAndroid}">
			<script>getScriptWithSeq('statusBar.js', false);</script>
		</c:if>
		<div id="main" class="container-fluid">
			<c:if test="${requestScope.isAndroid}">
				<script>
                    $("#main").css("marginTop", "7rem");
				</script>
			</c:if>
			<c:if test="${!requestScope.isAndroid}">
				<script>
                    $("#main").css("marginTop", "5rem");
				</script>
			</c:if>
			<div class="row">
				<div class="col-12">
					<p style="font-size: 2rem;font-weight: bold" class="text-center" id="curTime"></p>
				</div>
			</div>
			<div class="row">
				<div class="col-12 mb-3">
					<p style="font-size: 1.85rem;font-weight: bold" class="text-center" id="curWeekNum"></p>
				</div>
			</div>
			<script>
				const weekDayStr = ['日','一','二','三 ','四','五','六'];
                $('#curWeekNum').text('第' + curWeekNum + '周，周' + weekDayStr[new Date().getDay()]);
                $('#curTime').text(getFormatDate(new Date, 'yyyy年MM月dd日'));
			</script>
			<div class="row">
				<div class="sec col-4 pr-1 gradeButton" type="term">
					<div class="icons text-center">
						<span class="pIcon icon-list-alt"></span>
						<span id="subCheck" class="subIcon icon-check"></span>
					</div>
					<p class="text-center">本学期成绩</p>
				</div>
				<div class="sec col-4 pr-1 gradeButton" type="all">
					<div class="icons text-center">
						<span class="pIcon icon-list-alt"></span>
						<span class="subIcon icon-ok-sign subCircle"></span>
					</div>
					<p class="text-center">全部及格成绩</p>
				</div>
				<div class="sec col-4 gradeButton" type="failed">
					<div class="icons text-center">
						<span class="pIcon icon-list-alt"></span>
						<span class="subIcon icon-remove-sign subCircle"></span>
					</div>
					<p class="text-center">不及格成绩</p>
				</div>
			</div>
			<div class="row pt-5">
				<div id="timeTableButton" class="sec col-4 pr-1">
					<div class="icons text-center">
						<span class="pIcon icon-table"></span>
						<span id="subUserCircle" class="subIcon icon-circle-blank"></span>
						<span id="subUser" class="subIcon icon-user"></span>
					</div>
					<p class="text-center">查看个人课表</p>
				</div>
				<div id="classTimeTableQueryButton" class="sec col-4 pr-1">
					<div class="icons text-center">
						<span class="pIcon icon-table"></span>
						<span id="subGroupCircle" class="subIcon icon-circle-blank"></span>
						<span id="subGroup" class="subIcon icon-group"></span>
					</div>
					<p class="text-center">查询班级课表</p>
				</div>
				<div id="roomQueryButton" class="sec col-4">
					<div class="icons text-center">
						<span class="pIcon icon-th-large"></span>
						<span id="subRoomCircle" class="subIcon icon-circle-blank"></span>
						<span id="subRoom" class="subIcon icon-signin"></span>
					</div>
					<p class="text-center">查询空教室</p>
				</div>
			</div>
			<div class="row">
				<button id="logButton" class="col-12 mt-4 btn-lg btn-secondary"></button>
			</div>
			<div class="row">
				<button id="contactMeBtn" class="col-12 mt-1 text-secondary btn btn-link" data-toggle="modal"
				        data-target="#contactMeModal">联<span id="tipWord1" class="tipWords">&nbsp;&nbsp;</span>
					系<span id="tipWord2" class="tipWords">&nbsp;&nbsp;</span>作<span id="tipWord3" class="tipWords">&nbsp;&nbsp;</span>者
				</button>
			</div>
			<div class="row">
				<button id="beian" class="col-12 text-secondary btn btn-link">
					鲁ICP备19009966号-1
				</button>
			</div>
			<div id="debug" style="display: none"></div>
			<div id="manageDiv" style="display: none">M</div>
			<script>
				if(typeof clientUid !== 'undefined' && !isAdmin)
				    localStorage.removeItem('ymisadmin');
				$('#manageDiv').css({
					'position': 'fixed',
					'right': '0',
					'bottom': '0',
					'padding': '1rem',
					'font-size': '2rem',
					'color': 'white',
					'background-color': '#233333'
				}).click(function () {
					window.location.href = contextPath + '/monitor.ym?show=1&env=' + env;
                });
				if(localStorage.getItem('ymisadmin') === '1') {
				    $('#manageDiv').show();
				}else if(typeof isAdmin !== 'undefined' && isAdmin) {
				    localStorage.setItem('ymisadmin', '1');
                    $('#manageDiv').show();
				}
			</script>
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="confirmLogoutModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel3"
			     aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title" id="myModalLabel3">确认注销</h4>
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						</div>
						<div class="modal-body">您确认注销登录吗？(将清除您记住的密码)</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
							<button type="button" id="logoutConfirm" class="btn btn-primary" data-dismiss="modal">
								确定
							</button>
						</div>
					</div>
				</div>
			</div>
			<jsp:include page="pages/tipWords/tipWordsModal.jsp"></jsp:include>
		</div>
		<c:if test="${!requestScope.isAndroid}">
			<div id="appPrompt">
				<div id="appPromptClose" class="close">
					&times;
				</div>
				<img id="appIcon" src="${requestScope.staticFilePath}/image/appIcon.png"/>
				<a id="appStatement" href="${requestScope.staticFilePath}/ym.apk"><span>下载ytumore APP</span>
					<br/><span style="margin-left: 3rem">优雅地查询教务</span></a>
			</div>
			<script>
                if (localStorage.getItem('ymfirst') === null) {
                    $('#appPrompt').show();
                    $('#appPromptClose').click(function () {
                        localStorage.setItem('ymfirst', '1');
                        $('#appPrompt').hide();
                    });
                    $('#appStatement').click(function () {
                        localStorage.setItem('ymfirst', '1');
                    });
                }
			</script>
		</c:if>
	</body>
	<script>
        $('#beian').click(function () {
            window.location.href = 'http://www.beian.miit.gov.cn';
        });

        /**设置登录/注销按钮*/
        if (localStorage.getItem('ymhaslogin') !== '1') {
            //ymhaslogin不等于1表示未登录
            $('#logButton').text('登\xa0\xa0\xa0\xa0\xa0\xa0录')
                .click(function () {
                    window.location.href =  contextPath + '/login.ym?show=1';
                });
        } else {
            $('#logButton').text('注\xa0\xa0\xa0\xa0\xa0\xa0销')
                .click(function () {
                    $('#confirmLogoutModal').modal('show');
                    $('#logoutConfirm').click(function () {
                        window.location.href = contextPath + '/logout.ym?logout=1';
                    });
                });
        }

        /**设置各个模块的链接*/
        $('.gradeButton').click(function () {
            if ($(this).attr('type') === 'term') {
                window.location.href = getTermGradeUrl();
            } else if ($(this).attr('type') === 'all') {
                window.location.href = getAllGradeUrl();
            } else if ($(this).attr('type') === 'failed') {
                window.location.href = getFailGradeUrl();
            }
        });
        $('#timeTableButton').click(function () {
            window.location.href = getTimeTableUrl();
        });
        $('#roomQueryButton').click(function () {
            window.location.href = getRoomQueryUrl();
        });
        $("#classTimeTableQueryButton").click(function () {
            window.location.href = getClassTimeTableQueryUrl();
        });

        /**更改地址栏地址，否则会是登录页面的url*/
        let url = contextPath;
        if (url === "")
            url = "/";
        history.replaceState({}, null, url);
	</script>
</html>
