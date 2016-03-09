<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML triage patient details
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
    <t:head maintitle="patadmin" title="patient"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <h2><spring:message code="patient"/>: <em><c:out value="${patient.fullName}"/></em></h2>

      <c:if test="${not patient.done}">
        <p><a href="<c:url value="/patadmin/triage/edit/${patient.id}"/>" class="btn btn-default"><spring:message code="patient.edit"/></a></p>
        </c:if>

      <dl class="dl-horizontal">
        <dt><spring:message code="patient.id"/></dt>
        <dd class="clearfix"><c:out value="${patient.id}"/></dd>

        <dt><spring:message code="patient.lastname"/></dt>
        <dd class="clearfix"><c:out value="${patient.lastname}"/></dd>

        <dt><spring:message code="patient.firstname"/></dt>
        <dd class="clearfix"><c:out value="${patient.firstname}"/></dd>

        <dt><spring:message code="patient.externalId"/></dt>
        <dd class="clearfix"><c:out value="${patient.externalId}"/></dd>

        <dt><spring:message code="patient.birthday"/></dt>
        <dd class="clearfix"><c:out value="${patient.birthday}"/></dd>

        <dt><spring:message code="patient.naca"/></dt>
        <dd class="clearfix"><c:out value="${patient.naca}"/></dd>

        <dt><spring:message code="patient.diagnosis"/></dt>
        <dd class="clearfix"><span class="pre"><c:out value="${patient.diagnosis}"/></span></dd>

        <dt><spring:message code="patient.info"/></dt>
        <dd class="clearfix"><span class="pre"><c:out value="${patient.info}"/></span></dd>

        <c:if test="${not empty patient.group}">
          <dt><spring:message code="patadmin.group"/></dt>
          <dd class="clearfix">
            <c:forEach items="${patient.group}" var="group">
              <c:out value="${group.call}"/>
            </c:forEach>
          </dd>
        </c:if>

        <c:if test="${not empty patient.hospital}">
          <dt><spring:message code="patadmin.hospital"/></dt>
          <dd class="clearfix">
            <c:forEach items="${patient.hospital}" var="hospital">
              <span class="pre"><c:out value="${hospital}"/></span>
            </c:forEach>
          </dd>
        </c:if>

        <c:if test="${not patient.transport and patient.done}">
          <dt><spring:message code="patient.discharged"/></dt>
          <dd class="clearfix"><spring:message code="yes"/></dd>
        </c:if>
      </dl>

      <c:if test="${not empty patient.medinfo}">
        <c:set value="${patient.medinfo}" var="medinfo" scope="page"/>
        <h3><spring:message code="medinfo"/>: <em><c:out value="${medinfo.fullName}"/></em></h3>
        <dl class="dl-horizontal">
          <dt><spring:message code="patient.id"/></dt>
          <dd class="clearfix"><a href="<c:url value="/patadmin/triage/medinfo/${medinfo.id}"/>"><c:out value="${medinfo.id}"/></a></dd>

          <dt><spring:message code="patient.lastname"/></dt>
          <dd class="clearfix"><c:out value="${medinfo.lastname}"/></dd>

          <dt><spring:message code="patient.firstname"/></dt>
          <dd class="clearfix"><c:out value="${medinfo.firstname}"/></dd>

          <dt><spring:message code="patient.externalId"/></dt>
          <dd class="clearfix"><c:out value="${medinfo.externalId}"/></dd>

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
      </c:if>
    </div>
  </body>
</html>
