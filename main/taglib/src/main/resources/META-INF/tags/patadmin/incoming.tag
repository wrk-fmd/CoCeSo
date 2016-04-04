<%@tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@attribute name="incidents" required="true" rtexprvalue="true" type="java.util.List<at.wrk.coceso.entity.Incident>"%>
<%@attribute name="hideGroup" required="false" rtexprvalue="true" type="Boolean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML triage incoming patients list tag
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

<c:url var="editUrl" value="/patadmin/triage/takeover/"/>

<div class="table-responsive">
  <table class="table table-striped table-condensed">
    <tr>
      <th><spring:message code="patient.id"/></th>
      <th><spring:message code="patient.externalId"/></th>
      <th><spring:message code="patient.lastname"/></th>
      <th><spring:message code="patient.firstname"/></th>
        <c:if test="${not hideGroup}">
        <th><spring:message code="patient.target"/></th>
        </c:if>
      <th><spring:message code="units"/></th>
      <th></th>
    </tr>
    <c:forEach items="${incidents}" var="incident">
      <tr>
        <c:choose>
          <c:when test="${not empty incident.patient}">
            <td><c:out value="${incident.patient.id}"/></td>
            <td><c:out value="${incident.patient.externalId}"/></td>
            <td><c:out value="${incident.patient.lastname}"/></td>
            <td><c:out value="${incident.patient.firstname}"/></td>
          </c:when>
          <c:otherwise>
            <td></td><td></td><td></td><td></td>
          </c:otherwise>
        </c:choose>
        <c:if test="${not hideGroup}">
          <td><c:out value="${incident.ao}"/></td>
        </c:if>
        <td>
          <c:forEach items="${incident.units}" var="unit">
            <c:out value="${unit.key.call}"/>
            (<spring:message code="task.state.${fn:toLowerCase(unit.value)}" text="${unit.value}"/>)
          </c:forEach>
        </td>
        <td>
          <a href="${editUrl}${incident.id}" class="btn btn-default btn-xs">
            <spring:message code="patient.takeover"/>
          </a>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>
