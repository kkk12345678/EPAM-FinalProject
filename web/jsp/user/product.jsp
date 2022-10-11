<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/mytags.tld" prefix="m" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.locale}"/>
<c:if test="${sessionScope.locale == null}"><fmt:setLocale value="en"/></c:if>
<fmt:setBundle basename="user-labels"/>
<c:forEach items="${requestScope.languages}" var="entry">
    <c:if test="${entry.value.locale == sessionScope.locale}"><c:set var="languageId" scope ="request" value="${entry.key}"/></c:if>
    <c:if test="${entry.value.locale != sessionScope.locale}"><c:set var="languageId" scope ="request" value="1"/></c:if>
</c:forEach>
<c:forEach items="${requestScope.book.titles}" var="entry">
    <c:if test="${entry.key == languageId}"><c:set var="title" value="${entry.value}"/></c:if>
</c:forEach>
<c:forEach items="${requestScope.book.descriptions}" var="entry">
    <c:if test="${entry.key == languageId}"><c:set var="description" value="${entry.value}"/></c:if>
</c:forEach>


<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
    <title><c:out value="${requestScope.book.titles[languageId]}"/></title>
    <link rel="stylesheet" href="<c:url value="/static/css/user.css"/>">
    <link rel="stylesheet" href="<c:url value="/static/css/all.css"/>">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Jost:wght@500&display=swap">
</head>
<body>
<header>
    <div id="locale">
        <select id="select-locale" name="locale">
            <option disabled selected><fmt:message key="products.nav.language"/></option>
            <option value="ua">Українська</option>
            <option value="en">English</option>
        </select>
    </div>
    <div class="user-control">
        <img src="<c:url value="/static/img/user.png"/>" alt="" width="64">
        <div class="dropdown-content">
            <ul id="auth-ul">
                <li><a href="${pageContext.request.contextPath}/login"><fmt:message key="products.login"/></a></li>
                <li><a href="${pageContext.request.contextPath}/signup"><fmt:message key="products.signup"/></a></li>
            </ul>
        </div>
    </div>
</header>
<div>
    <div>
        <h1><fmt:message key="user.label.title"/>: <c:out value="${title}"/></h1>
    </div>
    <div id="book">
        <div id="book-image">
            <img class="book-img" id="${requestScope.book.isbn}" alt="" width="300px" src="<c:url value="/static/img/product/no-image.jpg"/>">
        </div>
        <div id="book-details">
            <h3>ISBN:</h3><p>${requestScope.book.isbn}</p>
            <h3><fmt:message key="user.label.price"/>:</h3><p>${requestScope.book.price}</p>
            <h3><fmt:message key="user.label.description"/>:</h3><p><c:out value="${description}"/></p>
        </div>
    </div>
    <div id="control">
        <button class="control-button" onclick="history.back()"><fmt:message key="product.button.back"/></button>
        <button class="control-button" onclick="addToCart()"><fmt:message key="product.button.add.to.cart"/></button>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/locale.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/book-front.js"></script>

</body>
</html>