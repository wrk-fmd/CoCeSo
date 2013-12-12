<%@ page import="at.wrk.coceso.entities.Unit" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap -->
    <c:url var="bootstrap" value="/static/bootstrap.css" />
    <link href="${bootstrap}" rel="stylesheet">

    <c:url var="bootstrap_theme" value="/static/bootstrap-theme.css" />
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
            <a class="navbar-brand" href="${home_link}">CoCeSo</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="#">Dashboard</a></li>
                <li><a href="#"></a></li>
                <li class="dropdown ${log}">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Log <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="?sub=Log">Full Log</a></li>
                        <li class="divider"></li>
                        <li><a href="?sub=Log&uid=0">Log by Unit</a></li>
                        <li><a href="?sub=Log&iid=0">Log by Incident</a></li>
                    </ul>
                </li>
                <li class="dropdown ${task}">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Task <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="?sub=Task&uid=0">Tasks by Unit</a></li>
                        <li><a href="?sub=Task&iid=0">Tasks by Incident</a></li>
                    </ul>
                </li>
                <li class="dropdown ${unit}">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Unit <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="?sub=Unit">Unit List</a></li>
                        <li class="divider"></li>
                        <li><a href="?sub=Unit&uid=0">Unit by Id</a></li>
                    </ul>
                </li>
                <li class="dropdown ${incident}">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Incident <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="?sub=Incident">Incident List</a></li>
                        <li><a href="?sub=Incident&uid=-1">Active Incidents</a></li>
                        <li class="divider"></li>
                        <li><a href="?sub=Incident&iid=0">Incident by Id</a></li>
                    </ul>
                </li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="${back_link}">Back</a></li>
                <li><a href="${logout_link}">Logout</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <strong>An Error occured: </strong>${error}
        </div>
    </c:if>


    <c:url var="get_inc" value="/dashboard?sub=Incident&iid=" />
    <c:url var="get_unit" value="/dashboard?sub=Unit&uid=" />

    <c:if test="${not empty unit && not empty uid}">
        <div>
            <form class="form-inline" action="?">
                <div class="row">
                    <div class="col-lg-2">
                        <input type="hidden" name="sub" value="Unit" />
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
                ID: <a href="${get_inc}${unts.key}" class="btn btn-primary">${unts.key}</a> : ${unts.value}
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
                        Call
                    </th>
                    <th>
                        State
                    </th>
                    <th>
                          ANI
                    </th>
                    <th>
                        Info
                    </th>
                    <th>
                        TaskState **
                    </th>
                </tr>
            </c:if>
            <c:if test="${not empty incidents}">
                <tr>
                    <th>
                        ID
                    </th>
                    <th>
                        Type
                    </th>
                    <th>
                        BO
                    </th>
                    <th>
                        AO
                    </th>
                    <th>
                        Info
                    </th>
                    <th>
                        Casus
                    </th>
                    <th>
                        State
                    </th>
                </tr>
            </c:if>
            <c:if test="${not empty tasks}">
                <tr>
                    <th>
                        ID
                    </th>
                    <th>
                        State
                    </th>
                </tr>
            </c:if>
        </thead>
        <tbody>
            <c:if test="${not empty logs}">
                <c:forEach items="${logs}" var="logEntry">
                    <tr>
                        <td>
                            ${logEntry.timestamp.hours}:${logEntry.timestamp.minutes}:${logEntry.timestamp.seconds}
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
                                if(u.incidents.size() == 1) {
                                    out.print(u.incidents.get(u.incidents.keySet().iterator().next()).name());
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
                                ## BO ##
                        </td>
                        <td>
                                ## AO ##
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
<c:url var="jquery" value="/static/jquery.js" />
<script src="${jquery}"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<c:url var="bootstrap_js" value="/static/bootstrap.js" />
<script src="${bootstrap_js}"></script>
</body>
</html>