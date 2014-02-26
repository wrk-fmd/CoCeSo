<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<!--
/**
* CoCeSo
* Client HTML main interface
* Copyright (c) WRK\Daniel Rohr
*
* Licensed under The MIT License
* For full copyright and license information, please see the LICENSE.txt
* Redistributions of files must retain the above copyright notice.
*
* @copyright Copyright (c) 2013 Daniel Rohr
* @link https://sourceforge.net/projects/coceso/
* @package coceso.client.html
* @since Rev. 1
* @license MIT License (http://www.opensource.org/licenses/mit-license.php)
*
* Dependencies:
* coceso.client.css
* coceso.client.js
* bootstrap.dropdown.js
*/
-->
<html>
<head>
    <title><spring:message code="label.coceso"/></title>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css" />" type="text/css"/>

    <script src="<c:url value="/static/js/jquery-1.11.0.js"/>" type="text/javascript"></script>
    <%-- See Bug #9166 of JQuery UI: http://bugs.jqueryui.com/ticket/9166 --%>
    <script src="<c:url value="/static/js/jquery-ui-1.9.2.custom.js"/>" type="text/javascript"></script>

    <script src="<c:url value="/static/js/jquery.ui.winman.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.i18n.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout.mapping.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/bootstrap.dropdown.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/bootstrap.modals.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/bindings.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/coceso.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.ui.touch-punch.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            Coceso.Conf.jsonBase = "${pageContext.request.contextPath}/data/";
            Coceso.Conf.contentBase = "${pageContext.request.contextPath}/main/";
            Coceso.Conf.langBase = "${pageContext.request.contextPath}/static/i18n/";
            Coceso.Conf.language = "<spring:message code="this.languageCode" />";
            Coceso.Conf.keyboardControl = false;
            Coceso.Conf.debug = true;

            Coceso.startup();

            Coceso.UI.openHierarchyUnits("<spring:message code="label.units"/>: <spring:message code="label.main.unit.hierarchy"/>", "unit_hierarchy.html");
            Coceso.UI.openIncidents("<spring:message code='label.main.incident.active' />", 'incident.html', {filter: ['overview', 'active']}, {
                position: {at: "left+70% top"}
            });

            if(Coceso.Conf.keyboardControl) {
                $( "body" ).keydown(function( event ) {
                    if(event.which == Coceso.Conf.keyMapping.openIncidentKey) {
                        Coceso.UI.openIncident("<spring:message code='label.incident' /> / <spring:message code='label.add' />", 'incident_form.html');
                    }
                });
                // TODO !! Prevent new Incident Window on Keydown in Input Tags
            }

            $(".tooltipped").tooltip();
        });
    </script>
