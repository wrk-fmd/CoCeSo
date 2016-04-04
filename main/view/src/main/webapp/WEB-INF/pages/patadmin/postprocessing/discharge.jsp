<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML postprocessing discharge patient
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<html>
  <head>
    <t:head maintitle="patadmin" title="patient.discharge" entry="navbar"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <div class="clearfix">
        <form:form method="post" servletRelativeAction="/patadmin/postprocessing/discharge" acceptCharset="utf-8">
          <form:hidden path="patient" data-bind="valueInit: patient"/>
          <h3 class="page-header"><spring:message code="patient.personal"/></h3>
          <div class="clearfix">
            <div class="form-group col-md-3">
              <form:label path="lastname"><spring:message code="patient.lastname"/></form:label>
              <form:input path="lastname" cssClass="form-control" data-bind="valueInit: lastname"/>
            </div>
            <div class="form-group col-md-3 col-md-offset-1">
              <form:label path="firstname"><spring:message code="patient.firstname"/></form:label>
              <form:input path="firstname" cssClass="form-control" data-bind="valueInit: firstname"/>
            </div>
            <div class="form-group col-md-3 col-md-offset-1">
              <form:label path="externalId"><spring:message code="patient.externalId"/></form:label>
              <form:input path="externalId" cssClass="form-control" data-bind="valueInit: externalId"/>
            </div>
          </div>
          <div class="clearfix">
            <div class="form-group col-md-1">
              <form:label path="insurance"><spring:message code="patient.insurance"/></form:label>
              <form:input path="insurance" cssClass="form-control" data-bind="valueInit: insurance"/>
            </div>
            <div class="form-group col-md-2">
              <form:label path="birthday"><spring:message code="patient.birthday"/></form:label>
              <form:input type="date" path="birthday" cssClass="form-control" data-bind="valueInit: birthday"/>
            </div>
            <div class="form-group col-md-3 col-md-offset-1">
              <form:label path="sex"><spring:message code="patient.sex"/></form:label>
                <div class="form-control-static">
                <c:forEach items="<%= at.wrk.coceso.entity.enums.Sex.values()%>" var="sex">
                  <spring:message code="patient.sex.long.${fn:toLowerCase(sex)}" var="label"/>
                  <form:radiobutton path="sex" value="${sex}" label="${label}"/>
                </c:forEach>
              </div>
            </div>
          </div>

          <h3 class="page-header"><spring:message code="patient.treatment"/></h3>
          <div class="clearfix">
            <div class="form-group col-md-3">
              <form:label path="diagnosis"><spring:message code="patient.diagnosis"/></form:label>
              <form:textarea path="diagnosis" cssClass="form-control" data-bind="valueInit: diagnosis"/>
            </div>
            <div class="form-group col-md-3 col-md-offset-1">
              <form:label path="info"><spring:message code="patient.info"/></form:label>
              <form:textarea path="info" cssClass="form-control" data-bind="valueInit: info"/>
            </div>
            <div class="form-group col-md-1 col-md-offset-1">
              <form:label path="naca"><spring:message code="patient.naca"/></form:label>
              <form:select path="naca" cssClass="form-control" data-bind="valueInit: naca">
                <c:forEach items="<%= at.wrk.coceso.entity.enums.Naca.values()%>" var="naca">
                  <form:option value="${naca}"/>
                </c:forEach>
              </form:select>
            </div>
          </div>
          <form:button class="btn btn-success"><spring:message code="patient.discharge"/></form:button>
          <a class="btn btn-warning" href="<c:url value="/patadmin/postprocessing/view/${command.patient}"/>"><spring:message code="cancel"/></a>
        </form:form>
      </div>
    </div>
  </body>
</html>
