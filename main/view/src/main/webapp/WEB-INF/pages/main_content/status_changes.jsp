<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
  <title>No direct access</title>
</head>
<body style="display: none">
<div class="ajax_content">
  <table class="table table-striped">
    <tr>
      <th><spring:message code="log.timestamp"/></th>
      <th><spring:message code="unit"/></th>
      <th><spring:message code="incident"/></th>
      <th><spring:message code="task.state"/></th>
    </tr>
    <!-- ko foreach: list -->
    <tr>
      <!-- ko with: latestState -->
      <td data-bind="text: stateChangedAt.formatted"></td>
      <td>
        <!-- ko if: unit -->
        <!-- ko with: unit -->
        <a href="#" data-bind="text: call, click: openForm"></a>
        <!-- /ko -->
        <!-- /ko -->
      </td>
      <td data-bind="if: incident">
        <!-- ko if: incident -->
        <!-- ko with: incident -->
        <a href="#" data-bind="html: assignedTitle, click: openForm"></a>
        <!-- /ko -->
        <!-- /ko -->
      </td>
      <td data-bind="text: localizedTaskState"></td>
      <!-- /ko -->
    </tr>
    <!-- /ko -->
  </table>
</div>
</body>
</html>
