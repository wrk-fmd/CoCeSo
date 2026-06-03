<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML postprocessing home
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
    <c:if test="${empty search}">
      <script type="text/javascript">
        var CocesoConf = {
          jsonBase: "<c:url value="/data/"/>",
          imageBase: "<c:url value="/static/imgs/"/>",
          langBase: "<c:url value="/static/i18n/"/>",
          language: "<spring:message code="this.languageCode"/>",
          treatmentViewUrl: "<c:url value="/patadmin/${viewType}/view/"/>",
          treatmentEditUrl: "<c:url value="/patadmin/${viewType}/edit/"/>",
          dischargeUrl: "<c:url value="/patadmin/${viewType}/discharge/"/>",
          transportUrl: "<c:url value="/patadmin/${viewType}/transport/"/>",
          transportedUrl: "<c:url value="/patadmin/${viewType}/transported/"/>",
          savedPatientId: null,
          treatmentCount: ${treatmentCount},
          transportCount: ${transportCount},
          countsMsg: "<spring:message code='patadmin.counts' arguments='|0|,|1|'/>"
        };
      </script>
    </c:if>
    <t:head maintitle="patadmin" title="${empty search ? 'patadmin.'.concat(viewType) : 'patadmin.searchresult'}" entry="${empty search ? 'patadmin_postprocessing' : 'navbar'}"/>
  </head>
  <body>
    <div class="container">
      <%@include file="../navbar.jsp"%>

      <c:choose>
        <c:when test="${empty search}">
          <h2><spring:message code="patients"/></h2>

          <p id="patadmin-counts">
            <spring:message code="patadmin.counts" arguments="${treatmentCount},${transportCount}"/>
          </p>

          <div id="treatment-list">
            <p data-bind="visible: treatmentPatients().length === 0">
              <spring:message code="patadmin.intreatment.no.patients"/>
            </p>
            <div class="table-responsive" data-bind="visible: treatmentPatients().length > 0">
              <table class="table table-striped table-condensed table-full">
                <thead>
                  <tr>
                    <th><spring:message code="patient.id"/></th>
                    <th><spring:message code="patient.externalId"/></th>
                    <th><spring:message code="patient.lastname"/></th>
                    <th><spring:message code="patient.firstname"/></th>
                    <th><spring:message code="patadmin.group"/></th>
                    <th></th>
                  </tr>
                </thead>
                <tbody data-bind="foreach: treatmentPatients">
                  <tr>
                    <td data-bind="text: patient.id"></td>
                    <td data-bind="text: patient.externalId"></td>
                    <td data-bind="text: patient.lastname()"></td>
                    <td data-bind="text: patient.firstname()"></td>
                    <td data-bind="text: groupName"></td>
                    <td>
                      <a data-bind="attr: {href: $root.treatmentViewUrl + patient.id}" class="btn btn-default btn-xs">
                        <spring:message code="patient.details"/>
                      </a>
                      <a data-bind="attr: {href: $root.treatmentEditUrl + patient.id}" class="btn btn-default btn-xs">
                        <spring:message code="patient.edit"/>
                      </a>
                      <a data-bind="visible: !$root.hasHospitalTransport(patient.id), attr: {href: $root.dischargeUrl + patient.id}" class="btn btn-default btn-xs">
                        <spring:message code="patient.discharge"/>
                      </a>
                      <a data-bind="visible: !$root.hasHospitalTransport(patient.id), attr: {href: $root.transportUrl + patient.id}" class="btn btn-default btn-xs">
                        <spring:message code="patient.requesttransport"/>
                      </a>
                      <a data-bind="visible: $root.hasHospitalTransport(patient.id) && !!groupName, attr: {href: $root.transportedUrl + patient.id}" class="btn btn-default btn-xs">
                        <spring:message code="patient.transported"/>
                      </a>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <h2><spring:message code="patadmin.searchresult"/>: <em><c:out value="${search}"/></em></h2>

          <c:url var="editUrl" value="/patadmin/${viewType}/edit/"/>
          <c:url var="viewUrl" value="/patadmin/${viewType}/view/"/>
          <c:url var="dischargeUrl" value="/patadmin/${viewType}/discharge/"/>
          <c:url var="transportUrl" value="/patadmin/${viewType}/transport/"/>
          <c:url var="transportedUrl" value="/patadmin/${viewType}/transported/"/>

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
                    <c:if test="${not patient.done && not patient.transport}">
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
        </c:otherwise>
      </c:choose>
    </div>
  </body>
</html>
