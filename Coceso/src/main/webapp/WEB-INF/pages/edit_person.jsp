<%@ page import="org.springframework.security.core.GrantedAuthority" %>
<%@ page import="java.util.List" %>
<%@ page import="at.wrk.coceso.entity.Operator" %>
<%@ page import="at.wrk.coceso.entity.enums.CocesoAuthority" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title><spring:message code="label.coceso"/> - <spring:message code="label.person.edit"/></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <c:url var="bootstrap" value="/static/css/bootstrap.css" />
    <link href="${bootstrap}" rel="stylesheet">

    <c:url var="bootstrap_theme" value="/static/css/bootstrap-theme.css" />
    <link href="${bootstrap_theme}" rel="stylesheet">

</head>
<body>

<div class="container">

    <c:set value="active" var="nav_person" />
    <%@include file="parts/navbar.jsp"%>

    <div class="page-header">
        <h2>
            <spring:message code="label.person.edit"/>: <strong>${p_person.sur_name} ${p_person.given_name}</strong>
        </h2>
    </div>
    <div>
        <form role="form" action="<c:url value="/edit/person/update" />" method="POST">
            <div class="row">
                <input type="hidden" name="id" value="${p_person.id}">
                <div class="form-group col-lg-4">
                    <label>
                        <spring:message code="label.person.given_name"/>
                        <input type="text" class="form-control" name="given_name" value="${p_person.given_name}">
                    </label>
                </div>
                <div class="form-group col-lg-4">
                    <label>
                        <spring:message code="label.person.sur_name"/>
                        <input type="text" class="form-control" name="sur_name" value="${p_person.sur_name}">
                    </label>
                </div>
                <div class="form-group col-lg-4">
                    <label>
                        <spring:message code="label.person.dnr"/>
                        <input class="form-control" name="dNr" value="${p_person.dNr}">
                    </label>
                </div>
            </div>
            <div class="row">
                <div class="form-group">
                    <label>
                        <spring:message code="label.person.contact"/><textarea class="form-control" rows="5" name="contact">${p_person.contact}</textarea>
                    </label>
                </div>
            </div>

            <div class="row">
                <div class="form-group">
                    <input type="submit" class="btn btn-success" value="<spring:message code="label.save" />">
                </div>
            </div>

        </form>
    </div>

    <c:if test="${not empty operator}">
        <div class="page-header">
            <h2>
                <spring:message code="label.operator.edit"/>
            </h2>
        </div>
        <div>
            <form role="form" action="<c:url value="/edit/person/updateOp" />" method="POST">
                <div class="row">
                    <input type="hidden" name="id" value="${operator.id}">

                    <div class="form-group col-lg-4">
                        <label>
                            <spring:message code="label.operator.username"/>
                            <input type="text" class="form-control" name="username" value="${operator.username}">
                        </label>
                    </div>

                    <div class="form-group col-lg-4">
                        <label>
                            <spring:message code="label.operator.allowlogin"/>
                            <c:if test="${operator.allowLogin}">
                                <c:set value="checked" var="o_allowlogin" />
                            </c:if>
                            <input class="form-control" name="allowLogin" type="checkbox" ${o_allowlogin}>
                        </label>
                    </div>

                    <div class="form-group col-lg-4">
                        <%--form:select multiple="true" path="internalAuthorities" items="${authorities}" cssClass="form-control"/--%>
                        <%--TODO Just a Workaround...--%>
                        <select multiple class="form-control" name="internalAuthorities">

                            <c:set value="${operator.internalAuthorities}" var="i_auths"/>
                            <%
                                List<CocesoAuthority> opAuths = (List<CocesoAuthority>) pageContext.getAttribute("i_auths");
                            %>
                            <c:forEach items="${authorities}" var="authority">
                                <%
                                    CocesoAuthority auth = (CocesoAuthority) pageContext.getAttribute("authority");
                                %>
                                <option <%= opAuths.contains(auth) ? "selected" : "" %>>${authority}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="row">
                    <div class="form-group">
                        <input type="submit" class="btn btn-success" value="<spring:message code="label.save" />">
                    </div>
                </div>

            </form>
        </div>

    </c:if>

    <c:if test="${not empty user_not_op}">
        <div class="page-header">
            <h2>
                <spring:message code="label.operator.make"/>
            </h2>
        </div>
        <div>
            <form role="form" action="<c:url value="/edit/person/createOp" />" method="POST">
                <div class="row">
                    <input type="hidden" name="id" value="${p_person.id}">

                    <div class="form-group col-lg-4">
                        <label>
                            <spring:message code="label.operator.username"/>
                            <input type="text" class="form-control" name="username" placeholder="<spring:message code="label.operator.username" />">
                        </label>
                    </div>

                    <div class="form-group col-lg-4">
                        <label>
                            <spring:message code="label.operator.allowlogin"/>
                            <input class="form-control" type="checkbox" name="allowLogin">
                        </label>
                    </div>
                </div>

                <div class="row">
                    <div class="form-group">
                        <input type="submit" class="btn btn-success" value="<spring:message code="label.create" />">
                    </div>
                </div>

            </form>
        </div>
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