<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML registration home
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
    <t:head maintitle="patadmin" title="patadmin.treatment" entry="patadmin_treatment"/>
  </head>
  <body>
    <div class="container">
      <%@include file="../navbar.jsp"%>

      <c:url var="editUrl" value="/patadmin/treatment/edit/"/>
      <c:url var="viewUrl" value="/patadmin/treatment/view/"/>
      <c:url var="dischargeUrl" value="/patadmin/treatment/discharge/"/>
      <c:url var="transportUrl" value="/patadmin/treatment/transport/"/>
      <c:url var="transportedUrl" value="/patadmin/treatment/transported/"/>

      <c:if test="${savedPatientId != null}">
        <div class="alert alert-success alert-dismissable">
          <p>
            <spring:message code="patient.saved.success"/>
          </p>
          <p>
            ID: <strong>#${savedPatientId}</strong>
          </p>
        </div>
      </c:if>

      <h2><spring:message code="patadmin.treatment"/></h2>
      <p>
        <spring:message code="patadmin.counts" arguments="${treatmentCount},${transportCount}"/>
      </p>
      <p>
        <a href="<c:url value="/patadmin/treatment/add"/>" class="btn btn-default autofocus">
          <spring:message code="patient.add"/>
        </a>
      </p>

      <h3><spring:message code="patadmin.intreatment"/></h3>
      <c:if test="${not empty patients}">
        <div class="table-responsive">
          <table class="table table-striped table-condensed table-full">
            <tr>
              <th><spring:message code="patient.id"/></th>
              <th><spring:message code="patient.externalId"/></th>
              <th><spring:message code="patient.lastname"/></th>
              <th><spring:message code="patient.firstname"/></th>
              <th><spring:message code="patadmin.group"/></th>
              <th></th>
            </tr>
            <c:forEach items="${patients}" var="patient">
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
                  <a href="${viewUrl}${patient.id}" class="btn btn-default btn-xs">
                    <spring:message code="patient.details"/>
                  </a>
                  <a href="${editUrl}${patient.id}" class="btn btn-default btn-xs">
                    <spring:message code="patient.edit"/>
                  </a>
                  <c:if test="${not patient.transport}">
                    <a href="${dischargeUrl}${patient.id}" class="btn btn-default btn-xs">
                      <spring:message code="patient.discharge"/>
                    </a>
                    <a href="${transportUrl}${patient.id}" class="btn btn-default btn-xs">
                      <spring:message code="patient.requesttransport"/>
                    </a>
                  </c:if>
                  <c:if test="${not empty patient.group && patient.transport}">
                    <a href="${transportedUrl}${patient.id}" class="btn btn-default btn-xs">
                      <spring:message code="patient.transported"/>
                    </a>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
          </table>
        </div>
      </c:if>
      <c:if test="${empty patients}">
        <p>
          <spring:message code="patadmin.intreatment.no.patients"/>
        </p>
      </c:if>

      <p>
        <a href="<c:url value="/patadmin/treatment/list"/>" class="btn btn-default autofocus">
          <spring:message code="patadmin.showAll"/>
        </a>
      </p>
    </div>
  </body>
</html>
