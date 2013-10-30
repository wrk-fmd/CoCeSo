<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>Edit Case - ${caze.name}</title>
</head>
<body>
<c:set var="transportVehicle" value="Transport Vehicle" />
<c:set var="withDoc" value="Doctor" />
<c:set var="call" value="Call" />
<c:set var="ani" value="ANI" />
<c:set var="portable" value="Portable" />
<c:set var="info" value="Info" />

<c:url var="welcomeScreen" value="/welcome" />
<a href="${welcomeScreen}">Back</a><br>

<h3>Edit Case</h3>
<c:url value="/update" var="update" />
<form action="${update}" method="post">
    <input type="hidden" name="id" value="${caze.id}">
    <label>Name: <input type="text" name="name" value="${caze.name}" maxlength="64"></label><br>
    <label>Organiser: <input type="text" name="organiser" value="${caze.organiser}" maxlength="64"></label><br>
    <label>Persons: <input type="number" name="pax" value="${caze.pax}"></label><br>
    <input type="submit" name="submit" value="Update">
</form>

<br><br>

<h3>Edit Units of Case</h3>
<table>
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
        <form action="${updateUnit}" method="post">
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
    <form action="${createUnit}" method="post">
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
<form action="${createUnitBatch}" method="post">

    <label>${call} Prefix: <input type="text" name="call_pre" maxlength="50"></label><br>
    Number Range
        <label>from <input type="number" name="from" value="1"></label>&nbsp;
        <label>to <input type="number" name="to" value="5"></label><br>
    <label><input type="checkbox" name="withDoc" >${withDoc}</label><br>
    <label><input type="checkbox" name="transportVehicle">${transportVehicle}</label><br>
    <label><input type="checkbox" name="portable" >${portable}</label><br>
    <input type="submit" value="Create">

</form>
</body>
</html>