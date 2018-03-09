<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Client HTML user edit interface
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2015-2018 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<html>
  <head>
    <script type="text/javascript">
      var CocesoConf = {
        jsonBase: "<c:url value="/data/"/>",
        langBase: "<c:url value="/static/i18n/"/>",
        language: "<spring:message code="this.languageCode"/>",
        plugins: ${cocesoConfig.jsPlugins}
      };
    </script>
    <t:head title="users.mgmt" entry="edit_user"/>
  </head>
  <body class="scroll">
    <div class="container">
      <c:set value="active" var="nav_users"/>
      <%@include file="parts/navbar.jsp"%>

      <div class="clearfix">
        <div class="form-group col-md-3">
          <input type="text" class="form-control" placeholder="<spring:message code="filter"/>" data-bind="value: users.filter, valueUpdate: 'input'"/>
        </div>
        <div class="form-group col-md-3 col-md-offset-1">
          <button type="button" class="btn btn-success" data-bind="click: create"><spring:message code="user.add"/></button>
        </div>
      </div>
      <form data-bind="submit: upload">
        <label for="user_csv"><spring:message code="user.csv"/></label>
        <div class="alert alert-danger" data-bind="visible: $root.error">
          <strong><spring:message code="error"/>:</strong> <span data-bind="text: $root.errorText"></span>
        </div>
        <div class="clearfix">
          <div class="form-group col-md-5">
            <input id="user_csv" type="file" class="form-control" data-bind="file: csv"/>
          </div>
          <div class="form-group col-md-2">
            <button type="submit" class="btn btn-success" data-bind="enable: csv"><spring:message code="user.import"/></button>
          </div>
        </div>
      </form>
      <nav>
        <ul class="pager">
          <li data-bind="css: {disabled: users.isFirst}">
            <a href="#" data-bind="click: users.prev"><span aria-hidden="true">&larr;</span> Previous</a>
          </li>
          <li><span>
              <span data-bind="text: users.page"></span>/<span data-bind="text: users.total"></span>
            </span></li>
          <li data-bind="css: {disabled: users.isLast}">
            <a href="#" data-bind="click: users.next">Next <span aria-hidden="true">&rarr;</span></a>
          </li>
        </ul>
      </nav>
      <div class="table-responsive">
        <table class="table table-striped table-condensed">
          <thead>
            <tr>
              <th>
                <a href="#" data-bind="click: users.getSort('personnelId')"><span class="glyphicon" data-bind="css: users.icon('personnelId')"></span></a>
                  <spring:message code="user.personnelId"/>
              </th>
              <th>
                <a href="#" data-bind="click: users.getSort('firstname,lastname')"><span class="glyphicon" data-bind="css: users.icon('firstname,lastname')"></span></a>
                  <spring:message code="user.name"/>
              </th>
              <sec:authorize access="@auth.hasAccessLevel('Root')">
                <th>
                  <a href="#" data-bind="click: users.getSort('username')"><span class="glyphicon" data-bind="css: users.icon('username')"></span></a>
                    <spring:message code="user.username"/>
                </th>
                <th>
                  <a href="#" data-bind="click: users.getSort('allowLogin')"><span class="glyphicon" data-bind="css: users.icon('allowLogin')"></span></a>
                    <spring:message code="user.allowlogin"/>
                </th>
                <th>
                  <spring:message code="user.roles"/>
                </th>
              </sec:authorize>
              <th></th>
            </tr>
          </thead>
          <tbody data-bind="foreach: users">
            <tr>
              <td data-bind="text: personnelId"></td>
              <td data-bind="text: fullname"></td>
              <sec:authorize access="@auth.hasAccessLevel('Root')">
                <td data-bind="text: username"></td>
                <td data-bind="if: username">
                  <span class="glyphicon" data-bind="css: allowlogin() ? 'glyphicon-ok-circle green' : 'glyphicon-ban-circle red'"></span>
                </td>
                <td data-bind="text: authorities().join(', ')"></td>
              </sec:authorize>
              <td>
                <button type="button" class="btn btn-primary btn-sm" data-bind="click: edit"><spring:message code="edit"/></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="page-header"></div>
    </div>

    <div id="edit_user" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">
      <div class="modal-dialog modal-lg">
        <div class="modal-content" data-bind="with: edit">
          <form data-bind="submit: save">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
              <h4 class="modal-title">
                <!-- ko if: idObs -->
                <spring:message code="user.edit"/>: <strong data-bind="text: fullname"></strong>
                <!-- /ko -->
                <!-- ko ifnot: idObs -->
                <spring:message code="user.add"/>
                <!-- /ko -->
              </h4>
            </div>
            <div class="modal-body">
              <div class="alert alert-danger" data-bind="visible: error">
                <strong><spring:message code="error"/>:</strong> <span data-bind="text: errorText"></span>
              </div>
              <div class="row">
                <div class="form-group col-md-4" data-bind="css: lastname.formcss">
                  <label for="edit_lastname"><spring:message code="user.lastname"/></label>
                  <input type="text" id="edit_lastname" class="form-control" data-bind="value: lastname, valueUpdate: 'input'"/>
                </div>
                <div class="form-group col-md-4" data-bind="css: firstname.formcss">
                  <label for="edit_firstname"><spring:message code="user.firstname"/></label>
                  <input type="text" id="edit_firstname" class="form-control" data-bind="value: firstname, valueUpdate: 'input'"/>
                </div>
                <div class="form-group col-md-4" data-bind="css: personnelId.formcss">
                  <label for="edit_personnelid"><spring:message code="user.personnelId"/></label>
                  <input type="text" id="edit_personnelid" class="form-control" data-bind="value: personnelId, valueUpdate: 'input'"/>
                </div>
              </div>
              <div class="row">
                <div class="form-group col-md-6" data-bind="css: contact.formcss">
                  <label for="edit_contact"><spring:message code="user.contact"/></label>
                  <textarea id="edit_contact" rows="3" class="form-control" data-bind="value: contact, valueUpdate: 'input'"></textarea>
                </div>
                <div class="form-group col-md-6" data-bind="css: info.formcss">
                  <label for="edit_info"><spring:message code="user.info"/></label>
                  <textarea id="edit_info" rows="3" class="form-control" data-bind="value: info, valueUpdate: 'input'"></textarea>
                </div>
              </div>
              <sec:authorize access="@auth.hasAccessLevel('Root')">
                <div class="row">
                  <div class="form-group col-md-4" data-bind="css: username.formcss">
                    <label for="edit_username"><spring:message code="user.username"/></label>
                    <input type="text" id="edit_username" class="form-control" data-bind="value: username, valueUpdate: 'input'"/>
                  </div>
                  <div class="form-group col-md-3 col-md-offset-1" data-bind="css: allowlogin.formcss">
                    <label for="edit_allowlogin"><spring:message code="user.allowlogin"/></label><br/>
                    <button type="button" id="edit_allowlogin" class="btn"
                            data-bind="click: allowlogin.toggle, css: (allowlogin() ? 'btn-success active' : 'btn-default')">
                      <span class="glyphicon" data-bind="css: allowlogin() ? 'glyphicon-ok-circle' : 'glyphicon-ban-circle'"></span>
                    </button>
                  </div>
                  <div class="form-group col-md-4" data-bind="css: {'has-change': authorities.changed}">
                    <select id="edit_auth" multiple class="form-control" data-bind="selectedOptions: authorities">
                      <c:forEach items="<%= at.wrk.coceso.entity.enums.Authority.values()%>" var="authority">
                        <option>${authority}</option>
                      </c:forEach>
                    </select>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-md-4" data-bind="css: password.formcss">
                    <label for="edit_password"><spring:message code="user.password.new"/></label>
                    <input type="password" id="edit_password" class="form-control" data-bind="value: password, valueUpdate: 'input'"/>
                  </div>
                  <div class="form-group col-md-4" data-bind="css: password2.formcss">
                    <label for="edit_password2"><spring:message code="user.password.repeat"/></label>
                    <input type="password" id="edit_password2" class="form-control" data-bind="value: password2, valueUpdate: 'input'"/>
                  </div>
                </div>
              </sec:authorize>
            </div>
            <div class="modal-footer">
              <div class="pull-right">
                <button type="submit" class="btn btn-success" data-bind="enable: form.enable"><spring:message code="save"/></button>
                <button type="button" class="btn btn-warning" data-bind="enable: form.changed, click: form.reset"><spring:message code="reset"/></button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </body>
</html>
