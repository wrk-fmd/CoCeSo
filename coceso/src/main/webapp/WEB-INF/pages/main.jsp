<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>
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
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<html>
  <head>
    <title><spring:message code="label.coceso"/></title>
    <t:head jquery="i18n, ui, ui.touch-punch" js="assets/typeahead.bundle, assets/leaflet, jquery.ui.winman, main"/>
    <script type="text/javascript">
      $(document).ready(function() {
        L.Icon.Default.imagePath = "<c:url value="/static/css/images/"/>";
        Coceso.Conf.layerBase = "<c:url value="/static/imgs/layer/"/>";
        Coceso.Conf.jsonBase = "<c:url value="/data/"/>";
        Coceso.Conf.contentBase = "<c:url value="/main/"/>";
        Coceso.Conf.langBase = "<c:url value="/static/i18n/"/>";
        Coceso.Conf.language = "<spring:message code="this.languageCode"/>";
        Coceso.startup();
        Coceso.UI.openHierarchyUnits();
        Coceso.UI.openIncidents({filter: ['overview', 'active'], title: "<spring:message code="label.main.incident.active"/>"}, {position: {at: "left+70% top"}});
      });
    </script>
  </head>
  <body class="main">
    <header>
      <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a href="<c:url value="/home"/>" class="navbar-brand" target="_blank"><spring:message code="label.coceso"/></a>
        </div>

        <div class="collapse navbar-collapse" id="navbar-collapse-1">
          <%-- Order of Elements has to be right - left - center ! Otherwise css produces bulls*!t --%>

          <div class="navbar-hide-on-mobile">
            <ul id="nav-notifications" class="nav navbar-nav navbar-right">

              <%-- Notifications
               !!! this.title doesn't work here, title attribute is deleted by $.tooltip() !!!--%>
              <li>
                <a href="#" title="<spring:message code="label.connection_status"/>" onclick="return false;"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-signal" data-bind="css: cssError"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="label.main.incident.new_or_open"/>"
                   onclick="return Coceso.UI.openIncidents({filter: ['overview', 'new_or_open'], title: '<spring:message code="label.main.incident.new_or_open"/>'});"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-time "></span>
                  <span class="badge" data-bind="text: openIncidentCounter, css: cssOpen"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="label.incident.type.transport.open"/>"
                   onclick="return Coceso.UI.openIncidents({filter: ['transport', 'new_or_open'], title: '<spring:message code="label.incident.type.transport.open"/>'});"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-log-out"></span>
                  <span class="badge" data-bind="text: openTransportCounter, css: cssTransport"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="label.main.unit.for_dispo"/>"
                   onclick="return Coceso.UI.openUnits({filter: ['radio'], title: '<spring:message code="label.main.unit.for_dispo"/>'}, {position: {at: 'left+30% bottom'}});"
                   data-toggle="tooltip" data-placement="bottom" class="tooltipped notification-icon">
                  <span class="glyphicon glyphicon-bullhorn"></span>
                  <span class="badge" data-bind="text: radioCounter, css: cssRadio"></span>
                </a>
              </li>
              <li>
                <a href="#" title="<spring:message code="label.main.unit.free"/>"
                   onclick="return Coceso.UI.openUnits({filter: ['free'], title: '<spring:message code="label.main.unit.free"/>'}, {position: {at: 'left bottom'}});"
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
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.units"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li>
                  <a href="#" title="<spring:message code="label.units"/>"
                     onclick="return Coceso.UI.openUnits();">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.overview"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.unit.hierarchy"/>" onclick="return Coceso.UI.openHierarchyUnits();">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.hierarchy"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.unit.for_dispo"/>"
                     onclick="return Coceso.UI.openUnits({filter: ['radio'], title: this.title});">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.for_dispo"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.unit.available"/>"
                     onclick="return Coceso.UI.openUnits({filter: ['available'], title: this.title}, {position: {at: 'left+40% bottom'}});">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.available"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.unit.free"/>"
                     onclick="return Coceso.UI.openUnits({filter: ['free'], title: this.title});">
                    <span class="glyphicon glyphicon-tasks"></span> <spring:message code="label.main.unit.free"/>
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
                  <a href="#" title="<spring:message code="label.incident.add"/>" onclick="return Coceso.UI.openIncident();">
                    <span class="glyphicon glyphicon-plus"></span> <spring:message code="label.incident.add"/></a>
                </li>
                <li class="divider"></li>
                <li>
                  <a href="#" title="<spring:message code="label.main.incident.overview"/>"
                     onclick="return Coceso.UI.openIncidents({filter: ['overview'], title: this.title});">
                    <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.overview"/></a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.incident.active"/>"
                     onclick="return Coceso.UI.openIncidents({filter: ['overview', 'active'], title: this.title});">
                    <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.active"/></a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.incident.new"/>"
                     onclick="return Coceso.UI.openIncidents({filter: ['overview', 'new'], title: this.title});">
                    <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.new"/></a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.incident.open"/>"
                     onclick="return Coceso.UI.openIncidents({filter: ['overview', 'open'], title: this.title});">
                    <span class="glyphicon glyphicon-list-alt"></span> <spring:message code="label.main.incident.open"/></a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.incident.complete"/>"
                     onclick="return Coceso.UI.openIncidents({filter: ['overview', 'completed'], title: this.title});">
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
                  <a href="#" title="<spring:message code="label.log.custom"/>"
                     onclick="return Coceso.UI.openLogs({url: 'log/getCustom.json', autoload: true, title: this.title});">
                    <span class="glyphicon glyphicon-info-sign"></span> <spring:message code="label.log.custom"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.log.add"/>" onclick="return Coceso.UI.openLogAdd();">
                    <span class="glyphicon glyphicon-book"></span> <spring:message code="label.log.add"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.map"/>" onclick="return Coceso.UI.openMap();">
                    <span class="glyphicon glyphicon-globe"></span> <spring:message code="label.map"/>
                  </a>
                </li>
                <%-- External (of Main Program) Links --%>
                <li class="divider"></li>
                <li>
                  <a href="<c:url value="/edit/"/>" target="_blank">
                    <span class="glyphicon glyphicon-link"></span> <spring:message code="label.nav.edit_concern"/>
                  </a>
                </li>
                <li>
                  <a href="<c:url value="/dashboard"/>" target="_blank">
                    <span class="glyphicon glyphicon-link"></span> <spring:message code="label.nav.dashboard"/>
                  </a>
                </li>
                <li>
                  <a href="<c:url value="/search/patient/"/>${concern.id}" target="_blank">
                    <span class="glyphicon glyphicon-link"></span> <spring:message code="label.patient.search"/>
                  </a>
                </li>
                <%-- Low Priority Links --%>
                <li class="divider"></li>
                <li>
                  <a href="#" title="<spring:message code="label.main.key"/>"
                     onclick="return Coceso.UI.openStatic(this.title, 'key.html');">
                    <span class="glyphicon glyphicon-question-sign"></span> <spring:message code="label.main.key"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.log"/>" onclick="return Coceso.UI.openLogs({title: this.title});">
                    <span class="glyphicon glyphicon-info-sign"></span> <spring:message code="label.log"/>
                  </a>
                </li>
                <li>
                  <a href="#" title="<spring:message code="label.main.license"/>"
                     onclick="return Coceso.UI.openExternalStatic(this.title, '<c:url value="/static/license.html"/>');">
                    <span class="glyphicon glyphicon-copyright-mark"></span> <spring:message code="label.main.license"/>
                  </a>
                </li>
              </ul>
            </li>
            <li><a href="<c:url value="/home"/>"><spring:message code="label.exit"/></a></li>
          </ul>

          <div class="navbar-hide-on-1000px">
            <ul class="nav navbar-nav navbar-center">
              <%-- Quicklinks --%>
              <li>
                <a href="#" class="quicklink quicklink_incident" title="<spring:message code="label.incident.add"/>"
                   onclick="return Coceso.UI.openIncident();">
                  <span class="glyphicon glyphicon-plus" title="Jetzt ist schon wieder was passiert."></span>
                </a>
              </li>
              <li>
                <a href="#" class="quicklink quicklink_log" title="<spring:message code="label.log.add"/>"
                   onclick="return Coceso.UI.openLogAdd();">
                  <span class="glyphicon glyphicon-book"></span>
                </a>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </header>

    <div id="dialog_container">
    </div>

    <footer>
      <ul id="taskbar"></ul>
    </footer>

    <%@include file="templates/main.jsp"%>
  </body>
</html>
