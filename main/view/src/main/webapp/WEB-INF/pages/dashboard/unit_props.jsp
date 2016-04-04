<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard unit properties
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

<dl class="list-spacing dl-horizontal">
  <dt><spring:message code="unit.call"/></dt>
  <dd><c:out value="${unit.call}"/></dd>

  <dt><spring:message code="unit.state"/></dt>
  <dd><spring:message code="unit.state.${fn:toLowerCase(unit.state)}" text="${unit.state}"/></dd>

  <dt><spring:message code="unit.position"/></dt>
  <dd>
    <c:choose>
      <c:when test="${not empty unit.position}"><span class="pre"><c:out value="${unit.position}"/></span></c:when>
      <c:otherwise>N/A</c:otherwise>
    </c:choose>
  </dd>

  <c:if test="${not empty unit.home}">
    <dt><spring:message code="unit.home"/></dt>
    <dd><span class="pre"><c:out value="${unit.home}"/></span></dd>
  </c:if>

  <c:if test="${not empty unit.info}">
    <dt><spring:message code="unit.info"/></dt>
    <dd><span class="pre"><c:out value="${unit.info}"/></span></dd>
  </c:if>

  <dt><spring:message code="unit.withdoc"/></dt>
  <dd>
    <c:choose>
      <c:when test="${unit.withDoc}"><spring:message code="yes"/></c:when>
      <c:otherwise><spring:message code="no"/></c:otherwise>
    </c:choose>
  </dd>

  <dt><spring:message code="unit.portable"/></dt>
  <dd>
    <c:choose>
      <c:when test="${unit.portable}"><spring:message code="yes"/></c:when>
      <c:otherwise><spring:message code="no"/></c:otherwise>
    </c:choose>
  </dd>

  <dt><spring:message code="unit.vehicle"/></dt>
  <dd>
    <c:choose>
      <c:when test="${unit.transportVehicle}"><spring:message code="yes"/></c:when>
      <c:otherwise><spring:message code="no"/></c:otherwise>
    </c:choose>
  </dd>

  <c:if test="${not empty unit.ani}">
    <dt><spring:message code="unit.ani"/></dt>
    <dd><c:out value="${unit.ani}"/></dd>
  </c:if>
</dl>
