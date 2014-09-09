<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML concern edit interface
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
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
    <%-- Knockout --%>
    <script src="<c:url value="/static/js/assets/knockout.min.js"/>" type="text/javascript"></script>
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
        ko.applyBindings(new Coceso.ViewModels.Concern());

        $(".tooltipped").tooltip();
      });
    </script>
  </head>
  <body>
    <spring:message var="call" code="label.unit.call"/>
    <spring:message var="ani" code="label.unit.ani"/>
    <spring:message var="doc" code="label.unit.withdoc"/>
    <spring:message var="vehicle" code="label.unit.vehicle"/>
    <spring:message var="portable" code="label.unit.portable"/>
    <spring:message var="info" code="label.unit.info"/>
    <spring:message var="home" code="label.unit.home"/>

    <div class="container">
      <c:set value="active" var="nav_concern"/>
      <%@include file="parts/navbar.jsp"%>

      <h2><spring:message code="label.nav.edit_concern"/>: <span data-bind="text: concern.name.orig"></span></h2>

      <div class="page-header">
        <h3>
          <spring:message code="label.concern.edit.unit"/>
          <a href="<c:url value="/edit/container"/>" class="btn btn-default btn-sm pull-right"><spring:message code="label.container.edit"/></a>
        </h3>
      </div>
      <div class="alert alert-danger" data-bind="visible: error">
        <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: errorText"></span>
      </div>
      <div class="table-responsive">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>
                ${call} /
                ${ani}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.ani"/>"
                      data-toggle="tooltip" data-placement="top"></span>
              </th>
              <th>
                ${doc}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.withdoc"/>"
                      data-toggle="tooltip" data-placement="top"></span>
                /
                ${vehicle}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.vehicle"/>"
                      data-toggle="tooltip" data-placement="top"></span>
                /
                ${portable}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.portable"/>"
                      data-toggle="tooltip" data-placement="top"></span>
              </th>
              <th>
                ${info}
              </th>
              <th>
                ${home}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.home"/>"
                      data-toggle="tooltip" data-placement="top"></span>
              </th>
              <th>
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.remove_locked"/>"
                      data-toggle="tooltip" data-placement="top"></span>
              </th>
            </tr>
          </thead>
          <tbody>
            <!-- ko foreach: units -->
            <tr>
              <!-- ko template: 'template-unit-row' -->
              <td></td><td></td><td></td><td></td>
              <!-- /ko -->
              <td>
                <form data-bind="attr: {id: 'table_form_' + id}, submit: save">
                  <p><button type="submit" class="btn btn-success btn-sm" data-bind="enable: localChange"><spring:message code="label.update"/></button></p>
                  <p><button type="button" class="btn btn-danger btn-sm" data-bind="visible: !locked, click: $root.remove"><spring:message code="label.remove"/></button></p>
                </form>
              </td>
            </tr>
            <!-- /ko -->

            <tr data-bind="with: newUnit">
              <!-- ko template: 'template-unit-row' --><!-- /ko -->
              <td>
                <form data-bind="attr: {id: 'table_form_' + id}, submit: $root.create">
                  <button type="submit" value="" class="btn btn-success btn-sm" data-bind="enable: call.localChange"><spring:message code="label.create"/></button>
                </form>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="page-header">
        <h3><spring:message code="label.concern.edit"/></h3>
      </div>
      <!-- ko with: concern -->
      <div class="alert alert-danger" data-bind="visible: error">
        <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: errorText"></span>
      </div>
      <form data-bind="submit: save">
        <div class="clearfix">
          <div class="col-md-3">
            <div class="form-group">
              <label for="concern_name"><spring:message code="label.concern.name"/></label>
              <input type="text" id="concern_name" maxlength="64" class="form-control" required
                     data-bind="value: name, valueUpdate: 'input', css: {'form-changed': name.localChange}">
            </div>
            <div class="form-group">
              <div><label for="concern_pax"><spring:message code="label.concern.pax"/></label></div>
              <input type="number" id="concern_pax" class="form-control" min="0" max="1000000" step="1000"
                     data-bind="value: pax, valueUpdate: 'input', css: {'form-changed': pax.localChange}">
            </div>
          </div>

          <div class="form-group col-md-5 col-md-offset-1">
            <label for="concern_info"><spring:message code="label.concern.info"/></label>
            <textarea id="concern_info" class="form-control" rows="5"
                      data-bind="value: info, valueUpdate: 'input', css: {'form-changed': info.localChange}"></textarea>
          </div>
        </div>

        <div class="form-group">
          <button type="submit" class="btn btn-success" data-bind="enable: localChange"><spring:message code="label.update"/></button>
        </div>
      </form>
      <!-- /ko -->

      <div class="page-header">
        <h3><spring:message code="label.concern.edit.batch"/></h3>
      </div>
      <!-- ko with: batch -->
      <div class="alert alert-danger" data-bind="visible: error">
        <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: errorText"></span>
      </div>
      <form data-bind="submit: save">
        <div class="clearfix">
          <div class="col-md-3">
            <div class="form-group">
              <label for="batch_call">${call} <spring:message code="label.prefix"/></label>
              <input type="text" id="batch_call" maxlength="50" class="form-control" data-bind="value: call, valueUpdate: 'input'"
                     placeholder="${call} <spring:message code="label.prefix"/>"/>
            </div>
            <div class="form-group">
              <label for="batch_from"><spring:message code="label.range"/></label>
              <div class="form-group">
                <div class="sr-only">
                  <label for="batch_from"><spring:message code="label.from"/></label> /
                  <label for="batch_to"><spring:message code="label.to"/></label>
                </div>
                <input type="number" min="1" max="999" id="batch_from" class="form-control"
                       placeholder="<spring:message code="label.from"/>" data-bind="value: from, valueUpdate: 'input'"/> -
                <input type="number" min="1" max="999" id="batch_to" class="form-control"
                       placeholder="<spring:message code="label.to"/>" data-bind="value: to, valueUpdate: 'input'"/>
              </div>
            </div>
          </div>
          <div class="col-md-2 col-md-offset-1">
            <div class="form-group">
              <div>
                <label for="batch_doc">
                  ${doc}
                  <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.withdoc"/>"
                        data-toggle="tooltip" data-placement="top"></span>
                </label>
                /
                <label for="batch_vehicle">
                  ${vehicle}
                  <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.vehicle"/>"
                        data-toggle="tooltip" data-placement="top"></span>
                </label>
                /
                <label for="batch_portable">
                  ${portable}
                  <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.portable"/>"
                        data-toggle="tooltip" data-placement="top"></span>
                </label>
              </div>
              <div class="btn-group">
                <button type="button" id="batch_doc" class="btn btn-default" data-bind="click: doc.toggle, css: {active: doc}">${doc}</button>
                <button type="button" id="batch_vehicle" class="btn btn-default" data-bind="click: vehicle.toggle, css: {active: vehicle}">${vehicle}</button>
                <button type="button" id="batch_portable" class="btn btn-default" data-bind="click: portable.toggle, css: {active: portable}">${portable}</button>
              </div>
            </div>
          </div>
          <div class="col-md-4 col-md-offset-1">
            <div class="form-group">
              <label for="batch_home">${home}</label>
              <textarea rows="3" id="batch_home" class="form-control" placeholder="${home}"
                        data-bind="value: home, valueUpdate: 'input'"></textarea>
            </div>
          </div>
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-success" data-bind="enable: enable"><spring:message code="label.create"/></button>
        </div>
      </form>
      <!-- /ko -->
    </div>

    <%@include file="templates/edit.jsp"%>
  </body>
</html>
