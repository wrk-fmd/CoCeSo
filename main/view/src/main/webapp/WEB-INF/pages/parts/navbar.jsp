<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%--
/**
 * CoCeSo
 * Client HTML navbar
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
<div class="navbar navbar-inverse" role="navigation">
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="#" onclick="return false;"><spring:message code="coceso"/>&nbsp;<spring:message code="coceso.version"/></a>
  </div>
  <div class="navbar-collapse collapse">
    <ul class="nav navbar-nav">
      <li class="${nav_home}"><a href="<c:url value="/home"/>"><spring:message code="nav.home"/></a></li>
        <sec:authorize access="@auth.hasAccessLevel('Edit')">
        <li class="${nav_concern}"><a href="<c:url value="/edit"/>"><spring:message code="nav.edit_concern"/></a></li>
        <li class="${nav_users}"><a href="<c:url value="/edit/user"/>"><spring:message code="nav.edit_users"/></a></li>
        </sec:authorize>
        <sec:authorize access="@auth.hasAccessLevel('Main')">
        <li><a href="<c:url value="/main"/>"><strong><spring:message code="nav.main"/></strong></a></li>
            </sec:authorize>
        <li><a href="<c:url value="/patadmin"/>"><spring:message code="patadmin"/></a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <sec:authorize access="@auth.hasAccessLevel('Dashboard')">
        <li><a href="<c:url value="/dashboard"/>"><spring:message code="nav.dashboard"/></a></li>
        </sec:authorize>
      <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="language"/> <b class="caret"></b></a>
        <ul class="dropdown-menu">
          <li>
            <a href="?lang=en">
              <span class="flag flag-us"></span>
              <spring:message code="language.en"/>
            </a>
          </li>
          <li>
            <a href="?lang=de">
              <span class="flag flag-at"></span>
              <spring:message code="language.de"/>
            </a>
          </li>
        </ul>
      </li>
      <li><a href="<c:url value="/logout"/>"><spring:message code="nav.logout"/></a></li>
    </ul>
  </div>
</div>
