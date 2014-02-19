<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--
/**
*
* Licensed under The MIT License
* For full copyright and license information, please see the LICENSE.txt
* Redistributions of files must retain the above copyright notice.
*
* @link          https://sourceforge.net/projects/coceso/
* @package       coceso.client.html
* @since         Rev. 1
* @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
*
* Dependencies:
*	coceso.css
*/
-->
<html>
<head>
    <title><spring:message code="label.units" /> / <spring:message code="label.main.list" /></title>

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css">
</head>
<body>
<div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access" /></div>

<div class="ajax_content">

    <a href="#" class="ui-helper-hidden-accessible">&nbsp;</a>

    <div data-bind="template: { name: 'container-template', data: top }"></div>

    <%-- BEGIN TEMPLATE DEFINITIONs --%>
    <script type="text/html" id="unit-list-entry-template">
        <li class="dropdown">
            <!-- ko if: portable -->
            <a href="#" class="unit_state dropdown-toggle" data-bind="draggable: dragOptions, popover: popover" data-toggle="dropdown" oncontextmenu="this.click(); return false;">
                <span class="ui-corner-left" data-bind="text: call, css: stateCss"></span><span class="ui-corner-right" data-bind="html: taskText, css: taskCss, click: function() {nextState()}, clickBubble: incidentCount() !== 1"></span>
            </a>
            <!-- /ko -->
            <!-- ko ifnot: portable -->
            <a href="#" class="unit_state dropdown-toggle" data-toggle="dropdown" oncontextmenu="this.click(); return false;">
                <span class="ui-corner-all" data-bind="text: call, css: stateCss"></span>
            </a>
            <!-- /ko -->
            <ul class="dropdown-menu">
                <li class="dropdown-header"><spring:message code="label.unit.state_set" /></li>
                <li data-bind="css: {disabled: isNEB}"><a href="#" title="<spring:message code="label.set" />: <spring:message code="label.unit.state.neb" />" data-bind="click: setNEB"><spring:message code="label.unit.state.neb" /></a></li>
                <li data-bind="css: {disabled: isEB}"><a href="#" title="<spring:message code="label.set" />: <spring:message code="label.unit.state.eb" />" data-bind="click: setEB"><spring:message code="label.unit.state.eb" /></a></li>
                <li data-bind="css: {disabled: isAD}"><a href="#" title="<spring:message code="label.set" />: <spring:message code="label.unit.state.ad" />" data-bind="click: setAD"><spring:message code="label.unit.state.ad" /></a></li>

                <!-- ko if: portable -->
                <li class="divider"></li>
                <li class="dropdown-header"><spring:message code="label.actions" /></li>
                <li data-bind="css: {disabled: disableSendHome}"><a href="#" title="<spring:message code="label.unit.send_home" />" data-bind="click: sendHome"><spring:message code="label.unit.send_home" /></a></li>
                <li data-bind="css: {disabled: disableStandby}"><a href="#" title="<spring:message code="label.incident.type.standby" />" data-bind="click: standby"><spring:message code="label.incident.type.standby" /></a></li>
                <li data-bind="css: {disabled: disableHoldPosition}"><a href="#" title="<spring:message code="label.incident.type.hold_position" />" data-bind="click: holdPosition"><spring:message code="label.incident.type.hold_position" /></a></li>
                <!-- /ko -->

                <li class="divider"></li>
                <!-- ko if: portable -->
                <li><a href="#" title="<spring:message code="label.unit.new_incident" />" data-bind="click: addIncident"><spring:message code="label.unit.new_incident" /></a></li>
                <!-- /ko -->
                <li><a href="#" title="<spring:message code="label.unit.report_incident" />" data-bind="click: reportIncident"><spring:message code="label.unit.report_incident" /></a></li>
                <li><a href="#" title="<spring:message code="label.unit.edit" />" data-bind="click: openForm"><spring:message code="label.unit.edit" /></a></li>
                <li><a href="#" title="<spring:message code="label.log.view" />" data-bind="click: openLog"><spring:message code="label.log.view" /></a></li>
            </ul>
        </li>
    </script>

    <script type="text/html" id="container-template">

        <!-- UNIT LIST -->
        <ul class="unit_list" data-bind="template: {name: 'unit-list-entry-template', foreach: units}"></ul>
        <!-- /UNIT LIST -->

        <div data-bind="foreach: subContainer">
            <div class="panel panel-default">
                <div class="panel-heading unit-view-toggle">
                    <h3 class="panel-title" data-bind="text: name"></h3>
                </div>
                <div class="panel-body">

                    <div data-bind="template: 'container-template'"></div>

                </div>
            </div>
        </div>
    </script>
    <%-- END TEMPLATE DEFINITIONs --%>

</div>


</body>
</html>
