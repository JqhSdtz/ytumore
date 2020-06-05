<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/29
  Time: 上午 11:15
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
                getScriptSync('${pageContext.request.contextPath}/js_tpl/args_date.jsp?' + new Date().getTime());
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
		<script src="${requestScope.staticFilePath}/js/dateFormat.js"></script>
		<title>空教室查询</title>
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

		.inputDiv {
			margin-top: 1.5rem;
		}

		#checkDiv {
			margin-top: 2rem;
		}

		#sBar {
			margin-top: 2rem;
		}

		#sBar .inputDiv {
			margin: 0 0.5rem;
		}

		.custom-checkbox .custom-control-label::before {
			background-color: white;
		}

		<c:choose>
			<c:when test="${requestScope.isAndroid}">
				#main {margin-top: 7.5rem;min-height: 100%;}
			</c:when>
			<c:otherwise>
				#main {margin-top: 4rem}
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
                title: "空教室查询",
                backHref: getIndexUrl(),
            });
		</script>
		<div id="main">
			<form method="get" onsubmit="submitProcess()">
				<input type="hidden" name="result" value="1"/>
				<div class="inputDiv form-group">
					<p class="text-center" style="font-size:1.5rem">选择教学楼：</p>
					<select name="building" id="building" class="form-control">
						<option value="1">一教</option>
						<option value="2">二教</option>
						<option value="3">三教</option>
						<option value="4">四教</option>
						<option value="5">五教</option>
						<option value="6">六教</option>
						<option value="7">七教</option>
						<option value="8">综合楼</option>
						<option value="9">计算中心</option>
						<option value="10">外院公共语音室</option>
						<option value="11">工程实训中心</option>
						<option value="12">建筑馆</option>
						<option value="14">外院</option>
						<option value="15">网球场</option>
						<option value="16">外院专业语音室</option>
						<option value="17">足球场</option>
						<option value="18">数学机房</option>
						<option value="19">外院视听室</option>
						<option value="21">物理实验中心</option>
						<option value="22">体教部舞蹈房</option>
						<option value="23">育秀大楼</option>
						<option value="24">科技馆</option>
						<option value="25">大学生活动</option>
						<option value="26">校园管理</option>
						<option value="27">社区中心</option>
						<option value="28">实验室</option>
						<option value="29">设计实习地</option>
						<option value="30">音乐系</option>
						<option value="31">体育课</option>
						<option value="32">外院</option>
					</select>
				</div>
				<p id="curDate" class="text-center" style="margin-top: 1rem;margin-bottom: -1rem;"></p>
				<script>$('#curDate').text(getFormatDate(new Date, 'yyyy年MM月dd日'));</script>
				<div id="sBar" class="text-center">
					<div class="inputDiv d-inline" style="margin-left:2rem">
						<span>周次：</span>
						<input type="number" id="weekNum" name="week" min="1" max="24"/>
					</div>
					<div class="inputDiv form-group d-inline">
						<lable for="weekday">星期</lable>
						<select id="weekday" name="weekday" id="weekday" class="custom-control-inline">
							<option value="1">一</option>
							<option value="2">二</option>
							<option value="3">三</option>
							<option value="4">四</option>
							<option value="5">五</option>
							<option value="6">六</option>
							<option value="7">日</option>
						</select>
					</div>
				</div>
				<div id="checkDiv" class="custom-control custom-checkbox text-center">
					<input id="queryBySec" type="checkbox" class="custom-control-input"/>
					<label class="custom-control-label" for="queryBySec">按大节查询（不选则按天查询）</label>
				</div>
				<div class="inputDiv form-group d-inline text-center">
<%--					<div style="margin-top: 1rem;margin-left: 2rem">--%>
<%--						第<select name="sec" id="sec" class="custom-control-inline" disabled="disabled">--%>
<%--						<option value="1">1</option>--%>
<%--						<option value="2">2</option>--%>
<%--						<option value="3">3</option>--%>
<%--						<option value="4">4</option>--%>
<%--						<option value="5">5</option>--%>
<%--						<option value="6">6</option>--%>
<%--						<option value="7">7</option>--%>
<%--						<option value="8">8</option>--%>
<%--						<option value="9">9</option>--%>
<%--						<option value="10">10</option>--%>
<%--						<option value="11">11</option>--%>
<%--						<option value="12">12</option>--%>
<%--					</select><span style="margin-left:-1rem">节</span></div>--%>
						<div style="margin-top: 1rem;margin-left: 2rem">
							节次：<select name="sec" id="sec" class="custom-control-inline" disabled="disabled">
							<option value="1">第一大节(8:00-9:40)</option>
							<option value="3">第二大节(10:00-11:40)</option>
							<option value="5">第三大节(14:00-15:40)</option>
							<option value="7">第四大节(16:00-17:40)</option>
							<option value="9">第五大节(19:00-20:40)</option>
							<option value="11">第六大节(20:50-22:30)</option>
						</select></div>
				</div>
				<input type="hidden" name="buildingName" id="buildingName"/>
				<input type="hidden" name="queryType" id="queryType" value="1"/>
				<input type="hidden" name="pageSeq" id="pageSeq"/>
				<input type="hidden" name="env" id="env"/>
				<input type="hidden" name="timeStamp" id="timeStamp"/>
				<input type="submit" id="submitButton" class="btn btn-lg btn-secondary" value="查询"/>
			</form>
			<p class="text-secondary text-center" style="margin-top: 2rem">注：空教室数据每天都会有变动，以当天查到的为准。全部数据来源于教务系统。</p>
		</div>
	</body>
	<script>
        function submitProcess() {
            $('#buildingName').val($('#building>option:selected').text());
            localStorage.setItem('ymrqbdsted', $('#building').val());
            const seq = $('#queryType').val() === '1' ? fileSeqMap.get('roomResultByDay.jsp') : fileSeqMap.get('roomResult.jsp');
            $('#pageSeq').val(seq);
            $('#timeStamp').val(new Date().getFullYear() + '000' + new Date().getMonth()
	            + '000' + new Date().getDate() + (new Date().getHours() < 4 ? '0' : '1'));
            $('#env').val(env);
        }

        $('#weekNum').val(curWeekNum);
        const _building = localStorage.getItem('ymrqbdsted');
        $('#building').val(_building ? _building : 1);
        $('#weekday').val(curWeekDay);
        $('#sec').val(curSecNum % 2 ? curSecNum : curSecNum - 1);

        function changeQueryType(type) {
            if(type === '1') {//按天查询
                $('#queryBySec').prop("checked", false);
                $('#sec').attr('disabled', 'disabled');
                $('#queryType').val(1);
            } else if(type === '2') {//按节查询
                $('#queryBySec').prop("checked", true);
                $('#sec').removeAttr('disabled');
                $('#queryType').val(2);
            }
            localStorage.setItem('ymroomquerytype', type);
        }

        changeQueryType(localStorage.getItem('ymroomquerytype') === '1' ? '1' : '2');

        $('#queryBySec').change(function () {
            if ($('#queryBySec').is(':checked')) {
                changeQueryType('2');
            } else {
                changeQueryType('1');
            }
        });
	</script>
</html>
