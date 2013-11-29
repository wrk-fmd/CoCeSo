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
<div class="container">
    <c:url var="logout_link" value="/logout" />
    <c:url var="back_link" value="/welcome" />
    <c:url var="home_link" value="/" />

    <!-- Static navbar -->
    <div class="navbar navbar-inverse" role="navigation">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${home_link}">CoCeSo</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="#">Dashboard</a></li>
                <li class="${log}"><a href="?sub=Log">Log</a></li>
                <li class="${unit}"><a href="?sub=Unit">Unit</a></li>
                <li class="${incident}"><a href="?sub=Incident">Incident</a></li>
                <!--li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="#">Action</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li class="divider"></li>
                        <li class="dropdown-header">Nav header</li>
                        <li><a href="#">Separated link</a></li>
                        <li><a href="#">One more separated link</a></li>
                    </ul>
                </li-->
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="${back_link}">Back</a></li>
                <li><a href="${logout_link}">Logout</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>

    <c:url var="get_inc" value="/data/incident/get" />
    <c:url var="get_unit" value="/data/unit/get" />
    <table class="table table-striped">
        <thead>
            <c:if test="${not empty log}">
                <tr>
                    <th>
                        Time
                    </th>
                    <th>
                        User
                    </th>
                    <th>
                        Text
                    </th>
                    <th>
                        Unit
                    </th>
                    <th>
                        Incident
                    </th>
                    <th>
                        State
                    </th>
                </tr>
            </c:if>
        </thead>
        <tbody>
            <c:if test="${not empty log}">
                <c:forEach items="${logs}" var="logEntry">
                    <tr>
                        <td>
                            ${logEntry.timestamp.hours}:${logEntry.timestamp.minutes}:${logEntry.timestamp.seconds}
                        </td>
                        <td>
                            ${logEntry.user.username}
                        </td>
                        <td>
                            ${logEntry.text}
                        </td>
                        <td>
                            <a href="${get_unit}/${logEntry.unit.id}">${logEntry.unit.call}</a>
                        </td>
                        <td>
                            <a href="${get_inc}/${logEntry.incident.id}">${logEntry.incident.id}</a>
                        </td>
                        <td>
                            ${logEntry.state}
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
        </tbody>
    </table>

</div>


<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<c:url var="jquery" value="/static/jquery.js" />
<script src="${jquery}"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<c:url var="bootstrap_js" value="/static/bootstrap.js" />
<script src="${bootstrap_js}"></script>
</body>
</html>