<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@page import="at.wrk.coceso.entity.enums.AccessLevel" %>
<%--
/**
 * CoCeSo
 * Patadmin HTML navbar
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2019 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>
<div class="navbar navbar-default" role="navigation">
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="<c:url value="/patadmin"/>"><spring:message code="patadmin"/></a>
  </div>
  <div class="navbar-collapse collapse">
    <ul class="nav navbar-nav">
      <c:if test="${accessLevels.contains(AccessLevel.PatadminRegistration)}">
        <li class="${viewType eq 'registration' ? 'active' : ''}">
          <a href="<c:url value="/patadmin/registration"/>"><spring:message code="patadmin.registration"/></a>
        </li>
      </c:if>
      <c:if test="${accessLevels.contains(AccessLevel.PatadminTreatment)}">
        <li class="${viewType eq 'treatment' ? 'active' : ''}">
          <a href="<c:url value="/patadmin/treatment"/>"><spring:message code="patadmin.treatment"/></a>
        </li>
      </c:if>
      <c:if test="${accessLevels.contains(AccessLevel.PatadminPostprocessing)}">
        <li class="${viewType eq 'postprocessing' ? 'active' : ''}">
          <a href="<c:url value="/patadmin/postprocessing"/>"><spring:message code="patadmin.postprocessing"/></a>
        </li>
      </c:if>
      <c:if test="${accessLevels.contains(AccessLevel.PatadminInfo)}">
        <li class="${viewType eq 'info' ? 'active' : ''}">
          <a href="<c:url value="/patadmin/info"/>"><spring:message code="patadmin.info"/></a>
        </li>
      </c:if>
      <c:if test="${accessLevels.contains(AccessLevel.PatadminSettings)}">
        <li class="${viewType eq 'settings' ? 'active' : ''}">
          <a href="<c:url value="/patadmin/settings"/>"><spring:message code="patadmin.settings"/></a>
        </li>
      </c:if>

      <li>
        <a href="<c:url value="/home"/>"><spring:message code="exit"/></a>
      </li>
    </ul>

    <c:if test="${showSearch}">
      <form action="<c:url value="/patadmin/${viewType}/search"/>" class="navbar-form navbar-right" method="get">
        <div class="form-group">
          <input type="text"
                 name="q"
                 class="form-control"
                 placeholder="<spring:message code="patadmin.search"/>"
                 accesskey="s"
                 id="search"
                 value="<c:out value="${search}"/>"
          />
        </div>
      </form>
    </c:if>
  </div>
</div>
