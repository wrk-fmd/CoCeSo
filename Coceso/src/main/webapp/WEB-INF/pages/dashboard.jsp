<!DOCTYPE html>
<%@page import="at.wrk.coceso.entity.Unit"%>
<%@page import="java.util.Map"%>
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
            <li class="dropdown ${log}">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.log"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="?concern=${concern}&amp;sub=Log"><spring:message code="label.dashboard.log.full"/></a></li>
                <li class="divider"></li>
                <li><a href="?concern=${concern}&amp;sub=Log&amp;uid=0"><spring:message code="label.dashboard.log.unit"/></a></li>
                <li><a href="?concern=${concern}&amp;sub=Log&amp;iid=0"><spring:message code="label.dashboard.log.incident"/></a></li>
              </ul>
            </li>
            <li class="dropdown ${task}">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Task <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="?concern=${concern}&amp;sub=Task&amp;uid=0">Tasks by Unit</a></li>
                <li><a href="?concern=${concern}&amp;sub=Task&amp;iid=0">Tasks by Incident</a></li>
              </ul>
            </li>
            <li class="dropdown ${unit}">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Unit <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="?concern=${concern}&amp;sub=Unit">Unit List</a></li>
                <li class="divider"></li>
                <li><a href="?concern=${concern}&amp;sub=Unit&amp;uid=0">Unit by Id</a></li>
              </ul>
            </li>
            <li class="dropdown ${incident}">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.incident"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="?concern=${concern}&amp;sub=Incident">Incident List</a></li>
                <li><a href="?concern=${concern}&amp;sub=Incident&amp;uid=-1">Active Incidents</a></li>
                <li class="divider"></li>
                <li><a href="?concern=${concern}&amp;sub=Incident&amp;iid=0">Incident by Id</a></li>
              </ul>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.concern"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <c:forEach items="${concerns}" var="c_concern">
                  <li><a href="?concern=${c_concern.id}"><c:out value="${c_concern.name}"/></a></li>
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


      <c:url var="get_inc" value="/dashboard?concern=${concern}&amp;sub=Incident&amp;iid="/>
      <c:url var="get_unit" value="/dashboard?concern=${concern}&amp;sub=Unit&amp;uid="/>

      <c:if test="${not empty unit && not empty uid}">
        <div>
          <form class="form-inline" action="?">
            <div class="row">
              <div class="col-lg-2">
                <input type="hidden" name="sub" value="Unit"/>
                <input type="hidden" name="concern" value="${concern}"/>
              </div>
              <div class="col-lg-2">
                  <!--input type="number" placeholder="Unit ID" name="uid" class="form-control" value="${uid}"/-->
                <select name="uid" size="1" class="form-control">
                  <c:forEach var="sel_unit" items="${sel_units}">
                    <option value="${sel_unit.id}"><c:out value="${sel_unit.call}"/></option>
                  </c:forEach>
                </select>
              </div>
              <div class="col-lg-2">
                <input type="submit" class="btn btn-success">
              </div>
            </div>
          </form>
        </div>
      </c:if>
      <c:if test="${not empty u_unit}">
        <div class="alert alert-success">
          <strong><c:out value="${u_unit.call}"/>: </strong> <c:out value="${u_unit.info}"/><!-- TODO -->
        </div>
        <div class="page-header">
          <h3>
            Assigned Incidents:
          </h3>
        </div>
        <c:forEach var="incs" items="${u_unit.incidents}">
          <div class="alert alert-info">
            ID: <a href="${get_inc}${incs.key}" class="btn btn-primary">${incs.key}</a> : <c:out value="${incs.value}"/>
          </div>
        </c:forEach>
      </c:if>

      <!-- ################ -->

      <%--
      <c:if test="${not empty incident && not empty iid && iid != -1}">
          <div>
              <form class="form-inline" action="?">
                  <div class="row">
                      <div class="col-lg-2">
                          <input type="hidden" name="sub" value="Incident"/>
                      </div>
                      <div class="col-lg-2">
                          <select name="id" size="1" class="form-control">
                              <c:forEach var="sel_inc" items="${sel_incs}">
                                  <option value="${sel_inc.id}">${sel_unit.call}</option>
                              </c:forEach>
                          </select>
                      </div>
                      <div class="col-lg-2">
                          <input type="submit" class="btn btn-success">
                      </div>
                  </div>
              </form>
          </div>
      </c:if>
      --%>
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

      <c:if test="${not empty logs || not empty units || not empty incidents || not empty tasks}">
        <table class="table table-striped">
          <thead>
            <c:if test="${not empty logs}">
              <tr>
                <th>
                  Time
                </th>
                <th>
                  User
                </th>
                <th>
                  Text
                </th>
                <th>
                  Unit
                </th>
                <th>
                  Incident
                </th>
                <th>
                  State
                </th>
              </tr>
            </c:if>
            <c:if test="${not empty units}">
              <tr>
                <th>
                  <spring:message code="label.unit.call"/>
                </th>
                <th>
                  <spring:message code="label.unit.state"/>
                </th>
                <th>
                  <spring:message code="label.unit.ani"/>
                </th>
                <th>
                  <spring:message code="label.unit.info"/>
                </th>
                <th>
                  <spring:message code="label.task.state"/> **
                </th>
              </tr>
            </c:if>
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
            <c:if test="${not empty logs}">
              <c:forEach items="${logs}" var="logEntry">
                <tr>
                  <td>
                    <fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${logEntry.timestamp}"/>
                  </td>
                  <td>
                    <c:out value="${logEntry.user.username}"/>
                  </td>
                  <td>
                    ${logEntry.text}
                  </td>
                  <td>
                    <a href="${get_unit}${logEntry.unit.id}"><c:out value="${logEntry.unit.call}"/></a>
                  </td>
                  <td>
                    <a href="${get_inc}${logEntry.incident.id}"><c:out value="${logEntry.incident.id}"/></a>
                  </td>
                  <td>
                    <c:out value="${logEntry.state}"/>
                  </td>
                </tr>
              </c:forEach>
            </c:if>
            <c:if test="${not empty units}">
              <c:forEach items="${units}" var="eUnit">
                <tr>
                  <td>
                    <c:out value="${eUnit.call}"/>
                  </td>
                  <td>
                    <c:out value="${eUnit.state}"/>
                  </td>
                  <td>
                    <c:out value="${eUnit.ani}"/>
                  </td>
                  <td>
                    <c:out value="${eUnit.info}"/>
                  </td>
                  <td>
                    <%
                      Unit u = (Unit) pageContext.getAttribute("eUnit");
                      if (u.getIncidents().size() == 1) {
                        out.print(u.getIncidents().get(u.getIncidents().keySet().iterator().next()).name());
                      }
                    %>
                  </td>
                </tr>
              </c:forEach>
            </c:if>
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
    </div>
  </body>
</html>
