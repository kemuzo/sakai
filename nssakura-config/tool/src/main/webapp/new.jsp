<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<script type="text/javascript">
$(function(){
	$("#entry_button").click(function(){
		submit("entry");
		return;
	});
	
	$("#cancel_button").click(function(){
		location.href="index.html";
	});
	
	function submit(command) {
		var $input = $('<input>', { type: 'hidden', name: 'command', value: command });
		$('#entryForm').append($input);
		$('#entryForm').submit();
	}
});
</script>
<c:if test="${not empty errorMessage}">
  <div class="validation">
    <c:out value="${errorMessage}"/>
  </div>
</c:if>
<form:form id="entryForm" action="registration.html" method="POST" modelAttribute="config">
  <ul>
    <li>
      <label for="name">${msgs.label_name}</label>
      <form:input id="name" path="name"/>
    </li>
    <li>
      <label for="value">${msgs.label_value}</label>
      <form:input id="value" path="value"/>
    </li>
    <li>
      <label for="description">${msgs.label_description}</label>
      <form:textarea rows="3" id="description" path="description"/>
    </li>
    <li>
      <input type="submit" id="entry_button" value="${msgs.button_entry}"/>
      <input type="button" id="cancel_button" value="${msgs.button_cancel}"/>
    </li>
  </ul>
</form:form>
<jsp:directive.include file="/templates/footer.jsp"/>
