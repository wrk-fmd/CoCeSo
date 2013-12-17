<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="label.coceso"/> - <spring:message code="label.concern.edit"/>: ${caze.name}</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <c:url var="bootstrap" value="/static/css/bootstrap.css" />
    <link href="${bootstrap}" rel="stylesheet">

    <c:url var="bootstrap_theme" value="/static/css/bootstrap-theme.css" />
    <link href="${bootstrap_theme}" rel="stylesheet">

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
        <h2><spring:message code="label.concern.edit.unit"/></h2>
    </div>
    <div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th style="display: none;">ID</th>
                <th>${call}</th>
                <th>${ani}</th>
                <th>${withDoc}</th>
                <th>${transportVehicle}</th>
                <th>${portable}</th>
                <th>${info}</th>
                <th>${home}</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
            <tbody>
                <c:url value="/edit/updateUnit" var="updateUnit" />
                <c:forEach items="${unit_list}" var="unit">
                    <form action="${updateUnit}" method="post" class="form-inline" role="form">
                        <tr>
                            <td style="display: none;"><input type="hidden" name="id" value="${unit.id}"></td>
                            <td>
                                <input type="text" name="call" value="${unit.call}" maxlength="64" class="form-control"
                                        placeholder="${call}">
                            </td>
                            <td>
                                <input type="text" name="ani" value="${unit.ani}" maxlength="16" class="form-control"
                                        placeholder="${ani}">
                            </td>
                            <td>
                                <div class="btn-group" data-toggle="buttons">
                                    <c:choose>
                                        <c:when test="${unit.withDoc}">
                                            <label class="btn btn-default active">
                                            <input type="checkbox" name="withDoc" checked>${withDoc}
                                        </c:when>
                                        <c:otherwise>
                                            <label class="btn btn-default">
                                            <input type="checkbox" name="withDoc">${withDoc}
                                        </c:otherwise>
                                    </c:choose>

                                    </label>
                                </div>
                            </td>
                            <td>
                                <div class="btn-group" data-toggle="buttons">
                                    <c:choose>
                                        <c:when test="${unit.transportVehicle}">
                                            <label class="btn btn-default active">
                                            <input type="checkbox" name="transportVehicle" checked>${transportVehicle}
                                        </c:when>
                                        <c:otherwise>
                                            <label class="btn btn-default">
                                            <input type="checkbox" name="transportVehicle">${transportVehicle}
                                        </c:otherwise>
                                    </c:choose>
                                    </label>
                                </div>
                            </td>
                            <td>
                                <div class="btn-group" data-toggle="buttons">
                                        <c:choose>
                                            <c:when test="${unit.portable}">
                                                <label class="btn btn-default active">
                                                <input type="checkbox" name="portable" checked>${portable}
                                            </c:when>
                                            <c:otherwise>
                                                <label class="btn btn-default">
                                                <input type="checkbox" name="portable">${portable}
                                            </c:otherwise>
                                        </c:choose>
                                    </label>
                                </div>
                            </td>
                            <td>
                                <input type="text" name="info" value="${unit.info}" maxlength="128" class="form-control"
                                        placeholder="${info}">
                            </td>
                            <td>
                                <input type="text" name="home" value="${unit.home}" class="form-control"
                                        placeholder="${home}">
                            </td>
                            <td>
                                <input type="submit" name="update" value="<spring:message code="label.update"/>" class="btn btn-success">
                            </td>
                            <td>
                                <input type="submit" name="remove" value="<spring:message code="label.remove"/>" class="btn btn-danger">
                            </td>
                        </tr>
                    </form>
                </c:forEach>

                <c:url value="/edit/createUnit" var="createUnit" />
                <form action="${createUnit}" method="post" class="form-inline" role="form">
                    <tr>
                        <td style="display: none;"></td>
                        <td>
                            <label class="sr-only" for="new_call">${call}</label>
                            <input type="text" id="new_call" name="call" maxlength="64" class="form-control"
                                   placeholder="${call}">
                        </td>
                        <td>
                            <label class="sr-only" for="new_ani">${ani}</label>
                            <input type="text" id="new_ani" name="ani" maxlength="16" class="form-control" placeholder="${ani}">
                        </td>
                        <td>
                            <div class="btn-group" data-toggle="buttons">
                                <label class="btn btn-default">
                                    <input type="checkbox" name="withDoc">${withDoc}
                                </label>
                            </div>
                        </td>
                        <td>
                            <div class="btn-group" data-toggle="buttons">
                                <label class="btn btn-default">
                                    <input type="checkbox" name="transportVehicle">${transportVehicle}
                                </label>
                                </div>
                        </td>
                        <td>
                            <div class="btn-group" data-toggle="buttons">
                                <label class="btn btn-default">
                                    <input type="checkbox" name="portable">${portable}
                                </label>
                            </div>
                        </td>
                        <td>
                            <label class="sr-only" for="new_info">${info}</label>
                            <input type="text" id="new_info" name="info" maxlength="128" class="form-control"
                                   placeholder="${info}">
                        </td>
                        <td>
                            <label class="sr-only" for="new_home">${home}</label>
                            <input type="text" id="new_home" name="home" class="form-control"
                                   placeholder="${home}">
                        </td>
                        <td>
                            <input type="submit" value="<spring:message code="label.create"/>" class="btn btn-success">
                        </td>
                        <td>

                        </td>
                    </tr>
                </form>
            </tbody>
        </table>
    </div>

    <div class="page-header">
        <h2><spring:message code="label.concern.edit"/></h2>
    </div>

    <div>
        <c:url value="/edit/update" var="update" />
        <form action="${update}" method="post" role="form">
            <input type="hidden" name="id" value="${caze.id}">

            <div class="row">
                <div class="col-lg-1">
                    &nbsp;
                </div>
                <div class="col-lg-3">
                    <div class="form-group">
                        <label for="case_name"><spring:message code="label.concern.name"/></label>
                        <input type="text" id="case_name" name="name" value="${caze.name}"
                               maxlength="64" class="form-control">
                    </div>
                </div>

                <div class="col-lg-3">
                    <div class="form-group">
                        <label for="case_pax"><spring:message code="label.concern.pax"/></label>
                        <input type="number" id="case_pax" name="pax" value="${caze.pax}"
                               class="form-control input-sm" min="0" max="2000000000">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="form-group col-lg-8">
                    <label for="case_organiser"><spring:message code="label.concern.info"/></label>
                    <textarea id="case_organiser" name="info" maxlength="128" class="form-control" rows="5">${caze.info}</textarea>
                </div>
            </div>

            <div class="form-group">
                <input class="btn btn-success" type="submit" name="submit" value="<spring:message code="label.update"/>">
            </div>

        </form>
    </div>



    <div class="page-header">
        <h2><spring:message code="label.concern.edit.batch"/></h2>
    </div>
    <div>
        <c:url value="/edit/createUnitBatch" var="createUnitBatch" />
        <form action="${createUnitBatch}" method="post" role="form">

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
                <div class="col-lg-4">
                    <label class="sr-only" for="batch_home">${home}</label>
                    <input type="text" id="batch_home" name="home" class="form-control"
                           placeholder="${home}">

                </div>

            </div>
            <div class="form-group">
                <input type="submit" value="<spring:message code="label.create"/>" class="btn btn-success">
            </div>

        </form>
    </div>

</div>


<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<c:url var="jquery" value="/static/js/jquery.js" />
<script src="${jquery}"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<c:url var="bootstrap_js" value="/static/js/bootstrap.js" />
<script src="${bootstrap_js}"></script>

</body>
</html>