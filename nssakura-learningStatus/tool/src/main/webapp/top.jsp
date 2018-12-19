<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.learningStatus.bundle.messages" />
</jsp:useBean>
<c:set var="viewName" value="list"/>
<jsp:directive.include file="/templates/navi.jsp"/>
<c:if test="${sectionNum < 1}">
	<div class="nsvalidation">
		<c:out value="${msgs.msgs_no_section}"  escapeXml="false"/>
	</div>
</c:if>
<c:if test="${sectionNum > 0}">
<form>
	<div class="act">
		<input type="button" name="totalize" id="totalize" value="<c:out value="${msgs.label_totalize_button}"/>" />
	</div>
</form>
<div class="memo" id="memo">
<c:out value="${msgs.msgs_totalizing }" /><img src="/nssakura-learningStatus-tool/images/doing.gif" alt="doing..."/>
</div>
</c:if>
<div class="modalBodyDummy">&nbsp</div>
<jsp:directive.include file="/templates/footer.jsp"/>
<script type="text/javascript">
	$(function() {
		$("#memo").css("display","none");
		$("input:button").click(function(){
			$(this).attr('disabled',true);
			$("#memo").css("display","block");
			$.ajax({
				type : "GET",
				url : "getJson.htm", 
				dataType : "json",
				cache : false,
				success : function(data, status, xhr) {
					window.location.href = 'index.htm';
				},
				error : function(XMLHttpRequest, status, errorThrown) {
					alert("fail:" + XMLHttpRequest);
					alert("status:" + status);
				}
			});
		});
	});
</script>
