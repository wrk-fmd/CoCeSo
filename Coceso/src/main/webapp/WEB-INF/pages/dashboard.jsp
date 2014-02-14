<%@ page import="at.wrk.coceso.entity.Unit" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="label.coceso"/> - <spring:message code="label.nav.dashboard"/></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">
    <!-- Bootstrap -->
    <c:url var="bootstrap" value="/static/css/bootstrap.css" />
    <link href="${bootstrap}" rel="stylesheet">

    <c:url var="bootstrap_theme" value="/static/css/bootstrap-theme.css" />
    <link href="${bootstrap_theme}" rel="stylesheet">

</head>
<body>
<div class="container">
    <c:url var="logout_link" value="/logout" />
    <c:url var="back_link" value="/welcome" />
    <c:url var="home_link" value="/" />

    <!-- Static navbar -->
    <div class="navbar navbar-inverse" role="navigation">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${home_link}"><spring:message code="label.coceso"/></a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="#">Dashboard</a></li>
                <li><a href="#"></a></li>
                <li class="dropdown ${log}">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.log"/> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="?concern=${concern}&sub=Log"><spring:message code="label.dashboard.log.full"/></a></li>
                        <li class="divider"></li>
                        <li><a href="?concern=${concern}&sub=Log&uid=0"><spring:message code="label.dashboard.log.unit"/></a></li>
                        <li><a href="?concern=${concern}&sub=Log&iid=0"><spring:message code="label.dashboard.log.incident"/></a></li>
                    </ul>
                </li>
                <li class="dropdown ${task}">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Task <b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li><a href="?concern=${concern}&sub=Task&uid=0">Tasks by Unit</a></li>
                    <li><a href="?concern=${concern}&sub=Task&iid=0">Tasks by Incident</a></li>
                </ul>
            </li>
                <li class="dropdown ${unit}">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Unit <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="?concern=${concern}&sub=Unit">Unit List</a></li>
                        <li class="divider"></li>
                        <li><a href="?concern=${concern}&sub=Unit&uid=0">Unit by Id</a></li>
                    </ul>
                </li>
                <li class="dropdown ${incident}">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.incident"/> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="?concern=${concern}&sub=Incident">Incident List</a></li>
                        <li><a href="?concern=${concern}&sub=Incident&uid=-1">Active Incidents</a></li>
                        <li class="divider"></li>
                        <li><a href="?concern=${concern}&sub=Incident&iid=0">Incident by Id</a></li>
                    </ul>
                </li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.concern"/> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <c:forEach items="${concerns}" var="c_concern">
                            <li><a href="?concern=${c_concern.id}">${c_concern.name}</a></li>
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


    <c:url var="get_inc" value="/dashboard?concern=${concern}&sub=Incident&iid=" />
    <c:url var="get_unit" value="/dashboard?concern=${concern}&sub=Unit&uid=" />

    <c:if test="${not empty unit && not empty uid}">
        <div>
            <form class="form-inline" action="?">
                <div class="row">
                    <div class="col-lg-2">
                        <input type="hidden" name="sub" value="Unit" />
                        <input type="hidden" name="concern" value="${concern}" />
                    </div>
                    <div class="col-lg-2">
                        <!--input type="number" placeholder="Unit ID" name="uid" class="form-control" value="${uid}"/-->
                        <select name="uid" size="1" class="form-control">
                            <c:forEach var="sel_unit" items="${sel_units}">
                                <option value="${sel_unit.id}">${sel_unit.call}</option>
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
            <strong>${u_unit.call}: </strong> ${u_unit.info}<!-- TODO -->
        </div>
        <div class="page-header">
            <h3>
                Assigned Incidents:
            </h3>
        </div>
        <c:forEach var="incs" items="${u_unit.incidents}">
            <div class="alert alert-info">
                ID: <a href="${get_inc}${incs.key}" class="btn btn-primary">${incs.key}</a> : ${incs.value}
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
                        <input type="hidden" name="sub" value="Incident" />
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
            <strong>${i_incident.id}: </strong> ${i_incident.caller}<!-- TODO -->
        </div>
        <div class="page-header">
            <h3>
                Assigned Units:
            </h3>
        </div>
        <c:forEach var="unts" items="${i_incident.units}">
            <div class="alert alert-info">
                ID:
                <a href="${get_unit}${unts.key}" class="btn btn-primary">${i_map[unts.key]}</a> : ${unts.value}
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
                            <fmt:formatNumber minIntegerDigits="2" value="${logEntry.timestamp.hours}" />:<fmt:formatNumber minIntegerDigits="2" value="${logEntry.timestamp.minutes}" />:<fmt:formatNumber minIntegerDigits="2" value="${logEntry.timestamp.seconds}" />
                        </td>
                        <td>
                            ${logEntry.user.username}
                        </td>
                        <td>
                            ${logEntry.text}
                        </td>
                        <td>
                            <a href="${get_unit}${logEntry.unit.id}">${logEntry.unit.call}</a>
                        </td>
                        <td>
                            <a href="${get_inc}${logEntry.incident.id}">${logEntry.incident.id}</a>
                        </td>
                        <td>
                            ${logEntry.state}
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            <c:if test="${not empty units}">
                <c:forEach items="${units}" var="eUnit">
                    <tr>
                        <td>
                            ${eUnit.call}
                        </td>
                        <td>
                            ${eUnit.state}
                        </td>
                        <td>
                            ${eUnit.ani}
                        </td>
                        <td>
                            ${eUnit.info}
                        </td>
                        <td>
                            <%
                                Unit u = (Unit) pageContext.getAttribute("eUnit");
                                if(u.getIncidents().size() == 1) {
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
                                ${eIncident.id}
                        </td>
                        <td>
                                ${eIncident.type}
                        </td>
                        <td>
                                ${eIncident.bo}
                        </td>
                        <td>
                                ${eIncident.ao}
                        </td>
                        <td>
                            ${eIncident.info}
                        </td>
                        <td>
                            ${eIncident.casusNr}
                        </td>
                        <td>
                            ${eIncident.state}
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            <c:if test="${not empty tasks}">
                <c:forEach items="${tasks}" var="etask">
                    <tr>
                        <td>
                            ${etask.key}
                        </td>
                        <td>
                            ${etask.value}
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
        </tbody>
    </table>
    </c:if>
</div>


<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<c:url var="jquery" value="/static/js/jquery.js" />
<script src="${jquery}"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<c:url var="bootstrap_js" value="/static/js/bootstrap.js" />
<script src="${bootstrap_js}"></script>
</body>
</html>