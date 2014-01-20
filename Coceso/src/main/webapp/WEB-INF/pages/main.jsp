<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
* @copyright     Copyright (c) 2013 Daniel Rohr
* @link          https://sourceforge.net/projects/coceso/
* @package       coceso.client.html
* @since         Rev. 1
* @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
*
* Dependencies:
*	coceso.client.css
*  coceso.client.js
*  bootstrap.dropdown.js
*/
-->
<html>
  <head>
    <title><spring:message code="label.coceso" /></title>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css" />" type="text/css" />

    <script src="<c:url value="/static/js/jquery.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.ui.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/jquery.ui.winman.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/util.js" />"></script>
    <script src="<c:url value="/static/js/knockout.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout.mapping.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/bootstrap.dropdown.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/bindings.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/coceso.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
      $(document).ready(function() {
        Coceso.Conf.jsonBase = "${pageContext.request.contextPath}/data/";
        Coceso.Conf.contentBase = "${pageContext.request.contextPath}/main/";

        Coceso.startup();

        setInterval('updateClock()', 1000);
      });
    </script>
  </head>
  <body>
    <header>
      <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="navbar-header">
          <a class="navbar-brand"><spring:message code="label.coceso" /></a>
        </div>

        <ul class="nav navbar-nav">
          <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.units" /> <b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li><a href="#" title="<spring:message code='label.units' />" onclick="return Coceso.UI.openUnits(this.title, 'unit.html');"><spring:message code="label.main.unit.overview" /></a></li>
              <li><a href="#" title="<spring:message code='label.main.unit.assigned' />" onclick="return Coceso.UI.openUnits(this.title, 'unit.html', {filter: ['radio']});"><spring:message code="label.main.unit.assigned" /></a></li>
            </ul>
          </li>
          <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.incidents" /> <b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li><a href="#" title="<spring:message code='label.incident' /> / <spring:message code='label.add' />" onclick="return Coceso.UI.openIncident(this.title, 'incident_form.html');"><spring:message code="label.add" /></a></li>
              <li><a href="#" title="<spring:message code='label.main.incident.active' />" onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'active']});"><spring:message code="label.main.incident.active" /></a></li>
              <li><a href="#" title="<spring:message code='label.main.incident.new' />" onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'new'], showTabs: false});"><spring:message code="label.main.incident.new" /></a></li>
              <li><a href="#" title="<spring:message code='label.main.incident.open' />" onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'open'], showTabs: false});"><spring:message code="label.main.incident.open" /></a></li>
              <li><a href="#" title="<spring:message code='label.main.incident.complete' />" onclick="return Coceso.UI.openIncidents(this.title, 'incident.html', {filter: ['overview', 'completed']});"><spring:message code="label.main.incident.complete" /></a></li>
            </ul>
          </li>
          <li><a href="#" title="<spring:message code='label.log' />" onclick="return Coceso.UI.openLogs(this.title, 'log.html');"><spring:message code="label.log" /></a></li>
          <li><a href="<c:url value="/edit/"/>" target="_blank"><spring:message code="label.nav.edit_concern" /></a></li>
          <li><a href="<c:url value="/dashboard"/>" target="_blank"><spring:message code="label.nav.dashboard" /></a></li>
          <li><a href="#" title="Debug" onclick="return Coceso.UI.openDebug(this.title, 'debug.html');">Debug</a></li>
        </ul>
          <ul class="nav navbar-nav navbar-right">
               <li><a id="clock" class="navbar-brand"></a></li>
          </ul>
      </nav>
    </header>

    <noscript>
    <div class="alert alert-danger"><strong>JavaScript required</strong><br/>Enable JavaScript to use this page.</div>
    </noscript>

    <footer>
      <ul id="taskbar"></ul>
    </footer>
  </body>
</html>
