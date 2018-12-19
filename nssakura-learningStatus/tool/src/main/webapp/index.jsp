<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.learningStatus.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="list"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<%-- Instruction --%>
<div class="nssectionInstruction">
	<c:out value="${msgs.msgs_instruction_1}" escapeXml="false"/>
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

<c:if test="${not empty gradeCourse}">
<div class="csvout">
	<a href="csvout.htm"  class="buttonLink" >
		<c:out value="${msgs.label_csv_out }"/>
	</a>
</div>
	<table id="sectionList"  class="treetable"  cellspacing="0" cellpadding="0"  summary="sectionList" >
<caption style="font-size:12px;margin:0 0 8px 0;">
	<a onclick="jQuery('#sectionList').treetable('expandAll'); return false;" href="#">
		<img src="/library/image/sakai/dir_openroot.gif"><c:out value="${msgs.label_all_expand}"/></a>&nbsp;&nbsp;&nbsp;
	<a onclick="jQuery('#sectionList').treetable('collapseAll'); return false;" href="#"><c:out value="${msgs.label_all_close}"/>
		<img src="/library/image/sakai/dir_closed.gif"></a>&nbsp;&nbsp;&nbsp;
	<c:out value="${gradeCourse.dateStr}"/>
</caption>
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.label_section_name}"/></th>
				<th scope="col"><c:out value="${msgs.label_number_of_students}"/></th>
				<c:forEach var="title" items="${gradeCourse.titleList}">
					<th scope="col"><c:out value="${title}"/></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
				<c:forEach var="section" items="${gradeCourse.sections }"  varStatus="stat">
					<tr data-tt-id='<c:out value="${section.tableId }"/>' data-tt-parent-id='<c:out value="${section.parentTableId }"/>'>
						<td style="white-space:normal; min-width:270px">
							<c:if test="${stat.first}">
								<c:out value="${msgs.label_all_students}"/>
							</c:if>
							<c:if test="${not stat.first}">
								<span class="folder">
								<a href="sectionGrade.htm?sectionId=<c:out value='${section.sectionId}'/>">
								<c:out value="${section.title }"/></a>
								</span>
							</c:if>
						</td>
						<td style="white-space:nowrap; text-align:right">
							<c:out value="${section.allNum }"/>
						</td>
						<c:forEach var="assignment" items="${section.assignmentItems }">
						<td style="white-space:nowrap;text-align:right">
							<c:out value="${assignment.executeNumStr }"/>
						</td>
						</c:forEach>
					</tr>
					<c:if test="${stat.first}">
					<tr><td></td></tr>
					</c:if>
				</c:forEach>
		</tbody>
	</table>
</c:if>
<c:if test="${empty gradeCourse}">
	<c:out value="${msgs.msgs_no_section }" /><br/>
</c:if>
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!-- 
jQuery( function() {
	jQuery( '#sectionList' ).treetable({expandable: true});
	jQuery('#sectionList').treetable('expandAll'); 
	});


// -->
</script>
