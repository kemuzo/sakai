<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="importJob"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<c:out value="${msgs.label_import_job}" /> </br>
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
<%-- Display Form  --%>
<c:if test="${jobs != null}">
	<table id="jobList"  class="jobList listHier"  cellspacing="0" cellpadding="0"  summary="jobList" >
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.label_startdate}"/></th>
				<th scope="col"><c:out value="${msgs.label_enddate}"/></th>
				<th scope="col" class="numTh"><c:out value="${msgs.label_addusernum }"/></th>
				<th scope="col" class="numTh"><c:out value="${msgs.label_alterusernum }"/></th>
				<th scope="col" class="numTh"><c:out value="${msgs.label_addmembernum }"/></th>
				<th scope="col" class="numTh"><c:out value="${msgs.label_errorusernum}"/></th>
				<th scope="col" class="numTh"><c:out value="${msgs.label_processusernum }"/></th>
			</tr>
		</thead>
		<tbody>
				<c:forEach var="data" items="${jobs}">
					<tr>
						<td class="dateTd">
							<c:out value="${data.startDateStr}"/>
						</td>
						<td class="dateTd">
							<c:out value="${data.endDateStr}"/>
						</td>
						<td class="numTd">
							<c:out value="${data.addUserNum}"/>
						</td>
						<td class="numTd">
							<c:out value="${data.alterUserNum}"/>
						</td>
						<td class="numTd">
							<c:out value="${data.addMemberNum}"/>
						</td>
						<td class="numTd">
							<c:out value="${data.errorUserNum}"/>
						</td>
						<td class="numTd">
							<c:out value="${data.processUserNum}"/>
						</td>
					</tr>
				</c:forEach>
		</tbody>
	</table>
</c:if>
<%-- end Display Form  --%>
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
$(document).ready( function() {
	$( '#jobList' ).dataTable({
		"aaSorting":[[0,"desc"]],
		"bLengthChange": false,
		"iDisplayLength": 15,
		"bStateSave": false,
		"bFilter": false
	});
});

// -->
</script>