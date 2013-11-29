<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>Edit Case - ${caze.name}</title>
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

<c:set var="transportVehicle" value="Transport Vehicle" />
<c:set var="withDoc" value="Doctor" />
<c:set var="call" value="Call" />
<c:set var="ani" value="ANI" />
<c:set var="portable" value="Portable" />
<c:set var="info" value="Info" />

<div class="container">
    <c:url var="welcomeScreen" value="/welcome" />
    <a href="${welcomeScreen}" class="">Back</a><br>

    <h3 class="h3">Edit Case</h3>
    <c:url value="/update" var="update" />
    <form action="${update}" method="post" role="form">
        <input type="hidden" name="id" value="${caze.id}">
        <div class="form-group">
            <label>Name:</label> <input type="text" name="name" value="${caze.name}" maxlength="64">
        </div>
        <div class="form-group">
            <label>Organiser:</label> <textarea name="organiser" maxlength="128">${caze.organiser}</textarea>
        </div>
        <div class="form-group">
            <label>Persons:</label> <input type="number" name="pax" value="${caze.pax}">
        </div>
        <input type="submit" name="submit" value="Update">
    </form>

    <br><br>

    <h3 class="h3">Edit Units of Case</h3>
    <table class="table table-striped">
        <tr>
            <th style="display: none;">ID</th>
            <th>${call}</th>
            <th>${ani}</th>
            <th>${withDoc}</th>
            <th>${transportVehicle}</th>
            <th>${portable}</th>
            <th>${info}</th>
            <th></th>
        </tr>
        <c:url value="/updateUnit" var="updateUnit" />
        <c:forEach items="${unit_list}" var="unit">
            <form action="${updateUnit}" method="post" class="form-inline" role="form">
            <tr>
                <td style="display: none;"><input type="hidden" name="id" value="${unit.id}"></td>
                <td><input type="text" name="call" value="${unit.call}" maxlength="64"></td>
                <td><input type="text" name="ani" value="${unit.ani}" maxlength="16"></td>
                <c:choose>
                    <c:when test="${unit.withDoc}">
                        <td><input type="checkbox" name="withDoc" checked></td>
                    </c:when>
                    <c:otherwise>
                        <td><input type="checkbox" name="withDoc"></td>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${unit.transportVehicle}">
                        <td><input type="checkbox" name="transportVehicle" checked></td>
                    </c:when>
                    <c:otherwise>
                        <td><input type="checkbox" name="transportVehicle"></td>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${unit.portable}">
                        <td><input type="checkbox" name="portable" checked></td>
                    </c:when>
                    <c:otherwise>
                        <td><input type="checkbox" name="portable"></td>
                    </c:otherwise>
                </c:choose>
                <td><input type="text" name="info" value="${unit.info}" maxlength="128"></td>
                <td>
                    <input type="submit" name="update" value="Update">&nbsp;
                    <input type="submit" name="remove" value="Remove">
                </td>
            </tr>
            </form>
        </c:forEach>

        <c:url value="/createUnit" var="createUnit" />
        <form action="${createUnit}" method="post" class="form-inline" role="form">
            <tr>
                <td style="display: none;"></td>
                <td><input type="text" name="call" maxlength="64"></td>
                <td><input type="text" name="ani" maxlength="16"></td>
                <td><input type="checkbox" name="withDoc" ></td>
                <td><input type="checkbox" name="transportVehicle" ></td>
                <td><input type="checkbox" name="portable" ></td>
                <td><input type="text" name="info" maxlength="128"></td>
                <td><input type="submit" value="Create"></td>
            </tr>
        </form>
    </table>

    <h3>Create Units as Batch Job</h3>
    <c:url value="/createUnitBatch" var="createUnitBatch" />
    <form action="${createUnitBatch}" method="post" role="form">

        <label>${call} Prefix: <input type="text" name="call_pre" maxlength="50"></label><br>
        Number Range
            <label>from <input type="number" name="from" value="1"></label>&nbsp;
            <label>to <input type="number" name="to" value="5"></label><br>
        <label><input type="checkbox" name="withDoc" >${withDoc}</label><br>
        <label><input type="checkbox" name="transportVehicle">${transportVehicle}</label><br>
        <label><input type="checkbox" name="portable" >${portable}</label><br>
        <input type="submit" value="Create">

    </form>
</div>

</body>
</html>