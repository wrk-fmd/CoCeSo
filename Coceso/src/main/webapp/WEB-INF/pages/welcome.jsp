<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
  <head>
    <title><spring:message code="label.coceso"/> - <spring:message code="label.nav.home"/></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">
    <link href="<c:url value="/static/css/coceso.css" />" rel="stylesheet">

    <script src="<c:url value="/static/js/assets/jquery.min.js" />" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/bootstrap.min.js" />" type="text/javascript"></script>
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
        <p><strong><spring:message code="label.operator"/>:</strong> ${user.given_name} ${user.sur_name} (${user.username})</p>
        <p>Roles:</p>
        <ul>
          <c:forEach items="${user.authorities}" var="role">
            <li>${role}</li>
            </c:forEach>
        </ul>
      </div>

      <%-- Last Active Concern --%>
      <c:if test="${not empty activeConcern}">
        <div class="alert alert-success">
          <p><strong><spring:message code="label.concern.lastActive" />:</strong>&nbsp;${activeConcern.name}</p>
          <p><a href="<c:url value="/main"/>" class="btn btn-success"><spring:message code="label.start" /></a></p>
        </div>
      </c:if>

      <%-- Create new Concern --%>
      <div class="page-header col-md-12">
        <h2><spring:message code="label.concern.create"/></h2>
      </div>
      <form class="clearfix" action="<c:url value="/edit/create"/>" method="post" role="form">
        <div class="col-md-offset-2 col-md-5 form-group">
          <label class="sr-only" for="new_name"><spring:message code="label.concern.name"/></label>
          <input type="text" id="new_name" name="name" maxlength="64" class="form-control"
                 placeholder="<spring:message code="label.concern.name"/>" required/>
        </div>
        <div class="col-md-3">
          <input type="submit" value="<spring:message code="label.create"/>" class="btn btn-success"/>
        </div>
      </form>

      <%-- Active Concerns --%>
      <div class="page-header col-md-12">
        <h2><spring:message code="label.concerns"/></h2>
      </div>
      <div class="col-md-offset-2 col-md-6 clearfix">
        <form action="<c:url value="/welcome"/>" method="post" role="form">
          <div class="row form-group">
            <select name="case_id" size="10" class="form-control">
              <c:forEach var="caze" items="${concern_list}">
                <option value="${caze.id}">${caze.name}</option>
              </c:forEach>
            </select>
          </div>

          <div class="row form-group">
            <input type="submit" value="<spring:message code="label.start"/>" name="start" class="btn btn-success">
            <input type="submit" value="<spring:message code="label.edit"/>" name="edit" class="btn btn-warning">
            <c:if test="${not empty authorized}">
              <input type="submit" value="<spring:message code="label.close"/>" name="close" class="btn btn-danger">
            </c:if>
            <input type="submit" value="<spring:message code="label.print"/>" name="print" class="btn btn-primary">
            <input type="submit" value="<spring:message code="label.dump"/>" name="dump" class="btn btn-primary">
          </div>
        </form>
      </div>

      <%-- Closed Concerns --%>
      <div class="page-header col-md-12">
        <h2><spring:message code="label.concern.closed"/></h2>
      </div>
      <div class="col-md-offset-2 col-md-6 clearfix">
        <form action="<c:url value="/welcome"/>" method="post" role="form">
          <div class="row form-group">
            <select name="case_id" size="10" class="form-control">
              <c:forEach var="caze" items="${closed_concern_list}">
                <option value="${caze.id}">${caze.name}</option>
              </c:forEach>
            </select>
          </div>

          <div class="row form-group">
            <input type="submit" value="<spring:message code="label.print"/>" name="print" class="btn btn-success">
            <input type="submit" value="<spring:message code="label.transportlist"/>" name="transportlist" class="btn btn-warning">
            <c:if test="${not empty authorized}">
              <input type="submit" value="<spring:message code="label.reopen"/>" name="reopen" class="btn btn-danger">
            </c:if>
          </div>
        </form>
      </div>

      <div class="page-header col-md-12">
        &nbsp; <%--Just for a nicer view --%>
      </div>
    </div>
  </body>
</html>
