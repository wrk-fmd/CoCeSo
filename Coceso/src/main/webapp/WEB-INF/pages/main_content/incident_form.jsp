<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--
/**
* CoCeSo
* Client HTML Incident form content
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
    <title><spring:message code="label.incident" /> / <spring:message code="label.main.form" /></title>

    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="<c:url value='/static/css/coceso.css'/>" type="text/css" />
</head>
<body>
<div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access" /></div>

<div class="ajax_content incident_form" data-bind="droppable: {drop: assignUnitForm}">
    <div class="alert alert-danger" id="error" style="display: none"><strong>Saving failed</strong><br/>Try again or see <em>Debug</em> for further information.</div>

    <div class="clearfix">
        <div class="form-group col-md-8">
            <label class="sr-only"><spring:message code="label.incident.type" />:</label>
            <div class="ui-buttonset">
                <input id="type_task" type="radio" class="ui-helper-hidden-accessible" name="type" data-bind="checked: type, disable: disableTask, value: Coceso.Constants.Incident.type.task" />
                <label for="type_task" class="ui-button ui-widget ui-state-default ui-corner-left" data-bind="css: {'ui-state-active': isTask, 'ui-state-disabled': disableTask}">
                    <span class="ui-button-text"><spring:message code="label.incident.type.task" /></span>
                </label>

                <input id="type_transport" type="radio" class="ui-helper-hidden-accessible" name="type" data-bind="checked: type, disable: disableTransport, value: Coceso.Constants.Incident.type.transport" />
                <label for="type_transport" class="ui-button ui-widget ui-state-default" data-bind="css: {'ui-state-active': isTransport, 'ui-state-disabled': disableTransport}">
                    <span class="ui-button-text"><spring:message code="label.incident.type.transport" /></span>
                </label>

                <input id="type_relocation" type="radio" class="ui-helper-hidden-accessible" name="type" data-bind="checked: type, disable: disableRelocation, value: Coceso.Constants.Incident.type.relocation" />
                <label for="type_relocation" class="ui-button ui-widget ui-state-default ui-corner-right" data-bind="css: {'ui-state-active': isRelocation, 'ui-state-disabled': disableRelocation}">
                    <span class="ui-button-text"><spring:message code="label.incident.type.relocation" /></span>
                </label>
            </div>
        </div>

        <div class="form-group col-md-4 text-right">
            <input id="blue" type="checkbox" class="ui-helper-hidden-accessible" name="blue" data-bind="checked: blue" />
            <label for="blue" class="ui-button ui-widget ui-state-default ui-corner-all" data-bind="css: {'ui-state-active': blue}">
                <span class="ui-button-text"><spring:message code="label.incident.blue" /></span>
            </label>
        </div>
    </div>

    <div class="clearfix">
        <div class="form-group col-md-8">
            <label for="bo" class=""><spring:message code="label.incident.bo" />:</label>
            <textarea id="bo" name="bo" rows="3" class="form-control"
                      placeholder="<spring:message code='label.incident.bo' />"
                      data-bind="disable: disableBO, value: bo.info, css: {'form-changed': bo.info.localChange, 'form-highlight': bo.info() === '' && !disableBO()}, valueUpdate: 'afterkeydown'"
                      autofocus>
            </textarea>
        </div>

        <div class="form-group btn-group-vertical col-md-4">
            <!-- TODO data-bind -->
            <button type="button" class="btn btn-default" disabled><spring:message code="label.addressbook" /></button>
            <button type="button" class="btn btn-default" disabled><spring:message code="label.hospitals" /></button>
            <button type="button" class="btn btn-default" disabled><spring:message code="label.favorites" /></button>
        </div>
    </div>

    <div class="form-group col-md-12">
        <label for="caller" class=""><spring:message code="label.incident.caller" />:</label>
        <input type="text" id="caller" name="caller" class="form-control"
               placeholder="<spring:message code='label.incident.caller' />"
               data-bind="value: caller, css: {'form-changed': caller.localChange, 'form-highlight': caller() === ''}, valueUpdate: 'afterkeydown'" />
    </div>

    <div class="form-group col-md-12">
        <label for="info" class=""><spring:message code="label.incident.info" />:</label>
        <div class="alert alert-warning" data-bind="visible: info.serverChange">
            Field has changed on server!<br>
            New Value: <a href="#" title="Apply new value" data-bind="text: info.serverChange, click: info.reset"></a>
        </div>
        <textarea id="info" name="info" rows="3" class="form-control" placeholder="<spring:message code='label.incident.info' />" data-bind="value: info, css: {'form-changed': info.localChange}, valueUpdate: 'afterkeydown'"></textarea>
    </div>

    <div class="clearfix">
        <div class="form-group col-md-8">
            <label for="ao" class=""><spring:message code="label.incident.ao" />:</label>
            <textarea id="ao" name="ao" rows="3" class="form-control"
                      placeholder="<spring:message code='label.incident.ao' />"
                      data-bind="value: ao.info, css: {'form-changed': ao.info.localChange, 'form-highlight': highlightAO}, valueUpdate: 'afterkeydown'">
            </textarea>
            <!-- ko if: patient -->
            <strong><spring:message code="text.patient.needs" /></strong>: <span data-bind="text: patient().erType"></span>
            <!-- /ko -->
        </div>

        <div class="form-group btn-group-vertical col-md-4">
            <!-- TODO data-bind -->
            <button type="button" class="btn btn-default" disabled><spring:message code="label.addressbook" /></button>
            <button type="button" class="btn btn-default" disabled><spring:message code="label.hospitals" /></button>
            <button type="button" class="btn btn-default" disabled><spring:message code="label.favorites" /></button>
        </div>
    </div>

    <div class="clearfix">
        <div class="form-group col-md-6">
            <label for="casus" class=""><spring:message code="label.incident.casus" />:</label>
            <input type="text" id="casus" name="casus" class="form-control"
                   placeholder="<spring:message code='label.incident.casus' />"
                   data-bind="value: casusNr, css: {'form-changed': casusNr.localChange}, valueUpdate: 'afterkeydown'" />
        </div>
        <div class="form-group col-md-6">
            <!-- ko if: patient -->
            <button type="button" class="btn btn-primary" data-bind="click: openPatient"><spring:message code="label.patient.edit"/></button>
            <!-- /ko -->
            <!-- ko ifnot: patient -->
            <button type="button" class="btn btn-danger" data-bind="click: openPatient, enable: id"><spring:message code="label.patient.add"/></button>
            <!-- /ko -->
        </div>
    </div>

    <%-- Incident State --%>
    <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
            <label class="sr-only"><spring:message code="label.incident.state" />:</label>
            <div class="ui-buttonset">
                <input id="state_new" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, disable: disableNew, value: Coceso.Constants.Incident.state.new" />
                <label for="state_new" class="ui-button ui-widget ui-state-default ui-corner-left" data-bind="css: {'ui-state-active': isNew, 'ui-state-disabled': disableNew}">
                    <span class="ui-button-text"><spring:message code="label.incident.state.new" /></span>
                </label>

                <input id="state_open" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, value: Coceso.Constants.Incident.state.open" />
                <label for="state_open" class="ui-button ui-widget ui-state-default" data-bind="css: {'ui-state-active': isOpen}">
                    <span class="ui-button-text"><spring:message code="label.incident.state.open" /></span>
                </label>

                <input id="state_dispo" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, disable: disableDispo, value: Coceso.Constants.Incident.state.dispo" />
                <label for="state_dispo" class="ui-button ui-widget ui-state-default" data-bind="css: {'ui-state-active': isDispo(), 'ui-state-disabled': disableDispo}">
                    <span class="ui-button-text"><spring:message code="label.incident.state.dispo" /></span>
                </label>

                <input id="state_working" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, disable: disableWorking, value: Coceso.Constants.Incident.state.working" />
                <label for="state_working" class="ui-button ui-widget ui-state-default" data-bind="css: {'ui-state-active': isWorking(), 'ui-state-disabled': disableWorking}">
                    <span class="ui-button-text"><spring:message code="label.incident.state.working" /></span>
                </label>

                <input id="state_done" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, value: Coceso.Constants.Incident.state.done" />
                <label for="state_done" class="ui-button ui-widget ui-state-default ui-corner-right" data-bind="css: {'ui-state-active': isDone()}">
                    <span class="ui-button-text"><spring:message code="label.incident.state.done" /></span>
                </label>
            </div>
        </div>
    </div>

    <%-- Assigned Units --%>
    <div class="assigned" data-bind="foreach: units.unitlist">
        <div class="form-group clearfix">
            <label class="col-md-2 control-label">
                <!-- ko if: $parent.unitCount() > 1 -->
                <span class="glyphicon glyphicon-share" data-bind="click: function() { $parent.duplicate($data)}"></span>
                <!-- /ko -->
                <span data-bind="text: call"></span>
            </label>
            <div class="col-md-10 ui-buttonset">
                <input type="radio" class="ui-helper-hidden-accessible"
                       data-bind="checked: taskState, disable: $parent.disableAssigned, attr: {id: $root.ui + '-taskState_assigned_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.assigned" />
                <label class="ui-button ui-widget ui-state-default ui-corner-left"
                       data-bind="attr: {for: $root.ui + '-taskState_assigned_' + $index()}, css: {'ui-state-active': isAssigned, 'ui-state-disabled': $parent.disableAssigned}">
                    <span class="ui-button-text"><spring:message code="label.task.state.assigned" /></span>
                </label>

                <input type="radio" class="ui-helper-hidden-accessible"
                       data-bind="checked: taskState, disable: $parent.disableBO, attr: {id: $root.ui + '-taskState_zbo_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.zbo" />
                <label class="ui-button ui-widget ui-state-default"
                       data-bind="attr: {for: $root.ui + '-taskState_zbo_' + $index()}, css: {'ui-state-active': isZBO, 'ui-state-disabled': $parent.disableBO}">
                    <span class="ui-button-text"><spring:message code="label.task.state.zbo" /></span>
                </label>

                <input type="radio" class="ui-helper-hidden-accessible"
                       data-bind="checked: taskState, disable: $parent.disableBO, attr: {id: $root.ui + '-taskState_abo_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.abo" />
                <label class="ui-button ui-widget ui-state-default"
                       data-bind="attr: {for: $root.ui + '-taskState_abo_' + $index()}, css: {'ui-state-active': isABO, 'ui-state-disabled': $parent.disableBO}">
                    <span class="ui-button-text"><spring:message code="label.task.state.abo" /></span>
                </label>

                <input type="radio" class="ui-helper-hidden-accessible"
                       data-bind="checked: taskState, disable: $parent.disableZAO, attr: {id: $root.ui + '-taskState_zao_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.zao" />
                <label class="ui-button ui-widget ui-state-default"
                       data-bind="attr: {for: $root.ui + '-taskState_zao_' + $index()}, css: {'ui-state-active': isZAO, 'ui-state-disabled': $parent.disableZAO}">
                    <span class="ui-button-text"><spring:message code="label.task.state.zao" /></span>
                </label>

                <input type="radio" class="ui-helper-hidden-accessible"
                       data-bind="checked: taskState, disable: $parent.disableAAO, attr: {id: $root.ui + '-taskState_aao_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.aao" />
                <label class="ui-button ui-widget ui-state-default"
                       data-bind="attr: {for: $root.ui + '-taskState_aao_' + $index()}, css: {'ui-state-active': isAAO, 'ui-state-disabled': $parent.disableAAO}">
                    <span class="ui-button-text"><spring:message code="label.task.state.aao" /></span>
                </label>

                <input type="radio" class="ui-helper-hidden-accessible"
                       data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_det_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.detached" />
                <label class="ui-button ui-widget ui-state-default ui-corner-right"
                       data-bind="attr: {for: $root.ui + '-taskState_det_' + $index()}, css: {'ui-state-active': isDetached}">
                    <span class="ui-button-text"><spring:message code="label.task.state.detached" /></span>
                </label>
            </div>
        </div>
    </div>

    <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
            <input type="button" class="btn btn-success" value="<spring:message code='label.ok' />" data-bind="enable: localChange, click: ok" />
            <input type="button" class="btn btn-primary" value="<spring:message code='label.save' />" data-bind="enable: localChange, click: save" />
            <input type="button" class="btn btn-warning" value="Reset" data-bind="enable: localChange, click: reset" />
            <input type="button" class="btn btn-default" value="<spring:message code="label.incident.duplicate" />" data-bind="click: function() { duplicate() }" /> <%-- Force undefined on method param --%>
        </div>
    </div>
</div>
</body>
</html>
