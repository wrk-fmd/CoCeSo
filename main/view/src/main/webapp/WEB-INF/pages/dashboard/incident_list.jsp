<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard incident list view
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

<c:url var="get_inc" value="?iid="/>
<table class="table table-striped table-hover">
  <thead>
    <tr>
      <th>#</th>
      <th><spring:message code="incident.type"/></th>
      <th><spring:message code="incident.bo"/>/<spring:message code="incident.ao"/></th>
      <th><spring:message code="incident.info"/></th>
      <th><spring:message code="incident.state"/></th>
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
                <c:when test="${inc.blue}"><spring:message code="incident.type.task.blue"/></c:when>
                <c:otherwise><spring:message code="incident.type.task"/></c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              <spring:message code="incident.type.${fn:toLowerCase(inc.type)}"/>
            </c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${inc.type == 'Task' || inc.type == 'Transport'}">
              <div class="clearfix">
                <div class="pull-left">
                  <c:choose>
                    <c:when test="${not empty inc.bo}"><span class="pre"><c:out value="${inc.bo}"/></span></c:when>
                    <c:otherwise><spring:message code="incident.nobo"/></c:otherwise>
                  </c:choose>
                </div>
                <c:if test="${not empty inc.ao}">
                  <div class="glyphicon glyphicon-arrow-right pull-left"></div>
                  <div class="pre pull-left"><c:out value="${inc.ao}"/></div>
                </c:if>
              </div>
            </c:when>
            <c:otherwise>
              <c:choose>
                <c:when test="${not empty inc.ao}"><span class="pre"><c:out value="${inc.ao}"/></span></c:when>
                <c:otherwise><spring:message code="incident.noao"/></c:otherwise>
              </c:choose>
            </c:otherwise>
          </c:choose>
        </td>
        <td class="pre"><c:out value="${inc.info}"/></td>
        <td><spring:message code="incident.state.${fn:toLowerCase(inc.state)}"/></td>
      </tr>
    </c:forEach>
  </tbody>
</table>
