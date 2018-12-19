<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.section.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="list"/>
<jsp:directive.include file="/templates/navi.jsp"/>

<c:out value="${msgs.label_section_list}" /> </br>

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
	
<c:if test="${empty sections }">
	<c:out value="${msgs.msgs_no_section }" /><br/>
	<c:out value="${msgs.msgs_no_section_explain}" />
</c:if>
<c:if test="${not empty sections }">
<form method="post" id="updateSections" action=index.htm?action=updateSections&from=list >
<%-- <div class="count">
	<c:out value="${membersCountMsg}"/>
</div>--%>
	<table id="sectionList"  class="treetable sectionList listHier"  cellspacing="0" cellpadding="0"  summary="sectionList" >
<caption style="font-size:12px;margin:0 0 8px 0;"><a onclick="jQuery('#sectionList').treetable('expandAll'); return false;" href="#"><img src="/library/image/sakai/dir_openroot.gif"><c:out value="${msgs.label_all_expand}"/></a>&nbsp;&nbsp;&nbsp;<a onclick="jQuery('#sectionList').treetable('collapseAll'); return false;" href="#"><c:out value="${msgs.label_all_close}"/><img src="/library/image/sakai/dir_closed.gif"></a></caption>
		<thead>
			<tr>
				<th scope="col"><c:out value="${msgs.label_section_name }"/></th>
				<th scope="col"><c:out value="${msgs.label_section_ta }"/></th>
				<th scope="col"><c:out value="${msgs.label_section_totalenrollment }"/></th>
				<th scope="col">
				<c:if test="${sectionRemoveEnabled}">
					<input type="checkbox" id="toggleAllSelected" onclick="javascript:toggleSelectAll(this,'sectionremove')" />
				</c:if>

				</th>
					<%-- <th scope="col"><c:out value="${msgs.label_remove}"/></th>--%>
			</tr>
		</thead>
		<tbody>
				<c:forEach var="section" items="${sections }">
					<tr data-tt-id='<c:out value="${section.data_tt_id }"/>' data-tt-parent-id='<c:out value="${section.data_tt_parent_id }"/>'>
						<td style="white-space:normal">
							<span class="folder">
							<c:out value="${section.title }"/></span>
							<span class="sectionAction">
								<c:if test="${taAssignedEnabled}">
								<a href=assignTa.htm?action=init&sectionId=<c:out value="${section.section.uuid }"/> title='<c:out value="${msgs.link_assign_manager}"/>'>
									<img src="/library/image/silk/status_online.png" alt='<c:out value="${msgs.link_assign_manager}"/>'/></a>
									<c:out value=" ${msgs.sep_char } "/>
								</c:if>
								<a href=assignStudent.htm?action=init&sectionId=<c:out value="${section.section.uuid }"/> title='<c:out value="${msgs.link_assign_student}"/>'>
									<img src="/library/image/silk/group.png" alt='<c:out value="${msgs.link_assign_student}"/>'/>
								</a>
							</span>
						</td>
						<td style="white-space:nowrap">
							<c:forEach var="instructorName" items="${section.instructorNames }">
								<c:out value="${instructorName }"/><br>
							</c:forEach>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${section.totalEnrollments }"/>
						</td>
						<td style="white-space:nowrap; padding-left:2em !important;">
						<c:if test="${sectionRemoveEnabled}">
							<input type="checkbox" name="sectionremove"  id="sectionremove"  value="<c:out value="${section.section.uuid }"/>">
						</c:if>
						</td>
					</tr>
				</c:forEach>
		</tbody>
	</table>
	<c:if test="${sectionRemoveEnabled}">
	<table class="listHier">
	<tr><td align="right">
	<div class="act">
		<input type="submit" name="submitButton" value="<c:out value="${msgs.label_section_remove}"/>" />
	</div>
	</td></tr></table>
	</c:if>
</form>
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
