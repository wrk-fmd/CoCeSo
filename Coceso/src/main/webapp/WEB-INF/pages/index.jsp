<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
    <title><spring:message code="label.coceso"/></title>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon"/>
    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css"/>
  </head>
  <body>
    <div class="container">
      <div class="page-header">
        <h1><spring:message code="label.welcome"/></h1>
      </div>
      <div>
        <spring:message code="text.welcome"/>
      </div>

      <div class="page-header">
        <h3><spring:message code="label.getting_started"/></h3>
      </div>
      <div>
        <spring:message code="text.getting_started"/>
      </div>

      <div class="page-header">
        <h3><spring:message code="label.nav.main"/></h3>
      </div>
      <div>
        <a href="<c:url value="/home"/>" class="btn btn-lg btn-success"><spring:message code="label.nav.login"/></a>
      </div>

      <div class="page-header">
        <h3><spring:message code="label.main.license"/></h3>
      </div>
      <div>
        <a href="<c:url value="/static/license.html"/>" target="_blank"><spring:message code="text.license"/></a>
      </div>
    </div>
  </body>
</html>
