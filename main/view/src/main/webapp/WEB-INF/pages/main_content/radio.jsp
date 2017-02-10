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
    <div class="ajax_content calls">
      <div class="form-group form-inline">
        <label><spring:message code="radio.port"/></label>:
        <select class="form-control"
                data-bind="options: ports, optionsValue: 'path', optionsText: 'name', optionsCaption: '<spring:message code="radio.all"/>', value: portId, valueAllowUnset: true">
        </select>
      </div>

      <!-- ko if: calls().length -->
      <div class="alert alert-success" data-bind="with: calls()[0].call, css: calls()[0].emergency ? 'alert-danger' : 'alert-success'">
        <p><spring:message code="radio.last"/>: <strong class="pull-right" data-bind="text: timestamp.fmtInterval"></strong></p>

        <!-- ko if: unit -->
        <!-- ko with: unit -->
        <p><strong><a href="#" data-bind="text: call, click: openDetails"></a></strong></p>
        <!-- ko if: incidentCount() -->
        <hr/>
        <dl class="dl-horizontal list-narrow">
          <!-- ko if: portable -->
          <dt><span class="glyphicon glyphicon-map-marker"></span></dt>
          <dd><span class="pre" data-bind="text: position.isEmpty() ? 'N/A' : position.info()"></span></dd>
          <!-- /ko -->
          <!-- ko foreach: incidents -->
          <dt data-bind="html: incident() && incident().assignedTitle()"></dt>
          <dd>
            <span data-bind="text: localizedTaskState"></span>
            <button type="button" class="btn btn-xs btn-default" data-bind="click: nextState">
              <span class="glyphicon glyphicon-forward"></span>
            </button>
          </dd>
          <!-- /ko -->
        </dl>
        <!-- /ko -->
        <!-- /ko -->
        <!-- /ko -->
        <!-- ko ifnot: unit -->
        <span data-bind="text: ani"></span>
        <!-- /ko -->
      </div>
      <!-- /ko -->

      <ul class="calls-list" data-bind="foreach: calls, accordion: accordionOptions, accordionRefresh: calls">
        <li>
          <h3 data-bind="css: {'calls-single': !additional.length, 'no-open': !additional.length && !call.unit()}">
            <span data-bind="text: call.timestamp.formatted"></span>:
            <!-- ko if: call.unit -->
            <strong data-bind="text: call.unit().call, css: {'text-danger': emergency}"></strong>
            <!-- /ko -->
            <!-- ko ifnot: call.unit -->
            <strong data-bind="text: call.ani, css: {'text-danger': emergency}"></strong>
            <!-- /ko -->
          </h3>
          <!-- ko if: additional.length || call.unit() -->
          <div>
            <!-- ko if: additional.length -->
            <ul class="list-unstyled pull-right">
              <li data-bind="text: call.timestamp.formatted"></li>
              <!-- ko foreach: additional -->
              <li data-bind="text: timestamp.formatted"></li>
              <!-- /ko -->
            </ul>
            <!-- /ko -->

            <!-- ko if: call.unit -->
            <!-- ko with: call.unit -->
            <p><a href="#" data-bind="click: openDetails"><spring:message code="unit.details"/></a></p>
            <!-- ko if: incidentCount() -->
            <hr/>
            <dl class="dl-horizontal list-narrow">
              <!-- ko if: portable -->
              <dt><span class="glyphicon glyphicon-map-marker"></span></dt>
              <dd><span class="pre" data-bind="text: position.isEmpty() ? 'N/A' : position.info()"></span></dd>
              <!-- /ko -->
              <!-- ko foreach: incidents -->
              <dt data-bind="html: incident() && incident().assignedTitle()"></dt>
              <dd>
                <span data-bind="text: localizedTaskState"></span>
                <button type="button" class="btn btn-xs btn-default" data-bind="click: nextState">
                  <span class="glyphicon glyphicon-forward"></span>
                </button>
              </dd>
              <!-- /ko -->
            </dl>
            <!-- /ko -->
            <!-- /ko -->
            <!-- /ko -->
          </div>
          <!-- /ko -->
        </li>
      </ul>
    </div>
  </body>
</html>
