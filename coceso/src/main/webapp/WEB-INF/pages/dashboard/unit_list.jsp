<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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

<c:url var="get_unit" value="?uid="/>
<table class="table table-striped table-hover">
  <thead>
    <tr>
      <th><spring:message code="label.unit.call"/></th>
      <th><spring:message code="label.unit.state"/></th>
      <th><spring:message code="label.unit.position"/></th>
      <th><spring:message code="label.unit.home"/></th>
      <th><spring:message code="label.unit.info"/></th>
      <th><spring:message code="label.unit.ani"/></th>
      <th><spring:message code="label.incidents"/></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${units}" var="unit">
      <tr>
        <td><a href="${get_unit}${unit.id}"><c:out value="${unit.call}"/></a></td>
        <td><c:out value="${unit.state}"/></td>
        <td><c:out value="${unit.position}"/></td>
        <td><c:out value="${unit.home}"/></td>
        <td><c:out value="${unit.info}"/></td>
        <td><c:out value="${unit.ani}"/></td>
        <td>
          <ul class="list-unstyled">
            <c:forEach items="${unit.incidents}" var="task">
              <li>
                <span class="key"><c:out value="${task.key}"/></span>
                <span><a href="${get_inc}${task.key}"><spring:message code="label.task.state.${fn:toLowerCase(task.value)}" text="${task.value}"/></a></span>
              </li>
            </c:forEach>
          </ul>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>
