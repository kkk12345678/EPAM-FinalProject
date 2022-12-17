<%@ include file="header1.jspf"%>
<link rel="stylesheet" href="<c:url value="/static/css/slider.css"/>">
<title><fmt:message key="products.title"/></title>
<%@ include file="header2.jspf"%>
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
                    <option label="${category.tag}" value="${category.id}" name="category" <c:if test="${category.id == param.category}">selected</c:if>>
                    <c:forEach items="${category.names}" var="entry">
                        <c:if test="${entry.key == languageId}">${entry.value}</c:if>
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
                    <option label="${publisher.tag}" value="${publisher.id}" name="publisher" <c:if test="${publisher.id == param.publisher}">selected</c:if>>
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
                    <p><fmt:message key="user.label.quantity"/>: ${book.quantity}</p>
                    <p>ISBN: ${book.isbn}</p>
                    <p><fmt:message key="user.label.price"/>: ${book.price}</p>
                </div>
            </div>
            <div class="book-link">
                <a href="./product/${book.tag}">
                    <c:forEach items="${book.names}" var="entry">
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
<button class="control-button" onclick="loadBooks('<fmt:message key="user.label.price"/>', '<fmt:message key="user.label.quantity"/>', ${languageId})"><fmt:message key="products.button.load.more"/></button>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/load.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/slider.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/sort.js"></script>
<%@ include file="footer.jspf"%>