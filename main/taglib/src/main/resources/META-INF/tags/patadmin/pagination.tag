<%@tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@attribute name="url" required="true" rtexprvalue="true" type="String"%>
<%@attribute name="page" required="true" type="org.springframework.data.domain.Page<Object>"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML pagination tag
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
<nav>
  <ul class="pagination">
    <c:choose>
      <c:when test="${page.first}">
        <li class="disabled">
          <a href="#">&laquo;</a>
        </li>
      </c:when>
      <c:otherwise>
        <li>
          <a href="<c:url value="${url}page=${page.number - 1}"/>">&laquo;</a>
        </li>
      </c:otherwise>
    </c:choose>
    <c:forEach var="i" begin="1" end="${page.totalPages}">
      <li<c:if test="${i == page.number + 1}"> class="active"</c:if>>
        <a href="<c:url value="${url}page=${i}"/>"><c:out value="${i}"/></a>
      </li>
    </c:forEach>
    <c:choose>
      <c:when test="${page.last}">
        <li class="disabled">
          <a href="#">&raquo;</a>
        </li>
      </c:when>
      <c:otherwise>
        <li>
          <a href="<c:url value="${url}page=${page.number + 1}"/>">&raquo;</a>
        </li>
      </c:otherwise>
    </c:choose>
  </ul>
</nav>