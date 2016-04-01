<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Client HTML dashboard patient detail view
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

<c:url var="get_unit" value="?uid="/>
<c:url var="get_inc" value="?iid="/>

<h2><spring:message code="patient"/> #<c:out value="${patient.id}"/>: <c:out value="${patient.fullName}"/></h2>

<div class="clearfix">
  <div class="col-md-4">
    <dl class="list-spacing dl-horizontal">
      <dt><spring:message code="patient.id"/></dt>
      <dd class="clearfix"><c:out value="${patient.id}"/></dd>

      <dt><spring:message code="patient.lastname"/></dt>
      <dd class="clearfix"><c:out value="${patient.lastname}"/></dd>

      <dt><spring:message code="patient.firstname"/></dt>
      <dd class="clearfix"><c:out value="${patient.firstname}"/></dd>

      <dt><spring:message code="patient.externalId"/></dt>
      <dd class="clearfix"><c:out value="${patient.externalId}"/></dd>

      <dt><spring:message code="patient.sex"/></dt>
      <dd class="clearfix">
        <c:if test="${not empty patient.sex}">
          <spring:message code="patient.sex.long.${fn:toLowerCase(patient.sex)}" text="${patient.sex}"/>
        </c:if>
      </dd>

      <dt><spring:message code="patient.insurance"/></dt>
      <dd class="clearfix"><c:out value="${patient.insurance}"/></dd>

      <dt><spring:message code="patient.birthday"/></dt>
      <dd class="clearfix"><c:out value="${patient.birthday}"/></dd>

      <dt><spring:message code="patient.naca"/></dt>
      <dd class="clearfix"><c:out value="${patient.naca}"/></dd>

      <dt><spring:message code="patient.diagnosis"/></dt>
      <dd class="clearfix"><span class="pre"><c:out value="${patient.diagnosis}"/></span></dd>

      <dt><spring:message code="patient.ertype"/></dt>
      <dd class="clearfix"><c:out value="${patient.ertype}"/></dd>

      <dt><spring:message code="patient.info"/></dt>
      <dd class="clearfix"><span class="pre"><c:out value="${patient.info}"/></span></dd>

      <c:if test="${not empty patient.group}">
        <dt><spring:message code="patadmin.group"/></dt>
        <dd class="clearfix">
          <c:forEach items="${patient.group}" var="group">
            <a href="${get_unit}${group.id}"><c:out value="${group.call}"/></a>
          </c:forEach>
        </dd>
      </c:if>

      <c:if test="${not empty patient.hospital}">
        <dt><spring:message code="patadmin.hospital"/></dt>
        <dd class="clearfix">
          <c:forEach items="${patient.hospital}" var="hospital">
            <span class="pre"><c:out value="${hospital}"/></span>
          </c:forEach>
        </dd>
      </c:if>

      <c:if test="${not patient.transport and patient.done}">
        <dt><spring:message code="patient.discharged"/></dt>
        <dd class="clearfix"><spring:message code="yes"/></dd>
      </c:if>
    </dl>
  </div>

  <c:if test="${not empty patient.medinfo}">
    <div class="col-md-4">
      <c:set value="${patient.medinfo}" var="medinfo" scope="page"/>
      <h4><spring:message code="medinfo"/>: <em><c:out value="${medinfo.fullName}"/></em></h4>
      <dl class="dl-horizontal">
        <dt><spring:message code="patient.id"/></dt>
        <dd class="clearfix"><a href="<c:url value="/patadmin/triage/medinfo/${medinfo.id}"/>"><c:out value="${medinfo.id}"/></a></dd>

        <dt><spring:message code="patient.lastname"/></dt>
        <dd class="clearfix"><c:out value="${medinfo.lastname}"/></dd>

        <dt><spring:message code="patient.firstname"/></dt>
        <dd class="clearfix"><c:out value="${medinfo.firstname}"/></dd>

        <dt><spring:message code="patient.externalId"/></dt>
        <dd class="clearfix"><c:out value="${medinfo.externalId}"/></dd>

        <dt><spring:message code="patient.birthday"/></dt>
        <dd><c:out value="${medinfo.birthday}"/></dd>

        <c:forEach items="${medinfo.data}" var="entry">
          <c:if test="${not empty entry.value}">
            <dt><spring:message code="medinfo.${entry.key}" text="${entry.key}"/></dt>
            <dd class="clearfix">
              <c:choose>
                <c:when test="${entry.value['class'] == java.lang.Boolean}">
                  <spring:message code="${entry.value ? 'yes' : 'no'}"/>
                </c:when>
                <c:otherwise>
                  <span class="pre"><c:out value="${entry.value}"/></span>
                </c:otherwise>
              </c:choose>
            </dd>
          </c:if>
        </c:forEach>
      </dl>
    </div>
  </c:if>


  <c:if test="${not empty patient.incidents}">
    <div class="col-md-4">
      <h4><spring:message code="incidents"/></h4>
      <dl class="list-spacing dl-horizontal no-colon">
        <c:forEach items="${patient.incidents}" var="incident">
          <dt><t:inctitle incident="${incident}"/></dt>
          <dd>
            <a href="${get_inc}${incident.id}"><spring:message code="incident.state.${fn:toLowerCase(incident.state)}" text="${incident.state}"/></a>
          </dd>
        </c:forEach>
      </dl>
    </div>
  </c:if>
</div>

<h3><spring:message code="log"/></h3>
<table class="table table-striped table-hover">
  <thead>
    <tr>
      <th><spring:message code="log.timestamp"/></th>
      <th><spring:message code="user"/></th>
      <th><spring:message code="log.text"/></th>
      <th><spring:message code="incident"/></th>
      <th><spring:message code="unit"/></th>
      <th><spring:message code="task.state"/></th>
      <th><spring:message code="log.changes"/></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${logs}" var="log">
      <tr>
        <td><fmt:formatDate type="both" dateStyle="short" timeStyle="medium" value="${log.timestamp}"/></td>
        <td><c:out value="${log.username}"/></td>
        <td class="log_text"><t:logtext log="${log}"/></td>
        <td>
          <c:if test="${not empty log.incident}">
            <a href="${get_inc}${log.incident.id}"><t:inctitle incident="${log.incident}"/></a>
          </c:if>
        </td>
        <td>
          <c:if test="${not empty log.unit}">
            <a href="${get_unit}${log.unit.id}"><c:out value="${log.unit.call}"/></a>
          </c:if>
        </td>
        <td>
          <c:if test="${not empty log.state}">
            <spring:message code="task.state.${fn:toLowerCase(log.state)}" text="${log.state}"/>
          </c:if>
        </td>
        <td><t:changes changes="${log.changes}"/></td>
      </tr>
    </c:forEach>
  </tbody>
</table>
