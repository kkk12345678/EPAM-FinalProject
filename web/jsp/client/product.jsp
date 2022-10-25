<%@ include file="header1.jspf"%>
<c:forEach items="${requestScope.book.names}" var="entry">
    <c:if test="${entry.key == languageId}"><c:set var="title" value="${entry.value}"/></c:if>
</c:forEach>
<c:forEach items="${requestScope.book.descriptions}" var="entry">
    <c:if test="${entry.key == languageId}"><c:set var="description" value="${entry.value}"/></c:if>
</c:forEach>
<title><c:out value="${title}"/></title>
<%@ include file="header2.jspf"%>
<div>
    <div>
        <h1><fmt:message key="user.label.title"/>: <c:out value="${title}"/></h1>
    </div>
    <div id="book">
        <div id="book-image">
            <img class="book-img" id="${requestScope.book.isbn}" alt="${title}" width="300px" src="<c:url value="/static/img/product/no-image.jpg"/>">
        </div>
        <div id="book-details">
            <h3>ISBN:</h3><p>${requestScope.book.isbn}</p>
            <h3><fmt:message key="user.label.quantity"/>:</h3><p>${requestScope.book.quantity}</p>
            <h3><fmt:message key="user.label.price"/>:</h3><p>${requestScope.book.price}</p>
            <h3><fmt:message key="user.label.description"/>:</h3><p><c:out value="${description}"/></p>
        </div>
    </div>
    <div id="control">
        <div id="back">
            <button class="control-button" onclick="window.location.href = '${pageContext.request.contextPath}/shop'"><fmt:message key="product.button.back"/></button>
        </div>
        <div id="add-to-cart">
            <input type="number" id="quantity" value="1" min="1">
            <button class="control-button" onclick="addToCart(${requestScope.book.id}, ${requestScope.book.quantity}, '<fmt:message key="user.message.not.enough.products"/>')"><fmt:message key="product.button.add.to.cart"/></button>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/book-front.js"></script>

<%@ include file="footer.jspf"%>