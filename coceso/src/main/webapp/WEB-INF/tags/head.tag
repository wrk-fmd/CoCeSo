<%@tag body-content="empty"%>
<%@attribute name="nojs" required="false" type="Boolean"%>
<%@attribute name="jquery" required="false" type="String"%>
<%@attribute name="knockout" required="false" type="String"%>
<%@attribute name="js" required="false" type="String"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
<c:set var="suffix" value=".min"/>

<meta charset="utf-8"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<link rel="icon" href="<c:url value="/static/favicon.ico"/>" type="image/x-icon">
<link rel="stylesheet" href="<c:url value="/static/css/style${suffix}.css"/>" type="text/css"/>

<c:if test="${empty nojs || !nojs}">
  <%-- jQuery --%>
  <script src="<c:url value="/static/js/assets/jquery${suffix}.js"/>" type="text/javascript"></script>
  <c:if test="${not empty jquery}">
    <c:forEach items="${fn:split(jquery, ',')}" var="file">
      <script src="<c:url value="/static/js/assets/jquery.${fn:trim(file)}${suffix}.js"/>" type="text/javascript"></script>
    </c:forEach>
  </c:if>

  <%-- Knockout --%>
  <c:if test="${not empty knockout || not empty js}">
    <script src="<c:url value="/static/js/assets/knockout${suffix}.js"/>" type="text/javascript"></script>
    <c:if test="${not empty knockout}">
      <c:forEach items="${fn:split(knockout, ',')}" var="file">
        <script src="<c:url value="/static/js/assets/knockout.${fn:trim(file)}${suffix}.js"/>" type="text/javascript"></script>
      </c:forEach>
    </c:if>
  </c:if>

  <%-- Bootstrap --%>
  <script src="<c:url value="/static/js/assets/bootstrap${suffix}.js"/>" type="text/javascript"></script>

  <%-- Coceso JS --%>
  <c:if test="${not empty js}">
    <script src="<c:url value="/static/js/knockout.extensions${suffix}.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/static/js/coceso${suffix}.js"/>" type="text/javascript"></script>
    <c:forEach items="${fn:split(js, ',')}" var="file">
      <script src="<c:url value="/static/js/${fn:trim(file)}${suffix}.js"/>" type="text/javascript"></script>
    </c:forEach>
  </c:if>
</c:if>
