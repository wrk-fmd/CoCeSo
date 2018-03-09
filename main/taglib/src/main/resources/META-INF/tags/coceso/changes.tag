<%@tag body-content="empty"%>
<%@attribute name="changes" required="true" rtexprvalue="true" type="at.wrk.coceso.entity.helper.Changes"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@tag trimDirectiveWhitespaces="true"%>
<%--
/**
 * CoCeSo
 * Client HTML log changes tag
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 ( http://opensource.org/licenses/GPL-3.0 )
 */
--%>

<c:if test="${not empty changes}">
  <dl class="list-narrow dl-horizontal">
    <c:forEach items="${changes.iterator()}" var="change">
      <dt><spring:message code="${changes.type}.${change.key}" text="${change.key}"/></dt>
      <dd>
        <div class="clearfix">
          <c:if test="${not empty change.oldValue}">
            <div class="pre pull-left"><c:out value="${change.oldValue}"/></div>
            <div class="glyphicon glyphicon-arrow-right small pull-left"></div>
          </c:if>
          <div class="pull-left">
            <c:choose>
              <c:when test="${not empty change.newValue}"><span class="pre"><c:out value="${change.newValue}"/></span></c:when>
              <c:otherwise>[<spring:message code="empty"/>]</c:otherwise>
            </c:choose>
          </div>
        </div>
      </dd>
    </c:forEach>
  </dl>
</c:if>
