<%@page import="at.wrk.coceso.entity.Unit"%>
<%@page import="java.util.Map"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
<table class="table table-striped">
  <thead>
    <tr>
      <th><spring:message code="label.unit.call"/></th>
      <th><spring:message code="label.unit.state"/></th>
      <th><spring:message code="label.unit.ani"/></th>
      <th><spring:message code="label.unit.info"/></th>
      <th><spring:message code="label.task.state"/></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${units}" var="unit">
      <tr>
        <td><a href="${get_unit}${unit.id}"><c:out value="${unit.call}"/></a></td>
        <td><c:out value="${unit.state}"/></td>
        <td><c:out value="${unit.ani}"/></td>
        <td><c:out value="${unit.info}"/></td>
        <td>
          <%
            Unit u = (Unit) pageContext.getAttribute("unit");
            if (u.getIncidents().size() == 1) {
              out.print(u.getIncidents().get(u.getIncidents().keySet().iterator().next()).name());
            }
          %>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>
