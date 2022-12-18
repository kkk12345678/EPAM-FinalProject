<%@ include file="../header.jspf"%>
<!-- The Modal -->
<div id="container">
<div id="add-user-modal">
    <form action="${pageContext.request.contextPath}/admin/add-user" method="post">
        <input class="user-input" type="text" name="firstname" placeholder="<fmt:message key="admin.users.header.firstname"/>" required>
        <input class="user-input" type="text" name="lastname" placeholder="<fmt:message key="admin.users.header.lastname"/>" required>
        <input class="user-input" type="email" name="email" placeholder="<fmt:message key="admin.users.header.email"/>" required>
        <input class="user-input" type="password" name="password" placeholder="<fmt:message key="admin.users.header.password"/>" required>
        <button class="control-button"><fmt:message key="admin.users.button.add"/></button>
    </form>
    <button id="btnCancel" class="control-button"><fmt:message key="admin.users.button.cancel"/></button>
</div>
</div>
<div id="content">
    <%@ include file="../menu.jspf"%>
    <div id="data">
        <table>
            <tbody>
                <tr>
                    <th><fmt:message key="admin.users.header.id"/></th>
                    <th><fmt:message key="admin.users.header.firstname"/></th>
                    <th><fmt:message key="admin.users.header.lastname"/></th>
                    <th><fmt:message key="admin.users.header.email"/></th>
                    <th><fmt:message key="admin.users.header.administrator"/></th>
                    <th><fmt:message key="admin.users.header.blocked"/></th>
                    <th><fmt:message key="admin.users.header.actions"/></th>
                </tr>
                <c:forEach items="${requestScope.users}" var="user">
                <tr>
                    <td class="row-center">${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.email}</td>
                    <td class="row-center">
                    <c:if test="${user.isAdmin == true}">
                        <img src="${pageContext.request.contextPath}/static/img/admin/tick.png" alt="<fmt:message key="admin.users.header.administrator"/>"/>
                    </c:if>
                    </td>
                    <td class="row-center">
                    <c:if test="${user.isBlocked == true}">
                        <img src="${pageContext.request.contextPath}/static/img/admin/tick.png" alt="<fmt:message key="admin.users.header.blocked"/>"/>
                    </c:if>
                    </td>
                    <td class="td-control">
                        <div class="row-control">
                            <c:if test="${user.isAdmin != true}">
                            <form id="delete${user.id}" method="post" action="${pageContext.request.contextPath}/admin/delete-user">
                                <input type="hidden" name="id" value="${user.id}">
                                <img onclick="send('delete', ${user.id}, '<fmt:message key="admin.users.confirm.delete"/>')" title="<fmt:message key="admin.users.control.delete"/>" class="control-img" src="${pageContext.request.contextPath}/static/img/admin/delete.png" alt="<fmt:message key="admin.users.control.delete"/>">
                            </form>
                            <c:if test="${user.isBlocked == true}">
                            <form id="unblock${user.id}" method="post" action="${pageContext.request.contextPath}/admin/unblock-user">
                                <input type="hidden" name="id" value="${user.id}">
                                <img onclick="send('unblock', ${user.id}, '<fmt:message key="admin.users.confirm.unblock"/>')" title="<fmt:message key="admin.users.control.unblock"/>" class="control-img" src="${pageContext.request.contextPath}/static/img/admin/unblock.png" alt="<fmt:message key="admin.users.control.unblock"/>"/>
                            </form>
                            </c:if>
                            <c:if test="${user.isBlocked == false}">
                            <form id="block${user.id}" method="post" action="${pageContext.request.contextPath}/admin/block-user">
                                <input type="hidden" name="id" value="${user.id}">
                                <img onclick="send('block', ${user.id}, '<fmt:message key="admin.users.confirm.block"/>')" title="<fmt:message key="admin.users.control.block"/>" class="control-img" src="${pageContext.request.contextPath}/static/img/admin/block.png" alt="<fmt:message key="admin.users.control.block"/>"/>
                            </form>
                            </c:if>
                            </c:if>
                        </div>
                    </td>
                </tr>
                </c:forEach>
                <tr>
                    <td colspan="7">
                        <div id="add-user">
                            <img id="add-button" src="${pageContext.request.contextPath}/static/img/admin/add.png" alt="<fmt:message key="admin.users.control.add"/>">
                            <p id="add-label"><fmt:message key="admin.users.control.add"/></p>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/user.js"></script>
<%@ include file="../footer.jspf"%>




