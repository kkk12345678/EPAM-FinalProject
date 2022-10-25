<%@ include file="header1.jspf"%>
<title><fmt:message key="user.cart.title"/></title>
<%@ include file="header2.jspf"%>
<c:if test="${sessionScope.cart.size() == 0}">
<div id="empty-cart">
    <h2><fmt:message key="cart.empty.message"/></h2>
    <button class="control-button" onclick="window.location.href = '${pageContext.request.contextPath}/shop'"><fmt:message key="cart.button.continue"/></button>
</div>
</c:if>
<c:if test="${sessionScope.cart.size() > 0}">
<div id="cart">
    <table id="cart-table">
        <tbody>
        <tr>
            <th><fmt:message key="cart.header.number"/></th>
            <th><fmt:message key="cart.header.title"/></th>
            <th><fmt:message key="cart.header.price"/></th>
            <th><fmt:message key="cart.header.quantity"/></th>
            <th><fmt:message key="cart.header.sum"/></th>
            <th><fmt:message key="cart.header.actions"/></th>
        </tr>
        <c:forEach items="${requestScope.cart}" var="line">
            <tr>
                <td><c:forEach items="${line.key.names}" var="entry"><c:if test="${entry.key == languageId}">${entry.value}</c:if></c:forEach></td>
                <td class="price">${line.key.price}</td>
                <td class="quantity">${line.value}</td>
                <td class="sum"></td>
                <td class="td-control">
                    <div class="row-control">
                        <img id="${line.key.id}-delete" class="delete" src="<c:url value="/static/img/admin/delete.png"/>" alt="" title="<fmt:message key="cart.control.delete"/>">
                        <img id="${line.key.id}-plus" class="plus" src="<c:url value="/static/img/admin/add.png"/>" alt="" title="<fmt:message key="cart.control.plus"/>">
                        <img id="${line.key.id}-minus" class="minus" src="<c:url value="/static/img/admin/minus.png"/>" alt="" title="<fmt:message key="cart.control.minus"/>">
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <h3 id="total"><fmt:message key="cart.label.total"/> :</h3>
    <div id="control">
        <div id="back">
            <button class="control-button" onclick="window.location.href = '${pageContext.request.contextPath}/shop'"><fmt:message key="cart.button.continue"/></button>
        </div>
        <div id="add-to-cart">
            <c:if test="${sessionScope.user == null}"><button class="control-button" onclick="window.location.href='${pageContext.request.contextPath}/login'"><fmt:message key="cart.button.login"/></button></c:if>
            <c:if test="${sessionScope.user != null && sessionScope.user.isBlocked == false}"><button class="control-button" onclick="placeOrder()"><fmt:message key="cart.button.place.order"/></button></c:if>
            <c:if test="${sessionScope.user != null && sessionScope.user.isBlocked == true}"><button class="control-button" onclick="alert('<fmt:message key="user.message.user.blocked"/>')"><fmt:message key="cart.button.place.order"/></button></c:if>
        </div>
    </div>
</div>
</c:if>
<form id="form" method="post" action="${pageContext.request.contextPath}/order"><input id="input" type="hidden" name="total"></form>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/cart.js"></script>
<%@ include file="footer.jspf"%>