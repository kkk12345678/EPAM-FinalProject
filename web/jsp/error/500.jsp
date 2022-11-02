<%@ include file="header.jspf"%>

    <h3>${param.message}</h3>
    <div id="buttons">
        <div class="button">
            <button class="control-button" onclick="window.history.back()"><fmt:message key="error.button.back"/></button>
        </div>
        <div class="button">
            <button class="control-button" onclick="home()"><fmt:message key="error.button.home"/></button>
        </div>
    </div>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/success.js"></script>
</body>
</html>
