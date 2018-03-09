<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard incident/unit crossdetail view
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

<div class="list-spacing clearfix">
  <div class="col-md-6">
    <h3><a href="<c:url value="?iid=${incident.id}"/>"><spring:message code="incident"/> #<c:out value="${incident.id}"/></a></h3>
    <%@include file="incident_props.jsp"%>
  </div>
  <div class="col-md-5">
    <h3><a href="<c:url value="?uid=${unit.id}"/>"><spring:message code="unit"/> #<c:out value="${unit.id}"/></a></h3>
    <%@include file="unit_props.jsp"%>
  </div>
</div>

<h3><spring:message code="log"/></h3>
<table class="table table-striped table-hover">
  <thead>
    <tr>
      <th><spring:message code="log.timestamp"/></th>
      <th><spring:message code="user"/></th>
      <th><spring:message code="log.text"/></th>
      <th><spring:message code="task.state"/></th>
      <th><spring:message code="log.changes"/></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${logs}" var="log">
      <tr>
        <td><fmt:formatDate type="both" dateStyle="short" timeStyle="medium" value="${log.timestamp}"/></td>
        <td><c:out value="${log.username}"/></td>
        <td class="log_text"><t:logtext log="${log}"/></td>
        <td>
          <c:if test="${not empty log.state}">
            <spring:message code="task.state.${fn:toLowerCase(log.state)}" text="${log.state}"/>
          </c:if>
        </td>
        <td><t:changes changes="${log.changes}"/></td>
      </tr>
    </c:forEach>
  </tbody>
</table>
