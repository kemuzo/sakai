<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.learningStatus.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="messageCreate"/>
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

<form method="post" id="sendMessage" action="sendMessage.htm" modelAttribute="mailForm">
	<div class="section">
	<div class="headerkeyholder"><c:out value="${msgs.labe_from}"/></div>
	<div class="headervalue"><c:out value="${sender}"/></div>
	<div class="headerkeyholder"><form:label path="mailForm.to"><c:out value="${msgs.label_to}"/></form:label></div>
	<div class="headervalue">
		<c:if test="${instructor || superuser}">
		<div id="rolePush" class="rolesection"><c:out value="${msgs.label_role}"/></div>
		</c:if>
		<div id="sectionPush" class="rolesection"><c:out value="${msgs.label_section}"/></div>
		<c:if test="${instructor || superuser}">
		<div id="roleBox">
			<div class="nssectionInstruction">
				<c:out value="${msgs.msgs_recipents_explain}"/><br/>
				<c:out value="${msgs.msgs_recipents_explain2}"/><br/>
				<c:out value="${msgs.msgs_recipents_explain3}"/>
			</div>
			<form:checkboxes items="${roles}" path="mailForm.roles"/>
		</div>
		</c:if>
		<div id="sectionBox">
			<div class="nssectionInstruction">
				<c:out value="${msgs.msgs_recipents_explain}"/><br/>
				<c:out value="${msgs.msgs_recipents_explain2}"/><br/>
				<c:out value="${msgs.msgs_recipents_explain3}"/>
			</div>
			<table id="sectionList"  class="treetable"  cellspacing="0" cellpadding="0"  summary="sectionList" >
				<c:forEach var="section" items="${sections }">
					<tr data-tt-id='<c:out value="${section.data_tt_id }"/>' data-tt-parent-id='<c:out value="${section.data_tt_parent_id}"/>'>
					<td><form:checkbox id="${section.data_tt_id }" value="${section.section.uuid }" path="mailForm.sections"/><c:out value="${section.title}"/></td></tr>
				</c:forEach>
			</table>
		</div>
		<div><c:out value="${msgs.label_personal}"/></div>
		<div><form:textarea name="to" id="to" path="mailForm.to" cols="40" rows="3"></form:textarea></div>
	</div>
	</div>
	<div class="section">
	<div class="headerkeyholder"><form:label path="mailForm.subject"><c:out value="${msgs.label_subject}"/></form:label></div>
	<div class="headervalue"><form:input type="text" name="subject" id="subject" path="mailForm.subject" size="100" maxlength="255"/></div>
	</div>
	<div class="section">
		<form:errors path="*" element="div" />
	</div>
	<div class="section">
	<form:label path="mailForm.content"><c:out value="${msgs.label_content}"/></form:label>
	<form:textarea name="content" id="content" rows="30" cols="80" wrap="virtual" path="mailForm.content"></form:textarea>
	<script type="text/javascript" defer="1">
	CKEDITOR.replace('content',{
		toolbar:[
		     	{ name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Save', 'NewPage', 'Preview', 'Print', '-', 'Templates' ] },
		    	{ name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
		    	{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },
		    	'/',
		    	{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },
		    	{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl', 'Language' ] },
		    	{ name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
		    	'/',
		    	{ name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
		    	{ name: 'colors', items: [ 'TextColor', 'BGColor' ] },
		    	{ name: 'tools', items: [ 'Maximize', 'ShowBlocks' ] },
		    	{ name: 'others', items: [ '-' ] },
		    	{ name: 'about', items: [ 'About' ] }
		         ]
	});
	sakai.editor.launch('content',{height:300, toolbarSet:'Basic'});
	</script>
	</div>
	<input type="submit" name="submitButton" value="<c:out value="${msgs.label_send}"/>" />
</form>
<jsp:directive.include file="/templates/footer.jsp"/>
<script type="text/javascript">
$(function(){
	$("#roleBox").css("display","none");
	$("#sectionBox").css("display","none");
	
	$("#rolePush").click(function(){
		$("#roleBox").toggle();
		$("#sectionBox").css("display","none");
	});

	$("#sectionPush").click(function(){
		$("#sectionBox").toggle();
		$("#roleBox").css("display","none");
	});
});

$(function(){
	$("#sectionBox :checkbox").click(function(){
		varid=$(this).attr("id");
		varchecked = $(this).is(":checked");
		$("#sectionBox :checkbox").each(function(){
			if($(this).attr("id").indexOf(varid)==0 && $(this).attr("id")!=varid){
				$(this).prop("checked", false);
				if(varchecked){
					$(this).prop("disabled", true);
				}else{
					$(this).prop("disabled", false);
				}
			}
		});
	});
});

jQuery( function() {
	jQuery( '#sectionList' ).treetable({expandable: true});
	});
</script>
