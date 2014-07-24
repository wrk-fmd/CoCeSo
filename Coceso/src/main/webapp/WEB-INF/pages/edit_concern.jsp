<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="label.coceso"/> - <spring:message code="label.concern.edit"/>: ${concern.name}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8" />

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">

    <link href="<c:url value="/static/css/bootstrap.css" />" rel="stylesheet">
    <link href="<c:url value="/static/css/bootstrap-theme.css" />" rel="stylesheet">
    <link href="<c:url value="/static/css/flags.css"/>" rel="stylesheet" />
    <link href="<c:url value="/static/css/coceso.css"/>" rel="stylesheet">

    <script src="<c:url value="/static/js/jquery.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.ui.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/bootstrap.js" />"></script>
    <script src="<c:url value="/static/js/knockout.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout.mapping.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.ui.touch-punch.js"/>" type="text/javascript"></script>

    <script src="<c:url value="/static/js/concern.edit.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
        $(document).ready(function() {

            baseURL = "${pageContext.request.contextPath}/";
            activeConcern = ${concern.id};

            var model = new PageViewModel();
            model.reload();

            ko.applyBindings(model);

            // Activate help tooltip
            $(".tooltipped").tooltip();
        });
    </script>

    <style type="text/css">
        .tooltip {
            white-space: normal;
        }
        th {
            white-space: nowrap;
        }
    </style>
</head>

<body>

<spring:message var="transportVehicle" code="label.unit.vehicle" />
<spring:message var="withDoc" code="label.unit.withdoc" />
<spring:message var="call" code="label.unit.call"/>
<spring:message var="ani" code="label.unit.ani" />
<spring:message var="portable" code="label.unit.portable" />
<spring:message var="info" code="label.unit.info" />
<spring:message var="home" code="label.unit.home" />

