<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML registration home
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
    <t:head maintitle="patadmin" title="patadmin.registration" entry="patadmin_registration"/>
  </head>
  <body>
    <div class="container">
      <%@include file="navbar.jsp"%>

      <h2><spring:message code="patadmin.registration"/></h2>
      <p>
        <spring:message code="patadmin.counts" arguments="${treatmentCount},${transportCount}"/>
      </p>
      <p>
        <a href="<c:url value="/patadmin/registration/add"/>" class="btn btn-default autofocus">
          <spring:message code="patient.add"/>
        </a>
      </p>

      <h3><spring:message code="patadmin.groups"/></h3>
      <p:groups/>

      <h3><spring:message code="patadmin.incoming"/></h3>
      <c:if test="${not empty incoming}">
        <p:incoming incidents="${incoming}"/>
      </c:if>
      <c:if test="${empty incoming}">
        <p>
          <spring:message code="patadmin.incoming.no.transports"/>
        </p>
      </c:if>

      <h3><spring:message code="patadmin.intreatment"/></h3>
      <c:if test="${not empty treatment}">
        <p:patients patients="${treatment}"/>
      </c:if>
      <c:if test="${empty treatment}">
        <p>
          <spring:message code="patadmin.intreatment.no.patients"/>
        </p>
      </c:if>
    </div>
  </body>
</html>
