<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML person edit interface
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
    <title><spring:message code="label.coceso"/> - <spring:message code="label.person.mgmt"/></title>
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
        ko.applyBindings(new Coceso.ViewModels.Person());
      });
    </script>
  </head>
  <body class="scroll">
    <div class="container">
      <c:set value="active" var="nav_person"/>
      <%@include file="parts/navbar.jsp"%>

      <div class="clearfix">
        <div class="form-group col-md-3">
          <input type="text" class="form-control" placeholder="<spring:message code="label.filter"/>" data-bind="value: filter, valueUpdate: 'input'"/>
        </div>
        <div class="form-group col-md-3 col-md-offset-1">
          <button type="button" class="btn btn-success" data-bind="click: create"><spring:message code="label.person.create"/></button>
        </div>
      </div>
      <div class="table-responsive">
        <table class="table table-striped table-condensed">
          <thead>
            <tr>
              <th>
                <a href="#" data-bind="click: function() {filtered.sort('sortdnr');}"><span class="glyphicon" data-bind="css: filtered.icon('sortdnr')"></span></a>
                  <spring:message code="label.person.dnr"/>
              </th>
              <th>
                <a href="#" data-bind="click: function() {filtered.sort('fullname');}"><span class="glyphicon" data-bind="css: filtered.icon('fullname')"></span></a>
                  <spring:message code="label.person.name"/>
              </th>
              <c:if test="${not empty authorized}">
                <th>
                  <a href="#" data-bind="click: function() {filtered.sort('sortusername');}"><span class="glyphicon" data-bind="css: filtered.icon('sortusername')"></span></a>
                    <spring:message code="label.username"/>
                </th>
                <th>
                  <a href="#" data-bind="click: function() {filtered.sort('sortallowlogin');}"><span class="glyphicon" data-bind="css: filtered.icon('sortallowlogin')"></span></a>
                    <spring:message code="label.operator.allowlogin"/>
                </th>
                <th>
                  <spring:message code="label.operator.roles"/>
                </th>
              </c:if>
              <th></th>
            </tr>
          </thead>
          <tbody data-bind="foreach: filtered">
            <tr>
              <td data-bind="text: dnr.orig"></td>
              <td data-bind="text: fullname"></td>
              <c:if test="${not empty authorized}">
                <td data-bind="text: username.orig"></td>
                <td data-bind="if: isOperator">
                  <span class="glyphicon" data-bind="css: allowlogin.orig() ? 'glyphicon-ok-circle green' : 'glyphicon-ban-circle red'"></span>
                </td>
                <td data-bind="text: authorities.orig().join(', ')"></td>
              </c:if>
              <td>
                <button type="button" class="btn btn-primary btn-sm" data-bind="click: edit"><spring:message code="label.edit"/></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="page-header"></div>
    </div>

    <div id="edit_person" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" data-bind="if: edit" data-backdrop="static">
      <div class="modal-dialog modal-lg">
        <div class="modal-content" data-bind="with: edit">
          <form data-bind="submit: save">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
              <h4 class="modal-title">
                <!-- ko if: id -->
                <spring:message code="label.person.edit"/>: <strong data-bind="text: fullname"></strong>
                <!-- /ko -->
                <!-- ko ifnot: id -->
                <spring:message code="label.person.create"/>
                <!-- /ko -->
              </h4>
            </div>
            <div class="modal-body">
              <div class="alert alert-danger" data-bind="visible: $root.error">
                <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: $root.errorText"></span>
              </div>
              <div class="row">
                <div class="form-group col-md-4" data-bind="css: surname.formcss">
                  <label for="edit_surname"><spring:message code="label.person.sur_name"/></label>
                  <input type="text" id="edit_surname" class="form-control" data-bind="value: surname, valueUpdate: 'input'"/>
                </div>
                <div class="form-group col-md-4" data-bind="css: givenname.formcss">
                  <label for="edit_givenname"><spring:message code="label.person.given_name"/></label>
                  <input type="text" id="edit_givenname" class="form-control" data-bind="value: givenname, valueUpdate: 'input'"/>
                </div>
                <div class="form-group col-md-4" data-bind="css: dnr.formcss">
                  <label for="edit_dnr"><spring:message code="label.person.dnr"/></label>
                  <input type="text" id="edit_dnr" class="form-control" data-bind="value: dnr, valueUpdate: 'input'"/>
                </div>
              </div>
              <div class="row">
                <div class="form-group col-md-8" data-bind="css: contact.formcss">
                  <label for="edit_contact"><spring:message code="label.person.contact"/></label>
                  <textarea id="edit_contact" rows="3" class="form-control" data-bind="value: contact, valueUpdate: 'input'"></textarea>
                </div>
              </div>
              <c:if test="${not empty authorized}">
                <div class="page-header">
                  <h5>
                    <!-- ko if: isOperator -->
                    <spring:message code="label.operator.edit"/>
                    <!-- /ko -->
                    <!-- ko ifnot: isOperator -->
                    <spring:message code="label.operator.make"/>
                    <!-- /ko -->
                  </h5>
                </div>
                <div class="row">
                  <div class="form-group col-md-4" data-bind="css: username.formcss">
                    <label for="edit_username"><spring:message code="label.operator.username"/></label>
                    <input type="text" id="edit_username" class="form-control" data-bind="value: username, valueUpdate: 'input'"/>
                  </div>
                  <div class="form-group col-md-3 col-md-offset-1" data-bind="css: allowlogin.formcss">
                    <label for="edit_allowlogin"><spring:message code="label.operator.allowlogin"/></label><br/>
                    <button type="button" id="edit_allowlogin" class="btn"
                            data-bind="click: allowlogin.toggle, css: (allowlogin() ? 'btn-success active' : 'btn-default')">
                      <span class="glyphicon" data-bind="css: allowlogin() ? 'glyphicon-ok-circle' : 'glyphicon-ban-circle'"></span>
                    </button>
                  </div>
                  <div class="form-group col-md-4" data-bind="css: {'has-change': authorities.changed}">
                    <select id="edit_auth" multiple class="form-control" data-bind="selectedOptions: authorities">
                      <c:forEach items="${authorities}" var="authority">
                        <option>${authority}</option>
                      </c:forEach>
                    </select>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-md-4" data-bind="css: password.formcss">
                    <label for="edit_password"><spring:message code="label.operator.username"/></label>
                    <input type="password" id="edit_password" class="form-control" data-bind="value: password, valueUpdate: 'input'"/>
                  </div>
                  <div class="form-group col-md-4" data-bind="css: password2.formcss">
                    <label for="edit_password2"><spring:message code="label.operator.username"/></label>
                    <input type="password" id="edit_password2" class="form-control" data-bind="value: password2, valueUpdate: 'input'"/>
                  </div>
                </div>
              </c:if>
            </div>
            <div class="modal-footer">
              <div class="pull-right">
                <button type="submit" class="btn btn-success" data-bind="enable: form.enable"><spring:message code="label.save"/></button>
                <button type="button" class="btn btn-warning" data-bind="enable: form.changed, click: form.reset"><spring:message code="label.reset"/></button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </body>
</html>
