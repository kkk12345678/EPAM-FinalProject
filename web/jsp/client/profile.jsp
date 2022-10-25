<%@ include file="header1.jspf"%>
<title><fmt:message key="user.profile.title"/> ${sessionScope.user.firstName} ${sessionScope.user.lastName}</title>
<%@ include file="header2.jspf"%>


<div id="data">
    <h1><fmt:message key="user.welcome"/>, ${sessionScope.user.firstName} ${sessionScope.user.lastName}</h1>
    <div id="pages">
        <m:pages current="${requestScope.currentPage}" total="${requestScope.totalPages}"/>
    </div>
    <div id="orders">
        <table>
            <tbody>
            <tr>
                <th><fmt:message key="user.orders.header.number"/></th>
                <th><fmt:message key="user.orders.header.date"/></th>
                <th><fmt:message key="user.orders.header.total"/></th>
                <th><fmt:message key="user.orders.header.status"/></th>
            </tr>
            <c:forEach items="${requestScope.orders}" var="order">
                <tr class="order" id="row-${order.id}" onclick="makeVisible(${order.id})">
                    <td class="row-center">${order.id}</td>
                    <td class="row-center">${order.dateAdded}</td>
                    <td class="row-right">${order.total}</td>
                    <td class="row-center">${order.status.name}</td>
                </tr>
                <tr class="details" id="details-${order.id}">
                    <td colspan="5">
                        <table>
                            <tbody>
                            <tr><th><fmt:message key="user.orders.label.title"/></th><th><fmt:message key="user.orders.label.quantity"/></th></tr>
                            <c:forEach items="${order.details}" var="entry">
                                <tr>
                                    <td>
                                        <c:forEach items="${entry.key.names}" var="title">
                                            <c:if test="${title.key == languageId}">${title.value}</c:if>
                                        </c:forEach>
                                    </td>
                                    <td class="row-center">${entry.value}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/order.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/pagination.js"></script>
<%@ include file="footer.jspf"%>