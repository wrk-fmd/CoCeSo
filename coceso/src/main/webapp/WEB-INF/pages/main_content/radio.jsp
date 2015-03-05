<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML radio window
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
      <div class="alert alert-danger" data-bind="visible: error">
        <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: errorText"></span>
      </div>
      <div class="form-group form-inline">
        <label>Port</label>:
        <select class="form-control"
                data-bind="options: Coceso.Data.Radio.ports, optionsCaption: 'All', value: port">
        </select>
      </div>

      <!-- ko if: calls().length -->
      <div data-bind="with: calls()[0]">
        <span data-bind="text: fmtTimer"></span>
        <span data-bind="text: ani"></span>
      </div>
      <!-- /ko -->

      <ul data-bind="foreach: calls">
        <li>
          <span data-bind="text: time"></span>:
          <!-- ko if: unit -->
          <span data-bind="text: unit().call"></span>
          <!-- /ko -->
          <!-- ko ifnot: unit -->
          <span data-bind="text: ani"></span>
          <!-- /ko -->
        </li>
      </ul>

    </div>
  </body>
</html>
