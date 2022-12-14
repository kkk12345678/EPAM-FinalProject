<%@ include file="../header.jspf"%>

<div id="content">
    <%@ include file="../menu.jspf"%>
    <div id="data">
    <form id="form-book" action="${pageContext.request.contextPath}/admin/edit-book" method="post" enctype="multipart/form-data">
        <input name="id" type="hidden" value="${requestScope.book.id}">
        <div class="div-input">
            <p class="edit-label">Tag:</p>
            <input class="text-input" type="text" name="tag" value="${requestScope.book.tag}" required>
        </div>
        <div class="input-group">
            <span class="div-input">
                <p class="edit-label">ISBN:</p>
                <input class="text-input" type="text" name="isbn" value="${requestScope.book.isbn}" required>
            </span>
            <span class="div-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.date"/>:</p>
                <input class="text-input" type="date" name="date" value="${requestScope.book.date}" required max="${requestScope.today}">
            </span>
        </div>
        <div class="input-group">
            <span class="div-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.price"/>:</p>
                <input class="text-input" type="text" name="price" value="${requestScope.book.price}" required min="0">
            </span>
            <span class="div-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.quantity"/>:</p>
                <input class="text-input" type="number" name="quantity" value="${requestScope.book.quantity}" required min="0">
            </span>
        </div>
        <div class="input-group">
            <span class="div-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.category"/>:</p>
                <select name="category">
                    <option selected><fmt:message key="admin.books.edit.choose.category"/></option>
                    <c:forEach items="${requestScope.categories}" var="category">
                    <option value="${category.id}" <c:if test="${category.id == requestScope.book.category.id}">selected</c:if>>
                        <c:forEach items="${category.names}" var="entry"><c:if test="${entry.key == languageId}">${entry.value}</c:if></c:forEach>
                    </option>
                    </c:forEach>
                </select>
            </span>
            <span class="div-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.publisher"/>:</p>
                <select name="publisher">
                    <option selected><fmt:message key="admin.books.edit.choose.publisher"/></option>
                    <c:forEach items="${requestScope.publishers}" var="publisher">
                    <option value="${publisher.id}" <c:if test="${publisher.id == requestScope.book.publisher.id}">selected</c:if>>
                        <c:forEach items="${publisher.names}" var="entry"><c:if test="${entry.key == languageId}">${entry.value}</c:if></c:forEach>
                    </option>
                    </c:forEach>
                </select>
            </span>
        </div>
        <div id="image-input">
            <p class="edit-label"><fmt:message key="admin.books.edit.image"/>:</p>
            <c:if test="${requestScope.book.id != 0}">
            <img src="${pageContext.request.contextPath}/static/img/product/${requestScope.book.isbn}.jpg" width="100px" alt="${requestScope.book.tag}">
            </c:if>
            <input type="file" name="image" accept="image/jpeg">
        </div>
        <div id="language-tabs">
            <c:forEach items="${requestScope.languages}" var="language">
            <div class="language-tab" id="tab${language.key}" onclick="show('tab${language.key}')" style="border-style:<c:choose><c:when test="${language.key == 1}"> solid</c:when><c:otherwise> none</c:otherwise></c:choose>">
                <img src="..${language.value.image}" alt="${language.value.locale}">
                <p onclick="show('tab${language.key}')">${language.value.name}</p>
            </div>
            </c:forEach>
        </div>
        <div id="input-tabs">
            <c:forEach items="${requestScope.languages}" var="language">
            <div id="tab${language.key}" style="display:<c:choose><c:when test="${language.key == 1}"> block</c:when><c:otherwise> none</c:otherwise></c:choose>">
                <div class=div-input>
                    <p class="edit-label"><fmt:message key="admin.books.edit.title"/> :</p>
                    <input class=text-input type=text name="name${language.key}" value="<c:out value="${requestScope.book.names[language.key]}"/>" required/>
                </div>
                <div class=div-input>
                    <p class="edit-label"><fmt:message key="admin.books.edit.description"/> :</p>
                    <textarea class=text-input rows=20 name="description${language.key}" required><c:out value="${requestScope.book.descriptions[language.key]}"/></textarea>
                </div>
            </div>
            </c:forEach>
        </div>
        <input type="submit" class="control-button" value="<fmt:message key="admin.books.edit.button.submit"/>">
    </form>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/tabs.js"></script>
<%@ include file="../footer.jspf"%>