<!-- #################### Start of Page ############## -->
<div class="container">
    <%--
    <div class="">
        <c:url var="welcomeScreen" value="/welcome" />
        <a href="${welcomeScreen}" class="active btn btn-warning">Back</a>
    </div>
    --%>

    <c:set value="active" var="nav_concern" />
    <%@include file="parts/navbar.jsp"%>


    <!--##### TABLE -->
    <div class="page-header">
        <h3>
            <spring:message code="label.concern.edit.unit"/>
            <a href="<c:url value="/edit/container"/>" class="btn btn-default pull-right"><spring:message code="label.container.edit" /></a>
        </h3>
    </div>
    <div>
        <div data-bind="visible: false">Please activate Javascript! Knockout not loaded</div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>${call}</th>
                <th>
                    ${ani}
                    <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.ani"/>"
                          data-toggle="tooltip" data-placement="top"></span>
                </th>
                <th>
                    ${withDoc}
                    <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.withdoc"/>"
                                     data-toggle="tooltip" data-placement="top"></span>
                </th>
                <th>
                    ${transportVehicle}
                    <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.vehicle"/>"
                                              data-toggle="tooltip" data-placement="top"></span>
                </th>
                <th>
                    ${portable}
                    <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.portable"/>"
                                      data-toggle="tooltip" data-placement="top"></span>
                </th>
                <th>${info}</th>
                <th>
                    ${home}
                    <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.home"/>"
                          data-toggle="tooltip" data-placement="top"></span>
                </th>
                <th></th>
                <th class="text-center"><span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.remove_locked"/>"
                          data-toggle="tooltip" data-placement="top"></span></th>
            </tr>
            </thead>
            <tbody>
            <!-- ko foreach: unitlist -->
                <tr>
                     <!-- ko template: 'template-unit-row' -->
                    <!-- /ko -->

                    <td>
                        <input type="submit" name="update" value="<spring:message code="label.update"/>" class="btn btn-success" data-bind="enable: localChange, click: save">
                    </td>
                    <td>
                        <input type="submit" name="remove" value="<spring:message code="label.remove"/>" class="btn btn-danger" data-bind="disable: locked, click: function() { $root.remove(id()) }">
                    </td>
                </tr>
            <!-- /ko -->


                <tr data-bind="with: newUnit">
                    <!-- ko template: 'template-unit-row' -->
                    <!-- /ko -->
                    <td>
                        <input type="submit" value="<spring:message code="label.create"/>" class="btn btn-success" data-bind="click: function() { $root.create() }, enable: call.localChange">
                    </td>
                    <td>

                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <script type="text/html" id="template-unit-row">
        <td>
            <input type="text" name="call" maxlength="64" class="form-control"
                   placeholder="${call}" data-bind="value: call" required>
        </td>
        <td>
            <input type="text" name="ani" maxlength="64" class="form-control" placeholder="${ani}" data-bind="value: ani">
        </td>
        <td>
            <button class="btn btn-default"
                    data-bind="click: function() { withDoc(!withDoc()) }, css: {active: withDoc}">${withDoc}</button>
        </td>
        <td>
            <button class="btn btn-default"
                    data-bind="click: function() { transportVehicle(!transportVehicle()) }, css: {active: transportVehicle}">
                    ${transportVehicle}
            </button>
        </td>
        <td>
            <button class="btn btn-default"
                    data-bind="click: function() { portable(!portable()) }, css: {active: portable}">
                    ${portable}
            </button>
        </td>
        <td>
            <input type="text" name="info" class="form-control"
                   placeholder="${info}" data-bind="value: info">
        </td>
        <td>
            <input type="text" name="home" class="form-control"
                   placeholder="${home}" data-bind="value: home.info">
        </td>
    </script>


    <div class="page-header">
        <h3><spring:message code="label.concern.edit"/></h3>
    </div>

    <%-- Show Error Message --%>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissable">
            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
            <strong><spring:message code="label.error" />:</strong> <spring:message code="label.error.${error}" />
        </div>
    </c:if>

    <div>
        <input type="hidden" name="id" value="${concern.id}">

        <div class="row">
            <div class="col-lg-3">
                <div class="form-group">
                    <label for="case_name"><spring:message code="label.concern.name"/></label>
                    <input type="text" id="case_name" name="name" value="${concern.name}"
                           maxlength="64" class="form-control" required>
                </div>
            </div>

            <div class="col-lg-3">
                <div class="form-group">
                    <label for="case_pax"><spring:message code="label.concern.pax"/></label>
                    <input type="number" id="case_pax" name="pax" value="${concern.pax}"
                           class="form-control input-sm" min="0" max="2000000000">
                </div>
            </div>
        </div>
        <div class="row">
            <div class="form-group col-lg-8">
                <label for="case_organiser"><spring:message code="label.concern.info"/></label>
                <textarea id="case_organiser" name="info" class="form-control" rows="5">${concern.info}</textarea>
            </div>
        </div>

        <div class="form-group">
            <input class="btn btn-success" type="submit" name="submit" value="<spring:message code="label.update"/>">
        </div>

    </div>



    <div class="page-header">
        <h3><spring:message code="label.concern.edit.batch"/></h3>
    </div>
    <div>
        <div class="row">
            <div class="col-lg-5">
                <div class="form-group">
                    <label for="batch_call">${call} <spring:message code="label.prefix"/></label>
                    <input type="text" id="batch_call" name="call_pre" maxlength="50" class="form-control"
                           placeholder="${call} Prefix">
                </div>
            </div>

            <div class="col-lg-3 text-center form-control-static">
                <strong><spring:message code="label.range"/></strong>
            </div>
            <div class="col-lg-2">
                <div class="form-group">
                    <label for="batch_from"><spring:message code="label.from"/></label>
                    <input type="number" id="batch_from" name="from" value="1" class="form-control">
                </div>
            </div>

            <div class="col-lg-2">
                <div class="form-group">
                    <label for="batch_to"><spring:message code="label.to"/></label>
                    <input type="number" id="batch_to" name="to" value="5" class="form-control">
                </div>
            </div>
        </div>


        <div class="row">
            <%--div class="col-lg-1">
                &nbsp;
            </div--%>
            <div class="col-lg-4 form-group">
                <label for="batch_home">${home}</label>
                <input type="text" id="batch_home" name="home" class="form-control"
                       placeholder="${home}">

            </div>

            <div class="col-lg-4 btn-group text-center" data-toggle="buttons">
                <label class="btn btn-default">
                    <input type="checkbox" id="batch_doc" name="withDoc">
                    ${withDoc}
                </label>

                <label class="btn btn-default">
                    <input type="checkbox" id="batch_vhcl" name="transportVehicle">
                    ${transportVehicle}
                </label>

                <label class="btn btn-default">
                    <input type="checkbox" id="batch_portable" name="portable">
                    ${portable}
                </label>
            </div>


        </div>
        <div class="form-group">
            <input type="submit" value="<spring:message code="label.create"/>" class="btn btn-success">
        </div>
    </div>

</div>


</body>
</html>
