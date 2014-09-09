<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
    <title><spring:message code="label.patient.search"/></title>

    <meta charset="utf-8"/>
    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css"/>

    <script src="<c:url value="/static/js/assets/jquery.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/jquery.ui.1.10.4.min.js"/>" type="text/javascript"></script>

    <script src="<c:url value="/static/js/assets/knockout.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/bootstrap.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/jquery.ui.touch-punch.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/search.patient.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
        var model;
        $(document).ready(function() {
            model = new SearchViewModel({
                concernID: ${ not empty active ? active : 0}
                ,urlprefix: "${pageContext.request.contextPath}/"
                ,concerns: [
                    {id: 0, name: "<spring:message code="label.select"/>"}
                    <c:forEach var="concern" items="${concerns}">,{ id: ${concern.id}, name: "${concern.name}" }</c:forEach>
                ]
            });
            ko.applyBindings(model);

        });
    </script>
    <style type="text/css">
        /* Hide title bar of popover */
        .popover-title {
            display: none;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="page-title">
        <h2>
            <spring:message code="label.patient.search"/>
        </h2>
    </div>
    <div class="page-header">
        <h4>
            <spring:message code="label.concern"/>
        </h4>
    </div>
    <div class="page-header">
        <div class="row">
            <div class="col-md-6">
                <select class="form-control" data-bind="options: opts.concerns, optionsText: 'name', optionsValue: 'id', value: concernID"></select>
            </div>
            <div class="col-md-2">
                <button type="button" class="btn btn-lg btn-default" data-bind="click: fetch">
                    <span class="glyphicon glyphicon-refresh"></span>
                </button>
            </div>
        </div>
    </div>
    <div class="page-header">
        <div class="row">
            <div class="col-md-6">
                <input type="text" class="form-control" data-bind="value: query, valueUpdate: 'keyup'" autocomplete="off"
                       placeholder="<spring:message code="label.search"/>" autofocus>
            </div>
            <div class="col-md-2">
                <button type="button" class="btn btn-lg btn-default" data-bind="click: clearQuery">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </div>
        </div>

    </div>
    <div>
        <table class="table table-striped text-center">
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
                    <span class="glyphicon glyphicon-info-sign" style="font-size: x-large"
                          data-bind="popover: $root.tooltip($data)"></span>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

</div>

</body>
</html>
