<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard patient list view
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>

<c:url var="get_patient" value="?pid="/>
<c:url var="get_unit" value="?uid="/>
<table class="table table-striped table-hover">
  <thead>
    <tr>
      <th><spring:message code="patient.id"/></th>
      <th><spring:message code="patient.externalId"/></th>
      <th><spring:message code="patient.lastname"/></th>
      <th><spring:message code="patient.firstname"/></th>
      <th><spring:message code="patadmin.group"/> / <spring:message code="patadmin.hospital"/></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${patients}" var="patient">
      <tr>
        <td><a href="${get_patient}${patient.id}"><c:out value="${patient.id}"/></a></td>
        <td><c:out value="${patient.externalId}"/></td>
        <td><c:out value="${patient.lastname}"/></td>
        <td><c:out value="${patient.firstname}"/></td>
        <td>
          <c:if test="${not empty patient.group}">
            <c:forEach items="${patient.group}" var="group">
              <a href="${get_unit}${group.id}"><c:out value="${group.call}"/></a>
            </c:forEach>
          </c:if>
          <c:if test="${not empty patient.hospital}">
            <c:forEach items="${patient.hospital}" var="hospital">
              <span class="pre"><c:out value="${hospital}"/></span>
            </c:forEach>
          </c:if>
          <c:if test="${not patient.transport and patient.done}">
            <spring:message code="patient.discharged"/>
          </c:if>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>
