<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap -->
    <c:url var="bootstrap" value="/static/bootstrap.css" />
    <link href="${bootstrap}" rel="stylesheet">

    <c:url var="bootstrap_theme" value="/static/bootstrap-theme.css" />
    <link href="${bootstrap_theme}" rel="stylesheet">

</head>
<body>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<c:url var="jquery" value="/static/jquery.js" />
<script src="${jquery}"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<c:url var="bootstrap_js" value="/static/bootstrap.js" />
<script src="${bootstrap_js}"></script>



</body>
</html>