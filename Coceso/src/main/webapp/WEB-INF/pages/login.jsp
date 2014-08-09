<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
  <head>
    <title><spring:message code="label.nav.login"/></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">
    <link href="<c:url value='/static/css/coceso.css' />" rel="stylesheet">
  </head>
  <body class="login">
    <div class="container">
      <c:if test="${not empty error}">
        <div class="alert alert-danger">
          Your login attempt was not successful, try again.<br /> <%-- TODO i18 --%>
          Is your Account enabled?
        </div>
      </c:if>

      <form action="<c:url value='j_spring_security_check' />" method="POST">
        <h2><spring:message code="label.login" /></h2>
        <input type="text" class="form-control" name="j_username" placeholder="<spring:message code="label.username"/>" required autofocus>
        <input type="password" class="form-control" name="j_password" placeholder="<spring:message code="label.password"/>" required>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Login</button>
      </form>
    </div>
  </body>
</html>
