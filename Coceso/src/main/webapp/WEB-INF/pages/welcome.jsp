<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="label.coceso"/> - <spring:message code="label.nav.home"/></title>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">

    <link href="<c:url value="/static/css/bootstrap.css" />" rel="stylesheet">
    <link href="<c:url value="/static/css/bootstrap-theme.css" />" rel="stylesheet">
    <link href="<c:url value="/static/css/flags.css"/>" rel="stylesheet" />

</head>
<body>

<div class="container">

    <c:set value="active" var="nav_home" />
    <%@include file="parts/navbar.jsp"%>

    <%-- Show Error Message --%>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissable">
            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
            <strong><spring:message code="label.error" />:</strong> <spring:message code="label.error.${error}" />
        </div>
    </c:if>

    <%-- Userdetails -- DEBUG --%>
    <div class="alert alert-info alert-dismissable">
        <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
        <strong><spring:message code="label.operator"/>:</strong> ${user.given_name} ${user.sur_name} (${user.username})&nbsp;&nbsp;
        Roles: <c:forEach items="${user.authorities}" var="role">${role} - </c:forEach>
    </div>

    <%-- Last Active Concern --%>
    <c:if test="${not empty activeConcern}">
        <div class="alert alert-success">
            <strong><spring:message code="label.concern.lastActive" />:</strong>&nbsp;${activeConcern.name}
            <a href="<c:url value="/main/"/>" class="btn btn-success"><spring:message code="label.start" /></a>
        </div>
    </c:if>

    <%-- Create new Concern --%>
    <div class="page-header">
        <h2>
            <spring:message code="label.concern.create"/>
        </h2>
    </div>
    <div>
        <form action="${pageContext.request.contextPath}/edit/create" method="post" role="form">
            <div class="row">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-5">
                    <label class="sr-only" for="new_name"><spring:message code="label.concern.name"/></label>
                    <input type="text" id="new_name" name="name" maxlength="64" class="form-control"
                           placeholder="<spring:message code="label.concern.name"/>" required/>
                </div>
                <!--div>
                    <Info of Organiser: <input type="text" name="info" maxlength="64" /><br>>
                </div-->
                <div class="col-lg-3">
                    <input type="submit" value="<spring:message code="label.create"/>" class="btn btn-success"/>
                </div>
            </div>


        </form>
    </div>

    <%-- Active Concerns --%>
    <div class="page-header">
        <h2>
            <spring:message code="label.concerns"/>
        </h2>
    </div>
    <div>
        <form action="${pageContext.request.contextPath}/welcome" method="post" role="form">
            <div class="row form-group">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-6">
                    <select name="case_id" size="10" class="form-control">
                        <c:forEach var="caze" items="${concern_list}">
                            <option value="${caze.id}">${caze.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="row form-group">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-2 text-center">
                    <input type="submit" value="<spring:message code="label.start"/>" name="start" class="btn btn-success">
                </div>
                <div class="col-lg-2 text-center">
                    <input type="submit" value="<spring:message code="label.edit"/>" name="edit" class="btn btn-warning">
                </div>
                <c:if test="${not empty authorized}">
                    <div class="col-lg-2 text-center">
                        <input type="submit" value="<spring:message code="label.close"/>" name="close" class="btn btn-danger">
                    </div>
                </c:if>
            </div>

            <div class="row form-group">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-3 text-center">
                    <input type="submit" value="<spring:message code="label.print"/>" name="print" class="btn btn-primary">
                </div>
                <div class="col-lg-3 text-center">
                    <input type="submit" value="<spring:message code="label.dump"/>" name="dump" class="btn btn-primary" disabled>
                </div>
            </div>
        </form>
    </div>

    <%-- Closed Concerns --%>
    <div class="page-header">
        <h2>
            <spring:message code="label.concern.closed"/>
        </h2>
    </div>

    <div>
        <form action="${pageContext.request.contextPath}/welcome" method="post" role="form">
            <div class="row">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-6">
                    <select name="case_id" size="10" class="form-control">
                        <c:forEach var="caze" items="${closed_concern_list}">
                            <option value="${caze.id}">${caze.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-2">
                    &nbsp;
                </div>
                <div class="col-lg-2 text-center">
                    <input type="submit" value="<spring:message code="label.print"/>" name="print" class="btn btn-primary">
                </div>
                <c:if test="${not empty authorized}">
                    <div class="col-lg-2 text-center">
                        <input type="submit" value="<spring:message code="label.reopen"/>" name="reopen" class="btn btn-danger">
                    </div>
                </c:if>
            </div>
        </form>
    </div>


    <div class="page-header">
        &nbsp; <%--Just for a nicer view --%>
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