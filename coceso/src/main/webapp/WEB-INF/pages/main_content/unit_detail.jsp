<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML unit detail window
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
    <div class="ajax_content" data-bind="with: model">

      <dl class="dl-horizontal list-spacing list-narrow">
        <dt><spring:message code="label.unit.call"/></dt>
        <dd data-bind="text: call"></dd>

        <!-- ko if: hasHome -->
        <dt><span class="glyphicon glyphicon-home"></span></dt>
        <dd><span class="pre" data-bind="text: home.info"></span></dd>
        <!-- /ko -->

        <dt><span class="glyphicon glyphicon-map-marker"></span></dt>
        <dd><span class="pre" data-bind="text: position.id() ? position.info() : 'N/A'"></span></dd>

        <dt><spring:message code="label.unit.state"/></dt>
        <dd>
          <div class="btn-group btn-group-xs">
            <button type="button" class="btn btn-default" data-bind="click: isEB.set, css: isEB.state">
              <spring:message code="label.unit.state.eb"/>
            </button>
            <button type="button" class="btn btn-default" data-bind="click: isNEB.set, css: isNEB.state">
              <spring:message code="label.unit.state.neb"/>
            </button>
            <button type="button" class="btn btn-default" data-bind="click: isAD.set, css: isAD.state">
              <spring:message code="label.unit.state.ad"/>
            </button>
          </div>
        </dd>

        <!-- ko if: ani -->
        <dt><spring:message code="label.unit.ani"/></dt>
        <dd data-bind="text: ani"></dd>
        <!-- /ko -->

        <!-- ko if: info -->
        <dt><spring:message code="label.unit.info"/></dt>
        <dd><span class="pre" data-bind="text: info"></span></dd>
        <!-- /ko -->
      </dl>
      <!-- ko if: incidentCount -->
      <hr/>
      <dl class="dl-horizontal list-spacing list-narrow">
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

      <div data-bind="if: crew.length">
        <hr/>
        <label><spring:message code="label.crew"/>:</label>
        <div class="table-responsive">
          <table class="table table-striped table-condensed">
            <thead>
              <tr>
                <th>
                  <spring:message code="label.person.dnr"/>
                </th>
                <th>
                  <spring:message code="label.person.name"/>
                </th>
                <th>
                  <spring:message code="label.person.contact"/>
                </th>
              </tr>
            </thead>

            <tbody data-bind="foreach: crew">
              <tr>
                <td data-bind="text: dNr"></td>
                <td>
                  <strong data-bind="text: sur_name"></strong> <span data-bind="text: given_name"></span>
                </td>
                <td class="pre" data-bind="text: contact"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <p>
        <a href="#" target="_blank" title="<spring:message code="label.log.view"/>" class="btn btn-default btn-sm"
            data-bind="attr: {href: '<c:url value="/dashboard?uid="/>' + id}">
          <spring:message code="label.log.view"/>
        </a>
      </p>
    </div>
  </body>
</html>
