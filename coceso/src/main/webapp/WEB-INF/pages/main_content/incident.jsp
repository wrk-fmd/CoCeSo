<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML incident list window
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
      <div class="filter" data-bind="accordion: {active: false, collapsible: true, heightStyle: 'content'}, if: (disableFilter !== true)">
        <h3>Filter</h3>
        <div>
          <div class="form-group" data-bind="visible: (disableFilter.type !== true)">
            <label><spring:message code="label.incident.type"/>:</label>
            <div class="clearfix">
              <div class="checkbox-inline">
                <label>
                  <input type="checkbox" data-bind="value: Coceso.Constants.Incident.type.task, checked: filter.type"/>
                  <spring:message code="label.incident.type.task"/>
                </label>
              </div>
              <div class="checkbox-inline">
                <label>
                  <input type="checkbox" data-bind="value: Coceso.Constants.Incident.type.transport, checked: filter.type"/>
                  <spring:message code="label.incident.type.transport"/>
                </label>
              </div>
              <br/>
              <div class="checkbox-inline">
                <label>
                  <input type="checkbox" data-bind="value: Coceso.Constants.Incident.type.relocation, checked: filter.type"/>
                  <spring:message code="label.incident.type.relocation"/>
                </label>
              </div>
            </div>
          </div>
          <div class="form-group" data-bind="visible: (disableFilter.blue !== true)">
            <label><spring:message code="label.incident.blue"/>:</label>
            <div class="clearfix">
              <div class="checkbox-inline">
                <label>
                  <input type="checkbox" value="true" data-bind="checked: filter.blue"/>
                  <spring:message code="label.yes"/>
                </label>
              </div>
              <div class="checkbox-inline">
                <label>
                  <input type="checkbox" value="false" data-bind="checked: filter.blue"/>
                  <spring:message code="label.no"/>
                </label>
              </div>
            </div>
          </div>
          <div class="form-group" data-bind="visible: (disableFilter.state !== true)">
            <label><spring:message code="label.incident.state"/>:</label>
            <div class="clearfix">
              <div class="checkbox-inline" data-bind="visible: (!disableFilter.state || disableFilter.state.new !== true)">
                <label>
                  <input type="checkbox" data-bind="value: Coceso.Constants.Incident.state.new, checked: filter.state"/>
                  <spring:message code="label.incident.state.new"/>
                </label>
              </div>
              <div class="checkbox-inline" data-bind="visible: (!disableFilter.state || disableFilter.state.open !== true)">
                <label>
                  <input type="checkbox" data-bind="value: Coceso.Constants.Incident.state.open, checked: filter.state"/>
                  <spring:message code="label.incident.state.open"/>
                </label>
              </div><br/>
              <div class="checkbox-inline" data-bind="visible: (!disableFilter.state || disableFilter.state.dispo !== true)">
                <label>
                  <input type="checkbox" data-bind="value: Coceso.Constants.Incident.state.dispo, checked: filter.state"/>
                  <spring:message code="label.incident.state.dispo"/>
                </label>
              </div>
              <div class="checkbox-inline" data-bind="visible: (!disableFilter.state || disableFilter.state.working !== true)">
                <label>
                  <input type="checkbox" data-bind="value: Coceso.Constants.Incident.state.working, checked: filter.state"/>
                  <spring:message code="label.incident.state.working"/>
                </label>
              </div><br/>
              <div class="checkbox-inline" data-bind="visible: (!disableFilter.state || disableFilter.state.done !== true)">
                <label>
                  <input type="checkbox" data-bind="value: Coceso.Constants.Incident.state.done, checked: filter.state"/>
                  <spring:message code="label.incident.state.done"/>
                </label>
              </div>
            </div>
          </div>
        </div>
      </div>

      <ul data-bind="foreach: filtered, accordion: {active: false, collapsible: true, heightStyle: 'content'}, accordionRefresh: filtered">
        <li data-bind="droppable: {drop: assignUnitList, tolerance: 'pointer'}">
          <h3 class="clearfix" data-bind="css: {incident_open: isNewOrOpen}">
            <span class="incident_type_text" data-bind="text: typeString, css: {incident_blue: blue}"></span>

            <span data-bind="text: title"></span>
            <span class="incident_ao clearfix" data-bind="visible: !disableBO() && !disableAAO()">
              <span class="ui-icon ui-icon-arrowthick-1-e"></span>
              <span data-bind="text: ao.info"></span>
            </span>
          </h3>
          <div>
            <p data-bind="visible: bo.info() && !disableBO()">
              <span class="key"><spring:message code="label.incident.bo"/></span>
              <span data-bind="text: bo.info"></span>
            </p>

            <p data-bind="visible: !disableAAO()">
              <span class="key"><spring:message code="label.incident.ao"/></span>
              <span data-bind="text: ao.info"></span>
            </p>

            <p data-bind="visible: info">
              <span class="key"><spring:message code="label.incident.info"/></span>
              <span data-bind="text: info"></span>
            </p>

            <p>
              <span class="key"><spring:message code="label.incident.state"/></span>
              <span data-bind="text: state"></span>
            </p>

            <!-- ko foreach: units -->
            <p>
              <span class="key" data-bind="text: unit() && unit().call"></span>
              <span data-bind="text: localizedTaskState"></span>
              <button type="button" class="btn btn-xs btn-default" data-bind="click: nextState">
                <span class="glyphicon glyphicon-forward"></span>
              </button>
            </p>
            <!-- /ko -->

            <p><button type="button" class="btn btn-default" data-bind="click: openForm"><spring:message code="label.edit"/></button></p>
          </div>
        </li>
      </ul>
    </div>
  </body>
</html>