<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../admin-header.jspf"%>
<%@ include file="../catalogue-header.jspf"%>

<div id="content">
    <form action="${pageContext.request.contextPath}/admin/edit-publisher" method="post">
        <input name="id" type="hidden" value="${requestScope.publisher.id}">
        <div id="tag-input">
            <p class="edit-label">Tag:</p>
            <input class="text-input" type="text" name="tag" value="${requestScope.publisher.tag}" required>
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
                    <p class="edit-label"><fmt:message key="admin.publishers.edit.name"/> :</p>
                    <input class=text-input type=text name="name${language.key}" value="<c:out value="${requestScope.publisher.names[language.key]}"/>" required/>
                </div>
                <div class=description-input>
                    <p class="edit-label"><fmt:message key="admin.publishers.edit.description"/> :</p>
                    <textarea class=text-input rows=20 name="description${language.key}" required><c:out value="${requestScope.publisher.descriptions[language.key]}"/></textarea>
                </div>
            </div>
        </c:forEach>
        </div>
        <input type="submit" class="control-button" value="<fmt:message key="admin.publishers.edit.button.submit"/>">
    </form>
</div>

<%@ include file="/jsp/admin/edit-catalogue-footer.jspf"%>
<%@ include file="/jsp/admin/admin-footer.jspf"%>