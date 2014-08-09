<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
  <head>
    <title><spring:message code="label.coceso"/> - <spring:message code="label.concern.edit"/>: ${caze.name}</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">
    <link href="<c:url value="/static/css/coceso.css" />" rel="stylesheet">

    <%-- jQuery --%>
    <script src="<c:url value="/static/js/assets/jquery.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/jquery.i18n.min.js"/>" type="text/javascript"></script>
    <%-- Knockout --%>
    <script src="<c:url value="/static/js/assets/knockout.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/knockout.extensions.js"/>" type="text/javascript"></script>
    <%-- Bootstrap --%>
    <script src="<c:url value="/static/js/assets/bootstrap.min.js"/>" type="text/javascript"></script>
    <%-- Client JS --%>
    <script src="<c:url value="/static/js/concern.edit.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
          $(document).ready(function() {
      CocesoEdit.Conf.jsonBase = "${pageContext.request.contextPath}/data/";
          CocesoEdit.Conf.langBase = "${pageContext.request.contextPath}/static/i18n/";
          CocesoEdit.Conf.language = "<spring:message code="this.languageCode" />";
          ko.applyBindings(new CocesoEdit.ViewModels.Concern());
          $(".tooltipped").tooltip();
      });</script>

    <style type="text/css">
      .tooltip {
        white-space: normal;
      }
      th {
        white-space: nowrap;
      }
    </style>
  </head>

  <body>
    <spring:message var="transportVehicle" code="label.unit.vehicle" />
    <spring:message var="withDoc" code="label.unit.withdoc" />
    <spring:message var="call" code="label.unit.call"/>
    <spring:message var="ani" code="label.unit.ani" />
    <spring:message var="portable" code="label.unit.portable" />
    <spring:message var="info" code="label.unit.info" />
    <spring:message var="home" code="label.unit.home" />

    <!-- #################### Start of Page ############## -->
    <div class="container">
      <c:set value="active" var="nav_concern" />
      <%@include file="parts/navbar.jsp"%>

      <!--##### TABLE -->
      <div class="page-header">
        <h3>
          <spring:message code="label.concern.edit.unit"/>
          <a href="<c:url value="/edit/container"/>" class="btn btn-default pull-right"><spring:message code="label.container.edit" /></a>
        </h3>
      </div>
      <div>
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
                ${withDoc}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.withdoc"/>"
                      data-toggle="tooltip" data-placement="top"></span>
                /
                ${transportVehicle}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.vehicle"/>"
                      data-toggle="tooltip" data-placement="top"></span>
                /
                ${portable}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.portable"/>"
                      data-toggle="tooltip" data-placement="top"></span>
              </th>
              <th>${info}</th>
              <th>
                ${home}
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.home"/>"
                      data-toggle="tooltip" data-placement="top"></span>
              </th>
              <th class="text-center">
                <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.remove_locked"/>"
                      data-toggle="tooltip" data-placement="top"></span>
              </th>
            </tr>
          </thead>
          <tbody>
            <!-- ko foreach: units -->
            <tr>
              <!-- ko template: 'template-unit-row' --><!-- /ko -->
              <td>
                <p><button type="submit" class="btn btn-success" data-bind="enable: localChange, click: save"><spring:message code="label.update"/></button></p>
                <p><button type="button" class="btn btn-danger" data-bind="visible: !locked, click: $root.remove"><spring:message code="label.remove"/></button></p>
            </tr>
            <!-- /ko -->

            <tr data-bind="with: newUnit">
              <!-- ko template: 'template-unit-row' --><!-- /ko -->
              <td>
                <input type="submit" value="<spring:message code="label.create"/>" class="btn btn-success" data-bind="click: function() { $root.create() }, enable: call.localChange">
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <script type="text/html" id="template-unit-row">
        <td>
          <p><input type="text" maxlength="64" class="form-control" placeholder="${call}" data-bind="value: call, valueUpdate: 'input', css: {'form-changed': call.localChange}" required></p>
          <p><input type="text" maxlength="64" class="form-control" placeholder="${ani}" data-bind="value: ani, valueUpdate: 'input', css: {'form-changed': ani.localChange}"></p>
        </td>
        <td>
          <div class="btn-group">
            <button type="button" class="btn btn-default" data-bind="click: doc.toggle, css: {active: doc, 'form-changed': doc.localChange}">${withDoc}</button>
            <button type="button" class="btn btn-default" data-bind="click: vehicle.toggle, css: {active: vehicle, 'form-changed': vehicle.localChange}">${transportVehicle}</button>
            <button type="button" class="btn btn-default" data-bind="click: portable.toggle, css: {active: portable, 'form-changed': portable.localChange}">${portable}</button>
          </div>
        </td>
        <td>
          <textarea rows="3" class="form-control" placeholder="${info}" data-bind="value: info, valueUpdate: 'input', css: {'form-changed': info.localChange}"></textarea>
        </td>
        <td>
          <textarea rows="3" class="form-control" placeholder="${home}" data-bind="value: home, valueUpdate: 'input', css: {'form-changed': home.localChange}"></textarea>
        </td>
      </script>

      <div class="page-header">
        <h3><spring:message code="label.concern.edit"/></h3>
      </div>

      <form data-bind="with: concern, submit: concern.save">
        <div class="alert alert-danger alert-dismissable" data-bind="visible: saveError">
          <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
          <strong><spring:message code="label.error" />:</strong> <spring:message code="label.error" />
        </div>

        <div class="clearfix">
          <div class="col-md-3">
            <div class="form-group">
              <label for="case_name"><spring:message code="label.concern.name"/></label>
              <input type="text" id="case_name" maxlength="64" class="form-control" required
                     data-bind="value: name, valueUpdate: 'input', css: {'form-changed': name.localChange}">
            </div>
            <div class="form-group">
              <div><label for="case_pax"><spring:message code="label.concern.pax"/></label></div>
              <input type="number" id="case_pax" class="form-control" min="0" max="1000000" step="1000" novalidate
                     data-bind="value: pax, valueUpdate: 'input', css: {'form-changed': pax.localChange}">
            </div>
          </div>

          <div class="form-group col-md-5 col-md-offset-1">
            <label for="case_organiser"><spring:message code="label.concern.info"/></label>
            <textarea id="case_organiser" class="form-control" rows="5"
                      data-bind="value: info, valueUpdate: 'input', css: {'form-changed': info.localChange}"></textarea>
          </div>
        </div>

        <div class="form-group">
          <button type="submit" class="btn btn-success" data-bind="enable: localChange"><spring:message code="label.update"/></button>
        </div>
      </form>

      <div class="page-header">
        <h3><spring:message code="label.concern.edit.batch"/></h3>
      </div>
      <form data-bind="with: batch, submit: batch.save">
        <div class="clearfix">
          <div class="col-md-3">
            <div class="form-group">
              <label for="batch_call">${call} <spring:message code="label.prefix"/></label>
              <input type="text" id="batch_call" maxlength="50" class="form-control" data-bind="value: call, valueUpdate: 'input'"
                     placeholder="${call} <spring:message code="label.prefix"/>" />
            </div>
            <div class="form-group">
              <label for="batch_from"><spring:message code="label.range"/></label>
              <div class="form-group">
                <div class="sr-only">
                  <label for="batch_from"><spring:message code="label.from"/></label> /
                  <label for="batch_to"><spring:message code="label.to"/></label>
                </div>
                <input type="number" min="1" max="999" id="batch_from" class="form-control"
                       placeholder="<spring:message code="label.from"/>" data-bind="value: from, valueUpdate: 'input'" /> -
                <input type="number" min="1" max="999" id="batch_to" class="form-control"
                       placeholder="<spring:message code="label.to"/>" data-bind="value: to, valueUpdate: 'input'" />
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
          <div class="col-md-2 col-md-offset-1">
            <div class="form-group">
              <div>
                <label for="batch_doc">
                  ${withDoc}
                  <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.withdoc"/>"
                        data-toggle="tooltip" data-placement="top"></span>
                </label>
                /
                <label for="batch_doc">
                  ${transportVehicle}
                  <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.vehicle"/>"
                        data-toggle="tooltip" data-placement="top"></span>
                </label>
                /
                <label for="batch_doc">
                  ${portable}
                  <span class="glyphicon glyphicon-question-sign tooltipped" title="<spring:message code="text.unit.portable"/>"
                        data-toggle="tooltip" data-placement="top"></span>
                </label>
              </div>
              <div class="btn-group">
                <button type="button" class="btn btn-default" data-bind="click: doc.toggle, css: {active: doc}">${withDoc}</button>
                <button type="button" class="btn btn-default" data-bind="click: vehicle.toggle, css: {active: vehicle}">${transportVehicle}</button>
                <button type="button" class="btn btn-default" data-bind="click: portable.toggle, css: {active: portable}">${portable}</button>
              </div>
            </div>
          </div>
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-success" data-bind="enable: enable"><spring:message code="label.create"/></button>
        </div>
      </form>
    </div>
  </body>
</html>
