<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML index
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<html>
  <head>
    <t:head maintitle="patadmin" entry="navbar"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <h2><spring:message code="patadmin"/>: <em><c:out value="${concern.name}"/></em></h2>

      <p>
        <c:if test="${accessLevels[0]}">
          <a class="btn btn-success" href="<c:url value="/patadmin/settings"/>"><spring:message code="patadmin.settings"/></a>
        </c:if>
        <c:if test="${accessLevels[1]}">
          <a class="btn btn-success" href="<c:url value="/patadmin/triage"/>"><spring:message code="patadmin.triage"/></a>
        </c:if>
        <c:if test="${accessLevels[2]}">
          <a class="btn btn-success" href="<c:url value="/patadmin/postprocessing"/>"><spring:message code="patadmin.postprocessing"/></a>
        </c:if>
        <c:if test="${accessLevels[3]}">
          <a class="btn btn-success" href="<c:url value="/patadmin/info"/>"><spring:message code="patadmin.info"/></a>
        </c:if>
      </p>
    </div>
  </body>
</html>
