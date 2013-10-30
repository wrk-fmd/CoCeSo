<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>CoCeSo - Internal Startpage</title>
</head>
<body>
<h4>
    Existing Cases
</h4>
<br>
<form action="${pageContext.request.contextPath}/welcome" method="post">
    <select name="case_id" size="10">
        <c:forEach var="caze" items="${case_list}">
            <option value="${caze.id}">${caze.name}</option>
        </c:forEach>
    </select>
    <br>
    <input type="submit" value="Start!" name="start">
    &nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" value="Edit" name="edit">
</form>

<br>
<br>

<h4>
    Create new Case
</h4>
<form action="${pageContext.request.contextPath}/create" method="post">
    Name: <input type="text" name="name" maxlength="64" /><br>
    <!--Info of Organiser: <input type="text" name="organiser" maxlength="64" /><br>-->
    <input type="submit" value="Create" />
</form>
</body>
</html>