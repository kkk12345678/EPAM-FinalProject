<%@ include file="../header.jspf"%>

<div id="content">
    <%@ include file="../menu.jspf"%>
    <div id="data">
        <div id="filter">
            <div class="filter-group">
                <label>Tag:
                    <input id="tag" name="tag" type="text">
                </label>
                <label>ISBN:
                    <input id="isbn" name="isbn" type="text">
                </label>
            </div>
            <div class="filter-group">
                <label><fmt:message key="admin.books.edit.category"/>
                    <select id="category" name="category">
                        <option selected value=""><fmt:message key="admin.books.all.categories"/></option>
                        <c:forEach items="${requestScope.categories}" var="category">
                        <option value="${category.id}">${category.tag}</option>
                        </c:forEach>
                    </select>
                </label>
                <label><fmt:message key="admin.books.edit.publisher"/>
                    <select id="publisher" name="publisher">
                        <option selected value=""><fmt:message key="admin.books.all.publishers"/></option>
                        <c:forEach items="${requestScope.publishers}" var="publisher">
                        <option value="${publisher.id}">${publisher.tag}</option>
                        </c:forEach>
                    </select>
                </label>
            </div>
            <button id="button-filter" class="control-button"><fmt:message key="admin.books.button.filter"/></button>
        </div>
    <div id="pages">
        <m:pages current="${requestScope.currentPage}" total="${requestScope.totalPages}"/>
    </div>
    <div id="books">
        <table>
            <tbody>
            <tr>
                <th><fmt:message key="admin.books.header.id"/></th>
                <th><fmt:message key="admin.books.header.image"/></th>
                <th><fmt:message key="admin.books.header.tag"/></th>
                <th><fmt:message key="admin.books.header.isbn"/></th>
                <th><fmt:message key="admin.books.header.price"/></th>
                <th><fmt:message key="admin.books.header.quantity"/></th>
                <th><fmt:message key="admin.books.header.actions"/></th>
            </tr>
            <c:forEach items="${requestScope.books}" var="book">
            <tr>
                <td class="row-center">${book.id}</td>
                <td class="row-center"><img src="${pageContext.request.contextPath}/static/img/product/${book.isbn}.jpg" width="40px" alt="${book.tag}"></td>
                <td>${book.tag}</td>
                <td>${book.isbn}</td>
                <td class="row-right"><fmt:formatNumber value="${book.price}" minFractionDigits = "2" pattern="### ###.##"/></td>
                <td class="row-center">${book.quantity}</td>
                <td class="td-control">
                    <div class="row-control">
                        <a class="edit-link" href="${pageContext.request.contextPath}/admin/edit-book?id=${book.id}">
                            <img class="edit-img" src="${pageContext.request.contextPath}/static/img/admin/edit.png" alt="<fmt:message key="admin.books.control.edit"/>" title="<fmt:message key="admin.books.control.edit"/>">
                        </a>
                        <form id="delete${book.id}" method="post" action="${pageContext.request.contextPath}/admin/delete-book">
                            <input type="hidden" name="id" value="${book.id}">
                            <input type="hidden" name="page" value="${requestScope.currentPage}">
                            <img onclick="send('delete', ${book.id}, '<fmt:message key="admin.books.confirm.delete"/>')" class="control-img" src="${pageContext.request.contextPath}/static/img/admin/delete.png" alt="<fmt:message key="admin.books.control.delete"/>" title="<fmt:message key="admin.books.control.delete"/>">
                        </form>
                    </div>
                </td>
            </tr>
            </c:forEach>
            <tr>
                <td colspan="7">
                    <div id="add-book">
                        <img id="add-button" src="${pageContext.request.contextPath}/static/img/admin/add.png" alt="<fmt:message key="admin.books.control.add"/>">
                        <p id="add-label"><fmt:message key="admin.books.control.add"/></p>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
        <div id="import-csv">
            <h3><fmt:message key="admin.books.control.import"/></h3>
            <form action="${pageContext.request.contextPath}/admin/import-books" method="post" enctype="multipart/form-data">
                <input type="file" accept=".csv" name="file" required id="file">
                <input type="submit" class="control-button" value="<fmt:message key="admin.books.button.import"/>">
            </form>
        </div>
    </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/book.js"></script>
<%@ include file="../footer.jspf"%>

