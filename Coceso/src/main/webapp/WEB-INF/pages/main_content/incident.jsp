<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<!--
/**
* CoCeSo
* Client HTML Incident list content
* Copyright (c) WRK\Daniel Rohr
*
* Licensed under The MIT License
* For full copyright and license information, please see the LICENSE.txt
* Redistributions of files must retain the above copyright notice.
*
* @copyright     Copyright (c) 2013 Daniel Rohr
* @link          https://sourceforge.net/projects/coceso/
* @package       coceso.client.html
* @since         Rev. 1
* @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
*
* Dependencies:
*	coceso.client.css
*/
-->
<html>
  <head>
    <title><spring:message code="label.incidents" /> / <spring:message code="label.main.list" /></title>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="<c:url value='/static/css/coceso.css'/>" type="text/css" />
  </head>
  <body>
    <div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access" /></div>

    <div class="ajax_content">
      <div class="ui-tabs ui-buttonset" data-bind="visible: showTabs">
        <input id="tab_emergency" type="radio" class="ui-helper-hidden-accessible" name="tab" value="0" data-bind="checked: selectedTab" />
        <label for="tab_emergency" class="ui-button ui-widget ui-state-default ui-corner-top" data-bind="css: {'ui-state-active': selectedTab() === '0'}">
          <span class="ui-button-text"><spring:message code="label.main.emergency" /></span>
        </label>

        <input id="tab_tasks" type="radio" class="ui-helper-hidden-accessible" name="tab" value="1" data-bind="checked: selectedTab" />
        <label for="tab_tasks" class="ui-button ui-widget ui-state-default ui-corner-top" data-bind="css: {'ui-state-active': selectedTab() === '1'}">
          <span class="ui-button-text"><spring:message code="label.incident.type.task" /></span>
        </label>

        <input id="tab_relocations" type="radio" class="ui-helper-hidden-accessible" name="tab" value="2" data-bind="checked: selectedTab" />
        <label for="tab_relocations" class="ui-button ui-widget ui-state-default ui-corner-top" data-bind="css: {'ui-state-active': selectedTab() === '2'}">
          <span class="ui-button-text"><spring:message code="label.incident.type.relocation" /></span>
        </label>
      </div>

      <ul data-bind="foreach: filtered, accordion: {active: false, collapsible: true, heightStyle: 'content'}, accordionRefresh: filtered">
        <li data-bind="droppable: {drop: assignUnitList}">
          <h3 class="clearfix" data-bind="css: {incident_open: isNew() || isOpen()}">
            <span class="incident_priority" data-bind="text: priority, css: {incident_blue: blue}, style: {fontSize: (priority() / 300 + 0.5) + 'em'}"></span>

            <span data-bind="text: enableBO() ? (bo.info() ? bo.info() : 'No BO') : (ao.info() ? ao.info() : 'No AO') "></span>
            <span class="incident_ao clearfix" data-bind="visible: enableBO() && ao.info()">
              <span class="ui-icon ui-icon-arrowthick-1-e"></span>
              <span data-bind="text: ao.info"></span>
            </span>
          </h3>
          <div>
            <p data-bind="visible: bo.info() && enableBO()">
              <span class="key"><spring:message code="label.incident.bo" /></span>
              <span data-bind="text: bo.info"></span>
            </p>

            <p data-bind="visible: ao.info">
              <span class="key"><spring:message code="label.incident.ao" /></span>
              <span data-bind="text: ao.info"></span>
            </p>

            <p data-bind="visible: info">
              <span class="key"><spring:message code="label.incident.info" /></span>
              <span data-bind="text: info"></span>
            </p>

            <p>
              <span class="key"><spring:message code="label.incident.state" /></span>
              <span data-bind="text: state"></span>
            </p>

            <!-- ko foreach: units.units -->
            <p>
              <span class="key" data-bind="text: call"></span>
              <span data-bind="text: taskState"></span>
            </p>
            <!-- /ko -->

            <p><button class="ui-button ui-state-default ui-corner-all" data-bind="click: openForm"><spring:message code="label.edit" /></button></p>
          </div>
        </li>
      </ul>
    </div>
  </body>
</html>
