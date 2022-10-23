<%@ include file="auth-header.jspf"%>
    <div id="login">
        <form action="${pageContext.request.contextPath}/login" method="post">
            <input class="user-input" type="email" name="email" placeholder="<fmt:message key="user.label.email"/>" required>
            <input class="user-input" type="password" name="password" placeholder="<fmt:message key="user.label.password"/>" required>
            <button class="control-button"><fmt:message key="user.button.login"/></button>
        </form>
        <button id="cancel" class="control-button"><fmt:message key="user.button.cancel"/></button>
        <div id="signup-div">
            <a id="signup-link" href="signup"><fmt:message key="user.label.signup"/></a>
        </div>
    </div>
<%@ include file="auth-footer.jspf"%>