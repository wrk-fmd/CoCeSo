<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>

<html>
<head>
    <title><spring:message code="label.coceso"/></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <c:url var="bootstrap" value="/static/bootstrap.css" />
    <link href="${bootstrap}" rel="stylesheet">

    <c:url var="bootstrap_theme" value="/static/bootstrap-theme.css" />
    <link href="${bootstrap_theme}" rel="stylesheet">

</head>
<body>

    <div class="container">
        <div class="page-header">
            <h1 class="h1"><spring:message code="label.welcome"/></h1>
        </div>
        <div>
            This will be filled with information....
        </div>
        <div class="page-header">
            <h2><spring:message code="label.nav.main"/></h2>
        </div>
        <div>

            <a href="<c:url value="/welcome" />" class="btn-lg btn-success"><spring:message code="label.nav.login"/></a>
        </div>

    </div>


    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <c:url var="jquery" value="/static/jquery.js" />
    <script src="${jquery}"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <c:url var="bootstrap_js" value="/static/bootstrap.js" />
    <script src="${bootstrap_js}"></script>

</body>
</html>