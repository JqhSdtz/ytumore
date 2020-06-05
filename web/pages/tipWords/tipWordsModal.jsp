<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 19/09/24
  Time: 上午 11:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	response.setHeader("Cache-Control", "max-age=2592000");
	response.addHeader("Cache-Control", "public");
	request.setAttribute("staticFilePath", request.getAttribute("ymLocal") == null ? "https://www.ytumore.cn/static_file" : "/ytumore");
%>
<div class="modal fade" id="contactMeModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel2"
     aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="myModalLabel2">联系作者</h4>
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;
				</button>
			</div>
			<div id="tipContent" class="modal-body"></div>
			<div class="modal-footer">
				<button type="button" style="display:none" id="tipWordsToggleBtn" class="btn btn-secondary"></button>
				<button type="button" style="display:none" id="tipWordsHasReadBtn" class="btn btn-secondary">已阅</button>
				<button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>
<script>getScriptWithSeq('tipWords.js');</script>
