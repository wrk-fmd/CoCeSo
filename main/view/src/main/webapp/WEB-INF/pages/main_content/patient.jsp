<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML patient list window
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
      <div class="form-group col-md-6">
        <input type="text" class="form-control" placeholder="<spring:message code="filter"/>" data-bind="value: filtertext, valueUpdate: 'input'"/>
      </div>
      <table class="table table-striped">
        <tr>
          <th>#</th>
          <th><spring:message code="patient.externalId"/></th>
          <th><spring:message code="patient.lastname"/></th>
          <th><spring:message code="patient.firstname"/></th>
        </tr>
        <!-- ko foreach: filtered -->
        <tr>
          <td><a href="#" data-bind="text: id, click: openForm"></a></td>
          <td data-bind="text: externalId"></td>
          <td data-bind="text: lastname"></td>
          <td data-bind="text: firstname"></td>
        </tr>
        <!-- /ko -->
      </table>
    </div>
  </body>
</html>
