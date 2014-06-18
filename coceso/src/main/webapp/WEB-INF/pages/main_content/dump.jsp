<!DOCTYPE html>
<%--
  Creates a Dump of current Concern
  UNDER CONSTRUCTION
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title>${concern.name}</title>
    <meta charset="utf-8" />

    <style type="text/css">
        @media print{
            @page {
                size: landscape
            }
        }
    </style>
</head>
<body>
<header>
    ${concern.name} (<fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${date}" />
</header>
<table>
    <thead>
    <tr>
        <th>
            <spring:message code="label.unit.call"/>
        </th>
        <th>
            <spring:message code="label.unit.ani"/>
        </th>
        <th>
            <spring:message code="label.unit.state"/>
        </th>
        <th>
            <spring:message code="label.unit.position"/>
        </th>
        <th>
            <spring:message code="label.unit.info"/>
        </th>
        <th>
            <spring:message code="label.incident.ref"/>
        </th>
        <th>
            <spring:message code="label.task.state"/>
        </th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="unit" items="${units}" varStatus="u_stat">
        <tr>
            <td>
                ${unit.call}
            </td>
            <td>
                ${unit.ani}
            </td>
            <td>
                ${unit.state}
            </td>
            <td>
                ${unit.position}
            </td>
            <td>
                ${unit.info}
            </td>
            <!-- TODO -->
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
