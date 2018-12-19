<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<link media="all" href="css/sectionselect.css" rel="stylesheet" type="text/css" />
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="assignTa"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<h3><c:out value="${msgs.label_assign_ta}" />:<c:out value="${section.title }" /></h3>
<br/>
<div class="nssectionInstruction">
<c:out value="${msgs.msgs_explain_display_tas }" escapeXml="false"/><br/>
<c:out value="${msgs.msgs_explain_display_tas_2 }" escapeXml="false"/>
</div>
<br/><br/>

<form method="post" id="memberForm" name="memberForm" action=assignTa.htm?action=updateTa&sectionId=<c:out value="${section.uuid}"/> >

	<table id="taAssign">
		<tbody>
			<tr>
				<td class="available">
					<div><c:out value="${msgs.label_available_tas }"/></div>
					<div>
						<select id="availableUsers" name="availableUsers" multiple="multiple" size="20" style="width:250px; border:#ff3300 2px dotted;">
							<c:forEach var="a_user" items="${availableUsers }">
								<option value="<c:out value='${a_user.label }'/>"><c:out value='${a_user.value}'/></option>
							</c:forEach>
						</select>
					</div>
				</td>
				<td class="transferButtons">
					<table id="transferButtons">
						<tbody>
							<tr><td class="transferButtonTable"><c:out value="${msgs.label_select_move }"/></td></tr>
							<tr><td class="transferButtonTable"><input type="button" onclick="addUser();" value="&gt;"/></td></tr>
							<tr><td class="transferButtonTable"><input type="button" onclick="removeUser();" value="&lt;"/></td></tr>
							<tr><td class="transferButtonTable"><div class="verticalPadding"><c:out value="${msgs.labe_all_move }"/></div></td></tr>
							<tr><td class="transferButtonTable"><input type="button" onclick="addAll();" value="&gt;&gt;"/></td></tr>
							<tr><td class="transferButtonTable"><input type="button" onclick="removeAll();" value="&lt;&lt"/></td></tr>
						</tbody>
					</table>
				</td>
				<td class="selected">
					<div><c:out value="${msgs.label_selected_tas  }"/></div>
					<div>
						<select id="selectedUsers" name="selectedUsers" multiple="multiple" size="20" style="width:250px; border:#3030ff 2px solid;">
							<c:forEach var="s_user" items="${selectedUsers }">
								<option value="<c:out value='${s_user.label }'/>"><c:out value='${s_user.value}'/></option>
							</c:forEach>
						</select>
					</div>
				</td>
			</tr>
			<tr>
				<td class="avaiable" colspan="3"><div"><c:out value="${msgs.msgs_explain_no_tas }"  escapeXml="false"/></div></td>
			</tr>
		</tbody>
	</table>
	
	<div class="act">
		<c:if test="${not ( empty availableUsers && empty selectedUsers) }">
		<input type="button" id="submitButton" value="<c:out value="${msgs.link_assign_manager}"/>" 
			onclick="highlightUsers();"/>
		</c:if>
		<c:if test="${ empty availableUsers && empty selectedUsers }">
		<input type="submit" name="submitButton" value="<c:out value="${msgs.link_assign_manager}"/>" 
			onclick="highlightUsers();" disabled="disabled"/>
		</c:if>
		<input type="submit" name="cancelButton" value="<c:out value="${msgs.label_cancel}"/>" />
	</div>
</form>
<%--modal --%>
<div class="doalert" style="display:none">
 <c:out value="${msgs.msgs_do_waite}" /><img src="lib/images/doing.gif"/ alt="doing.."/>
</div>
<%-- end modal --%>
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
$('input#submitButton').click( function(e) {
	e.preventDefault();
	$('form#memberForm').submit();
	$(".doalert").dialog({
		modal: true,
		title: 'Processing..',
		draggable: false,
		closeOnEscape:false,
		resizable: false,
		open:function(event,ui){
			$('.ui-dialog-titlebar-close').hide();}
	});
});
// -->
</script>