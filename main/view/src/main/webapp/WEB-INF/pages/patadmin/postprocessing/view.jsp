<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML postprocessing patient details
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<html>
  <head>
    <t:head maintitle="patadmin" title="patient" entry="navbar"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <h2><spring:message code="patient"/>: <em><c:out value="${patient.fullName}"/></em></h2>

      <p>
        <a href="<c:url value="/patadmin/postprocessing/edit/${patient.id}"/>" class="btn btn-default">
          <spring:message code="patient.edit"/>
        </a>
        <c:if test="${not patient.done && not patient.transport}">
          <a href="<c:url value="/patadmin/postprocessing/discharge/${patient.id}"/>" class="btn btn-default ">
            <spring:message code="patient.discharge"/>
          </a>
          <a href="<c:url value="/patadmin/postprocessing/transport/${patient.id}"/>" class="btn btn-default">
            <spring:message code="patient.requesttransport"/>
          </a>
        </c:if>
        <c:if test="${not empty patient.group && patient.transport}">
          <a href="<c:url value="/patadmin/postprocessing/transported/${patient.id}"/>" class="btn btn-default">
            <spring:message code="patient.transported"/>
          </a>
        </c:if></p>

      <dl class="dl-horizontal">
        <dt><spring:message code="patient.id"/></dt>
        <dd class="clearfix"><c:out value="${patient.id}"/></dd>

        <dt><spring:message code="patient.lastname"/></dt>
        <dd class="clearfix"><c:out value="${patient.lastname}"/></dd>

        <dt><spring:message code="patient.firstname"/></dt>
        <dd class="clearfix"><c:out value="${patient.firstname}"/></dd>

        <dt><spring:message code="patient.externalId"/></dt>
        <dd class="clearfix"><c:out value="${patient.externalId}"/></dd>

        <c:if test="${not empty patient.sex}">
          <dt><spring:message code="patient.sex"/></dt>
          <dd class="clearfix"><spring:message code="patient.sex.long.${fn:toLowerCase(patient.sex)}"/></dd>
        </c:if>

        <dt><spring:message code="patient.insurance"/></dt>
        <dd class="clearfix"><c:out value="${patient.insurance}"/></dd>

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
    </div>
  </body>
</html>
