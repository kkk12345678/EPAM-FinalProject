<%@ include file="../header.jspf"%>
<div id="content">
    <%@ include file="../menu.jspf"%>
    <div id="data">
        <table>
            <tbody>
            <tr>
                <th><fmt:message key="admin.categories.header.id"/></th>
                <th><fmt:message key="admin.categories.header.tag"/></th>
                <th><fmt:message key="admin.categories.header.actions"/></th>
            </tr>
            <c:forEach items="${requestScope.categories}" var="category">
            <tr>
                <td class="row-center">${category.id}</td>
                <td>${category.tag}</td>
                <td class="td-control">
                    <div class="row-control">
                        <a class="edit-link" href="${pageContext.request.contextPath}/admin/edit-category?id=${category.id}">
                            <img class="edit-img" src="${pageContext.request.contextPath}/static/img/admin/edit.png" alt="<fmt:message key="admin.categories.control.edit"/>" title="<fmt:message key="admin.categories.control.edit"/>">
                        </a>
                        <form id="delete${category.id}" method="post" action="${pageContext.request.contextPath}/admin/delete-category">
                            <input type="hidden" name="id" value="${category.id}">
                            <img onclick="send('delete', ${category.id}, '<fmt:message key="admin.categories.confirm.delete"/>')" class="control-img" src="${pageContext.request.contextPath}/static/img/admin/delete.png" alt="<fmt:message key="admin.categories.control.delete"/>" title="<fmt:message key="admin.categories.control.delete"/>">
                        </form>
                    </div>
                </td>
            </tr>
            </c:forEach>
            <tr>
                <td colspan="3">
                    <div id="add-category">
                        <img id="add-button" src="${pageContext.request.contextPath}/static/img/admin/add.png" alt="<fmt:message key="admin.categories.control.add"/>">
                        <p id="add-label"><fmt:message key="admin.categories.control.add"/></p>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
        <div id="import-csv">
            <h3><fmt:message key="admin.categories.control.import"/></h3>
            <form action="${pageContext.request.contextPath}/admin/import-categories" method="post" enctype="multipart/form-data">
                <input type="file" accept=".csv" name="file" required id="file">
                <input type="submit" class="control-button" value="<fmt:message key="admin.categories.button.import"/>">
            </form>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/category.js"></script>
<%@ include file="../footer.jspf"%>