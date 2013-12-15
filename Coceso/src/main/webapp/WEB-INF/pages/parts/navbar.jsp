<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="navbar navbar-inverse" role="navigation">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="<c:url value="/"/>">CoCeSo</a>
    </div>
    <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav">
            <li class="${nav_home}"><a href="<c:url value="/welcome"/>">Home</a></li>
            <li class="${nav_concern}"><a href="<c:url value="/edit/"/>">Edit Concern</a></li>
            <li class="${nav_person}"><a href="<c:url value="/edit/person/"/>">Edit Person</a></li>
            <li class="${nav_main}"><a href="<c:url value="/main"/>"><strong>Main Program</strong></a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="<c:url value="/dashboard"/>">Dashboard</a></li>
            <li><a href="<c:url value="/logout"/>">Logout</a></li>
        </ul>
    </div>
</div>
