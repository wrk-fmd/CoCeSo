<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Client HTML main interface
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<html>
  <head>
    <sec:authentication property="principal.username" htmlEscape="false" var="username"/>
    <script type="text/javascript">
      var CocesoConf = {
        map3d: false,
        jsonBase: "<c:url value="/data/"/>",
        contentBase: "<c:url value="/main/"/>",
        mapImagePath: "<c:url value="/static/css/images/"/>",
        langBase: "<c:url value="/static/i18n/"/>",
        language: "<spring:message code="this.languageCode"/>",
        username: "${fn:escapeXml(username)}",
        plugins: ${cocesoConfig.jsPlugins}
      };
    </script>
    <t:head entry="main"/>
  </head>
  <body class="main">
    <header>
      <nav id="navbar" class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a href="<c:url value="/home"/>" class="navbar-brand" target="_blank"><spring:message code="coceso"/></a>
        </div>

        <div class="collapse navbar-collapse" id="navbar-collapse-1">
          <%-- Order of Elements has to be right - left - center ! Otherwise css produces bulls*!t --%>

          <div class="navbar-hide-on-mobile">
            <ul id="nav-notifications" class="nav navbar-nav navbar-right">

              <%-- Notifications --%>
              <li>
                <a href="#" title="<spring:message code="main.connection"/>" onclick="return false;"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-signal" data-bind="css: cssError"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="main.incident.highlighted"/>"
                   data-bind="click: function() {openIncidents({filter: ['overview', 'highlighted'], title: '<spring:message code="main.incident.highlighted"/>'})}"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-time "></span>
                  <span class="badge" data-bind="text: openIncidentCounter, css: cssOpen"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="main.incident.open_transport"/>"
                   data-bind="click: function() {openIncidents({filter: ['transport', 'highlighted'], title: '<spring:message code="main.incident.open_transport"/>'})}"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-log-out"></span>
                  <span class="badge" data-bind="text: openTransportCounter, css: cssTransport"></span>
                </a>
              </li>
              <li>
                <!-- open units filtered for 'alarm not confirmed yet' -->
                <a href="#" title="<spring:message code="main.unit.radio"/>"
                   data-bind="click: function() {openUnits({filter: ['radio'], title: '<spring:message code="main.unit.radio"/>'}, 'left+30% bottom')}"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-bullhorn"></span>
                  <span class="badge" data-bind="text: radioCounter, css: cssRadio"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="main.unit.free"/>"
                   data-bind="click: function() {openUnits({filter: ['free'], title: '<spring:message code="main.unit.free"/>'}, 'left bottom')}"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-exclamation-sign"></span>
                  <span class="badge" data-bind="text: freeCounter, css: cssFree"></span>
                </a>
              </li>

              <%-- Concern Name, Clock --%>
              <li>
                <a href="#" onclick="return false;" class="notification-icon">
                  <span class="glyphicon glyphicon-info-sign tooltipped" title="<h5><c:out value="${concern.name}"/></h5>"
                        data-toggle="tooltip" data-html="true" data-placement="bottom"></span>
                </a>
              </li>
              <li>
                <%-- Initialization Value for auto-margin of centered element --%>
                <span class="navbar-brand" data-bind="text: clock_time">00:00:00</span>
              </li>
            </ul>
          </div>

          <ul class="nav navbar-nav">
            <%-- UNITS --%>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="units"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li>
                  <a href="#" title="<spring:message code="units"/>" data-bind="click: function() {openUnits()}">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="main.unit.overview"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="main.unit.hierarchy"/>" data-bind="click: function() {openHierarchyUnits()}">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="main.unit.hierarchy"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="main.unit.radio"/>"
                     data-bind="click: function() {openUnits({filter: ['radio'], title: '<spring:message code="main.unit.radio"/>'})}">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="main.unit.radio"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="main.unit.available"/>"
                     data-bind="click: function() {openUnits({filter: ['available'], title: '<spring:message code="main.unit.available"/>'}, 'left+40% bottom')}">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="main.unit.available"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="main.unit.free"/>"
                     data-bind="click: function() {openUnits({filter: ['free'], title: '<spring:message code="main.unit.free"/>'})}">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="main.unit.free"/>
                  </a>
                </li>
              </ul>
            </li>
            <%-- INCIDENTS --%>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                <spring:message code="incidents"/>
                <b class="caret"></b>
              </a>
              <ul class="dropdown-menu">
                <li>
                  <a href="#" title="<spring:message code="incident.add"/>" data-bind="click: function() {openIncident({section: sections.filter()})}">
                    <span class="glyphicon glyphicon-plus"></span> <spring:message code="incident.add"/>...</a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="incident.relocation.add"/>" data-bind="click: function() {openIncident({section: sections.filter(), type: 'Relocation'})}">
                    <span class="glyphicon glyphicon-share-alt"></span> <spring:message code="incident.relocation.add"/>...</a>
                </li>

                <li class="divider"></li>

                <li>
                  <a href="#" title="<spring:message code="main.incident.overview"/>"
                     data-bind="click: function() {openIncidents({filter: ['overview'], title: '<spring:message code="main.incident.overview"/>'})}">
                    <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="main.incident.overview"/></a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="main.incident.active"/>"
                     data-bind="click: function() {openIncidents({filter: ['overview', 'active'], title: '<spring:message code="main.incident.active"/>'})}">
                    <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="main.incident.active"/></a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="main.incident.complete"/>"
                     data-bind="click: function() {openIncidents({filter: ['overview', 'completed'], title: '<spring:message code="main.incident.complete"/>'})}">
                    <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="main.incident.complete"/></a>
                </li>
              </ul>
            </li>
            <%-- WINDOWS --%>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                <spring:message code="main.windows"/>
                <b class="caret"></b>
              </a>
              <ul class="dropdown-menu">
                <li>
                  <a href="#" title="<spring:message code="log.custom"/>"
                     data-bind="click: function() {openLogs({url: 'log/getCustom.json', autoload: true, title: '<spring:message code="log.custom"/>'})}">
                    <span class="glyphicon glyphicon-info-sign"></span> <spring:message code="log.custom"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="log.add"/>" data-bind="click: function() {openLogAdd()}">
                    <span class="glyphicon glyphicon-book"></span> <spring:message code="log.add"/>...
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="patients"/>" data-bind="click: function() {openPatients()}">
                    <span class="glyphicon glyphicon-info-sign"></span> <spring:message code="patients"/>
                  </a>
                </li>
                <li>
                  <a href="<c:url value="/main/map"/>" target="_blank" title="<spring:message code="map"/>"
                     data-bind="click: function() {openMap()}">
                    <span class="glyphicon glyphicon-globe"></span> <spring:message code="map"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="radio"/>" data-bind="click: function() {openRadio()}">
                    <span class="glyphicon glyphicon-phone"></span> <spring:message code="radio"/>
                  </a>
                </li>
                <%-- External (of Main Program) Links --%>
                <li class="divider"></li>
                <li>
                  <a href="<c:url value="/edit/"/>" target="_blank">
                    <span class="glyphicon glyphicon-new-window"></span> <spring:message code="nav.edit_concern"/>
                  </a>
                </li>
                <li>
                  <a href="<c:url value="/dashboard"/>" target="_blank">
                    <span class="glyphicon glyphicon-new-window"></span> <spring:message code="nav.dashboard"/>
                  </a>
                </li>
                <%-- Low Priority Links --%>
                <li class="divider"></li>
                <li>
                  <a href="#" title="<spring:message code="key"/>"
                     data-bind="click: function() {openStatic('<spring:message code="key"/>', 'key.html')}">
                    <span class="glyphicon glyphicon-question-sign"></span> <spring:message code="key"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="log"/>" data-bind="click: function() {openLogs({title: '<spring:message code="log"/>'})}">
                    <span class="glyphicon glyphicon-info-sign"></span> <spring:message code="log"/>
                  </a>
                </li>
                <%-- Resize workspace --%>
                <li class="divider"></li>
                <li>
                  <a href="#" title="<spring:message code="main.workspace.enlarge"/>"
                     data-bind="click: function() {resizeWorkspace(1.5)}">
                    <span class="glyphicon glyphicon-plus-sign"></span> <spring:message code="main.workspace.enlarge"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="main.workspace.reset"/>"
                     data-bind="click: function() {resizeWorkspace()}">
                    <span class="glyphicon glyphicon-minus-sign"></span> <spring:message code="main.workspace.reset"/>
                  </a>
                </li>
              </ul>
            </li>
            <!-- ko with: sections -->
            <!-- ko if: hasSections -->
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-bind="style: {color: filter() ? 'red' : ''}">
                <spring:message code="filter"/>
                <b class="caret"></b>
              </a>
              <ul class="dropdown-menu">
                <li data-bind="css: {active: !filter()}">
                  <a href="#" data-bind="click: showAll"><span class="glyphicon glyphicon-ban-circle"></span> <spring:message code="main.section.nofilter"/></a>
                </li>
                <li class="divider"></li>
                <!-- ko foreach: sections -->
                <li data-bind="css: {active: active}">
                  <a href="#" data-bind="click: select"><span class="glyphicon glyphicon-filter"></span> <span data-bind="text: name"></span></a>
                </li>
                <!-- /ko -->
              </ul>
            </li>
            <!-- /ko -->
            <!-- /ko -->
            <li><a href="<c:url value="/home"/>"><spring:message code="exit"/></a></li>
          </ul>

          <div class="navbar-hide-on-1000px">
            <ul class="nav navbar-nav navbar-center">
              <%-- Quicklinks --%>
              <li>
                <a href="#" title="<spring:message code="incident.add"/>" data-bind="click: function() {openIncident({section: sections.filter()})}"
                   data-toggle="tooltip" data-placement="bottom" class="quicklink quicklink_incident tooltipped notification-icon">
                  <span class="glyphicon glyphicon-plus" title="<spring:message code="incident.add.title"/>"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="incident.relocation.add"/>" data-bind="click: function() {openIncident({section: sections.filter(), type: 'Relocation'})}"
                   data-toggle="tooltip" data-placement="bottom" class="quicklink quicklink_relocation tooltipped notification-icon">
                  <span class="glyphicon glyphicon-share-alt" title="<spring:message code="incident.relocation.add.title"/>"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="log.add"/>" data-bind="click: function() {openLogAdd()}"
                   data-toggle="tooltip" data-placement="bottom" class="quicklink quicklink_log tooltipped notification-icon">
                  <span class="glyphicon glyphicon-book"></span>
                </a>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </header>

    <div id="dialog_scrolling"><div id="dialog_container"></div></div>

    <footer>
      <ul id="taskbar"></ul>
    </footer>

    <%@include file="templates/main.jsp"%>
  </body>
</html>
