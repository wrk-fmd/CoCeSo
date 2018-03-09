<%@tag body-content="empty"%>
<%@attribute name="incident" required="true" rtexprvalue="true" type="at.wrk.coceso.entity.Incident"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@tag trimDirectiveWhitespaces="true"%>
<%--
/**
 * CoCeSo
 * Client HTML incident title tag
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>

<span <c:if test="${incident.blue}">class="blue"</c:if>>
  <c:choose>
    <c:when test="${incident.type == IncidentType.Task}">
      <c:choose>
        <c:when test="${incident.blue}"><spring:message code="incident.stype.task.blue"/>:</c:when>
        <c:otherwise><spring:message code="incident.stype.task"/>:</c:otherwise>
      </c:choose>
    </c:when>
    <c:when test="${incident.type == IncidentType.HoldPosition}"><span class="glyphicon glyphicon-record"></span>:</c:when>
    <c:when test="${incident.type == IncidentType.Standby}"><span class="glyphicon glyphicon-pause"></span>:</c:when>
    <c:otherwise>
      <spring:message code="incident.stype.${fn:toLowerCase(incident.type)}" text="${incident.type}"/>:
    </c:otherwise>
  </c:choose>
  <c:choose>
    <c:when test="${incident.type == IncidentType.Task || incident.type == IncidentType.Transport}">
      <c:choose>
        <c:when test="${not empty incident.bo}"><c:out value="${incident.bo}"/></c:when>
        <c:otherwise><spring:message code="incident.nobo"/></c:otherwise>
      </c:choose>
      <c:if test="${not empty incident.ao}">
        <span class="glyphicon glyphicon-arrow-right"></span>
        <c:out value="${incident.ao}"/>
      </c:if>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${not empty incident.ao}"><c:out value="${incident.ao}"/></c:when>
        <c:otherwise><spring:message code="incident.noao"/></c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</span>
