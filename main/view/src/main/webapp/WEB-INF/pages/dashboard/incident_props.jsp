<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard incident properties
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

<dl class="list-spacing dl-horizontal">
  <dt><spring:message code="incident.type"/></dt>
  <dd <c:if test="${incident.blue}">class="blue"</c:if>>
    <c:choose>
      <c:when test="${incident.type == 'Task'}">
        <c:choose>
          <c:when test="${incident.blue}"><spring:message code="incident.type.task.blue"/></c:when>
          <c:otherwise><spring:message code="incident.type.task"/></c:otherwise>
        </c:choose>
      </c:when>
      <c:otherwise>
        <spring:message code="incident.type.${fn:toLowerCase(incident.type)}" text="${incident.type}"/>
      </c:otherwise>
    </c:choose>
  </dd>

  <c:if test="${incident.type == 'Task' || incident.type == 'Transport'}">
    <dt><spring:message code="incident.bo"/></dt>
    <dd>
      <c:choose>
        <c:when test="${not empty incident.bo}"><span class="pre"><c:out value="${incident.bo}"/></span></c:when>
        <c:otherwise><spring:message code="incident.nobo"/></c:otherwise>
      </c:choose>
    </dd>
  </c:if>

  <dt><spring:message code="incident.ao"/></dt>
  <dd>
    <c:choose>
      <c:when test="${not empty incident.ao}"><span class="pre"><c:out value="${incident.ao}"/></span></c:when>
      <c:otherwise><spring:message code="incident.noao"/></c:otherwise>
    </c:choose>
  </dd>

  <c:if test="${not empty incident.info}">
    <dt><spring:message code="incident.info"/></dt>
    <dd><span class="pre"><c:out value="${incident.info}"/></span></dd>
  </c:if>

  <c:if test="${not empty incident.caller}">
    <dt><spring:message code="incident.caller"/></dt>
    <dd><c:out value="${incident.caller}"/></dd>
  </c:if>

  <c:if test="${not empty incident.casusNr}">
    <dt><spring:message code="incident.casus"/></dt>
    <dd><c:out value="${incident.casusNr}"/></dd>
  </c:if>

  <dt><spring:message code="incident.state"/></dt>
  <dd><spring:message code="incident.state.${fn:toLowerCase(incident.state)}" text="${incident.state}"/></dd>
</dl>
