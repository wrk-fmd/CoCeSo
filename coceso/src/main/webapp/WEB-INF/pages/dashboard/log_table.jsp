<%@page import="at.wrk.coceso.entity.Unit"%>
<%@page import="java.util.Map"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard log table
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

<c:url var="get_unit" value="/dashboard?concern=${concern}&amp;sub=Unit&amp;uid="/>
<c:url var="get_unit" value="/dashboard?concern=${concern}&amp;sub=Incident&amp;iid="/>

<table class="table table-striped">
  <thead>
    <tr>
      <th>
        Time
      </th>
      <th>
        User
      </th>
      <th>
        Text
      </th>
      <th>
        Unit
      </th>
      <th>
        Incident
      </th>
      <th>
        State
      </th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${logs}" var="log">
      <tr>
        <td>
          <fmt:formatDate type="both" dateStyle="short" timeStyle="medium" value="${log.timestamp}"/>
        </td>
        <td>
          <c:out value="${log.user.username}"/>
        </td>
        <td>
          <c:out value="${log.text}"/>
        </td>
        <td>
          <c:if test="${not empty log.unit}"><a href="${get_unit}${log.unit.id}"><c:out value="${log.unit.call}"/></a></c:if>
        </td>
        <td>
          <a href="${get_inc}${log.incident.id}"><c:out value="${log.incident.id}"/></a>
        </td>
        <td>
          <c:out value="${log.state}"/>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>
