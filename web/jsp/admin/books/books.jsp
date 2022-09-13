<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ include file="../admin-header.jspf"%>
<%@ include file="../catalogue-header.jspf"%>



<div id="content">
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
                <th width="10%"><fmt:message key="admin.books.header.actions"/></th>
            </tr>
            <c:forEach items="${requestScope.books}" var="book">
            <tr>
                <td class="row-center">${book.id}</td>
                <td class="row-center"><img src="${pageContext.request.contextPath}/static/img/product/${book.isbn}.jpg" width="40px" alt="${book.tag}"></td>
                <td>${book.tag}</td>
                <td>${book.isbn}</td>
                <td class="row-right">${book.price}</td>
                <td class="row-center">${book.quantity}</td>
                <td>
                    <div class="row-control">
                        <a class="edit-link" href="${pageContext.request.contextPath}/admin/edit-book?id=${book.id}">
                            <img class="edit-img" src="${pageContext.request.contextPath}/static/img/admin/edit.png" alt="<fmt:message key="admin.books.control.edit"/>">
                        </a>
                        <a class="delete-link" href="${pageContext.request.contextPath}/admin/delete-book?id=${book.id}">
                           <img class="delete-img" src="${pageContext.request.contextPath}/static/img/admin/delete.png" alt="<fmt:message key="admin.books.control.delete"/>">
                        </a>
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
    </div>
</div>

<%@ include file="book-footer.jspf"%>
<%@ include file="../admin-footer.jspf"%>

