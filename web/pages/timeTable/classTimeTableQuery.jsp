<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/01/30
  Time: 下午 3:51
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
            getScriptWithSeq('classesInfo.js', false);
		</script>
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
		#main {
			width: 75%;
			margin-left: 12.5%;
		}

		#submitButton {
			width: 100%;
			margin-top: 2rem;
		}

		.inputDiv {
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
                title: "班级课表查询",
                backHref: getIndexUrl(),
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
			<form onsubmit="submitProcess()">
				<div class="inputDiv form-group">
					<label for="term">学年学期：</label>
					<select id="term" name="termNo" class="form-control">
						<option value="2017-2018-1-1">2017-2018学年秋(两学期)</option>
						<option value="2017-2018-2-1">2017-2018学年春(两学期)</option>
						<option value="2018-2019-1-1">2018-2019学年秋(两学期)</option>
						<option value="2018-2019-2-1">2018-2019学年春(两学期)</option>
						<option value="2019-2020-1-1" selected="selected">2019-2020学年秋(两学期)</option>
						<option value="2019-2020-2-1">2019-2020学年春(两学期)</option>
					</select>
				</div>
				<div class="inputDiv form-group">
					<lable for="school">院系：</lable>
					<select id="school" class="form-control" onchange="changeSpec(this)">
						<option value="" selected="selected">请选择</option>
					</select>
				</div>
				<div class="inputDiv form-group">
					<lable for="speciality">专业：</lable>
					<select id="speciality" class="form-control" onchange="changeClass(this)">
					</select>
				</div>
				<div class="inputDiv form-group">
					<lable for="class">班级：</lable>
					<select id="class" class="form-control">
					</select>
				</div>
				<input type="hidden" name="classNo" id="classNo"/>
				<input type="hidden" name="className" id="className"/>
				<input type="hidden" name="result" value="1"/>
				<input type="hidden" name="pageSeq" id="pageSeq"/>
				<input type="hidden" name="env" id="env"/>
				<input type="submit" id="submitButton" class="btn btn-lg btn-secondary" value="查询"/>
			</form>
		</div>
	</body>
	<script>
        const schools = classesInfo1.schools;
        const schoolsElem = $("#school");
        const specsElem = $('#speciality');
        const classesElem = $('#class');
        $(document).ready(function () {
            for (let i = 0; i < schools.length; ++i) {
                const school = $('<option value="' + i + '">' + schools[i].schoolName + '</option>');
                schoolsElem.append(school);
            }
        });

        function changeSpec(schoolElem) {
            specsElem.empty();
            if ($(schoolElem).val() == "")
                return;
            const school = schools[$(schoolElem).val()];
            const specs = school.specialities;
            for (let i = 0; i < specs.length; ++i) {
                const spec = $('<option value="' + i + '">' + specs[i].specialityName + '</option>');
                specsElem.append(spec);
            }
            changeClass($('#speciality'));
        }

        function changeClass(specElem) {
            classesElem.empty();
            const classes = schools[$('#school').val()].specialities[$(specElem).val()].classes;
            for (let i = 0; i < classes.length; ++i) {
                const class_ = $('<option value="' + i + '">' + classes[i].className + '</option>');
                classesElem.append(class_);
            }
        }

        function submitProcess(){
            const curSpec = schools[schoolsElem.val()].specialities[specsElem.val()];
            $('#classNo').val(curSpec.classes[classesElem.val()].classNo);
            $('#className').val(curSpec.classes[classesElem.val()].className);
            $('#pageSeq').val(fileSeqMap.get('classTimeTableResult.jsp'));
            $('#env').val(env);
        }

	</script>
</html>
