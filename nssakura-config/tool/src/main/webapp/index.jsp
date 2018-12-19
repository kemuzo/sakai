<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>

<a href="new.html">${msgs.label_new}</a>
<table>
  <thead>
    <tr>
      <td>${msgs.label_name}</td>
      <td>${msgs.label_value}</td>
      <td>${msgs.label_description}</td>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="cfg" items="${configurations}">
      <tr>
        <td><a href="edit.html?name=${cfg.name}">${cfg.name}</a></td>
        <td>${cfg.value}</td>
        <td>${cfg.description}</td>
      </tr>
    </c:forEach>
  </tbody>
</table>
<jsp:directive.include file="/templates/footer.jsp"/>

