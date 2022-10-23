<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="header.jspf"%>

<div id="welcome">
    <h1>
        <fmt:message key="admin.welcome"/>, ${requestScope.user.firstName} ${requestScope.user.lastName}
        (<a href="${pageContext.request.contextPath}/logout"><fmt:message key="admin.logout"/></a>)
    </h1>
    <div class="admin-row">
        <div class="admin-item">
            <h3><fmt:message key="admin.publishers.count"/>:</h3>
            <p class="counter">${requestScope.publishersCount}</p>
            <a href="${pageContext.request.contextPath}/admin/publishers"><fmt:message key="admin.nav.publishers"/></a>
        </div>
        <div class="admin-item">
            <h3><fmt:message key="admin.categories.count"/>:</h3>
            <p class="counter">${requestScope.categoriesCount}</p>
            <a href="${pageContext.request.contextPath}/admin/categories"><fmt:message key="admin.nav.categories"/></a>
        </div>
        <div class="admin-item">
            <h3><fmt:message key="admin.books.count"/>:</h3>
            <p class="counter">${requestScope.booksCount}</p>
            <a href="${pageContext.request.contextPath}/admin/books"><fmt:message key="admin.nav.books"/></a>
        </div>
    </div>
    <div class="admin-row">
        <div class="admin-item">
            <h3><fmt:message key="admin.users.count"/>:</h3>
            <p class="counter">${requestScope.usersCount}</p>
            <a href="${pageContext.request.contextPath}/admin/users"><fmt:message key="admin.nav.users"/></a>
        </div>
        <div class="admin-item">
            <h3><fmt:message key="admin.orders.count"/>:</h3>
            <p class="counter">${requestScope.ordersCount}</p>
            <a href="${pageContext.request.contextPath}/admin/orders"><fmt:message key="admin.nav.orders"/></a>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/locale.js"></script>
</body>
</html>
