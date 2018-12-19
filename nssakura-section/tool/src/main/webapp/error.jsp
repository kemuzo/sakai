<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="list"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<c:out value="${msgs.label_section_list}" /> </br>

<%-- Messages --%>
		<div class="nsvalidation">
			<c:out value="${msgs.msgs_global_exception}"  escapeXml="false"/></br>
			<c:out value="${errmsg }" />
		</div>
<%-- Messages end --%>
