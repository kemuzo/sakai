	<ul class="navIntraTool actionToolBar" role="menu">
		<li role="menuitem" aria-disabled="true">
			<c:if test="${viewName ne 'list'}">
				<a href="top.htm" >
					<c:out value="${msgs.label_learning_status_list }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'list'}">
				<span class="current">
					<c:out value="${msgs.label_learning_status_list }"/>
				</span>
			</c:if>
		</li>
		<c:if test="${instructor || superuser}">
		</c:if>
		<li role="menuitem" aria-disabled="true">
			<c:if test="${viewName ne 'messageIndex'}">
				<a href="messageIndex.htm" >
					<c:out value="${msgs.label_message_notify }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'messageIndex'}">
				<a href="messageCreate.htm" >
					<c:out value="${msgs.label_message_new}"/>
				</a>
			</c:if>
		</li>
	</ul>
	<br/>