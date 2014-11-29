<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard incident detail view
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

<c:url var="get_unit" value="?iid=${incident.id}&amp;uid="/>

<h2><spring:message code="label.incident"/> #<c:out value="${incident.id}"/></h2>

<div class="clearfix">
  <div class="col-md-6">
    <%@include file="incident_props.jsp"%>
  </div>
  <c:if test="${not empty units}">
    <div class="col-md-6">
      <h4><spring:message code="label.units"/></h4>
      <dl class="dashboard-list dl-horizontal">
        <c:forEach items="${units}" var="unit">
          <dt><c:out value="${unit.key.call}"/></dt>
          <dd>
            <a href="${get_unit}${unit.key.id}"><spring:message code="label.task.state.${fn:toLowerCase(unit.value)}" text="${unit.value}"/></a>
          </dd>
        </c:forEach>
      </dl>
    </div>
  </c:if>
</div>

<h3><spring:message code="label.log"/></h3>
<table class="table table-striped table-hover">
  <thead>
    <tr>
      <th><spring:message code="label.log.timestamp"/></th>
      <th><spring:message code="label.operator"/></th>
      <th><spring:message code="label.log.text"/></th>
      <th><spring:message code="label.unit"/></th>
      <th><spring:message code="label.task.state"/></th>
      <th><spring:message code="label.log.changes"/></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${logs}" var="log">
      <tr>
        <td><fmt:formatDate type="both" dateStyle="short" timeStyle="medium" value="${log.timestamp}"/></td>
        <td><c:out value="${log.user.username}"/></td>
        <td><t:logtext log="${log}"/></td>
        <td>
          <c:if test="${not empty log.unit}">
            <a href="${get_unit}${log.unit.id}"><c:out value="${log.unit.call}"/></a>
          </c:if>
        </td>
        <td>
          <c:if test="${not empty log.state}">
            <spring:message code="label.task.state.${fn:toLowerCase(log.state)}" text="${log.state}"/>
          </c:if>
        </td>
        <td><t:changes container="${log.changes}"/></td>
      </tr>
    </c:forEach>
  </tbody>
</table>
