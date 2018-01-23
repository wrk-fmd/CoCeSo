<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML registration form
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<html>
  <head>
    <script type="text/javascript">
      var CocesoConf = {
        jsonBase: "<c:url value="/data/"/>",
        imageBase: "<c:url value="/static/imgs/"/>",
        groupUrl: "<c:url value="/patadmin/registration/group/"/>",
        langBase: "<c:url value="/static/i18n/"/>",
        language: "<spring:message code="this.languageCode"/>"
      };
    </script>
    <t:head maintitle="patadmin" title="${empty command.patient ? 'patient.add' : 'patient.edit'}" entry="patadmin_form"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <div class="clearfix">
        <div class="col-md-7">
          <form:form method="post" servletRelativeAction="/patadmin/registration/save" acceptCharset="utf-8">
            <form:hidden path="patient" data-bind="valueInit: patient"/>
            <h3 class="page-header"><spring:message code="patient.personal"/>
              <c:if test="${not empty command.patient}"><span class="text-danger">#<c:out value="${command.patient}"/></span></c:if>
            </h3>
            <div class="clearfix">
              <div class="form-group col-md-6 hidden">
              <form:label path="lastname"><spring:message code="patient.lastname"/></form:label>
                <form:input path="lastname" cssClass="form-control nosubmit" data-bind="valueInit: lastname, ${empty command.patient ? 'patient: {key: \\\'lastname\\\', callback: callback}' : ''}"/>
              </div>
                <div class="form-group col-md-6 hidden">
                  <form:label path="firstname"><spring:message code="patient.firstname"/></form:label>
                <form:input path="firstname" cssClass="form-control nosubmit" data-bind="valueInit: firstname, ${empty command.patient ? 'patient: {key: \\\'firstname\\\', callback: callback}' : ''}"/>
              </div>
              <div class="form-group col-md-6">
                <form:label path="externalId"><spring:message code="patient.externalId"/></form:label>
                <form:input path="externalId" cssClass="form-control autofocus" required="true"
                            data-bind="valueInit: externalId, ${empty command.patient ? 'patient: {key: \\\'externalId\\\', callback: callback}' : ''}"/>
              </div>
              <div class="form-group col-md-6 hidden">
                <form:label path="birthday"><spring:message code="patient.birthday"/></form:label>
                <form:input type="date" path="birthday" cssClass="form-control" data-bind="valueInit: birthday"/>
              </div>
                <%--            </div>

            <h3 class="page-header"><spring:message code="patient.treatment"/></h3>
            <div class="clearfix">--%>
                <div class="form-group col-md-6 required hidden">
                  <form:label path="group"><spring:message code="patadmin.group"/></form:label>
                <form:select path="group" cssClass="form-control" data-bind="valueInit: group">
                  <c:forEach items="${groups}" var="group">
                    <form:option value="${group.id}">${group.call}</form:option>
                  </c:forEach>
                </form:select>
              </div>
              <div class="form-group col-md-6 hidden">
                <form:label path="naca"><spring:message code="patient.naca"/></form:label>
                <form:select path="naca" cssClass="form-control" data-bind="valueInit: naca">
                  <c:forEach items="<%= at.wrk.coceso.entity.enums.Naca.values()%>" var="naca">
                    <form:option value="${naca}"/>
                  </c:forEach>
                </form:select>
              </div>
              <div class="form-group col-md-6 hidden">
                <form:label path="diagnosis"><spring:message code="patient.diagnosis"/></form:label>
                <form:textarea path="diagnosis" cssClass="form-control" data-bind="valueInit: diagnosis"/>
              </div>
                <div class="form-group col-md-6 hidden">
                  <form:label path="info"><spring:message code="patient.info"/></form:label>
                <form:textarea path="info" cssClass="form-control" data-bind="valueInit: info"/>
              </div>
            </div>
            <form:button class="btn btn-success"><spring:message code="patient.save"/></form:button>
          </form:form>
        </div>

        <div class="col-md-5 clearfix hidden">
          <div class="pull-right"><p:groups/></div>
        </div>
      </div>
    </div>
  </body>
</html>
