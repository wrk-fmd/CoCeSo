<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML navbar
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
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
    <a class="navbar-brand" href="#" onclick="return false;"><spring:message code="label.coceso"/> v1.1.0</a>
  </div>
  <div class="navbar-collapse collapse">
    <ul class="nav navbar-nav">
      <li class="${nav_home}"><a href="<c:url value="/home"/>"><spring:message code="label.nav.home"/></a></li>
      <li class="${nav_concern}"><a href="<c:url value="/edit"/>"><spring:message code="label.nav.edit_concern"/></a></li>
      <li class="${nav_person}"><a href="<c:url value="/edit/person"/>"><spring:message code="label.nav.edit_person"/></a></li>
      <li><a href="<c:url value="/main"/>"><strong><spring:message code="label.nav.main"/></strong></a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="<c:url value="/dashboard"/>"><spring:message code="label.nav.dashboard"/></a></li>
      <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.language"/> <b class="caret"></b></a>
        <ul class="dropdown-menu">
          <li>
            <a href="?lang=en">
              <img src="<c:url value="/static/imgs/blank.gif"/>" class="flag flag-us" alt="<spring:message code="label.language.en"/>"/>
              <spring:message code="label.language.en"/>
            </a>
          </li>
          <li>
            <a href="?lang=de">
              <img src="<c:url value="/static/imgs/blank.gif"/>" class="flag flag-at" alt="<spring:message code="label.language.de"/>"/>
              <spring:message code="label.language.de"/>
            </a>
          </li>
        </ul>
      </li>
      <li><a href="<c:url value="/logout"/>"><spring:message code="label.nav.logout"/></a></li>
    </ul>
  </div>
</div>
