<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.locale}"/>
<fmt:setBundle basename="user-labels"/>
<html>
<head>
    <title><fmt:message key="error.401.title"/></title>
</head>
<body>
<h2>401 <fmt:message key="error.401.title"/></h2>
<p><fmt:message key="error.401.message"/></p>
<button><a href="login">Login</a></button>
</body>
</html>
