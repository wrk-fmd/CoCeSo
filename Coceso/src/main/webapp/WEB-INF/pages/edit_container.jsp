<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="label.coceso"/> - <spring:message code="label.concern.edit"/>: ${concern.name}</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">

    <link href="<c:url value="/static/css/bootstrap.css" />" rel="stylesheet">
    <link href="<c:url value="/static/css/bootstrap-theme.css" />" rel="stylesheet">
    <link href="<c:url value="/static/css/coceso.css"/>" rel="stylesheet">

    <script src="<c:url value="/static/js/jquery.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.ui.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout.mapping.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout-sortable.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.ui.touch-punch.js"/>" type="text/javascript"></script>

    <script src="<c:url value="/static/js/container.js"/>" type="text/javascript"></script>


    <script type="text/javascript">

        $(document).ready(function() {
            jsonBase = "${pageContext.request.contextPath}/data/";
            model.load();

            ko.bindingHandlers.sortable.options = {placeholder: "highlight"};

            ko.applyBindings(model);
        });
    </script>
</head>
<body>

<div class="container">
    <c:set value="active" var="nav_concern" />
    <%@include file="parts/navbar.jsp"%>
    <div>
        <a href="<c:url value="/edit/"/>" class="btn btn-warning"><spring:message code="label.nav.back" /></a>
    </div>
    <div class="page-header">
        <h3><spring:message code="label.container.edit" /></h3>
    </div>

    <div>

        <div class="unit_container list-group" data-bind="template: {name: 'container-template', data: top}"></div>

        <script type="text/html" id="container-template">
            <div class="list-group-item">
                <%-- HEADER --%>
                <span data-bind="text: name() == '' ? '---' : name(), click: select, visible: !selected()"></span>
                <form data-bind="submit: update" style="display: inline;"><input type="text" data-bind="value: name, event: {blur: update}, visibleAndSelect: selected" /></form>
                <button class="btn btn-danger" data-bind="click: remove, visible: id != head()"><span class="glyphicon glyphicon-minus-sign"></span></button>
                <%-- /HEADER --%>

                <%-- UNITS --%>
                <ul class="unit_list" data-bind="sortable: {data: units, connectClass: 'unit_list', afterMove: updateUnit}">
                    <li>
                        <a href="#" class="unit_state">
                            <span class="ui-corner-all unit_state_ad" data-bind="text: call"></span>
                        </a>
                    </li>
                </ul>
                <%-- /UNITS --%>

                <%-- Sub Container --%>
                <div class="unit_container list-group" data-bind="sortable: {template: 'container-template', data: subContainer, connectClass: 'unit_container', afterMove: drop}"></div>
                <button class="btn btn-success" data-bind="click: addContainer"><span class="glyphicon glyphicon-plus-sign"></span></button>
                <%-- /Sub Container --%>


            </div>
        </script>

        <div id="spare">
            <spring:message code="label.unit.spare"/>:
            <ul class="unit_list spare" data-bind="sortable: {data: spareUnits, connectClass: 'unit_list', afterMove: updateUnit}">
                <li>
                    <a href="#" class="unit_state">
                        <span class="ui-corner-all unit_state_ad" data-bind="text: call"></span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <div class="modal-footer">
        &nbsp;<br/>&nbsp;
    </div>

</div>

</body>
</html>