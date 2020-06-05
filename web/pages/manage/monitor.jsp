<%@ page import="info.SystemInfo" %>
<%@ page import="util.LogSessionUtil" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="util.C3P0Util" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="util.LogUtil" %><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/10/04
  Time: 下午 7:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	response.setHeader("Cache-Control", "no-cache");
	if(request.getSession().getAttribute("monitorAccess") == null) {
		response.setStatus(404);
		return;
	}
	String requestSource = request.getHeader("x-requested-with");
	if ("cn.ytumore.ym".equals(requestSource))
		request.setAttribute("isAndroid", true);
	request.setAttribute("staticFilePath", request.getAttribute("ymLocal") == null ? "https://www.ytumore.cn/static_file" : "/ytumore");

	int logCnt = 0, webLogCnt = 0, appLogCnt = 0, wxmpLogCnt = 0, qqmpLogCnt = 0, wxgzhLogCnt = 0;
	int newUserLogCnt = 0, locallyLogCnt = 0, connectedLogCnt = 0;
	int newWebUserLogCnt = 0, newAppUserLogCnt = 0, newWxmpUserLogCnt = 0, newQqmpUserLogCnt = 0, newWxgzhUserLogCnt = 0;
	int activeUserCnt = 0;
	String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	Connection con = C3P0Util.getMysqlConn();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	    String sql0 = "SELECT count(*) FROM ytumore.s_log where unix_timestamp(createTime) > unix_timestamp(?);";
		String sql1 = "SELECT count(*) FROM ytumore.s_log where unix_timestamp(createTime) > unix_timestamp(?) and plat=?;";
		String sql2 = "SELECT count(*) FROM ytumore.s_log where unix_timestamp(createTime) > unix_timestamp(?) and type=?;";
		String sql3 = "SELECT count(*) FROM ytumore.s_log where unix_timestamp(createTime) > unix_timestamp(?) and type=? and plat=?;";
		String sql4 = "SELECT count(*) FROM ytumore.s_user where unix_timestamp(lastLoginTime) > unix_timestamp(?);";
		st = con.prepareStatement(sql0);
		st.setString(1, dateStr);
		rs = st.executeQuery();
		rs.next();
		logCnt = rs.getInt(1);
		C3P0Util.closeAll(rs, st, null);
		st = con.prepareStatement(sql1);
		st.setString(1, dateStr);
		st.setInt(2, 1);
		rs = st.executeQuery();
		rs.next();
		webLogCnt = rs.getInt(1);
		st.setInt(2, 2);
		rs = st.executeQuery();
		rs.next();
		appLogCnt = rs.getInt(1);
		st.setInt(2, 3);
		rs = st.executeQuery();
		rs.next();
		wxmpLogCnt = rs.getInt(1);
		st.setInt(2, 4);
		rs = st.executeQuery();
		rs.next();
		qqmpLogCnt = rs.getInt(1);
		st.setInt(2, 5);
		rs = st.executeQuery();
		rs.next();
		wxgzhLogCnt = rs.getInt(1);
		C3P0Util.closeAll(rs, st, null);
		st = con.prepareStatement(sql2);
		st.setString(1, dateStr);
		st.setInt(2, 1);
		rs = st.executeQuery();
		rs.next();
		newUserLogCnt = rs.getInt(1);
		st.setInt(2, 2);
		rs = st.executeQuery();
		rs.next();
		locallyLogCnt = rs.getInt(1);
		st.setInt(2, 3);
		rs = st.executeQuery();
		rs.next();
		connectedLogCnt = rs.getInt(1);
		C3P0Util.closeAll(rs, st, null);
		st = con.prepareStatement(sql3);
		st.setString(1, dateStr);
		st.setInt(2, 1);
		st.setInt(3, 1);
		rs = st.executeQuery();
		rs.next();
		newWebUserLogCnt = rs.getInt(1);
		st.setInt(3, 2);
		rs = st.executeQuery();
		rs.next();
		newAppUserLogCnt = rs.getInt(1);
		st.setInt(3, 3);
		rs = st.executeQuery();
		rs.next();
		newWxmpUserLogCnt = rs.getInt(1);
		st.setInt(3, 4);
		rs = st.executeQuery();
		rs.next();
		newQqmpUserLogCnt = rs.getInt(1);
		st.setInt(3, 5);
		rs = st.executeQuery();
		rs.next();
		newWxgzhUserLogCnt = rs.getInt(1);
		C3P0Util.closeAll(rs, st, null);
		st = con.prepareStatement(sql4);
		st.setString(1, dateStr);
		rs = st.executeQuery();
		rs.next();
		activeUserCnt = rs.getInt(1);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
		C3P0Util.closeAll(rs, st, con);
	}
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
		<title>系统监控</title>
	</head>
	<style>
		#updateButton {
			width: 100%;
			margin-top: 1rem;
			margin-bottom: 1rem;
		}
		#main {
			width: 80%;
			margin-left: 10%;
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
                title: "系统监控",
                backHref: getIndexUrl(),
            });
		</script>
		<div id="main">
			<button id="updateButton" class="btn btn-outline-dark">更&nbsp;&nbsp;&nbsp;新</button>
			<h3>用户总数：<%=SystemInfo.CUR_USER_NUM.get()%></h3>
			<h3>Session总数：<%=SystemInfo.CUR_SESSION_NUM.get()%></h3>
			<h3>在线用户总数：<%=SystemInfo.CUR_ONLINE_USER_NUM.get()%></h3>
			<h3>连接教务系统的用户总数：<%=SystemInfo.CUR_CONNECTED_USER_NUM.get()%></h3>
			<h3>今日登录日志总记录条数：<%=logCnt%></h3>
			<h3>今日WEB端登录日志记录条数：<%=webLogCnt%></h3>
			<h3>今日APP登录日志记录条数：<%=appLogCnt%></h3>
			<h3>今日微信小程序登录日志记录条数：<%=wxmpLogCnt%></h3>
			<h3>今日QQ小程序登录日志记录条数：<%=qqmpLogCnt%></h3>
			<h3>今日微信公众号登录日志记录条数：<%=wxgzhLogCnt%></h3>
			<h3>今日新用户登录记录条数：<%=newUserLogCnt%></h3>
			<h3>今日WEB端新用户登录记录条数：<%=newWebUserLogCnt%></h3>
			<h3>今日APP新用户登录记录条数：<%=newAppUserLogCnt%></h3>
			<h3>今日微信小程序新用户登录记录条数：<%=newWxmpUserLogCnt%></h3>
			<h3>今日QQ小程序新用户登录记录条数：<%=newQqmpUserLogCnt%></h3>
			<h3>今日微信公众号新用户登录记录条数：<%=newWxgzhUserLogCnt%></h3>
			<h3>今日本地登录记录条数：<%=locallyLogCnt%></h3>
			<h3>今日连接教务系统登录记录条数：<%=connectedLogCnt%></h3>
			<h3>今日活跃用户总数：<%=activeUserCnt%></h3>
			<h3>学期：<%=SystemInfo.CUR_TERM%></h3>
			<h3>学期开始周（一年中的第几周）：<%=SystemInfo.TERM_START_WEEK_NUM%></h3>
			<h3>Cookie生存周期：<%=SystemInfo.COOKIE_MAX_AGE%></h3>
			<h3>用户登录时失败重试的次数：<%=SystemInfo.LOGIN_RETRY_TIMES%></h3>
			<h3>Servlet异步状态：<%=SystemInfo.IS_ASYNC == 1 ? "异步" : "同步"%></h3>
			<h3>异步Servlet超时时间：<%=SystemInfo.ASYNC_SERVLET_TIMEOUT%></h3>
			<h3>连接教务系统超时时间：<%=SystemInfo.CONNECTION_TIMEOUT%></h3>
			<h3>用户更新课表最低间隔：<%=SystemInfo.UPDATE_TIMETABLE_INTERVAL%></h3>
			<h3>用户更新成绩最低间隔：<%=SystemInfo.UPDATE_GRADE_INTERVAL%></h3>
			<h3>用户更新考试安排最低间隔：<%=SystemInfo.UPDATE_EXAM_INTERVAL%></h3>
			<h3>VPN连接状态：<%=SystemInfo.CONNECTED_TO_VPN ? "已连接" : "连接断开"%></h3>
			<h3 id="testPage" class="text-primary">Test页面</h3>
		</div>
	</body>
	<script>
		$('#updateButton').click(function () {
            $(this).attr('disabled', 'disabled');
			window.location.reload();
        });
		$('#testPage').click(function () {
			window.location.href = contextPath + '/test.jsp';
        });
	</script>
</html>
