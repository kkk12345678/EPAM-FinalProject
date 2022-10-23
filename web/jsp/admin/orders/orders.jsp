<%@ include file="../header.jspf"%>

<div id="content">
    <%@ include file="../menu.jspf"%>
    <div id="data">
        <div id="filter">
            <div class="filter-group">
                <label><fmt:message key="admin.orders.filter.label.user"/>:
                    <input id="user" name="user" type="text">
                </label>
                <label><fmt:message key="admin.orders.filter.label.sum"/>:
                    <input id="sum" name="sum" type="number">
                </label>
            </div>
            <div class="filter-group">
                <label><fmt:message key="admin.orders.filter.label.date"/>
                    <select id="category" name="category">
                        <option selected value=""><fmt:message key="admin.books.all.categories"/></option>
                        <c:forEach items="${requestScope.categories}" var="category">
                        <option value="${category.id}">${category.tag}</option>
                        </c:forEach>
                    </select>
                </label>
                <label><fmt:message key="admin.orders.filter.label.status"/>
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
        <div id="orders">
            <table>
                <tbody>
                <tr>
                    <th><fmt:message key="admin.orders.header.number"/></th>
                    <th><fmt:message key="admin.orders.header.client"/></th>
                    <th><fmt:message key="admin.orders.header.total"/></th>
                    <th><fmt:message key="admin.orders.header.status"/></th>
                    <th><fmt:message key="admin.orders.header.date"/></th>

                </tr>
                <c:forEach items="${requestScope.orders}" var="order">
                <tr class="order" id="row-${order.id}" onclick="makeVisible(${order.id})">
                    <td class="row-center">${order.id}</td>
                    <td>${order.user.firstName} ${order.user.lastName}</td>
                    <td class="row-right">${order.total}</td>
                    <td class="row-right">
                        <form action="${pageContext.request.contextPath}/update-status" method="post">
                            <input type="hidden" name="order_id" value="${order.id}">
                            <input type="hidden" name="page" value="${pageContext.request.contextPath}/admin/orders?<c:forEach items="${param}" var="entry">${entry.key}=${entry.value}&</c:forEach>">
                            <div class="status">
                                <p>${order.status.name}</p>
                                <select name="status_id" onchange="this.form.submit()">
                                    <option disabled selected><fmt:message key="admin.orders.label.change"/></option>
                                    <c:forEach items="${requestScope.statuses}" var="status">
                                    <option value="${status.id}">${status.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </form>
                    </td>
                    <td class="row-center">${order.dateAdded}</td>
                </tr>
                <tr class="details" id="details-${order.id}">
                    <td colspan="5">
                    <table>
                        <tbody>
                        <tr><th><fmt:message key="admin.orders.label.title"/></th><th><fmt:message key="admin.orders.label.quantity"/></th></tr>
                        <c:forEach items="${order.details}" var="entry">
                        <tr>
                            <td>
                            <c:forEach items="${entry.key.titles}" var="title">
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
<%@ include file="../footer.jspf"%>

