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
    <div class="ajax_content log_form">
      <form class="clearfix" data-bind="submit: ok">
        <div class="alert alert-danger" data-bind="visible: error">
          <strong><spring:message code="error"/>:</strong> <span data-bind="text: errorText"></span>
        </div>

        <div class="clearfix">
          <div class="col-md-6 form-group">
            <label for="unit"><spring:message code="unit"/>:</label>
            <select id="unit" class="form-control"
                    data-bind="options: unitList, optionsText: 'call', optionsValue: 'id', value: unit, optionsCaption: '<spring:message code="unit.select"/>'">
            </select>
          </div>
          <!-- ko if: incidentTitle -->
          <div class="col-md-6 form-group">
            <label><spring:message code="incident"/>:</label>
            <input type="text" class="form-control" readonly data-bind="value: incidentTitle"/>
          </div>
          <!-- /ko -->
        </div>

        <div class="col-md-12 form-group">
          <label for="text"><spring:message code="log.text"/>:</label>
          <textarea id="text" rows="3" class="form-control" placeholder="<spring:message code="log.text"/>" autofocus
                    data-bind="value: text, valueUpdate: 'input'"></textarea>
        </div>

        <div class="form-group col-md-12">
          <button type="submit" class="btn btn-success" data-bind="enable: text().trim()">
            <spring:message code="ok"/>
          </button>
        </div>
      </form>
    </div>
  </body>
</html>
