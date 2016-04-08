<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML triage form
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
    <script type="text/javascript">
      var CocesoConf = {
        jsonBase: "<c:url value="/data/"/>",
        imageBase: "<c:url value="/static/imgs/"/>",
        groupUrl: "<c:url value="/patadmin/triage/group/"/>",
        medinfoUrl: "<c:url value="/patadmin/triage/medinfo/"/>",
        langBase: "<c:url value="/static/i18n/"/>",
        language: "<spring:message code="this.languageCode"/>",
        initial: {patient: <c:out value="${command.patient}" default="null"/>, medinfo: <c:out value="${command.medinfo}" default="null"/>}
      };
    </script>
    <t:head maintitle="patadmin" title="${empty command.patient ? 'patient.add' : 'patient.edit'}" entry="patadmin_form"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <div class="clearfix">
        <div class="col-md-7">
          <form:form method="post" servletRelativeAction="/patadmin/triage/save" acceptCharset="utf-8">
            <form:hidden path="patient" data-bind="valueInit: patient"/>
            <form:hidden path="medinfo" data-bind="valueInit: medinfo"/>
            <h3 class="page-header"><spring:message code="patient.personal"/></h3>
            <div class="clearfix">
              <div class="form-group col-md-6">
                <form:label path="lastname"><spring:message code="patient.lastname"/></form:label>
                <form:input path="lastname" cssClass="form-control" data-bind="valueInit: lastname, patient: {key: 'lastname', types: types, callback: callback}"/>
              </div>
              <div class="form-group col-md-6">
                <form:label path="firstname"><spring:message code="patient.firstname"/></form:label>
                <form:input path="firstname" cssClass="form-control" data-bind="valueInit: firstname, patient: {key: 'firstname', types: types, callback: callback}"/>
              </div>
              <div class="form-group col-md-6">
                <form:label path="externalId"><spring:message code="patient.externalId"/></form:label>
                <form:input path="externalId" cssClass="form-control" autofocus="autofocus" data-bind="valueInit: externalId, patient: {key: 'externalId', types: types, callback: callback}"/>
              </div>
              <div class="form-group col-md-6 hidden">
                <form:label path="birthday"><spring:message code="patient.birthday"/></form:label>
                <form:input type="date" path="birthday" cssClass="form-control" data-bind="valueInit: birthday"/>
              </div>
<%--            </div>

            <h3 class="page-header"><spring:message code="patient.treatment"/></h3>
            <div class="clearfix">--%>
              <div class="form-group col-md-6 required">
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
              <div class="form-group col-md-6">
                <form:label path="info"><spring:message code="patient.info"/></form:label>
                <form:textarea path="info" cssClass="form-control" data-bind="valueInit: info"/>
              </div>
            </div>
            <form:button class="btn btn-success"><spring:message code="patient.save"/></form:button>
            <a href="#" class="btn btn-default" data-bind="visible: medinfo, click: openMedinfo" accesskey="m"><spring:message code="medinfo.details"/></a>
          </form:form>
        </div>

        <div class="col-md-5 clearfix">
          <div class="pull-right"><p:groups/></div>
        </div>
      </div>
    </div>

    <div id="medinfo-modal" class="modal" tabindex="-1" role="dialog" aria-hidden="true" style="display: none">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title"><spring:message code="medinfo"/></h4>
          </div>
          <div id="medinfo-modal-content" class="modal-body"></div>
        </div>
      </div>
    </div>
  </body>
</html>
