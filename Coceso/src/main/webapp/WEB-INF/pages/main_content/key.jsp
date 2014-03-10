<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="en">
<head>
    <title><spring:message code="label.main.key" /></title>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css" />
</head>
<body>

<div class="ajax_content">
    <table id="keytable" class="table">
        <thead>
            <tr>
                <th><spring:message code="label.key.category" /></th>
                <th><spring:message code="label.key.symbol" /></th>
                <th><spring:message code="label.key.meaning" /></th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>
                    <spring:message code="label.key.category.notification" />
                </td>
                <td>
                    <span class="glyphicon glyphicon-signal"></span>
                </td>
                <td>
                    <spring:message code="label.connection_status" />: <spring:message code="text.key.connection_status" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <span class="glyphicon glyphicon-time"></span>
                </td>
                <td>
                    <spring:message code="label.main.incident.new_or_open" />: <spring:message code="text.key.open_or_new" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <span class="glyphicon glyphicon-log-out"></span>
                </td>
                <td>
                    <spring:message code="label.incident.type.transport.open" />: <spring:message code="text.transport.open" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <span class="glyphicon glyphicon-bullhorn"></span>
                </td>
                <td>
                    <spring:message code="label.main.unit.for_dispo" />: <spring:message code="text.unit_for_dispo" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <span class="glyphicon glyphicon-exclamation-sign"></span>
                </td>
                <td>
                    <spring:message code="label.main.unit.free" />: <spring:message code="text.unit.free" />
                </td>
            </tr>
            <%-- UNITS --%>
            <tr>
                <td><spring:message code="label.units" /></td>
                <td>
                    <span class="glyphicon glyphicon-home"></span>
                </td>
                <td>
                    <spring:message code="label.unit.home" />: <spring:message code="text.unit.is_home" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <span class="glyphicon glyphicon-map-marker"></span>
                </td>
                <td>
                    <spring:message code="label.unit.position" />: <spring:message code="text.unit.position" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <span class="glyphicon glyphicon-record"></span>
                </td>
                <td>
                    <spring:message code="label.incident.type.holdposition" />: <spring:message code="text.unit.holdposition" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <span class="glyphicon glyphicon-pause"></span>
                </td>
                <td>
                    <spring:message code="label.incident.type.standby" />: <spring:message code="text.unit.standby" />
                </td>
            </tr>
        </tbody>
    </table>
</div>
</body>
</html>
