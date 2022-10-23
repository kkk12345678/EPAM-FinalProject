<%@ include file="header1.jspf"%>
<title><fmt:message key="user.success.title"/></title>
<%@ include file="header2.jspf"%>
<div id="success">
<h2><fmt:message key="user.success.header"/>${requestScope.id}.</h2>
<button class="control-button" onclick="home()">
    <fmt:message key="user.success.home"/>
</button>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/success.js"></script>
<%@ include file="footer.jspf"%>
