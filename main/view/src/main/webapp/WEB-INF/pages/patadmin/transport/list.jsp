<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<html>
  <head>
    <script type="text/javascript">
      var CocesoConf = {
        jsonBase: "<c:url value="/data/"/>",
        imageBase: "<c:url value="/static/imgs/"/>",
        langBase: "<c:url value="/static/i18n/"/>",
        language: "<spring:message code="this.languageCode"/>"
      };
    </script>
    <t:head maintitle="patadmin" title="patient.transport" entry="patadmin_transport_list"/>
  </head>
  <body>
    <div class="container">
      <%@include file="../navbar.jsp"%>

      <h2><spring:message code="patient.transport"/></h2>

      <div id="transport-list">
        <p data-bind="visible: hospitalTransportRows().length === 0">
          <spring:message code="patadmin.transport.no.transports"/>
        </p>
        <div class="table-responsive" data-bind="visible: hospitalTransportRows().length > 0">
          <table class="table table-striped table-condensed table-full">
            <thead>
              <tr>
                <th><spring:message code="patient.id"/></th>
                <th><spring:message code="patient.externalId"/></th>
                <th><spring:message code="patient.lastname"/></th>
                <th><spring:message code="patient.firstname"/></th>
                <th><spring:message code="patadmin.group"/></th>
                <th><spring:message code="patient.ambulance"/></th>
                <th><spring:message code="incident.casus.short"/></th>
                <th><spring:message code="unit"/></th>
                <th><spring:message code="unit.state"/></th>
                <th><spring:message code="patadmin.hospital"/></th>
              </tr>
            </thead>
            <tbody data-bind="foreach: hospitalTransportRows">
              <tr>
                <td data-bind="text: patient ? patient.id : ''"></td>
                <td data-bind="text: patient ? patient.externalId : ''"></td>
                <td data-bind="text: patient ? patient.lastname() : ''"></td>
                <td data-bind="text: patient ? patient.firstname() : ''"></td>
                <td data-bind="text: groupName"></td>
                <td data-bind="text: transportInfo"></td>
                <td data-bind="text: casusNr"></td>
                <td data-bind="foreach: transportUnits">
                  <div data-bind="text: callsign"></div>
                </td>
                <td data-bind="foreach: transportUnits">
                  <div data-bind="text: taskState"></div>
                </td>
                <td data-bind="text: hospital"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </body>
</html>
