<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="assignStudent"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<h3><c:out value="${msgs.label_assign_student}" />:<c:out value="${section.title }" /></h3>
<div class="nssectionInstruction">
	<c:out value="${msgs.msgs_add_member_explain1}"/><br/>
	<c:out value="${msgs.msgs_add_member_explain2}"/>
</div>
<br/>
<%-- Messages --%>
	<c:if test="${not empty msg }">
		<div class="success">
			<c:out value="${msg}" escapeXml="false"/>
		</div>
	</c:if>
	<c:if test="${not empty err }">
		<div class="nsvalidation">
			<c:out value="${err}" />
		</div>
	</c:if>
<%-- Messages end --%>

<form method="post" id="updateStudents" action=assignStudent.htm?action=updateStudents&sectionId=<c:out value="${section.uuid}"/>>
	<table width="100%">
	<tr>
	<td valign="top">
		<c:out value="${msgs.label_userId }"/>
		<input type="text" id="userEid" name="userEid"  maxlength="255"/>
		<input type="submit" name="addButton" value="<c:out value="${msgs.label_add}"/>" />
	</td>
<%-- 	<td align="left" valign="top">
		<input type="submit" name="addAutoButton" value="<c:out value="${msgs.label_add_auto}"/>" /><br/>
		<font color="#cc0033"><c:out value="${msgs.msgs_add_auto_explain1 }" escapeXml="false"/></font>
	</td>--%>
	</tr>
	</table>
<br/>

<c:if test="${empty enrollments }">
	<c:out value="${msgs.msgs_no_enrollment }" />
</c:if>
<c:if test="${not empty enrollments }">
	<c:out value="${msgs.label_enroolment_users }" />
	<c:out value="${enrollments_num}" />
	<table id="enrollmentList"  class="tablesorter enrollmentList lines listHier"  cellspacing="0" cellpadding="0"  summary="enrollmentList" >
		<thead>
			<tr>
				<th scope="col"><input type="checkbox" id="toggleAllSelected" onclick="javascript:toggleSelectAll(this,'enrollmentremove')" /></th>
				<th scope="col"><c:out value="${msgs.label_name }"/></th>
				<th scope="col"><c:out value="${msgs.label_userid }"/></th>
				<th scope="col"><c:out value="${msgs.label_email }"/></th>
				<th scope="col"><c:out value="${msgs.label_belongcompanyname}"/></th>
				<th scope="col"><c:out value="${msgs.label_chokugai }"/></th>
				<th scope="col"><c:out value="${msgs.label_typeofemproyment}"/></th>
			</tr>
		</thead>
		<tbody>
				<c:forEach var="user" items="${enrollments}">
					<tr>
						<td align="center">
							<input type="checkbox" name="enrollmentremove"  id="enrollmentremove"  value="<c:out value="${user.user.id}"/>">
						</td>
						<td style="white-space:nowrap">
							<c:out value="${user.user.displayName}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${user.user.displayId}"/><br>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${user.user.email}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${user.belongcompanyname}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${user.chokugai}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${user.typeofemproyment}"/>
						</td>
					</tr>
				</c:forEach>
		</tbody>
	</table>
	<div class="act">
		<input type="submit" name="removeButton" value="<c:out value="${msgs.label_remove_member}"/>" />
        <input type="submit" name="returnButton" value="<c:out value="${msgs.label_return}"/>" />
		
	</div>
</c:if>
</form>


<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
jQuery( function() {
	jQuery( '#enrollmentList' ).dataTable({
		"aoColumnDefs":[
		                {"bSortable": false, "aTargets":[0]}
		                ],
		"bLengthChange": false,
		"iDisplayLength": 20,
		"bStateSave":false
	});
	});
// -->

</script>
