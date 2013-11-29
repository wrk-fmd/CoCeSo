<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false" %>

<html>
<head>
    <title>CoCeSo</title>

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

    <div class="container">
        <div class="page-header">
            <h1 class="h1">Welcome to CoCeSo!</h1>
        </div>
        <div>
            This will be filled with information....
        </div>
        <div class="page-header">
            <h2>Main Program</h2>
        </div>
        <div>
            <c:url var="welcome" value="/welcome" />
            <a href="${welcome}" class="active btn-lg btn-success">Login</a>
        </div>
        <div class="page-header">
            <h2>Dashboard</h2>
        </div>
        <div>
            <c:url var="dashboard" value="/dashboard" />
            <a href="${dashboard}" class="active btn-lg btn-primary disabled">Dashboard</a>&nbsp;Coming Soon...
        </div>

    </div>

</body>
</html>