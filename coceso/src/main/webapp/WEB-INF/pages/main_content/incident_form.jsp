<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML incident form window
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
    <div class="ajax_content incident_form" data-bind="droppable: {drop: assignUnitForm, tolerance: 'pointer'}">
      <div class="alert alert-danger" id="error" style="display: none"><strong>Saving failed</strong><br/>Try again or see <em>Debug</em> for further information.</div>

      <div class="clearfix">
        <div class="form-group col-md-8">
          <label class="sr-only"><spring:message code="label.incident.type"/>:</label>
          <div class="btn-group btn-group-sm">
            <button class="btn btn-default" data-bind="disable: disableTask, click: setTypeTask, css: {active: isTask}">
              <spring:message code="label.incident.type.task"/>
            </button>
            <button class="btn btn-default" data-bind="disable: disableTransport, click: setTypeTransport, css: {active: isTransport}">
              <spring:message code="label.incident.type.transport"/>
            </button>
            <button class="btn btn-default" data-bind="disable: disableRelocation, click: setTypeRelocation, css: {active: isRelocation}">
              <spring:message code="label.incident.type.relocation"/>
            </button>
          </div>
        </div>

        <div class="form-group col-md-4 text-right">
          <button class="btn btn-default btn-sm" data-bind="click: toggleBlue, css: {active: blue}">
            <spring:message code="label.incident.blue"/>
          </button>
        </div>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-8">
          <label for="bo" class=""><spring:message code="label.incident.bo"/>:</label>
          <textarea id="bo" name="bo" rows="3" class="form-control"
                    placeholder="<spring:message code="label.incident.bo"/>"
                    data-bind="disable: disableBO, value: bo.info, css: {'form-changed': bo.info.localChange, 'form-highlight': bo.info() === '' && !disableBO()}, valueUpdate: 'afterkeydown'"
                    autofocus>
          </textarea>
        </div>

        <div class="form-group btn-group-vertical btn-group-sm col-md-4">
          <!-- TODO data-bind -->
          <button type="button" class="btn btn-default" disabled><spring:message code="label.addressbook"/></button>
          <button type="button" class="btn btn-default" disabled><spring:message code="label.hospitals"/></button>
          <button type="button" class="btn btn-default" disabled><spring:message code="label.favorites"/></button>
        </div>
      </div>

      <div class="form-group col-md-12">
        <label for="caller" class=""><spring:message code="label.incident.caller"/>:</label>
        <input type="text" id="caller" name="caller" class="form-control"
               placeholder="<spring:message code="label.incident.caller"/>"
               data-bind="value: caller, css: {'form-changed': caller.localChange, 'form-highlight': caller() === ''}, valueUpdate: 'afterkeydown'"/>
      </div>

      <div class="form-group col-md-12">
        <label for="info" class=""><spring:message code="label.incident.info"/>:</label>
        <div class="alert alert-warning" data-bind="visible: info.serverChange">
          Field has changed on server!<br>
          New Value: <a href="#" title="Apply new value" data-bind="text: info.serverChange, click: info.reset"></a>
        </div>
        <textarea id="info" name="info" rows="3" class="form-control" placeholder="<spring:message code="label.incident.info"/>" data-bind="value: info, css: {'form-changed': info.localChange}, valueUpdate: 'afterkeydown'"></textarea>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-8">
          <label for="ao" class=""><spring:message code="label.incident.ao"/>:</label>
          <textarea id="ao" name="ao" rows="3" class="form-control"
                    placeholder="<spring:message code="label.incident.ao"/>"
                    data-bind="value: ao.info, css: {'form-changed': ao.info.localChange, 'form-highlight': highlightAO}, valueUpdate: 'afterkeydown'">
          </textarea>
        </div>

        <div class="form-group btn-group-vertical btn-group-sm col-md-4">
          <!-- TODO data-bind -->
          <button type="button" class="btn btn-default" disabled><spring:message code="label.addressbook"/></button>
          <button type="button" class="btn btn-default" disabled><spring:message code="label.hospitals"/></button>
          <button type="button" class="btn btn-default" disabled><spring:message code="label.favorites"/></button>
        </div>
      </div>

      <div class="clearfix">
        <!-- ko if: patient -->
        <div class="col-md-7">
          <strong><spring:message code="text.patient.needs"/></strong>:
          <span data-bind="text: patient().erType"></span>
          <!-- ko if: !patient().isUnknown() -->
          (<span data-bind="text: _('label.patient.sex.' + patient().sex())"></span>)
          <!-- /ko -->
        </div>
        <div class="form-group col-md-5">
          <button type="button" class="btn btn-primary btn-sm" data-bind="click: openPatient"><spring:message code="label.patient.edit"/></button>
        </div>
        <!-- /ko -->
        <!-- ko ifnot: patient -->
        <div class="form-group col-md-5">
          <button type="button" class="btn btn-danger btn-sm" data-bind="click: openPatient, enable: id"><spring:message code="label.patient.add"/></button>
        </div>
        <!-- /ko -->
      </div>

      <div class="clearfix">
        <div class="form-group col-md-6">
          <label for="casus" class=""><spring:message code="label.incident.casus"/>:</label>
          <input type="text" id="casus" name="casus" class="form-control"
                 placeholder="<spring:message code="label.incident.casus"/>"
                 data-bind="value: casusNr, css: {'form-changed': casusNr.localChange}, valueUpdate: 'afterkeydown'"/>
        </div>
      </div>

      <%-- Incident State --%>
      <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
          <label class="sr-only"><spring:message code="label.incident.state"/>:</label>
          <div class="btn-group btn-group-sm">
            <button class="btn btn-default" data-bind="disable: disableNew, click: setStateNew, css: {active: isNew}">
              <spring:message code="label.incident.state.new"/>
            </button>
            <button class="btn btn-default" data-bind="click: setStateOpen, css: {active: isOpen}">
              <spring:message code="label.incident.state.open"/>
            </button>
            <button class="btn btn-default" data-bind="disable: disableDispo, click: setStateDispo, css: {active: isDispo}">
              <spring:message code="label.incident.state.dispo"/>
            </button>
            <button class="btn btn-default" data-bind="disable: disableWorking, click: setStateWorking, css: {active: isWorking}">
              <spring:message code="label.incident.state.working"/>
            </button>
            <button class="btn btn-default" data-bind="click: setStateDone, css: {active: isDone}">
              <spring:message code="label.incident.state.done"/>
            </button>
          </div>
        </div>
      </div>

      <%-- Assigned Units --%>
      <div class="assigned" data-bind="foreach: units">
        <div class="form-group clearfix">
          <label class="col-md-4 control-label">
            <!-- ko if: $parent.unitCount() > 1 -->
            <span class="glyphicon glyphicon-share" data-bind="click: function() {$parent.duplicate($data);}"></span>
            <!-- /ko -->
            <span data-bind="text: unit().call"></span>
          </label>
          <div class="col-md-8 btn-group btn-group-sm nowrap">
            <button class="btn btn-default" data-bind="disable: $parent.disableAssigned, click: setAssigned, css: {active: isAssigned}">
              <spring:message code="label.task.state.assigned"/>
            </button>
            <button class="btn btn-default" data-bind="disable: $parent.disableBO, click: setZBO, css: {active: isZBO}">
              <spring:message code="label.task.state.zbo"/>
            </button>
            <button class="btn btn-default" data-bind="disable: $parent.disableBO, click: setABO, css: {active: isABO}">
              <spring:message code="label.task.state.abo"/>
            </button>
            <button class="btn btn-default" data-bind="disable: $parent.disableZAO, click: setZAO, css: {active: isZAO}">
              <spring:message code="label.task.state.zao"/>
            </button>
            <button class="btn btn-default" data-bind="disable: $parent.disableAAO, click: setAAO, css: {active: isAAO}">
              <spring:message code="label.task.state.aao"/>
            </button>
            <button class="btn btn-default" data-bind="click: setDetached, css: {active: isDetached}">
              <spring:message code="label.task.state.detached"/>
            </button>
          </div>
        </div>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
          <input type="button" class="btn btn-success" value="<spring:message code="label.ok"/>" data-bind="enable: localChange, click: ok"/>
          <input type="button" class="btn btn-primary" value="<spring:message code="label.save"/>" data-bind="enable: localChange, click: save"/>
          <input type="button" class="btn btn-warning" value="Reset" data-bind="enable: localChange, click: reset"/>
          <input type="button" class="btn btn-default" value="<spring:message code="label.incident.duplicate"/>" data-bind="click: function() {duplicate();}"/> <%-- Force undefined on method param --%>
        </div>
      </div>
    </div>
  </body>
</html>
