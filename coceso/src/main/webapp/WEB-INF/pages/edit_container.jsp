<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML unit container edit interface
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
    <title><spring:message code="label.coceso"/> - <spring:message code="label.concern.edit"/>: <c:out value="${concern.name}"/></title>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon"/>
    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css"/>

    <%-- jQuery --%>
    <script src="<c:url value="/static/js/assets/jquery.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/jquery.i18n.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/jquery.ui.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/jquery.ui.touch-punch.min.js"/>" type="text/javascript"></script>
    <%-- Knockout --%>
    <script src="<c:url value="/static/js/assets/knockout.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/knockout.sortable.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout.extensions.js"/>" type="text/javascript"></script>
    <%-- Bootstrap --%>
    <script src="<c:url value="/static/js/assets/bootstrap.min.js"/>" type="text/javascript"></script>
    <%-- Client JS --%>
    <script src="<c:url value="/static/js/coceso.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/edit.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
      $(document).ready(function() {
        Coceso.Conf.jsonBase = "<c:url value="/data/"/>";
        Coceso.Conf.langBase = "<c:url value="/static/i18n/"/>";
        Coceso.Conf.language = "<spring:message code="this.languageCode"/>";
        Coceso.initi18n();
        Coceso.Lock.lock();
        ko.applyBindings(new Coceso.ViewModels.Container());
      });
    </script>
  </head>
  <body>
    <div class="container">
      <c:set value="active" var="nav_concern"/>
      <%@include file="parts/navbar.jsp"%>

      <h2><spring:message code="label.nav.edit_concern"/>: <c:out value="${concern.name}"/></h2>

      <div class="page-header">
        <h3 class="clearfix">
          <spring:message code="label.container.edit"/>
          <a href="<c:url value="/edit/"/>" class="btn btn-warning btn-sm pull-right"><spring:message code="label.nav.back"/></a>
        </h3>
      </div>

      <div>
        <div class="unit-container" data-bind="template: {name: 'template-container', data: top}"></div>
        <div id="spare">
          <spring:message code="label.unit.spare"/>:
          <ul class="unit_list unit_list_edit" data-bind="sortable: {data: spare, connectClass: 'unit_list_edit', afterMove: $root.dropUnit, options: {placeholder: 'unit-placeholder ui-corner-all'}}">
            <li>
              <a href="#" class="unit_state">
                <span class="ui-corner-all" data-bind="text: call"></span>
              </a>
            </li>
          </ul>
        </div>
      </div>
      <div class="page-header"></div>
    </div>

    <script type="text/html" id="template-container">
      <div class="panel panel-default">
        <div class="panel-heading clearfix">
          <span data-bind="text: name() || '---', click: selected.set, visible: !selected()"></span>
          <form data-bind="submit: selected.unset" style="display: inline;"><input type="text" data-bind="value: name, event: {blur: update}, visibleAndSelect: selected"/></form>

          <div class="pull-right">
            <button type="button" class="btn btn-danger btn-xs" data-bind="click: remove"><span class="glyphicon glyphicon-remove-sign"></span></button>
            <button type="button" class="btn btn-success btn-xs" data-bind="click: add"><span class="glyphicon glyphicon-plus-sign"></span></button>
          </div>
        </div>

        <div class="panel-body">
          <ul class="unit_list unit_list_edit" data-bind="sortable: {data: units, connectClass: 'unit_list_edit', afterMove: $root.dropUnit, options: {placeholder: 'unit-placeholder ui-corner-all'}}">
            <li>
              <a href="#" class="unit_state">
                <span class="ui-corner-all" data-bind="text: call"></span>
              </a>
            </li>
          </ul>

          <div class="unit_container_edit" data-bind="sortable: {template: 'template-container', data: subContainer, connectClass: 'unit_container_edit', afterMove: $root.drop, options: {placeholder: 'container-placeholder'}}"></div>
        </div>
      </div>
    </script>
  </body>
</html>
