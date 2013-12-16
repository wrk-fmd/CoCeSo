<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
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

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css" />
</head>
<body>
<div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access" /></div>

<div class="ajax_content" data-bind="droppable: {drop: assignUnitForm}">
    <form class="incidents_form">
        <div class="form-group col-md-12">
            <label class="sr-only"><spring:message code="label.incident.type" />:</label>
            <div class="ui-buttonset">
                <input id="type_task" type="radio" class="ui-helper-hidden-accessible" name="type" data-bind="checked: type, enable: enableTask, value: Coceso.Constants.Incident.type.task" />
                <label for="type_task" class="ui-button ui-widget ui-state-default ui-corner-left" data-bind="css: {'ui-state-active': isTask(), 'ui-state-disabled': !enableTask()}">
                    <span class="ui-button-text"><spring:message code="label.incident.type.task" /></span>
                </label>

                <input id="type_relocation" type="radio" class="ui-helper-hidden-accessible" name="type" data-bind="checked: type, enable: enableRelocation, value: Coceso.Constants.Incident.type.relocation" />
                <label for="type_relocation" class="ui-button ui-widget ui-state-default ui-corner-right" data-bind="css: {'ui-state-active': isRelocation(), 'ui-state-disabled': !enableRelocation()}">
                    <span class="ui-button-text"><spring:message code="label.incident.type.relocation" /></span>
                </label>
            </div>
        </div>

        <div class="clearfix">
            <div class="form-group col-md-6">
                <label for="priority"><spring:message code="label.incident.priority" />:</label>
                <input type="number" id="priority" class="form-control" name="priority" min="0" max="100" data-bind="value: priority, css: {'form-changed': priority.changed}" />
            </div>

            <div class="form-group col-md-6">
                <input id="blue" type="checkbox" class="ui-helper-hidden-accessible" name="blue" data-bind="checked: blue" />
                <label for="blue" class="ui-button ui-widget ui-state-default ui-corner-all" data-bind="css: {'ui-state-active': blue()}">
                    <span class="ui-button-text"><spring:message code="label.incident.blue" /></span>
                </label>
            </div>
        </div>

        <div class="clearfix">
            <div class="form-group col-md-6">
                <label for="bo" class="sr-only"><spring:message code="label.incident.bo" />:</label>
                <textarea id="bo" name="bo" rows="3" class="form-control" placeholder="BO" data-bind="enable: enableBO, value: bo.info, css: {'form-changed': bo.info.changed}"></textarea>
            </div>

            <div class="form-group col-md-6">
                <label for="ao" class="sr-only"><spring:message code="label.incident.ao" />:</label>
                <textarea id="ao" name="ao" rows="3" class="form-control" placeholder="AO" data-bind="value: ao.info, css: {'form-changed': ao.info.changed}"></textarea>
            </div>
        </div>

        <div class="form-group col-md-12">
            <label for="info" class="sr-only"><spring:message code="label.incident.info" />:</label>
            <textarea id="info" name="info" rows="3" class="form-control" placeholder="Info" data-bind="value: info, css: {'form-changed': info.changed}"></textarea>
        </div>

        <div class="clearfix">
            <div class="form-group col-md-6">
                <label for="caller" class="sr-only"><spring:message code="label.incident.caller" />:</label>
                <input type="text" id="caller" name="caller" class="form-control" placeholder="Caller" data-bind="value: caller, css: {'form-changed': caller.changed}" />
            </div>
            <div class="form-group col-md-6">
                <label for="casus" class="sr-only"><spring:message code="label.incident.casus" />:</label>
                <input type="text" id="casus" name="casus" class="form-control" placeholder="Casus" data-bind="value: casusNr, css: {'form-changed': casusNr.changed}" />
            </div>
        </div>

        <div class="clearfix">
            <div class="form-group col-md-offset-2 col-md-10">
                <label class="sr-only"><spring:message code="label.incident.state" />:</label>
                <div class="ui-buttonset">
                    <input id="state_new" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, enable: enableNew, value: Coceso.Constants.Incident.state.new" />
                    <label for="state_new" class="ui-button ui-widget ui-state-default ui-corner-left" data-bind="css: {'ui-state-active': isNew(), 'ui-state-disabled': !enableNew()}">
                        <span class="ui-button-text"><spring:message code="label.incident.state.new" /></span>
                    </label>

                    <input id="state_open" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, value: Coceso.Constants.Incident.state.open" />
                    <label for="state_open" class="ui-button ui-widget ui-state-default" data-bind="css: {'ui-state-active': isOpen()}">
                        <span class="ui-button-text"><spring:message code="label.incident.state.open" /></span>
                    </label>

                    <input id="state_dispo" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, enable: enableDispo, value: Coceso.Constants.Incident.state.dispo" />
                    <label for="state_dispo" class="ui-button ui-widget ui-state-default" data-bind="css: {'ui-state-active': isDispo(), 'ui-state-disabled': !enableDispo()}">
                        <span class="ui-button-text"><spring:message code="label.incident.state.dispo" /></span>
                    </label>

                    <input id="state_working" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, enable: enableWorking, value: Coceso.Constants.Incident.state.working" />
                    <label for="state_working" class="ui-button ui-widget ui-state-default" data-bind="css: {'ui-state-active': isWorking(), 'ui-state-disabled': !enableWorking()}">
                        <span class="ui-button-text"><spring:message code="label.incident.state.working" /></span>
                    </label>

                    <input id="state_done" type="radio" class="ui-helper-hidden-accessible" name="state" data-bind="checked: state, value: Coceso.Constants.Incident.state.done" />
                    <label for="state_done" class="ui-button ui-widget ui-state-default ui-corner-right" data-bind="css: {'ui-state-active': isDone()}">
                        <span class="ui-button-text"><spring:message code="label.incident.state.done" /></span>
                    </label>
                </div>
            </div>
        </div>

        <div class="assigned" data-bind="foreach: units.units">
            <div class="form-group clearfix">
                <label class="col-md-2 control-label" data-bind="text: call"></label>
                <div class="col-md-10 ui-buttonset">
                    <input type="radio" class="ui-helper-hidden-accessible"
                           data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_assigned_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.assigned" />
                    <label class="ui-button ui-widget ui-state-default ui-corner-left"
                           data-bind="attr: {for: $root.ui + '-taskState_assigned_' + $index()}, css: {'ui-state-active': isAssigned()}">
                        <span class="ui-button-text"><spring:message code="label.unit.state.assigned" /></span>
                    </label>

                    <input type="radio" class="ui-helper-hidden-accessible"
                           data-bind="checked: taskState, enable: $parent.enableBO, attr: {id: $root.ui + '-taskState_zbo_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.zbo" />
                    <label class="ui-button ui-widget ui-state-default"
                           data-bind="attr: {for: $root.ui + '-taskState_zbo_' + $index()}, css: {'ui-state-active': isZBO(), 'ui-state-disabled': !$parent.enableBO()}">
                        <span class="ui-button-text"><spring:message code="label.unit.state.zbo" /></span>
                    </label>

                    <input type="radio" class="ui-helper-hidden-accessible"
                           data-bind="checked: taskState, enable: $parent.enableBO, attr: {id: $root.ui + '-taskState_abo_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.abo" />
                    <label class="ui-button ui-widget ui-state-default"
                           data-bind="attr: {for: $root.ui + '-taskState_abo_' + $index()}, css: {'ui-state-active': isABO(), 'ui-state-disabled': !$parent.enableBO()}">
                        <span class="ui-button-text"><spring:message code="label.unit.state.abo" /></span>
                    </label>

                    <input type="radio" class="ui-helper-hidden-accessible"
                           data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_zao_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.zao" />
                    <label class="ui-button ui-widget ui-state-default"
                           data-bind="attr: {for: $root.ui + '-taskState_zao_' + $index()}, css: {'ui-state-active': isZAO()}">
                        <span class="ui-button-text"><spring:message code="label.unit.state.zao" /></span>
                    </label>

                    <input type="radio" class="ui-helper-hidden-accessible"
                           data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_aao_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.aao" />
                    <label class="ui-button ui-widget ui-state-default"
                           data-bind="attr: {for: $root.ui + '-taskState_aao_' + $index()}, css: {'ui-state-active': isAAO()}">
                        <span class="ui-button-text"><spring:message code="label.unit.state.aao" /></span>
                    </label>

                    <input type="radio" class="ui-helper-hidden-accessible"
                           data-bind="checked: taskState, attr: {id: $root.ui + '-taskState_det_' + $index(), name: $root.ui + '-taskState_' + $index()}, value: Coceso.Constants.TaskState.detached" />
                    <label class="ui-button ui-widget ui-state-default ui-corner-right"
                           data-bind="attr: {for: $root.ui + 'taskState_det_' + $index()}, css: {'ui-state-active': isDetached()}">
                        <span class="ui-button-text"><spring:message code="label.unit.state.detached" /></span>
                    </label>
                </div>
            </div>
        </div>

        <div class="clearfix">
            <div class="form-group col-md-offset-2 col-md-10" data-bind="visible: changed">
                <input type="button" class="btn btn-success" value="<spring:message code="label.save" />" data-bind="click: save" />
                <input type="button" class="btn btn-warning" value="<spring:message code="label.reset" />" />
            </div>
        </div>
    </form>
</div>
</body>
</html>
