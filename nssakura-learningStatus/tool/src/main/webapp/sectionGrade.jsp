<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.learningStatus.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="sectionGrade"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<script type="text/javascript">
$(function(){
	$.blockUI({
		message: 'Now Loading',
		css: {
			border: 'none',
			padding: '10px',
			backgroundColor: '#333',
			opacity: .5,
			color: '#fff'
		},
		overlayCSS: {
			backgroundColor: '#000',
			opacity: 0.6
		}
	});
});
</script>
<%-- Instruction --%>
<div class="nssectionInstruction">
	<c:out value="${msgs.msgs_instruction_2}" escapeXml="false"/><br/><br/>
	<img src="/library/image/silk/user_delete.png" alt="imperfect_list"/><c:out value="${msgs.msgs_instruction_3}" escapeXml="false"/><br/>
</div>
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

<c:if test="${not empty gradeData}">
<div class="csvout">
	<a href="csvoutStudent.htm?sectionId=<c:out value='${sectionId}'/>"  class="buttonLink" >
		<c:out value="${msgs.label_csv_out }"/>
	</a>
</div>
<b><c:out value="${sectionTitle }"/></b>
<c:if test="${not empty tas}">
	(
	<c:forEach var="ta" items="${tas}">
	<c:out value="${ta.displayName} &nbsp;"  escapeXml="false"/>	
	</c:forEach>
	)
</c:if>
	<table id="userList"  class="display" summary="userList" >
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.label_user_id}"/></th>
				<th scope="col"><c:out value="${msgs.label_user_name}"/></th>
				<c:forEach var="assignment" items="${assignments}">
					<th scope="col"><c:out value="${assignment.itemName}"/><br>
					<div id="impertfectList"><a href="javascript:openNonexecMember('${sectionId}','${assignment.itemId}','${msgs.msgs_confirm_csv_noexec}');">
					<img src="/library/image/silk/user_delete.png" alt="imperfect_list"/></a></div>
					</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
				<c:forEach var="userdata" items="${gradeData }"  varStatus="stat">
					<tr >
						<td style="white-space:normal;">
							<c:out value="${userdata.userId}"/>
						</td>
						<td style="white-space:normal;">
							<c:out value="${userdata.userName}"/>
						</td>
						<c:forEach var="assignment" items="${userdata.assignmentItems }">
						<td style="white-space:nowrap;text-align:right">
							<c:out value="${assignment.points}"/>
						</td>
						</c:forEach>
					</tr>
				</c:forEach>
		</tbody>
	</table>
</c:if>
<c:if test="${empty gradeData}">
	<c:out value="${msgs.msgs_no_user }" /><br/>
</c:if>
<jsp:directive.include file="/templates/footer.jsp"/>

<script>
<!-- 
jQuery('.dataTable').wrap('<div class="scrollStyle" />');
$(document).ready( function() {
	$( '#userList' ).dataTable( {
        "scrollY":        "400px",
        "scrollCollapse": true,
        "paging":         false
    } );
});

$(function(){
	$.unblockUI();
});

$(function(){
	$('#impertfectList a').click(function(e){
		e.stopPropagation();
	});
});
function  openNonexecMember(var1, var2,mess){
	if(window.confirm(mess)){
		var url1 = "csvoutNonexec.htm?sectionId=" + var1 + "&assignmentId=" + var2; 
		window.open(url1,'nonexecmember','width=580, height=400,scrollbars=yes,status=yes,directories=no,menubar=no,resizable=yes,toolbar=no');
	}
}
// -->
</script>