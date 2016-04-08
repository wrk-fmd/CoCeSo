<%@tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@attribute name="patients" required="true" rtexprvalue="true" type="java.util.List<at.wrk.coceso.entity.Patient>"%>
<%@attribute name="hideGroup" required="false" rtexprvalue="true" type="Boolean"%>
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

<c:url var="groupUrl" value="/patadmin/triage/group/"/>
<c:url var="editUrl" value="/patadmin/triage/edit/"/>
<c:url var="viewUrl" value="/patadmin/triage/view/"/>

<div class="table-responsive">
  <table class="table table-striped table-condensed table-full">
    <tr>
      <th><spring:message code="patient.id"/></th>
      <th><spring:message code="patient.externalId"/></th>
      <th><spring:message code="patient.lastname"/></th>
      <th><spring:message code="patient.firstname"/></th>
        <c:if test="${not hideGroup}">
        <th><spring:message code="patadmin.group"/></th>
        </c:if>
      <th></th>
    </tr>
    <c:forEach items="${patients}" var="patient">
      <tr>
        <td><c:out value="${patient.id}"/></td>
        <td><c:out value="${patient.externalId}"/></td>
        <td><c:out value="${patient.lastname}"/></td>
        <td><c:out value="${patient.firstname}"/></td>
        <c:if test="${not hideGroup}">
          <td>
            <c:if test="${not empty patient.group}">
              <c:forEach items="${patient.group}" var="group">
                <a href="${groupUrl}${group.id}"><c:out value="${group.call}"/></a>
              </c:forEach>
            </c:if>
          </td>
        </c:if>
        <td>
          <a href="${viewUrl}${patient.id}" class="btn btn-default btn-xs"><spring:message code="patient.details"/></a>
          <c:if test="${not patient.done}">
            <a href="${editUrl}${patient.id}" class="btn btn-default btn-xs autofocus"><spring:message code="patient.edit"/></a>
          </c:if>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>
