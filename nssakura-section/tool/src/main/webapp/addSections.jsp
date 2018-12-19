<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="sectionAdd"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<div class="nssectionInstruction">
<form method="post" id="addAutoSections" action=addSections.htm?action=addAutoSections >
	<font color="#cc0033"><c:out value="${msgs.msgs_add_section_rebuild_explain1}" escapeXml="false"/></font><br/>
	<input type="button" name="addAutoButton" id="addAutoButton" value="<c:out value="${msgs.label_add_section_rebuild}"/>" /><br/><br/>
</form>
</div>
<br/>

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

<c:out value="${msgs.label_section_add}" /> </br>

<c:if test="${empty unregisteredSectionNames }">
	<c:out value="${msgs.msgs_no_unregisterd_section }" />
</c:if>
<c:if test="${not empty unregisteredSectionNames }">
<form method="post" id="addSections" action=addSections.htm?action=addSections >

	<table id="sectionList"  class="sectionList listHier"  cellspacing="0" cellpadding="0"  summary="sectionList" >
		<thead>
			<tr>
				<th scope="col"><input type="checkbox" id="toggleAllSelected" onclick="javascript:toggleSelectAll(this,'sectionadd')" /></th>
				<th scope="col"><c:out value="${msgs.label_section_name }"/></th>
			</tr>
		</thead>
		<tbody>
				<c:forEach var="title" items="${unregisteredSectionNames }">
					<tr>
						<td style="white-space:nowrap; padding-left:2em !important;">
							<input type="checkbox" name="sectionadd"  id="sectionadd"  value="<c:out value="${title}"/>">
						</td>
						<td style="white-space:nowrap">
							<c:out value="${title}"/>
						</td>
					</tr>
				</c:forEach>
		</tbody>
	</table>
	<table class="listHier">
	<tr><td align="left">
	<div class="act">
		<input type="submit" name="submitButton" value="<c:out value="${msgs.label_add}"/>" />
        <input type="submit" name="cancelButton" value="<c:out value="${msgs.label_return}"/>" />
	</div>
	</td></tr></table>
</form>
</c:if>
<%--modal --%>
<div class="doalert" style="display:none">
 <c:out value="${msgs.msgs_do_waite}" /><img src="lib/images/doing.gif"/ alt="doing.."/>
</div>
<%-- end modal --%>
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
jQuery( function() {
	jQuery( '#sectionList' ).dataTable({
		"aoColumnDefs": [
			{ "bSortable": false, "aTargets":[0]}
		],
		"aaSorting":[[1,"asc"]],
		"bLengthChange": false,
		"iDisplayLength": 15,
		"bStateSave": false
	});
	});
$('input#addAutoButton').click( function(e) {
	e.preventDefault();
	$('form#addAutoSections').submit();
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
