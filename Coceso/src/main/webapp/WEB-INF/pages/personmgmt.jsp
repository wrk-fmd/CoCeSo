<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.coceso" /> - <spring:message code="label.person.mgmt" /></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8" />

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">
    <!-- Bootstrap -->

    <%-- For Autocomplete!! --%>
    <link href="<c:url value='/static/css/ui-bootstrap/jquery-ui-1.10.3.custom.css' />" rel="stylesheet">

    <link href="<c:url value="/static/css/bootstrap.css" />" rel="stylesheet">
    <link href="<c:url value="/static/css/bootstrap-theme.css" />" rel="stylesheet">
    <link href="<c:url value="/static/css/flags.css"/>" rel="stylesheet" />


    <script src="<c:url value='/static/js/jquery.js' />"></script>
    <script src="<c:url value='/static/js/bootstrap.js' />"></script>
    <script src="<c:url value='/static/js/jquery.ui.js' />"></script>


    <script>
        $(function() {
            var user = [
                <c:forEach items="${persons}" var="person" varStatus="loop">
                {
                    value: ${person.id},
                    label: "${person.dNr}: ${person.given_name} ${person.sur_name}",
                    desc: ""
                }<c:if test="${!loop.last}">,</c:if>
                </c:forEach>
            ];

            $( "#person" ).autocomplete({
                minLength: 0,
                source: user,
                focus: function( event, ui ) {
                    $( "#person" ).val( ui.item.label );
                    return false;
                },
                select: function( event, ui ) {
                    $( "#person" ).val( ui.item.label );
                    $( "#person-id" ).val( ui.item.value );
                    $( "#person-description" ).html( ui.item.desc );

                    return false;
                }
            })
                    .data( "ui-autocomplete" )._renderItem = function( ul, item ) {
                return $( "<li>" )
                        .append( "<a>" + item.label + "<br>" + item.desc + "</a>" )
                        .appendTo( ul );
            };

            <c:if test="${not empty operators}">
            var operator = [
                <c:forEach items="${operators}" var="op" varStatus="loop">
                {
                    value: ${op.id},
                    label: "${op.dNr}: ${op.given_name} ${op.sur_name}",
                    desc: "<c:forEach items="${op.authorities}" var="auth" varStatus="l2">${auth}<c:if test="${!l2.last}"> - </c:if></c:forEach>"
                }<c:if test="${!loop.last}">,</c:if>
                </c:forEach>
            ];
            $( "#operator" ).autocomplete({
                minLength: 0,
                source: operator,
                focus: function( event, ui ) {
                    $( "#operator" ).val( ui.item.label );
                    return false;
                },
                select: function( event, ui ) {
                    $( "#operator" ).val( ui.item.label );
                    $( "#operator-id" ).val( ui.item.value );
                    $( "#operator-description" ).html( ui.item.desc );

                    return false;
                }
            })
                    .data( "ui-autocomplete" )._renderItem = function( ul, item ) {
                return $( "<li>" )
                        .append( "<a>" + item.label + "<br>" + item.desc + "</a>" )
                        .appendTo( ul );
            };
            </c:if>
        });
    </script>

</head>

<body>

<div class="container">
    <%-- NAVBAR --%>
    <c:set value="active" var="nav_person" />
    <%@include file="parts/navbar.jsp"%>

    <%-- ERROR MESSAGE --%>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <strong><spring:message code="label.error"/>:</strong><br>${error} <%-- TODO error via i18 --%>
        </div>
    </c:if>

    <%-- Search for existing Person --%>
    <div class="page-header">
        <h2>
            <spring:message code="label.person.search"/>
        </h2>
    </div>
    <div class="row">
        <form role="form" action="<c:url value='/edit/person/update'/>?" class="form-inline">
            <div class="col-lg-3 form-group">
                <label for="person"><spring:message code="label.person.select"/>:</label>
                <input type="text" id="person" class="form-control">
                <p id="person-description" class="hidden"></p>
            </div>
            <div class="col-lg-2">
                    <input type="hidden" id="person-id" name="id" value="-1">
                    <input type="submit" class="btn btn-success" value="<spring:message code='label.edit'/>">
            </div>
        </form>
    </div>


    <%-- Edit Operator --%>
    <c:if test="${not empty operators}">
        <%-- Edit existing Operator --%>
        <div class="page-header">
            <h2>
                <spring:message code="label.operator.search" />
            </h2>
        </div>
        <div class="row">
            <form role="form" action="<c:url value='/edit/person/update'/>?" class="form-inline">
                <div class="col-lg-3 form-group">
                    <label for="operator"><spring:message code="label.operator.select"/>:</label>
                    <input type="text" id="operator" class="form-control">
                    <label for="operator-description"><spring:message code="label.operator.roles" /></label>
                    <p id="operator-description" class="form-control-static"></p>
                </div>
                <div class="col-lg-2">
                    <input type="hidden" id="operator-id" name="id" value="-1">
                    <input type="submit" class="btn btn-success" value="<spring:message code='label.edit'/>">
                </div>
            </form>
        </div>
    </c:if>

    <%-- Create new Person --%>
    <div class="page-header">
        <h2>
            <spring:message code="label.person.create"/>
        </h2>
    </div>
    <div class="row">
        <form role="form" action="<c:url value='/edit/person/create'/>" class="form-inline" method="POST">
            <div class="col-lg-3 form-group">
                <label>
                    <input type="text" name="given_name" class="form-control" placeholder="<spring:message code='label.person.given_name'/>">
                </label>
            </div>
            <div class="col-lg-3 form-group">
                <label>
                    <input type="text" name="sur_name" class="form-control" placeholder="<spring:message code='label.person.sur_name'/>">
                </label>
            </div>
            <div class="col-lg-1">
                <input class="form-control" placeholder="<spring:message code='label.person.dnr'/>" name="dNr">
            </div>
            <div class="col-lg-2">
                <input type="hidden" id="new_id" name="id" value="-1">
                <input type="submit" class="btn btn-success" value="<spring:message code='label.create'/>">
            </div>
        </form>
    </div>

</div>


</body>
</html>
