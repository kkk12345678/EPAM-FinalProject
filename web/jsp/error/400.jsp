<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="user-labels"/>
<html>
<head>
    <title><fmt:message key="error.400.title"/></title>
</head>
<body>
<h2>400 <fmt:message key="error.400.title"/></h2>
<p>${param.message}</p>
<button><a href="login">Login</a></button><button><a href="signup">Sign up</a></button>
</body>
</html>