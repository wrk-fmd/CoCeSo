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
    <script type="text/javascript">
      var CocesoConf = {
        jsonBase: "<c:url value="/data/"/>",
        imageBase: "<c:url value="/static/imgs/"/>",
        groupUrl: "<c:url value="/patadmin/registration/group/"/>",
        langBase: "<c:url value="/static/i18n/"/>",
        language: "<spring:message code="this.languageCode"/>",
        takeoverUrl: "<c:url value="/patadmin/registration/takeover/"/>",
        registrationViewUrl: "<c:url value="/patadmin/registration/view/"/>",
        registrationEditUrl: "<c:url value="/patadmin/registration/edit/"/>",
        newPatientId: ${newPatientId != null ? newPatientId : 'null'},
        treatmentCount: ${treatmentCount},
        transportCount: ${transportCount},
        countsMsg: "<spring:message code='patadmin.counts' arguments='|0|,|1|'/>"
      };
    </script>
    <t:head maintitle="patadmin" title="patadmin.registration" entry="patadmin_registration"/>
  </head>
  <body>
    <div class="container">
      <%@include file="../navbar.jsp"%>

      <h2><spring:message code="patadmin.registration"/></h2>
      <p id="patadmin-counts">
        <spring:message code="patadmin.counts" arguments="${treatmentCount},${transportCount}"/>
      </p>
      <p>
        <a href="<c:url value="/patadmin/registration/add"/>" class="btn btn-default autofocus">
          <spring:message code="patient.add"/>
        </a>
      </p>

      <h3><spring:message code="patadmin.groups"/></h3>
      <p:groups/>

      <h3><spring:message code="patadmin.incoming"/></h3>
      <div id="incoming-list">
        <p data-bind="visible: incomingIncidents().length === 0">
          <spring:message code="patadmin.incoming.no.transports"/>
        </p>
        <div class="table-responsive" data-bind="visible: incomingIncidents().length > 0">
          <table class="table table-striped table-condensed">
            <thead>
              <tr>
                <th><spring:message code="patient.id"/></th>
                <th><spring:message code="patient.externalId"/></th>
                <th><spring:message code="patient.lastname"/></th>
                <th><spring:message code="patient.firstname"/></th>
                <th><spring:message code="patient.target"/></th>
                <th><spring:message code="incident.info"/></th>
                <th><spring:message code="units"/></th>
                <th></th>
              </tr>
            </thead>
            <tbody data-bind="foreach: incomingIncidents">
              <tr>
                <td data-bind="text: patient ? patient.id : ''"></td>
                <td data-bind="text: patient ? patient.externalId : ''"></td>
                <td data-bind="text: patient ? patient.lastname() : ''"></td>
                <td data-bind="text: patient ? patient.firstname() : ''"></td>
                <td data-bind="text: aoInfo"></td>
                <td data-bind="text: incidentInfo"></td>
                <td data-bind="foreach: units">
                  <span data-bind="text: callsign + ' (' + taskState + ')'"></span>
                </td>
                <td>
                  <a data-bind="attr: {href: $root.takeoverUrl + incidentId}" class="btn btn-default btn-xs">
                    <spring:message code="patient.takeover"/>
                  </a>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

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
              <tr data-bind="css: {'success': patient.id === $root.newPatientId(), 'newly-added-patient': patient.id === $root.newPatientId()}">
                <td data-bind="text: patient.id"></td>
                <td data-bind="text: patient.externalId"></td>
                <td data-bind="text: patient.lastname()"></td>
                <td data-bind="text: patient.firstname()"></td>
                <td>
                  <a data-bind="visible: !!groupId, attr: {href: $root.groupUrl + groupId}, text: groupName"></a>
                </td>
                <td>
                  <a data-bind="attr: {href: $root.registrationViewUrl + patient.id}" class="btn btn-default btn-xs">
                    <spring:message code="patient.details"/>
                  </a>
                  <a data-bind="attr: {href: $root.registrationEditUrl + patient.id}" class="btn btn-default btn-xs autofocus">
                    <spring:message code="patient.edit"/>
                  </a>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </body>
</html>
