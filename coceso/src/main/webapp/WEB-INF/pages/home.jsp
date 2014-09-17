<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML home page
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
    <title><spring:message code="label.coceso"/> - <spring:message code="label.nav.home"/></title>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon"/>
    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css"/>

    <%-- jQuery --%>
    <script src="<c:url value="/static/js/assets/jquery.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/jquery.cookie.js"/>" type="text/javascript"></script>
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
        ko.applyBindings(new Coceso.ViewModels.Home(${error}));
      });
    </script>
  </head>
  <body class="scroll">
    <div class="container">
      <c:set value="active" var="nav_home"/>
      <%@include file="parts/navbar.jsp"%>

      <%-- Userdetails -- DEBUG --%>
      <div class="alert alert-info alert-dismissable">
        <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
        <p><strong><spring:message code="label.operator"/>:</strong> <c:out value="${user.given_name} ${user.sur_name} (${user.username})"/></p>
        <p>Roles:</p>
        <ul>
          <c:forEach items="${user.authorities}" var="role">
            <li><c:out value="${role}"/></li>
            </c:forEach>
        </ul>
      </div>

      <%-- Show Error Message --%>
      <div class="alert alert-danger" data-bind="visible: error">
        <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: errorText"></span>
      </div>

      <%-- Last Active Concern --%>
      <div class="alert alert-success" data-bind="visible: concernId">
        <p><strong><spring:message code="label.concern.lastActive"/>:</strong>&nbsp;<span data-bind="text: concernName"></span></p>
        <p><a href="<c:url value="/main"/>" class="btn btn-success"><spring:message code="label.start"/></a></p>
      </div>

      <%-- Create new Concern --%>
      <div class="page-header">
        <h2><spring:message code="label.concern.create"/></h2>
      </div>
      <!-- ko with: create -->
      <form class="clearfix" data-bind="submit: save">
        <div class="col-md-5 form-group">
          <label class="sr-only" for="create_name"><spring:message code="label.concern.name"/></label>
          <input type="text" id="create_name" maxlength="64" class="form-control"
                 data-bind="value: name, valueUpdate: 'input', css: name.css"
                 placeholder="<spring:message code="label.concern.name"/>"/>
        </div>
        <div class="col-md-3">
          <button type="submit" class="btn btn-success" data-bind="enable: localChange"><spring:message code="label.create"/></button>
        </div>
      </form>
      <!-- /ko -->

      <%-- Active Concerns --%>
      <div class="page-header">
        <h2><spring:message code="label.concerns"/></h2>
      </div>
      <div class="table-responsive">
        <table class="table table-striped">
          <tbody data-bind="foreach: open">
            <tr data-bind="css: {success: isActive}">
              <td data-bind="text: name"></td>
              <td>
                <button type="button" class="btn btn-sm btn-success" data-bind="click: select, disable: $root.locked"><spring:message code="label.selectconcern"/></button>
                <c:if test="${not empty authorized}">
                  <button type="button" class="btn btn-sm btn-danger" data-bind="click: close"><spring:message code="label.close"/></button>
                </c:if>
                <a target="_blank" class="btn btn-sm btn-success" data-bind="attr: {href: '<c:url value="/finalReport/report.pdf?id="/>' + id}"><spring:message code="label.print"/></a>
                <a target="_blank" class="btn btn-sm btn-warning" data-bind="attr: {href: '<c:url value="/pdfdump/dump.pdf?id="/>' + id}"><spring:message code="label.dump"/></a>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="form-group row" data-bind="visible: concernId">
          <a href="<c:url value="/main"/>" class="btn btn-success"><spring:message code="label.start"/></a>
          <a href="<c:url value="/edit"/>" class="btn btn-warning"><spring:message code="label.edit"/></a>
        </div>

        <div class="alert alert-danger" data-bind="visible: locked">
          <p><spring:message code="text.locked"/></p>
          <p><a class="btn btn-danger btn-xs" data-bind="click: forceUnlock"><spring:message code="label.unlock"/></a></p>
        </div>
      </div>

      <%-- Closed Concerns --%>
      <div class="page-header">
        <h2><spring:message code="label.concern.closed"/></h2>
      </div>
      <div class="table-responsive">
        <table class="table table-striped">
          <tbody data-bind="foreach: closed">
            <tr>
              <td data-bind="text: name"></td>
              <td>
                <a target="_blank" class="btn btn-sm btn-success" data-bind="attr: {href: '<c:url value="/finalReport/report.pdf?id="/>' + id}"><spring:message code="label.print"/></a>
                <a target="_blank" class="btn btn-sm btn-warning" data-bind="attr: {href: '<c:url value="/pdfdump/transportlist.pdf?id="/>' + id}"><spring:message code="label.transportlist"/></a>
                <c:if test="${not empty authorized}">
                  <button type="button" class="btn btn-sm btn-danger" data-bind="click: reopen"><spring:message code="label.reopen"/></button>
                </c:if>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="page-header"></div>
    </div>
  </body>
</html>
