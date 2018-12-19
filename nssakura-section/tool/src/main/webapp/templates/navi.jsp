	<ul class="navIntraTool actionToolBar" role="menu">
		<li role="menuitem" aria-disabled="true">
			<c:if test="${viewName ne 'list'}">
				<a href=index.htm?menu=list >
					<c:out value="${msgs.menu_title_list }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'list'}">
				<span class="current">
					<c:out value="${msgs.menu_title_list }"/>
				</span>
			</c:if>
		</li>
		<c:if test="${memberImportEnabled}">
		<li role="menuitem">
			<c:if test="${viewName ne 'importMember'}">
				<a href=importMembers.htm?menu=init >
					<c:out value="${msgs.menu_title_importmember }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'importMember'}">
				<span class="current">
					<c:out value="${msgs.menu_title_importmember }"/>
				</span>
			</c:if>
		</li>
		</c:if>
		<c:if test="${sectionAddEnabled}">
		<li role="menuitem">
			<c:if test="${viewName ne 'sectionAdd'}">
				<a href=addSections.htm?menu=init >
					<c:out value="${msgs.menu_title_addsection }"/>
				</a>
			</c:if>
			<c:if test="${viewName eq 'sectionAdd'}">
				<span class="current">
					<c:out value="${msgs.menu_title_addsection }"/>
				</span>
			</c:if>
		</li>
		</c:if>
	</ul>
	<br/>