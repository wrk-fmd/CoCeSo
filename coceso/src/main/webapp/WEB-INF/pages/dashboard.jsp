<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard
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
    <title><spring:message code="label.coceso"/> - <spring:message code="label.nav.dashboard"/></title>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css"/>

    <script src="<c:url value="/static/js/assets/jquery.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/assets/bootstrap.min.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
      $(document).ready(function() {
        $('[data-toggle="popover"]').popover();
      });
    </script>
  </head>
  <body>
    <div class="container">
      <c:url var="logout_link" value="/logout"/>
      <c:url var="back_link" value="/home"/>
      <c:url var="home_link" value="/"/>

      <!-- Static navbar -->
      <div class="navbar navbar-inverse" role="navigation">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#" onclick="return false;"><spring:message code="label.coceso"/></a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li><a href="#">Dashboard</a></li>
            <li><a href="#"></a></li>
            <li class="${log_menu}">
              <a href="<c:url value="?concern=${concern}"/>" ><spring:message code="label.log"/></a>
            </li>
            <li class="${unit_menu}">
              <a href="<c:url value="?concern=${concern}&amp;view=unit"/>"><spring:message code="label.units"/></a>
            </li>
            <li class="dropdown ${incident_menu}">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.incidents"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="?concern=${concern}&amp;view=incident"><spring:message code="label.main.incident.overview"/></a></li>
                <li><a href="?concern=${concern}&amp;view=incident&amp;active=1"><spring:message code="label.main.incident.active"/></a></li>
              </ul>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.concern"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <c:forEach items="${concerns}" var="c_concern">
                  <li <c:if test="${c_concern.id == concern}">class="active"</c:if>>
                    <a href="?concern=${c_concern.id}"><c:out value="${c_concern.name}"/></a>
                  </li>
                </c:forEach>
              </ul>
            </li>
            <li><a href="${back_link}"><spring:message code="label.nav.back"/></a></li>
            <li><a href="${logout_link}"><spring:message code="label.nav.logout"/></a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>

      <c:if test="${not empty error}">
        <div class="alert alert-danger">
          <strong>An Error occured: </strong>${error}
        </div>
      </c:if>

      <c:choose>
        <c:when test="${template == 'incident_list'}"><%@include file="dashboard/incident_list.jsp"%></c:when>
        <c:when test="${template == 'unit_list'}"><%@include file="dashboard/unit_list.jsp"%></c:when>
        <c:when test="${template == 'incident_detail'}"><%@include file="dashboard/incident_detail.jsp"%></c:when>
        <c:when test="${template == 'unit_detail'}"><%@include file="dashboard/unit_detail.jsp"%></c:when>
      </c:choose>
      <%--
            <c:url var="get_inc" value="/dashboard?concern=${concern}&amp;sub=Incident&amp;iid="/>
            <c:url var="get_unit" value="/dashboard?concern=${concern}&amp;sub=Unit&amp;uid="/>

      <c:if test="${not empty i_incident}">
        <div class="alert alert-success">
          <strong>${i_incident.id}: </strong> <c:out value="${i_incident.caller}"/><!-- TODO -->
        </div>
        <div class="page-header">
          <h3>
            Assigned Units:
          </h3>
        </div>
        <c:forEach var="unts" items="${i_incident.units}">
          <div class="alert alert-info">
            ID:
            <a href="${get_unit}${unts.key}" class="btn btn-primary"><c:out value="${i_map[unts.key]}"/></a> : <c:out value="${unts.value}"/>
          </div>
        </c:forEach>
      </c:if>

      <c:if test="${not empty incidents || not empty tasks}">
        <table class="table table-striped">
          <thead>
            <c:if test="${not empty incidents}">
              <tr>
                <th>
                  ID
                </th>
                <th>
                  <spring:message code="label.incident.type"/>
                </th>
                <th>
                  <spring:message code="label.incident.bo"/>
                </th>
                <th>
                  <spring:message code="label.incident.ao"/>
                </th>
                <th>
                  <spring:message code="label.incident.info"/>
                </th>
                <th>
                  <spring:message code="label.incident.casus"/>
                </th>
                <th>
                  <spring:message code="label.incident.state"/>
                </th>
              </tr>
            </c:if>
            <c:if test="${not empty tasks}">
              <tr>
                <th>
                  ID
                </th>
                <th>
                  <spring:message code="label.task.state"/>
                </th>
              </tr>
            </c:if>
          </thead>
          <tbody>
            <c:if test="${not empty incidents}">
              <c:forEach items="${incidents}" var="eIncident">
                <tr>
                  <td>
                    <c:out value="${eIncident.id}"/>
                  </td>
                  <td>
                    <c:out value="${eIncident.type}"/>
                  </td>
                  <td>
                    <c:out value="${eIncident.bo}"/>
                  </td>
                  <td>
                    <c:out value="${eIncident.ao}"/>
                  </td>
                  <td>
                    <c:out value="${eIncident.info}"/>
                  </td>
                  <td>
                    <c:out value="${eIncident.casusNr}"/>
                  </td>
                  <td>
                    <c:out value="${eIncident.state}"/>
                  </td>
                </tr>
              </c:forEach>
            </c:if>
            <c:if test="${not empty tasks}">
              <c:forEach items="${tasks}" var="etask">
                <tr>
                  <td>
                    <c:out value="${etask.key}"/>
                  </td>
                  <td>
                    <c:out value="${etask.value}"/>
                  </td>
                </tr>
              </c:forEach>
            </c:if>
          </tbody>
        </table>
      </c:if>
    </div>--%>
  </body>
</html>
