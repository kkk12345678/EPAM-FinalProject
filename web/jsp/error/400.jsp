<%@ include file="error-header.jspf"%>
    <title><fmt:message key="error.code.400.title"/></title>
</head>
<body>
    <h1><fmt:message key="error.code.400.title"/></h1>
    <p>${requestScope.message}</p>
    <a href="${requestScope.page}"><fmt:message key="error.return"/></a>
</body>
</html>