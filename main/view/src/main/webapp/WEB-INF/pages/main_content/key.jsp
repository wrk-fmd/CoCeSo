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
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
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
            <th><spring:message code="key.category"/></th>
            <th><spring:message code="key.symbol"/></th>
            <th><spring:message code="key.meaning"/></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <spring:message code="key.category.notification"/>
            </td>
            <td>
              <span class="glyphicon glyphicon-signal"></span>
            </td>
            <td>
              <spring:message code="main.connection"/>: <spring:message code="key.connection"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-time"></span>
            </td>
            <td>
              <spring:message code="main.incident.highlighted"/>: <spring:message code="key.incident.highlighted"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-log-out"></span>
            </td>
            <td>
              <spring:message code="main.incident.open_transport"/>: <spring:message code="key.incident.open_transport"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-bullhorn"></span>
            </td>
            <td>
              <spring:message code="main.unit.radio"/>: <spring:message code="key.unit.radio"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-exclamation-sign"></span>
            </td>
            <td>
              <spring:message code="main.unit.free"/>: <spring:message code="key.unit.free"/>
            </td>
          </tr>
          <%-- UNITS --%>
          <tr>
            <td><spring:message code="units"/></td>
            <td>
              <span class="glyphicon glyphicon-home"></span>
            </td>
            <td>
              <spring:message code="unit.home"/>: <spring:message code="key.unit.is_home"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-map-marker"></span>
            </td>
            <td>
              <spring:message code="unit.position"/>: <spring:message code="key.unit.position"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-record"></span>
            </td>
            <td>
              <spring:message code="incident.type.holdposition"/>: <spring:message code="key.unit.holdposition"/>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <span class="glyphicon glyphicon-pause"></span>
            </td>
            <td>
              <spring:message code="incident.type.standby"/>: <spring:message code="key.unit.standby"/>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>
