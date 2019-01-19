<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%--
/**
 * CoCeSo
 * Client HTML log form window
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<html>
<head>
  <title>No direct access</title>
</head>
<body style="display: none">
<div class="ajax_content alarm_form">
  <form class="clearfix" data-bind="submit: ok">
    <div class="alert alert-danger" data-bind="visible: error">
      <strong><spring:message code="error"/>:</strong> <span data-bind="text: errorText"></span>
    </div>

    <div class="form-group col-md-12">
      <span data-bind="value: statusMessage"></span>
    </div>

    <div class="col-md-12 form-group">
      <label for="alarmText"><spring:message code="incident.alarm.text.message.content"/>:</label>
      <textarea id="alarmText" rows="5" class="form-control" placeholder="<spring:message code="incident.alarm.text.message.content"/>" autofocus
                data-bind="value: alarmMessageContent, valueUpdate: 'input'"></textarea>
    </div>

    <div class="form-group col-md-12">
      <button type="submit" class="btn btn-success" data-bind="enable: alarmMessageContent().trim()">
        <spring:message code="ok"/>
      </button>
    </div>
  </form>
</div>
</body>
</html>
