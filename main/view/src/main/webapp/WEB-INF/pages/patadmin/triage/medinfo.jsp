<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Client HTML patadmin triage medinfo details
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

          <dt><spring:message code="patient.birthday"/></dt>
          <dd><c:out value="${medinfo.birthday}"/></dd>

          <c:forEach items="${medinfo.data}" var="entry">
            <c:if test="${not empty entry.value}">
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

      <div>
        <h3><spring:message code="patients"/></h3>
        <p>
          <a href="<c:url value="/patadmin/triage/add?medinfo=${medinfo.id}"/>" class="btn btn-default"><spring:message code="patient.add"/></a>
        </p>
        <c:if test="${not empty medinfo.patients}">
          <p:patients patients="${medinfo.patients}"/>
        </c:if>
      </div>
    </div>
  </body>
</html>
