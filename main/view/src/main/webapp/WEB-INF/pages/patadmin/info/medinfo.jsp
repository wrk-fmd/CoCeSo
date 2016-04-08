<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Client HTML patadmin info medinfo details
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
    <t:head entry="navbar"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <div id="medinfo-content">
        <h2><spring:message code="medinfo"/>: <em><c:out value="${medinfo.fullName}"/></em></h2>
        <dl class="dl-horizontal clearfix">
          <dt><spring:message code="patient.id"/></dt>
          <dd class="clearfix"><c:out value="${medinfo.id}"/></dd>

          <dt><spring:message code="patient.externalId"/></dt>
          <dd class="clearfix"><c:out value="${medinfo.externalId}"/></dd>

          <dt><spring:message code="patient.lastname"/></dt>
          <dd class="clearfix"><c:out value="${medinfo.lastname}"/></dd>

          <dt><spring:message code="patient.firstname"/></dt>
          <dd class="clearfix"><c:out value="${medinfo.firstname}"/></dd>

          <c:forEach items="${medinfo.data}" var="entry">
            <c:if test="${not empty entry.value && ['EMERGENCY_CONTACT_1', 'EMERGENCY_CONTACT_2'].contains(entry.key)}">
              <dt><spring:message code="medinfo.${entry.key}" text="${entry.key}"/></dt>
              <dd class="clearfix">
                <c:choose>
                  <c:when test="${entry.value['class'] == java.lang.Boolean}">
                    <spring:message code="${entry.value ? 'yes' : 'no'}"/>
                  </c:when>
                  <c:otherwise>
                    <span class="pre"><c:out value="${entry.value}"/></span>
                  </c:otherwise>
                </c:choose>
              </dd>
            </c:if>
          </c:forEach>
        </dl>
      </div>

      <c:if test="${not empty medinfo.patients}">
        <c:url var="viewUrl" value="/patadmin/info/view/"/>
        <div>
          <h3><spring:message code="patients"/></h3>
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
              <c:forEach items="${medinfo.patients}" var="patient">
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
        </div>
      </div>
    </c:if>
  </body>
</html>
