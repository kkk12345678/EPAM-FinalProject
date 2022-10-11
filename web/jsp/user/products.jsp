<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/mytags.tld" prefix="m" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.locale}"/>
<c:if test="${sessionScope.locale == null}"><fmt:setLocale value="en"/></c:if>
<fmt:setBundle basename="user-labels"/>
<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
    <title><fmt:message key="products.title"/></title>
    <link rel="stylesheet" href="<c:url value="/static/css/user.css"/>">
    <link rel="stylesheet" href="<c:url value="/static/css/all.css"/>">
    <link rel="stylesheet" href="<c:url value="/static/css/slider.css"/>">
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
                <li><a href="login"><fmt:message key="products.login"/></a></li>
                <li><a href="signup"><fmt:message key="products.signup"/></a></li>
            </ul>
        </div>
    </div>
</header>
<c:forEach items="${requestScope.languages}" var="entry">
<c:if test="${entry.value.locale == sessionScope.locale}">
    <c:set var="languageId" scope ="request" value="${entry.key}"/>
</c:if>
<c:if test="${entry.value.locale != sessionScope.locale}">
    <c:set var="languageId" scope ="request" value="1"/>
</c:if>
</c:forEach>
<div id="sort">
    <label>
        <select id="select-sort">
            <option disabled selected><fmt:message key="user.label.sort.by"/></option>
            <option value="1"><fmt:message key="user.label.sort.tag.asc"/></option>
            <option value="2"><fmt:message key="user.label.sort.tag.desc"/></option>
            <option value="3"><fmt:message key="user.label.sort.price.asc"/></option>
            <option value="4"><fmt:message key="user.label.sort.price.desc"/></option>
            <option value="5"><fmt:message key="user.label.sort.date.asc"/></option>
            <option value="6"><fmt:message key="user.label.sort.date.desc"/></option>
        </select>
    </label>
</div>
<div id="content">
    <div id="filter">
        <h2><fmt:message key="user.label.filter"/></h2>
        <div id="category" class="filter-item">
            <label>
                <fmt:message key="user.label.filter.category"/><br>
                <select id="category-filter">
                    <option disabled selected value="0">
                        <fmt:message key="user.label.filter.choose.category"/>
                    </option>
                    <c:forEach items="${requestScope.categories}" var="category">
                    <option label="${category.tag}" value="${category.id}" name="category">
                    <c:forEach items="${category.names}" var="entry">
                    <c:if test="${entry.key == languageId}">
                        ${entry.value}
                    </c:if>
                    </c:forEach>
                    </option>
                    </c:forEach>
                </select>
            </label>
        </div>
        <div id="publisher" class="filter-item">
            <label>
                <fmt:message key="user.label.filter.publisher"/><br>
                <select id="publisher-filter">
                    <option disabled selected value="0">
                        <fmt:message key="user.label.filter.choose.publisher"/>
                    </option>
                    <c:forEach items="${requestScope.publishers}" var="publisher">
                    <option label="${publisher.tag}" value="${publisher.id}" name="publisher">
                    <c:forEach items="${publisher.names}" var="entry">
                    <c:if test="${entry.key == languageId}">
                        ${entry.value}
                    </c:if>
                    </c:forEach>
                    </option>
                    </c:forEach>
                </select>
            </label>
        </div>
        <div id="price" class="filter-item">
            <label>
                <fmt:message key="user.label.filter.price"/>
                <div class="range_container">
                    <div class="sliders_control">
                        <input id="fromSlider" type="range" value="${requestScope.minPrice}" min="${requestScope.minPrice}" max="${requestScope.maxPrice}"/>
                        <input id="toSlider" type="range" value="${requestScope.maxPrice}" min="${requestScope.minPrice}" max="${requestScope.maxPrice}"/>
                    </div>
                    <div class="form_control">
                        <div class="form_control_container">
                            <div class="form_control_container__time">Min</div>
                            <input class="form_control_container__time__input" type="number" id="fromInput" value="${requestScope.minPrice}" min="${requestScope.minPrice}" max="${requestScope.maxPrice}"/>
                        </div>
                        <div class="form_control_container">
                            <div class="form_control_container__time">Max</div>
                            <input class="form_control_container__time__input" type="number" id="toInput" value="${requestScope.maxPrice}" min="${requestScope.minPrice}" max="${requestScope.maxPrice}"/>
                        </div>
                    </div>
                </div>
            </label>
        </div>
        <button class="control-button" onclick="filter()"><fmt:message key="user.button.filter.apply"/></button>
        <button class="control-button" onclick="cancelFilter()"><fmt:message key="user.button.filter.cancel"/></button>
    </div>
    <div id="books">
        <c:forEach items="${requestScope.books}" var="book">
        <div class="book">
            <div class="book-details">
                <div class="book-img">
                    <img class="book-img" id="${book.isbn}" alt="" width="100px" src="${pageContext.request.contextPath}/static/img/product/no-image.jpg">
                </div>
                <div class="book-text-details">
                    <p class="isbn">ISBN: ${book.isbn}</p>
                    <p class="price"><fmt:message key="user.label.price"/>: ${book.price}</p>
                </div>
            </div>
            <div class="book-link">
                <a href="./product/${book.tag}">
                    <c:forEach items="${book.titles}" var="entry">
                    <c:if test="${entry.key == languageId}">
                    ${entry.value}
                    </c:if>
                    </c:forEach>
                </a>
            </div>
        </div>
        </c:forEach>
    </div>
</div>
<button class="control-button" onclick="loadBooks('<fmt:message key="user.label.price"/>')"><fmt:message key="products.button.load.more"/></button>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/load.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/slider.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/locale.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/sort.js"></script>
</body>
</html>