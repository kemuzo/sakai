<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="importMember"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<c:out value="${msgs.label_member_import}" /> </br>
<%-- Instruction --%>
<div class="nssectionInstruction">
<ul>
	<li><c:out value="${msgs.instruction_member_import1 }" escapeXml="false"/>
	<li><c:out value="${msgs.instruction_member_import2 }" escapeXml="false" />
	<li><c:out value="${msgs.instruction_member_import3 }" escapeXml="false" />
	<li><c:out value="${msgs.instruction_member_import4 }" escapeXml="false" />
	<li><c:out value="${msgs.instruction_member_import5 }" escapeXml="false" />
	<li><c:out value="${msgs.instruction_member_import6 }" escapeXml="false" />
</ul>
</div>
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
<%-- CSV File Input Form --%>
<c:if test="${userCsvResultModel.notSet}">
 <form:form method="post" id="readMembers" action="importMembers.htm?action=selectCSVFile" modelAttribute="memberImportForm" enctype="multipart/form-data">
	<c:out value="${msgs.msgs_select_import_file}"/>&nbsp;&nbsp;&nbsp;<a href=/nssakura-section-tool/sample.csv target="_blank" class="buttonLink"><c:out value="${msgs.label_sample_csv}"/></a><br/>
	<p class="shorttext">
		<label for="file"><c:out value="${msgs.label_select_import_file} "/></label>
		<form:input type="file" path="file" id="csvfile" />
		<form:errors path="file" />
	</p>
	<p class="shorttext">
		<label for="charCode"><c:out value="${msgs.label_select_char_code} "/></label>
		<form:radiobutton path="charCode" value="utf8"/>utf8 &nbsp;&nbsp;
		<form:radiobutton path="charCode" value="MS932"/>ShiftJIS
	</p>
	<p class="shorttext">
		<label for="override"><c:out value="${msgs.label_select_override} "/></label>
	<c:if test="${isAdmin}">
			<form:radiobutton path="override" value="false" /><c:out value="${msgs.label_donot}" />&nbsp;&nbsp;
			<form:radiobutton path="override" value="true"  /><c:out value="${msgs.label_do}" />
	</c:if>
	<c:if test="${not isAdmin}">
			<form:radiobutton path="override" value="false"  disabled="true"/><c:out value="${msgs.label_donot}"/>&nbsp;&nbsp;
			<form:radiobutton path="override" value="true"  disabled="true" /><c:out value="${msgs.label_do}"/>
	</c:if>
	</p>
	<table class="listHier">
	<tr><td align="left">
	<div class="act">
		<input type="button" name="importButton" id="importButton" value="<c:out value="${msgs.label_import}"/>" />
        <input type="submit" name="cancelButton" value="<c:out value="${msgs.label_cancel}"/>" />
	</div>
	</td></tr></table>
</form:form>
<c:if test="${jobsNum > 0 }">
	<a href=importMembers.htm?menu=jobStatus  class="buttonLink">
		<c:out value="${msgs.menu_title_jobsstatus }"/>
	</a>
</c:if>
</c:if>
<%-- end CSV File Input Form --%>
<%-- CSV Display Form  --%>
<c:if test="${userCsvResultModel.set }">
 <form:form method="post" id="importMembers" action="importMembers.htm?action=exec" modelAttribute="userCsvResultModel">
	<input type="hidden" name="checkCode" value='<c:out value="${checkCode}" />' />
	<input type="hidden" name="override" value='<c:out value="${override}" />' />
	<div class="act">
		<input type="button" name="registerButton" id="registerButton" value="<c:out value="${msgs.label_button_user_import}"/>" />
        <input type="submit" name="cancelButton" value="<c:out value="${msgs.label_cancel}"/>" />
	</div>
	<table id="userCsvList"  class="userCsvList listHier"  cellspacing="0" cellpadding="0"  summary="userList" >
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.label_userid }"/></th>
				<th scope="col"><c:out value="${msgs.label_first_name}"/></th>
				<th scope="col"><c:out value="${msgs.label_last_name }"/></th>
				<th scope="col"><c:out value="${msgs.label_email }"/></th>
				<th scope="col"><c:out value="${msgs.label_password }"/></th>
				<th scope="col"><c:out value="${msgs.label_type }"/></th>
				<th scope="col"><c:out value="${msgs.label_properties }"/></th>
			</tr>
		</thead>
		<tbody>
				<c:forEach var="csvData" items="${userCsvResultModel.userCsvList}">
					<tr>
						<td style="white-space:normal">
							<c:out value="${csvData.eid}"/>
						</td>
						<td style="white-space:normal">
							<c:out value="${csvData.first_name}"/>
						</td>
						<td style="white-space:normal">
							<c:out value="${csvData.last_name}"/>
						</td>
						<td style="white-space:normal">
							<c:out value="${csvData.email}"/>
						</td>
						<td style="white-space:normal">
							<c:out value="${csvData.password}"/>
						</td>
						<td style="white-space:normal">
							<c:out value="${csvData.type}"/>
						</td>
						<td style="white-space:normal">
							<c:forEach var="prop" items="${csvData.propertiesValue}">
								<c:out value="${prop}"/><br/>
							</c:forEach>
						</td>
					</tr>
				</c:forEach>
		</tbody>
	</table>
	<div class="clear"></div>
	<div class="act">
		<input type="button" name="registerButton2" id="registerButton2" value="<c:out value="${msgs.label_button_user_import}"/>" />
        <input type="submit" name="cancelButton" value="<c:out value="${msgs.label_cancel}"/>" />
	</div>
 </form:form>
</c:if>
<%-- end CSV Display Form  --%>
<%--modal --%>
<div class="doalert" style="display:none">
 <c:out value="${msgs.msgs_do_waite}" /><img src="lib/images/doing.gif"/ alt="doing.."/>
</div>
<%-- end modal --%>
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
$(document).ready( function() {
	$( '#userCsvList' ).dataTable({
		"aaSorting":[[0,"asc"]],
		"bLengthChange": false,
		"iDisplayLength": 15,
		"bStateSave": false
	});
});

$('input#importButton').click( function(e) {
	e.preventDefault();
	/* var filemax = $('#maxfilesize').val();
	var msg = $('#fileexceedmsg').val();
	var file = $('input[type="file"]').get(0).files[0];
	if(file.size > filemax){
		alert(msg);
		return;
	}*/
	$('form#readMembers').submit();
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

$('input#registerButton, input#registerButton2').click( function(e) {
	e.preventDefault();
	$('form#importMembers').submit();
	$(".doalert").dialog({
		modal: true,
		draggable: false,
		title: 'Processing..',
		closeOnEscape:false,
		resizable: false,
		position:{
			of : window,
			collision: 'fit',
			at: 'top',
			my: 'center'
		},
		open:function(event,ui){
			$(".ui-dialog-titlebar-close").hide();}
	});
});

// -->
</script>