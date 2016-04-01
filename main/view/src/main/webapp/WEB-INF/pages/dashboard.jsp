<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
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
    <t:head title="nav.dashboard" entry="navbar"/>
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
          <a class="navbar-brand" href="#" onclick="return false;"><spring:message code="coceso"/></a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li><a href="#">Dashboard</a></li>
            <li><a href="#"></a></li>
            <li class="${log_menu}">
              <a href="<c:url value="?concern=${concern}"/>" ><spring:message code="log"/></a>
            </li>
            <li class="${unit_menu}">
              <a href="<c:url value="?concern=${concern}&amp;view=unit"/>"><spring:message code="units"/></a>
            </li>
            <li class="dropdown ${incident_menu}">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="incidents"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="?concern=${concern}&amp;view=incident"><spring:message code="main.incident.overview"/></a></li>
                <li><a href="?concern=${concern}&amp;view=incident&amp;active=1"><spring:message code="main.incident.active"/></a></li>
              </ul>
            </li>
            <li class="${patient_menu}">
              <a href="<c:url value="?concern=${concern}&amp;view=patient"/>"><spring:message code="patients"/></a>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="concern"/> <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <c:forEach items="${concerns}" var="c_concern">
                  <li <c:if test="${c_concern.id == concern}">class="active"</c:if>>
                    <a href="?concern=${c_concern.id}"><c:out value="${c_concern.name}"/></a>
                  </li>
                </c:forEach>
              </ul>
            </li>
            <li><a href="${back_link}"><spring:message code="nav.home"/></a></li>
            <li><a href="${logout_link}"><spring:message code="nav.logout"/></a></li>
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
        <c:when test="${template == 'patient_list'}"><%@include file="dashboard/patient_list.jsp"%></c:when>
        <c:when test="${template == 'cross_detail'}"><%@include file="dashboard/cross_detail.jsp"%></c:when>
        <c:when test="${template == 'incident_detail'}"><%@include file="dashboard/incident_detail.jsp"%></c:when>
        <c:when test="${template == 'unit_detail'}"><%@include file="dashboard/unit_detail.jsp"%></c:when>
        <c:when test="${template == 'patient_detail'}"><%@include file="dashboard/patient_detail.jsp"%></c:when>
        <c:when test="${template == 'log_table'}"><%@include file="dashboard/log_table.jsp"%></c:when>
      </c:choose>
    </div>
  </body>
</html>
