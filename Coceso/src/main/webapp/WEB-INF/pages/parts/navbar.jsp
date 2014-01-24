<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="navbar navbar-inverse" role="navigation">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="<c:url value="/"/>"><spring:message code="label.coceso"/> v0.2</a>
    </div>
    <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav">
            <li class="${nav_home}"><a href="<c:url value="/welcome"/>"><spring:message code="label.nav.home"/></a></li>
            <li class="${nav_concern}"><a href="<c:url value="/edit/"/>"><spring:message code="label.nav.edit_concern"/></a></li>
            <li class="${nav_person}"><a href="<c:url value="/edit/person/"/>"><spring:message code="label.nav.edit_person"/></a></li>
            <li><a href="<c:url value="/main/"/>"><strong><spring:message code="label.nav.main"/></strong></a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="<c:url value="/dashboard"/>"><spring:message code="label.nav.dashboard"/></a></li>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.language"/> <b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li><a href="?lang=en"><spring:message code="label.language.en"/></a></li>
                    <li><a href="?lang=de"><spring:message code="label.language.de"/></a></li>
                </ul>
            </li>
            <li><a href="<c:url value="/logout"/>"><spring:message code="label.nav.logout"/></a></li>
        </ul>
    </div>
</div>