</head>
<body class="main">
<header>
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="navbar-header">
            <a href="<c:url value="/welcome" />" class="navbar-brand" target="_blank"><spring:message code="label.coceso"/></a>
        </div>

        <ul class="nav navbar-nav">
            <%-- CALLTAKER --%>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.calltaker" /> <b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li>
                        <a href="#"
                           title="<spring:message code='label.incident' /> / <spring:message code='label.add' />"
                           onclick="return Coceso.UI.openIncident(this.title, 'incident_form.html');">
                            <span class="glyphicon glyphicon-plus"></span> <spring:message code="label.incident.add"/>
                        </a>
                    </li>
                    <li class="divider"></li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.incident.new' />"
                           onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'new'], showTabs: false});">
                            <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.new"/>
                        </a>
                    </li>
                </ul>
            </li>
            <%-- DISPONENT --%>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.dispatcher" /> <b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li>
                        <a href="#"
                           title="<spring:message code='label.incident' /> / <spring:message code='label.add' />"
                           onclick="return Coceso.UI.openIncident(this.title, 'incident_form.html');">
                            <span class="glyphicon glyphicon-plus"></span> <spring:message code="label.incident.add"/></a>
                    </li>
                    <li class="divider"></li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.incident.open' />"
                           onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'open'], showTabs: false});">
                            <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.open"/></a>
                    </li>
                </ul>
            </li>
            <%-- RADIO OPERATOR --%>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.radio_operator" /> <b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li>
                        <a href="#"
                           title="<spring:message code='label.incident' /> / <spring:message code='label.add' />"
                           onclick="return Coceso.UI.openIncident(this.title, 'incident_form.html');">
                            <span class="glyphicon glyphicon-plus"></span> <spring:message code="label.incident.add"/></a>
                    </li>
                    <li class="divider"></li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.unit.for_dispo' />"
                           onclick="return Coceso.UI.openUnits(this.title, 'unit.html', {filter: ['radio']});">
                            <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.for_dispo"/>
                        </a>
                    </li>
                </ul>
            </li>
            <%-- UNITS --%>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.units"/> <b
                        class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li>
                        <a href="#" title="<spring:message code='label.units' />"
                           onclick="return Coceso.UI.openUnits(this.title, 'unit.html');">
                            <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.overview"/>
                        </a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code="label.main.unit.hierarchy"/>"
                           onclick="return Coceso.UI.openHierarchyUnits(this.title, 'unit_hierarchy.html');">
                            <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.hierarchy"/>
                        </a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.unit.for_dispo' />"
                           onclick="return Coceso.UI.openUnits(this.title, 'unit.html', {filter: ['radio']});">
                            <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.for_dispo"/>
                        </a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.unit.available' />"
                           onclick="return Coceso.UI.openUnits(this.title, 'unit.html', {filter: ['available']}, { position: {at: 'left+40% bottom'}});">
                            <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.available"/>
                        </a>
                    </li>
                </ul>
            </li>
            <%-- INCIDENTS --%>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                    <spring:message code="label.incidents"/>
                    <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                    <li>
                        <a href="#"
                           title="<spring:message code='label.incident' /> / <spring:message code='label.add' />"
                           onclick="return Coceso.UI.openIncident(this.title, 'incident_form.html');">
                            <span class="glyphicon glyphicon-plus"></span> <spring:message code="label.incident.add"/></a>
                    </li>
                    <li class="divider"></li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.incident.overview' />"
                           onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview']});">
                            <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.overview"/></a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.incident.active' />"
                           onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'active']});">
                            <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.active"/></a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.incident.new' />"
                           onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'new'], showTabs: false});">
                            <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.new"/></a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.incident.open' />"
                           onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'open'], showTabs: false});">
                            <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.open"/></a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code='label.main.incident.complete' />"
                           onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'completed']});">
                            <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.complete"/></a>
                    </li>
                </ul>
            </li>
            <%-- WINDOWS --%>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                    <spring:message code="label.main.windows"/>
                    <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                    <li>
                        <a href="#" title="<spring:message code='label.log' />"
                           onclick="return Coceso.UI.openLogs(this.title, 'log.html');">
                            <span class="glyphicon glyphicon-info-sign"></span> <spring:message code="label.log"/>
                        </a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code="label.debug" />" onclick="return Coceso.UI.openDebug(this.title, 'debug.html');">
                            <span class="glyphicon glyphicon-warning-sign"></span> <spring:message code="label.debug" />
                        </a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code="label.main.license" />" onclick="return Coceso.UI.openExternalStatic(this.title, '<c:url value="/static/license.html" />');">
                            <span class="glyphicon glyphicon-copyright-mark"></span> <spring:message code="label.main.license" />
                        </a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code="label.main.key" />" onclick="return Coceso.UI.openStatic(this.title, 'key.html');">
                            <span class="glyphicon glyphicon-question-sign"></span>
                            <spring:message code="label.main.key" />
                        </a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code='label.log.custom' />"
                           onclick="return Coceso.UI.openLogs(this.title, 'log.html', {url: 'log/getCustom', autoload: true});">
                            <span class="glyphicon glyphicon-info-sign"></span> <spring:message code="label.log.custom"/>
                        </a>
                    </li>
                    <li>
                        <a href="#" title="<spring:message code="label.log.add" />" onclick="return Coceso.UI.openLogAdd(this.title, 'log_add.html');">
                            <span class="glyphicon glyphicon-book"></span>
                            <spring:message code="label.log.add" />
                        </a>
                    </li>
                    <%-- External (of Main Program) Links --%>
                    <li class="divider"></li>
                    <li>
                        <a href="<c:url value="/edit/"/>" target="_blank">
                            <span class="glyphicon glyphicon-link"></span>
                            <spring:message code="label.nav.edit_concern"/>
                        </a>
                    </li>
                    <li>
                        <a href="<c:url value="/dashboard"/>?concern=${concern.id}" target="_blank">
                            <span class="glyphicon glyphicon-link"></span>
                            <spring:message code="label.nav.dashboard"/>
                        </a>
                    </li>
                </ul>
            </li>

        </ul>
        <%-- Notifications
            !!! this.title doesn't work here, title attribute is deleted by $.tooltip() !!!--%>
        <ul id="nav-notifications" class="nav navbar-nav navbar-right">
            <li>
                <a href="#" title="<spring:message code="label.connection_status" />" onclick="return false;"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped">
                    <span class="glyphicon glyphicon-signal notification-icon" data-bind="css: cssError"></span>
                </a>
            </li>
            <li>
                <a href="#" title="<spring:message code="label.main.incident.new_or_open" />"
                   onclick="return Coceso.UI.openIncidents('<spring:message code="label.main.incident.new_or_open" />', 'incident.html', {filter: ['overview', 'new_or_open'], showTabs: false}, {});"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped">
                    <span class="glyphicon glyphicon-time notification-icon"></span>
                    <span class="badge" data-bind="text: openIncidentCounter, css: cssOpen"></span>
                </a>
            </li>
            <li>
                <a href="#" title="<spring:message code="label.incident.type.transport.open" />"
                   onclick="return false;"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped">
                    <span class="glyphicon glyphicon-log-out notification-icon"></span>
                    <span class="badge" data-bind="text: openTransportCounter, css: cssTransport"></span>
                </a>
            </li>
            <li>
                <a href="#" title="<spring:message code="label.main.unit.for_dispo" />"
                   onclick="return Coceso.UI.openUnits('<spring:message code="label.main.unit.for_dispo" />', 'unit.html', {filter: ['radio']}, { position: {at: 'left+30% bottom'}});"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped">
                    <span class="glyphicon glyphicon-earphone notification-icon"></span>
                    <span class="badge" data-bind="text: radioCounter, css: cssRadio"></span>
                </a>
            </li>
            <li>
                <a href="#" title="<spring:message code="label.main.unit.free" />"
                   onclick="return Coceso.UI.openUnits('<spring:message code="label.main.unit.free" />', 'unit.html', {filter: ['free']}, { position: {at: 'left bottom'}});"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped">
                    <span class="glyphicon glyphicon-exclamation-sign notification-icon"></span>
                    <span class="badge" data-bind="text: freeCounter, css: cssFree"></span>
                </a>
            </li>


            <%-- Concern Name, Clock --%>
            <li>
                <a href="#" onclick="return false;">
                    <span class="glyphicon glyphicon-info-sign tooltipped" title="<h5>${concern.name}</h5>"
                          data-toggle="tooltip" data-html="true" style="font-size: large;" data-placement="bottom"></span>
                </a>
            </li>
            <li><span id="clock" class="navbar-brand"></span></li>
        </ul>
    </nav>
</header>

<div id="dialog_container">
    <noscript>
        <div class="alert alert-danger"><strong>JavaScript required</strong><br/>Enable JavaScript to use this page.
        </div>
    </noscript>
</div>

<%@include file="main_content/templates.jsp"%>

<footer>
    <ul id="taskbar"></ul>
</footer>
</body>
</html>
