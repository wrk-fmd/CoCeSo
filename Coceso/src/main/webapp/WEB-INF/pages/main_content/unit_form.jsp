<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<!--
/**
* CoCeSo
* Client HTML Unit form content
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
<html lang="en">
  <head>
    <title><spring:message code="label.unit" /> / <spring:message code="label.main.form" /></title>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="<c:url value='/static/css/coceso.css'/>" type="text/css" />
  </head>
  <body>
    <div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access" /></div>

    <div class="ajax_content" data-bind="droppable: {drop: assignIncidentForm}">
      <div class="alert alert-danger" id="error" style="display: none"><strong>Saving failed</strong><br/>Try again or see <em>Debug</em> for further information.</div>

      <div class="clearfix">
        <div class="form-group col-md-6">
          <label for="call"><spring:message code="label.unit.call" />:</label>
          <input type="text" id="call" class="form-control" name="call" data-bind="value: call" readonly />
        </div>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-6">
          <label for="position"><spring:message code="label.unit.position" />:</label>
          <a href="#" style="float: right" data-bind="click: function() {position.info(home.info())}">Set to home</a>
          <textarea id="position" name="position" rows="3" class="form-control" data-bind="value: position.info, css: {'form-changed': position.info.localChange}, valueUpdate: 'afterkeydown'"></textarea>
        </div>

        <div class="form-group col-md-6">
          <label for="home"><spring:message code="label.unit.home" />:</label>
          <textarea id="home" name="home" rows="3" class="form-control" data-bind="value: home.info" readonly></textarea>
        </div>
      </div>

      <div class="form-group col-md-12">
        <label for="info" class="sr-only"><spring:message code="label.unit.info" />:</label>
        <div class="alert alert-warning" data-bind="visible: info.serverChange">
          Field has changed on server!<br>
          New Value: <a href="#" title="Apply new value" data-bind="text: info.serverChange, click: info.reset"></a>
        </div>
        <textarea id="info" name="info" rows="3" class="form-control" placeholder="<spring:message code='label.unit.info' />" data-bind="value: info, css: {'form-changed': info.localChange}, valueUpdate: 'afterkeydown'"></textarea>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
          <label class="sr-only"><spring:message code="label.unit.state" />:</label>
          <div class="ui-buttonset">
            <input id="state_eb" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, value: Coceso.Constants.Unit.state.eb" />
            <label for="state_eb" class="ui-button ui-widget ui-state-default ui-corner-left" data-bind="css: {'ui-state-active': isEB()}">
              <span class="ui-button-text"><spring:message code="label.unit.state.eb" /></span>
            </label>

            <input id="state_neb" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, value: Coceso.Constants.Unit.state.neb" />
            <label for="state_neb" class="ui-button ui-widget ui-state-default" data-bind="css: {'ui-state-active': isNEB()}">
              <span class="ui-button-text"><spring:message code="label.unit.state.neb" /></span>
            </label>

            <input id="state_ad" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, value: Coceso.Constants.Unit.state.ad" />
            <label for="state_ad" class="ui-button ui-widget ui-state-default ui-corner-right" data-bind="css: {'ui-state-active': isAD()}">
              <span class="ui-button-text"><spring:message code="label.unit.state.ad" /></span>
            </label>
          </div>
        </div>
      </div>

      <div class="assigned" data-bind="foreach: incidents.incidentlist">
        <div class="form-group clearfix">
          <label class="col-md-2 control-label" data-bind="text: id"></label>
          <div class="col-md-10 ui-buttonset">
            <input type="radio" class="ui-helper-hidden-accessible"
                   data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_assigned_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.assigned" />
            <label class="ui-button ui-widget ui-state-default ui-corner-left"
                   data-bind="attr: {for: $root.ui + '-taskState_assigned_' + $index()}, css: {'ui-state-active': isAssigned()}">
              <span class="ui-button-text"><spring:message code="label.task.state.assigned" /></span>
            </label>

            <input type="radio" class="ui-helper-hidden-accessible"
                   data-bind="checked: taskState, enable: enableBO, attr: {id: $root.ui + '-taskState_zbo_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.zbo" />
            <label class="ui-button ui-widget ui-state-default"
                   data-bind="attr: {for: $root.ui + '-taskState_zbo_' + $index()}, css: {'ui-state-active': isZBO(), 'ui-state-disabled': !enableBO()}">
              <span class="ui-button-text"><spring:message code="label.task.state.zbo" /></span>
            </label>

            <input type="radio" class="ui-helper-hidden-accessible"
                   data-bind="checked: taskState, enable: enableBO, attr: {id: $root.ui + '-taskState_abo_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.abo" />
            <label class="ui-button ui-widget ui-state-default"
                   data-bind="attr: {for: $root.ui + '-taskState_abo_' + $index()}, css: {'ui-state-active': isABO(), 'ui-state-disabled': !enableBO()}">
              <span class="ui-button-text"><spring:message code="label.task.state.abo" /></span>
            </label>

            <input type="radio" class="ui-helper-hidden-accessible"
                   data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_zao_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.zao" />
            <label class="ui-button ui-widget ui-state-default"
                   data-bind="attr: {for: $root.ui + '-taskState_zao_' + $index()}, css: {'ui-state-active': isZAO()}">
              <span class="ui-button-text"><spring:message code="label.task.state.zao" /></span>
            </label>

            <input type="radio" class="ui-helper-hidden-accessible"
                   data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_aao_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.aao" />
            <label class="ui-button ui-widget ui-state-default"
                   data-bind="attr: {for: $root.ui + '-taskState_aao_' + $index()}, css: {'ui-state-active': isAAO()}">
              <span class="ui-button-text"><spring:message code="label.task.state.aao" /></span>
            </label>

            <input type="radio" class="ui-helper-hidden-accessible"
                   data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_det_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.detached" />
            <label class="ui-button ui-widget ui-state-default ui-corner-right"
                   data-bind="attr: {for: $root.ui + '-taskState_det_' + $index()}, css: {'ui-state-active': isDetached()}">
              <span class="ui-button-text"><spring:message code="label.task.state.detached" /></span>
            </label>
          </div>
        </div>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
          <input type="button" class="btn btn-success" value="<spring:message code='label.save' />" data-bind="enable: localChange, click: save" />
          <input type="button" class="btn btn-warning" value="Reset" data-bind="enable: localChange, click: reset" />
        </div>
      </div>
    </div>
  </body>
</html>
