<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="coceso" prefix="t"%>
<%--
/**
 * CoCeSo
 * Patadmin HTML group settings
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
<html>
  <head>
    <t:head title="patadmin"/>
  </head>
  <body>
    <div class="container">
      <c:set value="active" var="nav_settings"/>
      <%@include file="navbar.jsp"%>

      <form:form method="post" servletRelativeAction="/patadmin/settings" acceptCharset="utf-8" commandName="form">
        <div class="table-responsive">
          <table class="table table-full table-striped table-condensed">
            <tr>
              <th>Id</th>
              <th>Name</th>
              <th>Image</th>
              <th>Capacity</th>
              <th>Active</th>
            </tr>
            <c:forEach items="${form.groups}" var="group" varStatus="status">
              <tr>
                <td>
                  <form:hidden path="groups[${status.index}].id"/>
                  <c:out value="${group.id}"/>
                </td>
                <td><c:out value="${group.call}"/></td>
                <td>
                  <form:select path="groups[${status.index}].imgsrc" cssClass="form-control">
                    <form:option value="">---</form:option>
                    <form:options items="${images}" itemLabel="name" itemValue="name"/>
                  </form:select>
                </td>
                <td><form:input path="groups[${status.index}].capacity" type="number" min="0" cssClass="form-control number-2"/></td>
                <td><form:checkbox path="groups[${status.index}].active"/></td>
              </tr>
            </c:forEach>
          </table>
        </div>
        <form:button class="btn btn-success">Speichern</form:button>
      </form:form>
    </div>
  </body>
</html>
