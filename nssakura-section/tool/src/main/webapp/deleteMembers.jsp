<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="deleteMembers"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<h3><c:out value="${msgs.label_remove_members}" />:<c:out value="${section.title }" /> </h3></br>
<%-- Messages --%>
	<c:if test="${not empty msg }">
		<div class="success">
			<c:out value="${msg}" />
		</div>
	</c:if>
	<c:if test="${not empty err }">
		<div class="nsvalidation">
			<c:out value="${err}" />
		</div>
	</c:if>
<%-- Messages end --%>

<%-- confirm removing sections --%>
<form method="post" id="updateMembers" action=assignStudent.htm?action=removeMembers&sectionId=<c:out value="${section.uuid}"/>>
<div class="validation">
	<c:out value="${msgs.msg_member_remove_confirm}"/>
	<ul id="deleteMemberTable">
		<c:forEach var="user" items="${users}">
			<li><c:out value="${user.user.displayName }"/>(<c:out value="${user.user.eid }"/>)</li>
			<input type="hidden" id="memberremove" name="memberremove" value="<c:out value="${user.user.id}" />" />
		</c:forEach>
	</ul>
	<c:out value="${msgs.msgs_member_remove_confirm_explain1}"/>
</div>
	<div class="act">
		<input type="submit" name="submitButton" value="<c:out value="${msgs.label_remove}"/>" />
        <input type="submit" name="cancelButton" value="<c:out value="${msgs.label_cancel}"/>" />
	</div>
</form>
<%-- confirm removing sections end.--%>
	
