<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>
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
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<html>
  <head>
    <title><spring:message code="label.coceso"/>: <spring:message code="label.nav.login"/></title>
    <t:head nojs="true"/>
  </head>
  <body class="login">
    <div class="container">
      <c:if test="${not empty error}">
        <div class="alert alert-danger">
          <spring:message code="label.loginfailed"/>
        </div>
      </c:if>

      <form action="<c:url value="j_spring_security_check"/>" method="POST">
        <h2><spring:message code="label.login"/></h2>
        <input type="text" class="form-control" name="j_username" placeholder="<spring:message code="label.username"/>" required autofocus/>
        <input type="password" class="form-control" name="j_password" placeholder="<spring:message code="label.password"/>" required>
        <button class="btn btn-lg btn-primary btn-block" type="submit"><spring:message code="label.nav.login"/></button>
      </form>
    </div>
  </body>
</html>