<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="label.patient.search" /></title>

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css" />" type="text/css"/>

    <script src="<c:url value="/static/js/jquery-1.11.0.min.js"/>" type="text/javascript"></script>
    <%-- See Bug #9166 of JQuery UI: http://bugs.jqueryui.com/ticket/9166 --%>
    <script src="<c:url value="/static/js/jquery.ui.min.js"/>" type="text/javascript"></script>

    <script src="<c:url value="/static/js/knockout.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/bootstrap.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.ui.touch-punch.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/search.patient.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            ko.applyBindings(new SearchViewModel({concernID: ${ not empty active ? active : 0}}));
        });
    </script>
</head>
<body>

<div class="container">
    <div>
           <input type="text" class="form-control" data-bind="value: query, valueUpdate: 'keyup'" autocomplete="off" autofocus>
    </div>
    <div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>
                    <spring:message code="label.person.sur_name"/>
                </th>
                <th>
                    <spring:message code="label.person.given_name"/>
                </th>
                <th>
                    <spring:message code="label.patient.insurance_number"/>
                </th>
                <th>
                    <spring:message code="label.patient.externalID"/>
                </th>
                <th>
                    <spring:message code="label.patient.erType"/>
                </th>
                <th>
                    <spring:message code="label.patient.info"/>
                </th>
                <th>
                    <spring:message code="label.incident.ao"/>
                </th>
                <th>
                    <spring:message code="label.transport_history"/>
                </th>
            </tr>
            <tr>
                <th>
                    <input class="form-control" type="checkbox" value="sur_name" data-bind="checked: checkedFilter">
                </th>
                <th>
                    <input class="form-control" type="checkbox" value="given_name" data-bind="checked: checkedFilter">
                </th>
                <th>
                    <input class="form-control" type="checkbox" value="insurance_number" data-bind="checked: checkedFilter">
                </th>
                <th>
                    <input class="form-control" type="checkbox" value="externalID" data-bind="checked: checkedFilter">
                </th>
                <th>
                    <input class="form-control" type="checkbox" value="erType" data-bind="checked: checkedFilter">
                </th>
                <th>
                    <input class="form-control" type="checkbox" value="info" data-bind="checked: checkedFilter">
                </th>
                <th>
                    <input class="form-control" type="checkbox" value="ao" data-bind="checked: checkedFilter">
                </th>
                <th>

                </th>
            </tr>
            </thead>
            <tbody data-bind="foreach: filtered">
            <tr>
                <td>
                    <span data-bind="text: sur_name"></span>
                </td>
                <td>
                    <span data-bind="text: given_name"></span>
                </td>
                <td>
                    <span data-bind="text: insurance_number"></span>
                </td>
                <td>
                    <span data-bind="text: externalID"></span>
                </td>
                <td>
                    <span data-bind="text: erType"></span>
                </td>
                <td>
                    <span data-bind="text: info"></span>
                </td>
                <td>
                    <span data-bind="text: ao"></span>
                </td>
                <td>
                    <span class="glyphicon glyphicon-info-sign" style="font-size: x-large" data-toggle="tooltip"
                          data-placement="auto rigth" data-bind="title: textTooltip"></span>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

</div>

</body>
</html>