<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.learningStatus.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="messageIndex"/>
<jsp:directive.include file="/templates/navi.jsp"/>


<%-- Instruction --%>
<%-- Messages --%>
	<c:if test="${not empty msg }">
		<div class="success">
			<c:out value="${msg}" escapeXml="false"/>
		</div>
	</c:if>
	<c:if test="${not empty err }">
		<div class="nsvalidation">
			<c:out value="${err}"  escapeXml="false"/>
		</div>
	</c:if>
<%-- Messages end --%>
<c:if test="${empty messages}">
	<c:out value="${msgs.msgs_no_messages}"/>
</c:if>
<c:if test="${not empty messages}">
	<table id="messageList" class="messageList tablesorter" cellspacing="0" cellpadding="0" summary="messageList">
		<thead>
			<tr>
				<th><c:out value="${msgs.label_senddate}"/></th>
				<th><c:out value="${msgs.label_subject }"/></th>
				<th><c:out value="${msgs.labe_from}"/></th>
				<%-- <th><c:out value="${msgs.label_content}"/></th>--%>
				<th><c:out value="${msgs.label_resend}"/></th>
				<c:if test="${superuser}">
				<th><c:out value="${msgs.label_remove}"/></th>
				</c:if>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="message" items="${messages}" varStatus="status">
				<tr>
					<td class="senddate"><c:out value="${message.daoModel.sendDateStr}" /></td>
					<td class="subject"><c:out value="${message.daoModel.subject}"/></td>
					<td class="sender"><c:out value="${message.senderName}"/></td>
					<%-- <td class="content"><p data-tgt="${status.index}" class="btns"><img src="/library/image/silk/page.png"/></p></td>--%>
					<td class="resend"><a href='messageResend.htm?messageId=<c:out value="${message.daoModel.id}"/>'><img src="/library/image/silk/email_go.png"/></a></td>
				<c:if test="${superuser}">
					<td class="remove">
					<a href='messageRemove.htm?messageId=<c:out value="${message.daoModel.id}"/>' onclick='return window.confirm("${msgs.msgs_confirm_remove}");'>
						<img src="/library/image/sakai/delete.gif"/></a>
					</td>
				</c:if>
				</tr>
			</c:forEach>
		</tbody>
	</table>
<%-- 	<c:forEach var="message" items="${messages}" varStatus="status">
		<div class="modal ${status.index}">
			<div class="modalBody">
				<p class="close"><img src="/library/image/silk/cross.png"/>close</p>
				<p class="mailHeader">
					<c:out value="${msgs.label_senddate}"/><c:out value="${message.sendDateStr}" /><br/><br/>
					<c:out value="${msgs.label_subject }"/><c:out value="${message.subject}"/>
				</p>
				<c:out value="${message.content}" escapeXml="false"/>
			</div>
			<div class="modalBK"></div>
		</div>
	</c:forEach>
	<div class="modalBodyDummy">&nbsp</div>--%>
</c:if>
<jsp:directive.include file="/templates/footer.jsp"/>
<script type="text/javascript">
$('#messageList').dataTable({
	"order":[[0,"desc"]],
	"bPaginate":true,
	"bLengthChange":true,
	"bInfo":true,
	"bStateSave":false
});


/*$(function(){
	$('.btns').click(function(){
		wn = '.' + $(this).data('tgt');
		var mW = $(wn).find('.modalBody').innerWidth() / 2;
		var mH = $(wn).find('.modalBody').innerHeight() / 2;
		$(wn).find('.modalBody').css({'margin-left':-mW,'margin-top':-mH});
		$(wn).fadeIn(500);
	});
	$('.close,.modalBK').click(function(){
		$(wn).fadeOut(500);
	});
});*/
</script>
