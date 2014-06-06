<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <div class="ajax_content unit_form">
      <div class="alert alert-danger" id="error" style="display: none"><strong>Saving failed</strong><br/>Try again or see <em>Debug</em> for further information.</div>

      <div class="clearfix">
        <div class="form-group col-md-6">
          <label for="call"><spring:message code="label.unit.call" />:</label>
          <input type="text" id="call" class="form-control" name="call" data-bind="value: model().call" readonly />
        </div>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-6">
          <label for="position"><spring:message code="label.unit.position" />:</label>
          <a href="#" style="float: right" data-bind="click: function() {position.info(model().home.info())}">Set to home</a>
          <textarea id="position" name="position" rows="3" class="form-control" data-bind="value: position.info, css: {'form-changed': position.info.localChange}, valueUpdate: 'afterkeydown'"></textarea>
        </div>

        <div class="form-group col-md-6">
          <label for="home"><spring:message code="label.unit.home" />:</label>
          <textarea id="home" name="home" rows="3" class="form-control" data-bind="value: model().home.info" readonly></textarea>
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
        <div class="form-group col-md-offset-4 col-md-8">
          <label class="sr-only"><spring:message code="label.unit.state" />:</label>
          <div class="btn-group btn-group-sm">
            <button class="btn btn-default" data-bind="click: setStateEB, css: {active: isEB}">
              <spring:message code="label.unit.state.eb" />
            </button>
            <button class="btn btn-default" data-bind="click: setStateNEB, css: {active: isNEB}">
              <spring:message code="label.unit.state.neb" />
            </button>
            <button class="btn btn-default" data-bind="click: setStateAD, css: {active: isAD}">
              <spring:message code="label.unit.state.ad" />
            </button>
          </div>
        </div>
      </div>

      <div class="assigned" data-bind="foreach: incidents">
        <div class="form-group clearfix">
          <label class="col-md-4 control-label" data-bind="html: incident().assignedTitle"></label>
          <div class="col-md-8 btn-group btn-group-sm">
            <button class="btn btn-default" data-bind="disable: incident().disableAssigned, click: setAssigned, css: {active: isAssigned}">
              <spring:message code="label.task.state.assigned" />
            </button>
            <button class="btn btn-default" data-bind="disable: incident().disableBO, click: setZBO, css: {active: isZBO}">
              <spring:message code="label.task.state.zbo" />
            </button>
            <button class="btn btn-default" data-bind="disable: incident().disableBO, click: setABO, css: {active: isABO}">
              <spring:message code="label.task.state.abo" />
            </button>
            <button class="btn btn-default" data-bind="disable: incident().disableZAO, click: setZAO, css: {active: isZAO}">
              <spring:message code="label.task.state.zao" />
            </button>
            <button class="btn btn-default" data-bind="disable: incident().disableAAO, click: setAAO, css: {active: isAAO}">
              <spring:message code="label.task.state.aao" />
            </button>
            <button class="btn btn-default" data-bind="click: setDetached, css: {active: isDetached}">
              <spring:message code="label.task.state.detached" />
            </button>
          </div>
        </div>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
          <input type="button" class="btn btn-success" value="<spring:message code='label.ok' />" data-bind="enable: localChange, click: ok" />
          <input type="button" class="btn btn-primary" value="<spring:message code='label.save' />" data-bind="enable: localChange, click: save" />
          <input type="button" class="btn btn-warning" value="Reset" data-bind="enable: localChange, click: reset" />
        </div>
      </div>
    </div>
  </body>
</html>
