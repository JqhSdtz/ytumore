<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/23
  Time: 下午 8:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String requestSource = request.getHeader("x-requested-with");
	if ("cn.ytumore.ym".equals(requestSource))
		request.setAttribute("isAndroid", true);
	response.setHeader("Cache-Control", "no-cache");
	request.setAttribute("staticFilePath", request.getAttribute("ymLocal") == null ? "https://www.ytumore.cn/static_file" : "/ytumore");
%>
<html>
	<head>
		<script src="https://cdn.staticfile.org/jquery/3.3.1/jquery.min.js"></script>
		<script src="${requestScope.staticFilePath}/js/getScript.js"></script>
		<script>
			getScriptSync('${pageContext.request.contextPath}/js_tpl/args_simp.jsp?' + new Date().getTime());
            getScriptSync('${requestScope.staticFilePath}/js/fileSeq.js?v=' + fileSeqVersion);
            getScriptWithSeq('common.js', false);
		</script>
		<script src="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>
		<script src="https://cdn.staticfile.org/crypto-js/3.1.9-1/crypto-js.min.js"></script>
		<link href="https://cdn.staticfile.org/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet"/>
		<link href="${requestScope.staticFilePath}/css/login.css" rel="stylesheet"/>
		<title>登录--ytumore</title>
	</head>
	<style>
		@media (max-device-width: 1000px) {
			body {
				background: url('https://www.ytumore.cn/static_file/image/zlbg.jpg') no-repeat fixed;
				background-size: cover;
			}
		}

		@media (min-device-width: 1024px) {
			body {
				background: url('https://tuchong.pstatp.com/3154235/f/389917145.jpg') no-repeat fixed;
				background-size: cover;
			}
		}
		#instrModal .modal-dialog {
			min-width: 80%;
			margin-left: 10%;
		}
	</style>
	<body>
		<script>
            getScriptWithSeq('setRem.js', false);
            <c:if test="${requestScope.isAndroid}">
                getScriptWithSeq('statusBar.js', false);
            </c:if>
            let login = true;
            let logout = false;
		</script>
		<div id="main">
			<div id="mainForm">
				<form id="loginForm" action="${pageContext.request.contextPath}/login.ym?show=1&env=" method="post" onsubmit="process()">
					<input name="login" type="hidden" value="1"/>
					<input id="oriUrl" name="oriUrl" type="hidden" value="${requestScope.oriUrl}"/>
					<input id="aesKey" type="hidden" value="${sessionScope.aesKey}"/>
					<input id="env" name="env" type="hidden" value="${param.env}"/>
					<div id="uidDIv">
						学号：<input class="form-control" id="uid" name="uid" placeholder="学号" type="text"/>
					</div>
					<div id="pwdDiv">
						密码：<input class="form-control" id="pwd" name="pwd" placeholder="密码" type="password"/>
					</div>
					<div id="hasReadDiv" style="margin-top:0.5rem;margin-left: 1rem;" class="custom-control custom-checkbox">
						<input id="hasRead" type="checkbox" class="custom-control-input"/>
						<label class="custom-control-label" for="hasRead">已阅读
							<u class="text-info" id="instrBtn">《网站使用须知》</u>
						</label>
					</div>
					<input type="submit" id="submitButton" class="btn btn-secondary" value="登&nbsp;&nbsp;录" disabled="disabled"/>
				</form>
				<c:choose>
					<c:when test="${requestScope.errorCode == -1 || requestScope.errorCode == -3}">
						<div class="alert alert-warning alert-dismissible" role="alert">
							<strong>未知错误！</strong> 您可以稍候再次尝试登陆
							<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<script>
                            clearStorage();
                            login = false;
						</script>
					</c:when>
					<c:when test="${requestScope.errorCode == -2}">
						<div class="alert alert-danger alert-dismissible" role="alert">
							<strong>用户名或密码错误!</strong>
							<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<script>
                            clearStorage();
                            login = false;
						</script>
					</c:when>
					<c:when test="${requestScope.errorCode == -5}">
						<div class="alert alert-success alert-dismissible" role="alert">
							<strong>注销成功！</strong>
							<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<script>
							clearStorage();
                            login = false;
                            localStorage.setItem('ymlgdiffcode', Math.floor(Math.random() * 1000000).toString());
                            logout = true;
						</script>
					</c:when>
					<c:otherwise>
						<div class="alert alert-info alert-dismissible" role="alert" style="margin-top:1rem">
							<strong>本网站不会保存您的教务系统密码，请放心登录</strong>
							<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</body>
	<div class="modal fade" id="instrModal" tabindex="-1" role="dialog" aria-labelledby="modalTitle"
	     aria-hidden="true" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<div id="instrDiv">
						<strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在使用本网站的所有功能之前，请您务必仔细阅读并透彻理解本声明。您可以选择不使用本网站，但如果您使用本网站，您的使用行为将被视为对本声明全部内容的认可。</strong>
						<h5 style="margin-top: 1rem;">一、关于信息收集</h5>
						<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本网站提供服务时会收集并存储您的学号、所有成绩、所有课表信息，除此之外，我们不会收集您的任何信息。除非您同意，本网站不会向任何第三方提供您的上述信息。</p>
						<h5>二、关于教务系统密码</h5>
						<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本网站不会以任何形式保存您的教务系统密码（以下简称“密码”），您的密码将仅保存在您使用的设备中。您每次访问本网站时会自动将您设备中保存的密码提交给本网站，以实现免密登录。</p>
						<h5>三、Cookie的使用</h5>
						<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.在您未拒绝接受cookies的情况下，本网站会在您的设备上设定或取用cookies，以便您能登录或使用依赖于cookies的本应用平台服务或功能。</p>
						<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.您有权选择接受或拒绝接受cookies。您可以通过修改浏览器设置的方式拒绝接受cookies。但如果您选择拒绝接受cookies，则您可能无法登录或使用依赖于cookies的本网站的功能。</p>
					</div>
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				</div>
				<div id="confirmFooter" class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">已阅读</button>
				</div>
			</div>
		</div>
	</div>
	<script>
		<c:if test="${requestScope.isAndroid}">
			$("#mainForm").css("top","27.5%");
			$('#env').val("app");
		</c:if>
        $('#instrBtn').click(function () {
            $('#instrModal').modal('show');
        });
        if($('#hasRead').is(':checked'))
            $('#submitButton').removeAttr('disabled');
        $('#hasRead').change(function () {
            if($(this).is(':checked'))
                $('#submitButton').removeAttr('disabled');
            else
                $('#submitButton').attr('disabled', 'disabled');
        });
		getScriptWithSeq('login.js', false);
	</script>
</html>
