<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML log list window
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
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
      <table class="table table-striped">
        <tr>
          <th><spring:message code="log.timestamp"/></th>
          <th><spring:message code="user"/></th>
          <th><spring:message code="log.text"/></th>
          <th><spring:message code="unit"/></th>
          <th><spring:message code="incident"/></th>
          <th><spring:message code="task.state"/></th>
        </tr>
        <!-- ko foreach: loglist -->
        <tr>
          <td data-bind="text: time"></td>
          <td data-bind="text: user"></td>
          <td class="log_text" data-bind="text: text" style="white-space: pre-line"></td>
          <td data-bind="if: unit"><a href="#" data-bind="text: unit.call, click: openUnitForm"></a></td>
          <td data-bind="if: incident"><a href="#"  data-bind="text: incident.id, click: openIncidentForm"></a></td>
          <td data-bind="text: state"></td>
        </tr>
        <!-- /ko -->
      </table>
    </div>
  </body>
</html>
