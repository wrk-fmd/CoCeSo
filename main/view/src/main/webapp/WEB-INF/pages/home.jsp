<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@taglib uri="coceso" prefix="t" %>
<%--
/**
 * CoCeSo
 * Client HTML home page
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
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
      plugins: ${cocesoConfig.jsPlugins},
      initalError: ${error}
    };
  </script>
  <t:head title="nav.home" entry="home"/>
</head>
<body class="scroll">
<div class="container">
  <c:set value="active" var="nav_home"/>
  <%@include file="parts/navbar.jsp" %>

  <div class="alert alert-danger" id="msie-detection-error" hidden>
    <p>
      <strong><spring:message code="error.msie.detected"/></strong>
    </p>

    <spring:message code="error.msie.detected.explanation"/>
  </div>

  <%-- Userdetails -- DEBUG --%>
  <div class="alert alert-info alert-dismissable">
    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
    <p><strong><spring:message code="user"/>:</strong> <c:out value="${user.firstname} ${user.lastname} (${user.username})"/></p>
    <p><spring:message code="user.roles"/>:</p>
    <ul>
      <c:forEach items="${authenticatedUser.authorities}" var="grantedAuthority">
        <li><c:out value="${grantedAuthority}"/></li>
      </c:forEach>
    </ul>
  </div>

  <%-- Show Error Message --%>
  <div class="alert alert-danger" data-bind="visible: error">
    <strong><spring:message code="error"/>:</strong> <span data-bind="text: errorText"></span>
  </div>

  <%-- Last Active Concern --%>
  <div class="alert alert-success" data-bind="visible: concernId">
    <p><strong><spring:message code="concern.last"/>:</strong>&nbsp;<span data-bind="text: concernName"></span></p>
    <p>
      <sec:authorize access="@auth.hasAccessLevel('Main')">
        <a href="<c:url value="/main"/>" class="btn btn-success"><spring:message code="concern.start"/></a>
      </sec:authorize>
      <a href="<c:url value="/patadmin"/>" class="btn btn-success"><spring:message code="patadmin"/></a>
    </p>
  </div>

  <%-- Create new Concern --%>
  <sec:authorize access="@auth.hasAccessLevel('Edit')">
    <div class="page-header">
      <h2><spring:message code="concern.create"/></h2>
    </div>
    <!-- ko with: create -->
    <form class="clearfix" data-bind="submit: save">
      <div class="col-md-5 form-group" data-bind="css: name.formcss">
        <label class="sr-only" for="create_name"><spring:message code="concern.name"/></label>
        <input type="text" id="create_name" maxlength="64" class="form-control"
               data-bind="value: name, valueUpdate: 'input'"
               placeholder="<spring:message code="concern.name"/>"/>
      </div>
      <div class="col-md-3">
        <button type="submit" class="btn btn-success" data-bind="enable: name.changed"><spring:message code="create"/></button>
      </div>
    </form>
    <!-- /ko -->
  </sec:authorize>

  <%-- Active Concerns --%>
  <div class="page-header">
    <h2><spring:message code="concerns"/></h2>
  </div>
  <div class="table-responsive" style="overflow-x: visible">
    <table class="table table-striped">
      <tbody data-bind="foreach: open">
      <tr data-bind="css: {success: isActive}">
        <td data-bind="text: name"></td>
        <td>
          <button type="button" class="btn btn-sm btn-primary" data-bind="click: select, disable: $root.locked"><spring:message code="concern.select"/></button>
          <sec:authorize access="@auth.hasAccessLevel('CloseConcern')">
            <button type="button" class="btn btn-sm btn-danger" data-bind="click: close"><spring:message code="concern.close"/></button>
          </sec:authorize>
          <sec:authorize access="@auth.hasAccessLevel('Report')">
            <div class="btn-group btn-group-sm" role="group">
              <button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <spring:message code="pdf.report.create"/> <span class="caret"></span>
              </button>
              <ul class="dropdown-menu">
                <li>
                  <a target="_blank" data-bind="attr: {href: '<c:url value="/pdf/report.pdf?id="/>' + id}"><spring:message code="pdf.report"/></a>
                </li>
                <li>
                  <a target="_blank" data-bind="attr: {href: '<c:url value="/pdf/transport.pdf?id="/>' + id}"><spring:message code="pdf.transport"/></a>
                </li>
                <li>
                  <a target="_blank" data-bind="attr: {href: '<c:url value="/pdf/patients.pdf?id="/>' + id}"><spring:message code="pdf.patients"/></a>
                </li>
                <li>
                  <a target="_blank" data-bind="attr: {href: '<c:url value="/pdf/dump.pdf?id="/>' + id}"><spring:message code="pdf.dump"/></a>
                </li>
              </ul>
            </div>
          </sec:authorize>
          <sec:authorize access="@auth.hasAccessLevel('Edit')">
            <c:if test="${isGeoBrokerFeatureAvailable}">
              <a target="_blank" data-bind="attr: {href: '<c:url value="/geo/qr-codes?concernId="/>' + id}" class="btn btn-sm btn-default">
                <spring:message code="concern.qr.codes"/>
              </a>
            </c:if>
          </sec:authorize>
        </td>
      </tr>
      </tbody>
    </table>
  </div>

  <div class="form-group" data-bind="visible: concernId">
    <sec:authorize access="@auth.hasAccessLevel('Main')">
      <a href="<c:url value="/main"/>" class="btn btn-success"><spring:message code="concern.start"/></a>
    </sec:authorize>
    <a href="<c:url value="/patadmin"/>" class="btn btn-success"><spring:message code="patadmin"/></a>
    <sec:authorize access="@auth.hasAccessLevel('Edit')">
      <a href="<c:url value="/edit"/>" class="btn btn-warning"><spring:message code="concern.edit"/></a>
    </sec:authorize>
  </div>

  <div class="alert alert-danger" data-bind="visible: locked">
    <p><spring:message code="concern.locked"/></p>
    <p><a class="btn btn-danger btn-xs" data-bind="click: forceUnlock"><spring:message code="concern.unlock"/></a></p>
  </div>

  <%-- Closed Concerns --%>
  <sec:authorize access="@auth.hasAccessLevel('CloseConcern') or @auth.hasAccessLevel('Report')">
    <div class="page-header">
      <h2><spring:message code="concern.closed"/></h2>
    </div>

    <div class="table-responsive" style="overflow-x: visible">
      <table class="table table-striped">
        <tbody data-bind="foreach: closed">
        <tr>
          <td data-bind="text: name"></td>
          <td>
            <sec:authorize access="@auth.hasAccessLevel('CloseConcern')">
              <button type="button" class="btn btn-sm btn-danger" data-bind="click: reopen"><spring:message code="concern.reopen"/></button>
            </sec:authorize>
            <sec:authorize access="@auth.hasAccessLevel('Report')">
              <div  class="btn-group btn-group-sm" role="group">
                <button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <spring:message code="pdf.report.create"/> <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">
                  <li>
                    <a target="_blank" data-bind="attr: {href: '<c:url value="/pdf/report.pdf?id="/>' + id}"><spring:message code="pdf.report"/></a>
                  </li>
                  <li>
                    <a target="_blank" data-bind="attr: {href: '<c:url value="/pdf/transport.pdf?id="/>' + id}"><spring:message code="pdf.transport"/></a>
                  </li>
                  <li>
                    <a target="_blank" data-bind="attr: {href: '<c:url value="/pdf/patients.pdf?id="/>' + id}"><spring:message code="pdf.patients"/></a>
                  </li>
                </ul>
              </div>
            </sec:authorize>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </sec:authorize>

  <div class="page-header">&nbsp;</div>
</div>

<script type="text/javascript">
  (function () {
    if (/msie\ [0-9]/i.test(navigator.userAgent)) {
      console.error("Internet Explorer is not supported!");
      document.getElementById('msie-detection-error').hidden = false;
    } else if (/Trident\/[0-9]/i.test(navigator.userAgent)) {
      console.error("Internet Explorer (Trident) is not supported!");
      document.getElementById('msie-detection-error').hidden = false;
    }
  })();
</script>
</body>
</html>
