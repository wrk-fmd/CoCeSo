<%@tag body-content="empty"%>
<%@attribute name="maintitle" required="false" type="String" %>
<%@attribute name="title" required="false" type="String"%>
<%@attribute name="entry" required="false" type="String"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@tag trimDirectiveWhitespaces="true"%>
<%--
/**
 * CoCeSo
 * Client HTML load resources tag
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

<c:set var="suffix" value="${cocesoConfig.debug ? '' : '-dist'}"/>

<title>
  <spring:message code="${empty maintitle ? 'coceso' : maintitle}"/>
  <c:if test="${not empty title}"> - <spring:message code="${title}"/></c:if>
  </title>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon"/>
<c:choose>
  <c:when test="${empty suffix}">
    <link rel="stylesheet/less" href="<c:url value="/static/less/coceso.less"/>" type="text/css"/>
    <script src="<c:url value="/static/js/assets/less/less.js"/>" type="text/javascript"></script>
  </c:when>
  <c:otherwise>
    <link rel="stylesheet" href="<c:url value="/static/css-dist/coceso.css"/>" type="text/css"/>
  </c:otherwise>
</c:choose>

<c:if test="${not empty entry}">
  <script type="text/javascript"
          data-main="<c:url value="/static/js${suffix}/${entry}"/>"
          src="<c:url value="/static/js${suffix}/assets/requirejs/require.js"/>">
  </script>
</c:if>
