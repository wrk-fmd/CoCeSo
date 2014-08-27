<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML log form window
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
      <div>
        <div class="col-md-8">
          <textarea class="form-control" data-bind="value: text, valueUpdate: 'input'" rows="3" autofocus></textarea>
        </div>
        <div class="col-md-3">
          <select class="form-control" data-bind="options: unitList, optionsText: 'call', optionsValue: 'id', value: unit, optionsCaption: '<spring:message code="label.unit.select"/>'"></select>
        </div>
        <div class="col-md-2">
          <input type="submit" class="btn btn-success" data-bind="click: ok, enable: text().trim()" value="<spring:message code="label.ok"/>">
          <span class="glyphicon glyphicon-warning-sign tooltipped" style="color: #ff0000; font-size: x-large" data-bind="visible: error"
                title="<spring:message code="label.error.2"/>" data-toggle="tooltip" data-placement="top"></span>
        </div>
      </div>
    </div>
  </body>
</html>
