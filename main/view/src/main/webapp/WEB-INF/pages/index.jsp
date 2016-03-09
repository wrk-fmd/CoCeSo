<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%@page session="false"%>
<%--
/**
 * CoCeSo
 * Client HTML index page
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
    <t:head/>
  </head>
  <body>
    <div class="container">
      <div class="page-header">
        <h1><spring:message code="welcome"/></h1>
      </div>
      <div>
        <spring:message code="welcome.text"/>
      </div>

      <div class="page-header">
        <h3><spring:message code="getting_started"/></h3>
      </div>
      <div>
        <spring:message code="getting_started.text"/>
      </div>

      <div class="page-header">
        <h3><spring:message code="nav.main"/></h3>
      </div>
      <div>
        <a href="<c:url value="/home"/>" class="btn btn-lg btn-success"><spring:message code="login"/></a>
      </div>

      <div class="page-header">
        <h3><spring:message code="license"/></h3>
      </div>
      <div>
        <a href="<c:url value="/static/license.html"/>" target="_blank"><spring:message code="license.text"/></a>
      </div>

      <div class="page-header"></div>
    </div>
  </body>
</html>
