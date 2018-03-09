<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%@page session="false"%>
<%--
/**
 * CoCeSo
 * Client HTML login page
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<html>
  <head>
    <t:head title="login"/>
  </head>
  <body class="login">
    <div class="container">
      <c:if test="${not empty error}">
        <div class="alert alert-danger">
          <spring:message code="login.failed"/>
        </div>
      </c:if>

      <form action="<c:url value="login"/>" method="POST">
        <h2><spring:message code="login.header"/></h2>
        <input type="text" class="form-control" name="username" placeholder="<spring:message code="user.username"/>" required autofocus/>
        <input type="password" class="form-control" name="password" placeholder="<spring:message code="user.password"/>" required>
        <button class="btn btn-lg btn-primary btn-block" type="submit"><spring:message code="login"/></button>
      </form>
    </div>
  </body>
</html>
