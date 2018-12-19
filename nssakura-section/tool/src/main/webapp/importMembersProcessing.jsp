<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="importMember"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<c:out value="${msgs.label_member_import}" /> </br>
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
