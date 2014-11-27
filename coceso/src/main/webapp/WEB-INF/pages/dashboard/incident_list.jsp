<%@page import="at.wrk.coceso.entity.Unit"%>
<%@page import="java.util.Map"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard unit list view
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

<c:url var="get_inc" value="?iid="/>
<table class="table table-striped">
  <thead>
    <tr>
      <th><spring:message code="label.unit.call"/></th>
      <th><spring:message code="label.incident.type"/></th>
      <th><spring:message code="label.incident.bo"/>/<spring:message code="label.incident.ao"/></th>
      <th><spring:message code="label.incident.state"/></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${incidents}" var="inc">
      <tr>
        <td><a href="${get_inc}${inc.id}"><c:out value="${inc.id}"/></a></td>
        <td <c:if test="${inc.blue}">class="blue"</c:if>>
          <c:choose>
            <c:when test="${inc.type == 'Task'}">
              <c:choose>
                <c:when test="${inc.blue}"><spring:message code="label.incident.type.task.blue"/></c:when>
                <c:otherwise><spring:message code="label.incident.type.task"/></c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              <spring:message code="label.incident.type.${fn:toLowerCase(inc.type)}"/>
            </c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${inc.type == 'Task' || inc.type == 'Transport'}">
              <c:choose>
                <c:when test="${not empty inc.bo}"><c:out value="${inc.bo}"/></c:when>
                <c:otherwise><spring:message code="label.incident.nobo"/></c:otherwise>
              </c:choose>
              <c:if test="${not empty inc.ao}">
                <span class="glyphicon glyphicon-arrow-right"></span>
                <c:out value="${inc.ao}"/>
              </c:if>
            </c:when>
            <c:otherwise>
              <c:choose>
                <c:when test="${not empty inc.ao}"><c:out value="${inc.ao}"/></c:when>
                <c:otherwise><spring:message code="label.incident.noao"/></c:otherwise>
              </c:choose>
            </c:otherwise>
          </c:choose>
        </td>
        <td><spring:message code="label.incident.state.${fn:toLowerCase(inc.state)}"/></td>
      </tr>
    </c:forEach>
  </tbody>
</table>
