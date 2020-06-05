<%@ page import="util.CalendarUtil"%><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/10/05
  Time: 上午 8:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="application/x-javascript;charset=UTF-8"%>
<%response.setHeader("Cache-Control", "no-cache");%>
const curWeekNum = <%=CalendarUtil.getCurWeekNum()%>;

const contextPath = '${pageContext.request.contextPath}';
const staticFilePath = '${requestScope.ymLocal ? "/ytumore" : "https://www.ytumore.cn/static_file"}';
const debug = ${sessionScope.debug == true};
const isAdmin = ${sessionScope.isAdmin == true};
const fileSeqVersion = '0123';