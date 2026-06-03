<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML treatment home
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
    <script type="text/javascript">
      var CocesoConf = {
        jsonBase: "<c:url value="/data/"/>",
        imageBase: "<c:url value="/static/imgs/"/>",
        langBase: "<c:url value="/static/i18n/"/>",
        language: "<spring:message code="this.languageCode"/>",
        treatmentViewUrl: "<c:url value="/patadmin/treatment/view/"/>",
        treatmentEditUrl: "<c:url value="/patadmin/treatment/edit/"/>",
        dischargeUrl: "<c:url value="/patadmin/treatment/discharge/"/>",
        transportUrl: "<c:url value="/patadmin/treatment/transport/"/>",
        transportedUrl: "<c:url value="/patadmin/treatment/transported/"/>",
        savedPatientId: ${savedPatientId != null ? savedPatientId : 'null'},
        treatmentCount: ${treatmentCount},
        transportCount: ${transportCount},
        countsMsg: "<spring:message code='patadmin.counts' arguments='|0|,|1|'/>"
      };
    </script>
    <t:head maintitle="patadmin" title="patadmin.treatment" entry="patadmin_treatment"/>
  </head>
  <body>
    <div class="container">
      <%@include file="../navbar.jsp"%>

      <div id="save-alert" data-bind="visible: showSavedAlert" class="alert alert-success alert-dismissable"
           style="${savedPatientId != null ? '' : 'display:none'}">
        <button type="button" class="close" data-bind="click: function(){ showSavedAlert(false); }">
          <span aria-hidden="true">&times;</span>
        </button>
        <p>
          <spring:message code="patient.saved.success"/>
        </p>
        <p>
          ID: <strong data-bind="text: '#' + savedPatientId"></strong>
        </p>
      </div>

      <h2><spring:message code="patadmin.treatment"/></h2>
      <p id="patadmin-counts">
        <spring:message code="patadmin.counts" arguments="${treatmentCount},${transportCount}"/>
      </p>
      <p>
        <a href="<c:url value="/patadmin/treatment/add"/>" class="btn btn-default autofocus">
          <spring:message code="patient.add"/>
        </a>
      </p>

      <h3><spring:message code="patadmin.intreatment"/></h3>
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

      <p>
        <a href="<c:url value="/patadmin/treatment/list"/>" class="btn btn-default">
          <spring:message code="patadmin.showAll"/>
        </a>
      </p>
    </div>
  </body>
</html>
