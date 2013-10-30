<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>Edit Case - ${caze.name}</title>
</head>
<body>
<h3>Edit Case</h3>
<c:url value="/update" var="update" />
<form action="${update}" method="post">
    <input type="hidden" name="id" value="${caze.id}">
    Name: <input type="text" name="name" value="${caze.name}" maxlength="64"><br>
    Organiser: <input type="text" name="organiser" value="${caze.organiser}" maxlength="64"><br>
    Persons: <input type="number" name="pax" value="${caze.pax}"><br>
    <input type="submit" name="submit" value="Update">
</form>

<br><br>

<h3>Edit Units of Case</h3>
<table>
    <tr>
        <th style="display: none;">ID</th>
        <th>Call</th>
        <th>ANI</th>
        <th>Doctor</th>
        <th>(Transport)Vehicle</th>
        <th>Portable</th>
        <th>Info</th>
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
            <td><input type="submit" value="Update"></td>
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
</body>
</html>