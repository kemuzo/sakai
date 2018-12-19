<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<c:if test="${not empty errorMessage}">
  <div class="validation">
    <c:out value="${errorMessage}"/>
  </div>
</c:if>
<jsp:directive.include file="/templates/footer.jsp"/>

