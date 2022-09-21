<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/mytags.tld" prefix="m" %>

<%@ page isELIgnored="false" %>

<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="labels"/>

<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/user.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/auth.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Jost:wght@500&display=swap">
    <title><fmt:message key="products.title"/></title>
</head>
<body>
<div id="header">
    <a onclick="login()">login</a>
    <a onclick="signup()">signup</a>
</div>
<div id="control"></div>
<div id="content"></div>
<div id="footer"></div>
<!-- Authentication modal -->
<div id="auth">
<%@include file="auth/login.jspf"%>
<%@include file="auth/signup.jspf"%>
</div>
<%@include file="auth/auth-footer.jspf"%>
</body>
</html>

