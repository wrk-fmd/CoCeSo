<%@tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@attribute name="medinfos" required="true" rtexprvalue="true" type="java.util.List<at.wrk.coceso.entity.Medinfo>"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML triage patients list tag
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>

<c:url var="addUrl" value="/patadmin/triage/add?medinfo="/>
<c:url var="viewUrl" value="/patadmin/triage/medinfo/"/>

<div class="table-responsive">
  <table class="table table-striped table-condensed table-full">
    <tr>
      <th><spring:message code="patient.externalId"/></th>
      <th><spring:message code="patient.lastname"/></th>
      <th><spring:message code="patient.firstname"/></th>
      <th></th>
    </tr>
    <c:forEach items="${medinfos}" var="medinfo">
      <tr>
        <td><c:out value="${medinfo.externalId}"/></td>
        <td><c:out value="${medinfo.lastname}"/></td>
        <td><c:out value="${medinfo.firstname}"/></td>
        <td>
          <a href="${viewUrl}${medinfo.id}" class="btn btn-default btn-xs"><spring:message code="medinfo.details"/></a>
          <a href="${addUrl}${medinfo.id}" class="btn btn-default btn-xs autofocus"><spring:message code="patient.add"/></a>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>
