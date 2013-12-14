<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>CoCeSo - Internal Startpage</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <c:url var="bootstrap" value="/static/bootstrap.css" />
    <link href="${bootstrap}" rel="stylesheet">

    <c:url var="bootstrap_theme" value="/static/bootstrap-theme.css" />
    <link href="${bootstrap_theme}" rel="stylesheet">

</head>
<body>

<div class="container">
    <div class="alert alert-success">
        <c:url var="logout_link" value="/logout" />
        <div class="row">
            <div class="col-lg-8 text-left">
                <strong>Current User:</strong> ${user.given_name} ${user.sur_name} (${user.username})<br>
                Roles: <c:forEach items="${user.authorities}" var="role">${role} - </c:forEach><br>
            </div>
            <div class="col-lg-4 text-right">
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Dashboard</a>
                <a href="${logout_link}" class="btn btn-danger">Logout</a>
            </div>
        </div>
    </div>
    <div class="page-header">
        <h2>
            Existing Cases
        </h2>
    </div>
    <div>
        <form action="${pageContext.request.contextPath}/welcome" method="post" role="form">
            <div class="row">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-5">
                    <select name="case_id" size="10" class="form-control">
                        <c:forEach var="caze" items="${concern_list}">
                            <option value="${caze.id}">${caze.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-2 text-center">
                    <input type="submit" value="Start!" name="start" class="btn btn-success">
                </div>
                <div class="col-lg-2 text-center">
                    <input type="submit" value="Edit" name="edit" class="btn btn-warning">
                </div>
            </div>

        </form>

    </div>
    <div class="page-header">
        <h2>
            Create new Case
        </h2>
    </div>
    <div>
        <form action="${pageContext.request.contextPath}/edit/create" method="post" role="form">
            <div class="row">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-5">
                    <label class="sr-only" for="new_name">Name</label>
                    <input type="text" id="new_name" name="name" maxlength="64" class="form-control" placeholder="Name"/>
                </div>
                <!--div>
                    <Info of Organiser: <input type="text" name="info" maxlength="64" /><br>>
                </div-->
                <div class="col-lg-3">
                    <input type="submit" value="Create" class="btn btn-success"/>
                </div>
                <div class="col-lg-2">
                    &nbsp;
                </div>
            </div>


        </form>
    </div>
    <!-- TODO REMOVE IN RELEASE!!! ###################-->
    <div class="page-header">
        <h2>
            Debugging
        </h2>
    </div>
    <div class="alert alert-danger text-center">
        <strong>Only for Developing!</strong><br />
        You have to set the Cookie first (Open once Edit-Page of Case)
    </div>
    <div>
        <c:url var="debug0" value="/data/incident/getAll" />
        <a href="${debug0}" class="active btn btn-primary">All Incidents</a>

        <c:url var="debug1" value="/data/incident/getAllActive" />
        <a href="${debug1}" class="active btn btn-primary">All Active Incidents</a>

        <c:url var="debug2" value="/data/unit/getAll" />
        <a href="${debug2}" class="active btn btn-primary">All Units</a>

        <c:url var="debug3" value="/data/log/getAll" />
        <a href="${debug3}" class="active btn btn-primary">Full Log</a>


    </div>
    <!--##############-->
</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<c:url var="jquery" value="/static/jquery.js" />
<script src="${jquery}"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<c:url var="bootstrap_js" value="/static/bootstrap.js" />
<script src="${bootstrap_js}"></script>


</body>
</html>