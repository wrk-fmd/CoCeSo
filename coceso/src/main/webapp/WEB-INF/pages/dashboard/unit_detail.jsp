<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard unit detail view
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

<c:url var="get_inc" value="?uid=${unit.id}&amp;iid="/>

<h2><spring:message code="label.unit"/> #<c:out value="${unit.id}"/></h2>

<div class="clearfix">
  <div class="col-md-6">
    <%@include file="unit_props.jsp"%>
  </div>
  <c:if test="${not empty incidents}">
    <div class="col-md-6">
      <h4><spring:message code="label.incidents"/></h4>
      <dl class="list-spacing dl-horizontal no-colon">
        <c:forEach items="${incidents}" var="task">
          <dt><t:inctitle incident="${task.key}"/></dt>
          <dd><a href="${get_inc}${task.key.id}"><spring:message code="label.task.state.${fn:toLowerCase(task.value)}" text="${task.value}"/></a></dd>
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
      <th><spring:message code="label.incident"/></th>
      <th><spring:message code="label.task.state"/></th>
      <th><spring:message code="label.log.changes"/></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${logs}" var="log">
      <tr>
        <td><fmt:formatDate type="both" dateStyle="short" timeStyle="medium" value="${log.timestamp}"/></td>
        <td><c:out value="${log.user.username}"/></td>
        <td class="log_text"><t:logtext log="${log}"/></td>
        <td>
          <c:if test="${not empty log.incident}">
            <a href="${get_inc}${log.incident.id}"><t:inctitle incident="${log.incident}"/></a>
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
