<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML key window
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<html>
  <head>
    <title>No direct access</title>
  </head>
  <body style="display: none">
    <div class="ajax_content">
      <table class="table keytable">
        <thead>
          <tr>
            <th><spring:message code="label.key.category"/></th>
            <th><spring:message code="label.key.symbol"/></th>
            <th><spring:message code="label.key.meaning"/></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <spring:message code="label.key.category.notification"/>
            </td>
            <td>
              <span class="glyphicon glyphicon-signal"></span>
            </td>
            <td>
              <spring:message code="label.connection_status"/>: <spring:message code="text.key.connection_status"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-time"></span>
            </td>
            <td>
              <spring:message code="label.main.incident.new_or_open"/>: <spring:message code="text.key.open_or_new"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-log-out"></span>
            </td>
            <td>
              <spring:message code="label.incident.type.transport.open"/>: <spring:message code="text.transport.open"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-bullhorn"></span>
            </td>
            <td>
              <spring:message code="label.main.unit.for_dispo"/>: <spring:message code="text.unit_for_dispo"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-exclamation-sign"></span>
            </td>
            <td>
              <spring:message code="label.main.unit.free"/>: <spring:message code="text.unit.free"/>
            </td>
          </tr>
          <%-- UNITS --%>
          <tr>
            <td><spring:message code="label.units"/></td>
            <td>
              <span class="glyphicon glyphicon-home"></span>
            </td>
            <td>
              <spring:message code="label.unit.home"/>: <spring:message code="text.unit.is_home"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-map-marker"></span>
            </td>
            <td>
              <spring:message code="label.unit.position"/>: <spring:message code="text.unit.position"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-record"></span>
            </td>
            <td>
              <spring:message code="label.incident.type.holdposition"/>: <spring:message code="text.unit.holdposition"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-pause"></span>
            </td>
            <td>
              <spring:message code="label.incident.type.standby"/>: <spring:message code="text.unit.standby"/>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>
