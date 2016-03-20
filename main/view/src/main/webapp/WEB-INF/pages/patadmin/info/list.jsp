<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML info patient list
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
<html>
  <head>
    <t:head maintitle="patadmin" title="${empty search ? 'patadmin.info' : 'patadmin.searchresult'}" entry="navbar"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <c:choose>
        <c:when test="${empty search}">
          <h2><spring:message code="patients"/></h2>
        </c:when>
        <c:otherwise>
          <h2><spring:message code="patadmin.searchresult"/>: <em><c:out value="${search}"/></em></h2>
        </c:otherwise>
      </c:choose>

      <c:url var="viewUrl" value="/patadmin/info/view/"/>

      <div class="table-responsive">
        <table class="table table-striped table-condensed table-full">
          <tr>
            <th><spring:message code="patient.id"/></th>
            <th><spring:message code="patient.externalId"/></th>
            <th><spring:message code="patient.lastname"/></th>
            <th><spring:message code="patient.firstname"/></th>
            <th><spring:message code="patadmin.group"/></th>
            <th><spring:message code="patadmin.hospital"/></th>
            <th></th>
          </tr>
          <c:forEach items="${patients.content}" var="patient">
            <tr>
              <td><c:out value="${patient.id}"/></td>
              <td><c:out value="${patient.externalId}"/></td>
              <td><c:out value="${patient.lastname}"/></td>
              <td><c:out value="${patient.firstname}"/></td>
              <td>
                <c:if test="${not empty patient.group}">
                  <c:forEach items="${patient.group}" var="group">
                    <c:out value="${group.call}"/>
                  </c:forEach>
                </c:if>
              </td>
              <td>
                <c:if test="${not empty patient.hospital}">
                  <c:forEach items="${patient.hospital}" var="hospital">
                    <c:out value="${hospital}"/>
                  </c:forEach>
                </c:if>
              </td>
              <td>
                <a href="${viewUrl}${patient.id}" class="btn btn-default btn-xs"><spring:message code="patient.details"/></a>
              </td>
            </tr>
          </c:forEach>
        </table>
      </div>
      <c:choose>
        <c:when test="${empty search}">
          <p:pagination page="${patients}" url="/patadmin/info?"/>
        </c:when>
        <c:otherwise>
          <p:pagination page="${patients}" url="/patadmin/info/search?q=${search}&"/>
        </c:otherwise>
      </c:choose>
    </div>
  </body>
</html>
