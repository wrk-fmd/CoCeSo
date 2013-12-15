<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Coceso - Person Management</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->

    <link href="<c:url value="/static/css/ui-bootstrap-0.5/jquery-ui-1.10.0.custom.css" />" rel="stylesheet">

    <link href="<c:url value="/static/bootstrap.css" />" rel="stylesheet">

    <link href="<c:url value="/static/bootstrap-theme.css" />" rel="stylesheet">

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->

    <script src="<c:url value="/static/jquery.js" />"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->

    <script src="<c:url value="/static/bootstrap.js" />"></script>

    <script src="<c:url value="/static/js/jquery.ui.js"/>"></script>


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
        });
    </script>

</head>

<body>

<div class="container">
    <c:set value="active" var="nav_person" />
    <%@include file="parts/navbar.jsp"%>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <strong>Error:</strong><br>${error}
        </div>
    </c:if>
    <div class="page-header">
        <h2>
            Search Person
        </h2>
    </div>
    <div class="row">
        <form role="form" action="?" class="form-inline">
            <div class="col-lg-3 form-group">
                <label for="person">Select a Person:</label>
                <input type="text" id="person" class="form-control">
                <p id="person-description" class="hidden"></p>
            </div>
            <div class="col-lg-2">
                    <input type="hidden" id="person-id" name="id" value="-1">
                    <input type="submit" class="btn btn-success" value="Send">
            </div>
        </form>


    </div>
    <div class="page-header">
        <h2>
            Create new Person
        </h2>
    </div>
    <div class="row">
        <form role="form" action="<c:url value="/edit/person/create"/>" class="form-inline" method="POST">
            <div class="col-lg-3 form-group">
                <label>
                    <input type="text" name="given_name" class="form-control" placeholder="First Name">
                </label>
            </div>
            <div class="col-lg-3 form-group">
                <label>
                    <input type="text" name="sur_name" class="form-control" placeholder="Last Name">
                </label>
            </div>
            <div class="col-lg-1">
                <input class="form-control" placeholder="DNr" name="dNr">
            </div>
            <div class="col-lg-2">
                <input type="hidden" id="new_id" name="id" value="-1">
                <input type="submit" class="btn btn-success" value="Send">
            </div>
        </form>


    </div>
</div>


</body>
</html>
