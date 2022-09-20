<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../admin-header.jspf"%>
<%@ include file="../catalogue-header.jspf"%>

<div id="content">
    <form action="${pageContext.request.contextPath}/admin/edit-book" method="post" enctype="multipart/form-data">
        <input name="id" type="hidden" value="${requestScope.book.id}">
        <div id="tag-input">
            <p class="edit-label">Tag:</p>
            <input class="text-input" type="text" name="tag" value="${requestScope.book.tag}" required>
        </div>
        <div class="input-group">
            <span id="isbn-input">
                <p class="edit-label">ISBN:</p>
                <input class="text-input" type="text" name="isbn" value="${requestScope.book.isbn}" required>
            </span>
            <span id="date-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.date"/>:</p>
                <input class="text-input" type="date" name="date" value="${requestScope.book.date}" required>
            </span>
        </div>
        <div class="input-group">
            <span id="price-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.price"/>:</p>
                <input class="text-input" type="text" name="price" value="${requestScope.book.price}" required>
            </span>
            <span id="quantity-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.quantity"/>:</p>
                <input class="text-input" type="number" name="quantity" value="${requestScope.book.quantity}" required>
            </span>
        </div>
        <div class="input-group">
            <span id="category-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.category"/>:</p>
                <select name="category">
                    <m:select-category category="${requestScope.book.category}"><fmt:message key="admin.books.edit.choose.category"/></m:select-category>
                </select>
            </span>
            <span id="publisher-input">
                <p class="edit-label"><fmt:message key="admin.books.edit.publisher"/>:</p>
                <select name="publisher">
                    <m:select-publisher publisher="${requestScope.book.publisher}"><fmt:message key="admin.books.edit.choose.publisher"/></m:select-publisher>
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
                <div class=name-input>
                    <p class="edit-label"><fmt:message key="admin.books.edit.title"/> :</p>
                    <input class=text-input type=text name="name${language.key}" value="<c:out value="${requestScope.book.titles[language.key]}"/>" required/>
                </div>
                <div class=description-input>
                    <p class="edit-label"><fmt:message key="admin.books.edit.description"/> :</p>
                    <textarea class=text-input rows=20 name="description${language.key}" required><c:out value="${requestScope.book.descriptions[language.key]}"/></textarea>
                </div>
            </div>
            </c:forEach>
        </div>
        <input type="submit" class="control-button" value="<fmt:message key="admin.books.edit.button.submit"/>">
    </form>
</div>

<%@ include file="/jsp/admin/edit-catalogue-footer.jspf"%>
<%@ include file="/jsp/admin/admin-footer.jspf"%>