<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML set to transported form
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<html>
  <head>
    <t:head maintitle="patadmin" title="patient.transported"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <form method="post" action="<c:url value="/patadmin/postprocessing/transported"/>" accept-charset="utf-8">
        <input type="hidden" name="patient" value="${patient.id}"/>
        <h2><spring:message code="patient.transported.confirm"/></h2>

        <p><spring:message code="patient"/>: <c:out value="${patient.fullName}"/></p>

        <p>
          <button type="submit" class="btn btn-success"><spring:message code="patient.transported"/></button>
          <a class="btn btn-warning" href="<c:url value="/patadmin/postprocessing/view/${command.patient}"/>"><spring:message code="cancel"/></a>
        </p>
      </form>
    </div>
  </body>
</html>
