<%@ include file="../header.jspf"%>

<div id="content">
    <%@ include file="../menu.jspf"%>
    <div id="data">
        <table>
            <tbody>
            <tr>
                <th><fmt:message key="admin.publishers.header.id"/></th>
                <th><fmt:message key="admin.publishers.header.title"/></th>
                <th><fmt:message key="admin.publishers.header.actions"/></th>
            </tr>
            <c:forEach items="${requestScope.publishers}" var="publisher">
            <tr>
                <td class="row-center">${publisher.id}</td>
                <td><c:forEach items="${publisher.names}" var="entry"><c:if test="${entry.key == languageId}">${entry.value}</c:if></c:forEach></td>
                <td class="td-control">
                    <div class="row-control">
                        <a class="edit-link" href="${pageContext.request.contextPath}/admin/edit-publisher?id=${publisher.id}">
                            <img class="edit-img" src="${pageContext.request.contextPath}/static/img/admin/edit.png" alt="<fmt:message key="admin.publishers.control.edit"/>" title="<fmt:message key="admin.publishers.control.edit"/>">
                        </a>
                        <form id="delete${publisher.id}" method="post" action="${pageContext.request.contextPath}/admin/delete-publisher">
                            <input type="hidden" name="id" value="${publisher.id}">
                            <img onclick="send('delete', ${publisher.id}, '<fmt:message key="admin.publishers.confirm.delete"/>')" class="control-img" src="${pageContext.request.contextPath}/static/img/admin/delete.png" alt="<fmt:message key="admin.publishers.control.delete"/>" title="<fmt:message key="admin.publishers.control.delete"/>">
                        </form>
                    </div>
                </td>
            </tr>
            </c:forEach>
            <tr>
                <td colspan="3">
                    <div id="add-publisher">
                        <img id="add-button" src="${pageContext.request.contextPath}/static/img/admin/add.png" alt="<fmt:message key="admin.publishers.control.add"/>">
                        <p id="add-label"><fmt:message key="admin.publishers.control.add"/></p>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
        <div id="import-csv">
            <h3><fmt:message key="admin.publishers.control.import"/></h3>
            <form action="${pageContext.request.contextPath}/admin/import-publishers" method="post" enctype="multipart/form-data">
                <input type="file" accept=".csv" name="file" required id="file">
                <input type="submit" class="control-button" value="<fmt:message key="admin.publishers.button.import"/>">
            </form>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/publisher.js"></script>
<%@ include file="../footer.jspf"%>