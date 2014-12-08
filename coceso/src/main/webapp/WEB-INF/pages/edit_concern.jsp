<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>
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
    <t:head jquery="i18n, ui, ui.touch-punch" knockout="sortable" js="edit"/>
    <script type="text/javascript">
      $(document).ready(function() {
        Coceso.Conf.jsonBase = "<c:url value="/data/"/>";
        Coceso.Conf.langBase = "<c:url value="/static/i18n/"/>";
        Coceso.Conf.language = "<spring:message code="this.languageCode"/>";
        Coceso.initi18n();
        Coceso.Lock.lock();
        ko.applyBindings(new Coceso.ViewModels.Edit());

        $(".tooltipped").tooltip();
      });
    </script>
  </head>
  <body class="scroll">
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

      <ul class="nav nav-tabs" role="tablist">
        <li><a href="#concern" role="tab" data-toggle="tab"><spring:message code="label.concern.edit"/></a></li>
        <li class="active"><a href="#units" role="tab" data-toggle="tab"><spring:message code="label.concern.edit.unit"/></a></li>
        <li><a href="#hierarchy" role="tab" data-toggle="tab"><spring:message code="label.container.edit"/></a></li>
        <li><a href="#batch" role="tab" data-toggle="tab"><spring:message code="label.concern.edit.batch"/></a></li>
      </ul>

      <div class="tab-content">
        <div class="tab-pane active" id="units" data-bind="with: units">
          <div class="page-header sr-only">
            <h3><spring:message code="label.concern.edit.unit"/></h3>
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
                      <p><button type="submit" class="btn btn-success btn-sm" data-bind="enable: form.enable"><spring:message code="label.update"/></button></p>
                      <p><button type="button" class="btn btn-danger btn-sm" data-bind="visible: !locked, click: $parent.remove"><spring:message code="label.remove"/></button></p>
                    </form>
                  </td>
                </tr>
                <!-- /ko -->

                <tr data-bind="with: newUnit">
                  <!-- ko template: 'template-unit-row' -->
                  <td></td><td></td><td></td><td></td>
                  <!-- /ko -->
                  <td>
                    <form data-bind="attr: {id: 'table_form_' + id}, submit: $parent.create">
                      <button type="submit" class="btn btn-success btn-sm" data-bind="enable: call.changed"><spring:message code="label.create"/></button>
                    </form>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="tab-pane" id="hierarchy" data-bind="with: container">
          <div class="page-header sr-only">
            <h3><spring:message code="label.container.edit"/></h3>
          </div>
          <div>
            <div class="unit-container" data-bind="template: {name: 'template-container', data: top}"></div>
            <div id="spare">
              <spring:message code="label.unit.spare"/>:
              <ul class="unit_list unit_list_edit" data-bind="sortable: {data: spare, connectClass: 'unit_list_edit', afterMove: $root.container.dropUnit, options: {placeholder: 'unit-placeholder ui-corner-all'}}">
                <li>
                  <a href="#" class="unit_state">
                    <span class="ui-corner-all" data-bind="text: call"></span>
                  </a>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <div class="tab-pane" id="concern" data-bind="with: concern">
          <div class="page-header sr-only">
            <h3><spring:message code="label.concern.edit"/></h3>
          </div>
          <div class="alert alert-danger" data-bind="visible: error">
            <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: errorText"></span>
          </div>
          <form data-bind="submit: save">
            <div class="clearfix">
              <div class="col-md-3">
                <div class="form-group" data-bind="css: name.formcss">
                  <label for="concern_name"><spring:message code="label.concern.name"/></label>
                  <input type="text" id="concern_name" class="form-control" maxlength="64" required
                         data-bind="value: name, valueUpdate: 'input'">
                </div>
                <div class="form-group" data-bind="css: pax.formcss">
                  <div><label for="concern_pax"><spring:message code="label.concern.pax"/></label></div>
                  <input type="number" id="concern_pax" class="form-control" min="0" max="1000000" step="1000"
                         data-bind="value: pax, valueUpdate: 'input'">
                </div>
              </div>

              <div class="form-group col-md-5 col-md-offset-1" data-bind="css: info.formcss">
                <label for="concern_info"><spring:message code="label.concern.info"/></label>
                <textarea id="concern_info" class="form-control" rows="5"
                          data-bind="value: info, valueUpdate: 'input'"></textarea>
              </div>
            </div>

            <div class="form-group">
              <button type="submit" class="btn btn-success" data-bind="enable: form.enable"><spring:message code="label.update"/></button>
            </div>
          </form>
        </div>

        <div class="tab-pane" id="batch" data-bind="with: batch">
          <div class="page-header sr-only">
            <h3><spring:message code="label.concern.edit.batch"/></h3>
          </div>
          <div class="alert alert-danger" data-bind="visible: error">
            <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: errorText"></span>
          </div>
          <form data-bind="submit: save">
            <div class="clearfix">
              <div class="col-md-3">
                <div class="form-group">
                  <label for="batch_call">${call} <spring:message code="label.prefix"/></label>
                  <input type="text" id="batch_call" class="form-control"  maxlength="50"
                         placeholder="${call} <spring:message code="label.prefix"/>"
                         data-bind="value: call, valueUpdate: 'input'"/>
                </div>
                <div class="form-group">
                  <label for="batch_from"><spring:message code="label.range"/></label>
                  <div class="form-group">
                    <div class="sr-only">
                      <label for="batch_from"><spring:message code="label.from"/></label> /
                      <label for="batch_to"><spring:message code="label.to"/></label>
                    </div>
                    <input type="number" id="batch_from" class="form-control" min="1" max="999"
                           placeholder="<spring:message code="label.from"/>" data-bind="value: from, valueUpdate: 'input'"/> -
                    <input type="number" id="batch_to" class="form-control" min="1" max="999"
                           placeholder="<spring:message code="label.to"/>" data-bind="value: to, valueUpdate: 'input'"/>
                  </div>
                </div>
              </div>
              <div class="col-md-3 col-md-offset-1">
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
                    <button type="button" id="batch_doc" class="btn btn-default" data-bind="click: doc.toggle, css: doc.state">${doc}</button>
                    <button type="button" id="batch_vehicle" class="btn btn-default" data-bind="click: vehicle.toggle, css: vehicle.state">${vehicle}</button>
                    <button type="button" id="batch_portable" class="btn btn-default" data-bind="click: portable.toggle, css: portable.state">${portable}</button>
                  </div>
                </div>
              </div>
              <div class="col-md-4 col-md-offset-1">
                <div class="form-group">
                  <label for="batch_home">${home}</label>
                  <textarea id="batch_home" class="form-control" rows="3" placeholder="${home}"
                            data-bind="value: home, valueUpdate: 'input'"></textarea>
                </div>
              </div>
            </div>
            <div class="form-group">
              <button type="submit" class="btn btn-success" data-bind="enable: enable"><spring:message code="label.create"/></button>
            </div>
          </form>
        </div>
      </div>

      <div class="page-header"></div>
    </div>

    <!-- ko with: units -->
    <div id="edit_crew" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" data-bind="if: edit" data-backdrop="static">
      <div class="modal-dialog modal-lg">
        <div class="modal-content" data-bind="with: edit">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">
              <spring:message code="label.crew.edit"/>: <strong data-bind="text: call"></strong>
            </h4>
          </div>
          <div class="modal-body clearfix">
            <div class="col-md-6">
              <h5><spring:message code="label.crew.assigned"/></h5>
              <div class="table-responsive">
                <table class="table table-striped table-condensed">
                  <tbody data-bind="foreach: crew">
                    <tr>
                      <td>
                        <a href="#" title="<spring:message code="label.crew.remove"/>" data-bind="click: $parent.removePerson">
                          <span class="glyphicon glyphicon-remove"></span>
                        </a>
                      </td>
                      <td data-bind="text: dnr"></td>
                      <td data-bind="text: fullname"></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div class="col-md-6">
              <div class="clearfix">
                <h5 class="col-md-6"><spring:message code="label.crew.available"/></h5>
                <div class="form-group col-md-6">
                  <input type="text" class="form-control" placeholder="<spring:message code="label.filter"/>" data-bind="value: $parent.filter, valueUpdate: 'input'"/>
                </div>
              </div>
              <div class="table-responsive">
                <table class="table table-striped table-condensed">
                  <tbody data-bind="foreach: $parent.filtered">
                    <tr>
                      <td>
                        <a href="#" title="<spring:message code="label.crew.assign"/>" data-bind="click: $parent.assignPerson">
                          <span class="glyphicon glyphicon-plus"></span>
                        </a>
                      </td>
                      <td data-bind="text: dnr"></td>
                      <td data-bind="text: fullname"></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- /ko -->

    <%@include file="templates/edit.jsp"%>
  </body>
</html>
