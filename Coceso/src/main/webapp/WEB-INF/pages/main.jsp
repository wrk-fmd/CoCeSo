<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>Main</title>

    <link href="<c:url value="/static/bootstrap.css" />" rel="stylesheet">

    <link href="<c:url value="/static/bootstrap-theme.css" />" rel="stylesheet">

</head>
<body>

<div class="container">
    <c:set value="active" var="nav_main" />
    <%@include file="parts/navbar.jsp"%>


</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->

<script src="<c:url value="/static/jquery.js" />"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->

<script src="<c:url value="/static/bootstrap.js" />"></script>

</body>
</html>