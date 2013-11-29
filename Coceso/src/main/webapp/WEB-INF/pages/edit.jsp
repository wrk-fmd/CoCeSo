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

<!-- #################### Start of Page ############## -->
<div class="container">
    <c:url var="welcomeScreen" value="/welcome" />
    <a href="${welcomeScreen}" class="">Back</a><br>

    <h3 class="h3">Edit Case</h3>
    <c:url value="/update" var="update" />
    <form action="${update}" method="post" role="form">
        <input type="hidden" name="id" value="${caze.id}">
        <div class="form-group">
            <label for="case_name">Name</label>
            <input type="text" id="case_name" name="name" value="${caze.name}" maxlength="64" class="form-control">
        </div>
        <div class="form-group">
            <label for="case_organiser">Organiser</label>
            <textarea id="case_organiser" name="organiser" maxlength="128" class="form-control">${caze.organiser}</textarea>
        </div>
        <div class="form-group">
            <label for="case_pax">Persons</label>
            <input type="number" id="case_pax" name="pax" value="${caze.pax}" class="form-control">
        </div>
        <input class="btn btn-success" type="submit" name="submit" value="Update">
    </form>

    <br><br>

    <!--##### TABLE -->
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
                <td><input type="text" name="call" value="${unit.call}" maxlength="64" class="form-control"></td>
                <td><input type="text" name="ani" value="${unit.ani}" maxlength="16" class="form-control"></td>
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
                <td><input type="text" name="info" value="${unit.info}" maxlength="128" class="form-control"></td>
                <td>
                    <input type="submit" name="update" value="Update" class="btn btn-success">&nbsp;
                    <input type="submit" name="remove" value="Remove" class="btn btn-danger">
                </td>
            </tr>
            </form>
        </c:forEach>

        <c:url value="/createUnit" var="createUnit" />
        <form action="${createUnit}" method="post" class="form-inline" role="form">
            <tr>
                <td style="display: none;"></td>
                <td>
                    <label class="sr-only" for="new_call">${call}</label>
                    <input type="text" id="new_call" name="call" maxlength="64" class="form-control">
                </td>
                <td>
                    <label class="sr-only" for="new_ani">${ani}</label>
                    <input type="text" id="new_ani" name="ani" maxlength="16">
                </td>
                <td>
                    <input type="checkbox" name="withDoc" >
                </td>
                <td>
                    <input type="checkbox" name="transportVehicle" >
                </td>
                <td>
                    <input type="checkbox" name="portable" >
                </td>
                <td>
                    <label class="sr-only" for="new_info">${info}</label>
                    <input type="text" id="new_info" name="info" maxlength="128">
                </td>
                <td>
                    <input type="submit" value="Create" class="btn btn-success">
                </td>
            </tr>
        </form>
    </table>

    <h3 class="h3">Create Units as Batch Job</h3>
    <c:url value="/createUnitBatch" var="createUnitBatch" />
    <form action="${createUnitBatch}" method="post" role="form">

        <div class="form-group">
            <label class="sr-only" for="batch_call">${call} Prefix</label>
            <input type="text" id="batch_call" name="call_pre" maxlength="50" class="form-control">
        </div>
        Number Range
        <label for="batch_from">from</label>
        <input type="number" id="batch_from" name="from" value="1" class="form-control">&nbsp;

        <label for="batch_to">to</label>
        <input type="number" id="batch_to" name="to" value="5" class="form-control"><br>

        <label for="batch_doc">${withDoc}</label><input type="checkbox" id="batch_doc" name="withDoc" ><br>
        <label for="batch_vhcl">${transportVehicle}</label><input type="checkbox" id="batch_vhcl" name="transportVehicle"><br>
        <label for="batch_portable">${portable}</label><input type="checkbox" id="batch_portable" name="portable" ><br>
        <input type="submit" value="Create" class="btn btn-success">

    </form>
</div>

</body>
</html>