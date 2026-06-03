<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="coceso" prefix="t"%>
<%@taglib uri="patadmin" prefix="p"%>
<html>
  <head>
    <t:head maintitle="patadmin" title="patient.transport" entry="navbar"/>
  </head>
  <body>
    <div class="container">
      <%@include file="../navbar.jsp"%>

      <h2><spring:message code="patient.transport"/></h2>

      <div class="table-responsive">
        <table class="table table-striped table-condensed table-full">
          <tr>
            <th><spring:message code="patient.id"/></th>
            <th><spring:message code="patient.externalId"/></th>
            <th><spring:message code="patient.lastname"/></th>
            <th><spring:message code="patient.firstname"/></th>
            <th><spring:message code="patadmin.group"/></th>
            <th><spring:message code="patient.ambulance"/></th>
            <th><spring:message code="incident.casus.short"/></th>
            <th><spring:message code="unit"/></th>
            <th><spring:message code="unit.state"/></th>
            <th><spring:message code="patadmin.hospital"/></th>
          </tr>
          <c:forEach items="${patients}" var="patient">
            <tr>
              <td><c:out value="${patient.id}"/></td>
              <td><c:out value="${patient.externalId}"/></td>
              <td><c:out value="${patient.lastname}"/></td>
              <td><c:out value="${patient.firstname}"/></td>
              <td>
                <c:if test="${not empty patient.group}">
                  <c:forEach items="${patient.group}" var="group">
                    <c:out value="${group.call}"/>
                  </c:forEach>
                </c:if>
              </td>
              <td>
                <c:if test="${not empty patient.transportInfo}">
                  <c:forEach items="${patient.transportInfo}" var="info">
                    <c:out value="${info}"/>
                  </c:forEach>
                </c:if>
              </td>
              <td>
                <c:if test="${not empty patient.casusNr}">
                  <c:forEach items="${patient.casusNr}" var="nr">
                    <c:out value="${nr}"/>
                  </c:forEach>
                </c:if>
              </td>
              <td>
                <c:if test="${not empty patient.transportUnits}">
                  <c:forEach items="${patient.transportUnits}" var="entry">
                    <c:out value="${entry.key.call}"/>
                  </c:forEach>
                </c:if>
              </td>
              <td>
                <c:if test="${not empty patient.transportUnits}">
                  <c:forEach items="${patient.transportUnits}" var="entry">
                    <c:out value="${entry.value}"/>
                  </c:forEach>
                </c:if>
              </td>
              <td>
                <c:if test="${not empty patient.hospital}">
                  <c:forEach items="${patient.hospital}" var="hospital">
                    <c:out value="${hospital}"/>
                  </c:forEach>
                </c:if>
              </td>
            </tr>
          </c:forEach>
        </table>
      </div>
    </div>
  </body>
</html>
