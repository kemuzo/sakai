<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="deleteSections"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<c:out value="${msgs.label_section_list}" /> </br>
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
<form method="post" id="updateSections" action=editSection.htm?action=removeSections>
<div class="validation">
	<c:out value="${msgs.msgs_section_remove_confirm }"/>
	<ul id="deleteSectionsTable">
		<c:forEach var="section" items="${sections }">
			<li><c:out value="${section.section.title }"/></li>
			<input type="hidden" id="sectionremove" name="sectionremove" value="<c:out value="${section.section.uuid}" />" />
		</c:forEach>
	</ul>
	<c:out value="${msgs.msgs_section_remove_confirm_explain1 }"/>
</div>
	<div class="act">
		<input type="submit" name="submitButton" value="<c:out value="${msgs.label_remove}"/>" />
        <input type="submit" name="cancelButton" value="<c:out value="${msgs.label_cancel}"/>" />
	</div>
</form>
<%-- confirm removing sections end.--%>
	
