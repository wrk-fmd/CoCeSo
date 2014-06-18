<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!--
/**
* CoCeSo
* Client HTML Log content
* Copyright (c) WRK\Daniel Rohr
*
* Licensed under The MIT License
* For full copyright and license information, please see the LICENSE.txt
* Redistributions of files must retain the above copyright notice.
*
* @copyright     Copyright (c) 2013 Daniel Rohr
* @link          https://sourceforge.net/projects/coceso/
* @package       coceso.client.html
* @since         Rev. 1
* @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
*
* Dependencies:
*	coceso.client.css
*/
-->
<html>
<head>
    <title><spring:message code="label.log" /></title>

    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css" />
</head>
<body>
<div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access" /></div>

<div class="ajax_content">
    <table class="table table-striped">
        <tr>
            <th><spring:message code="label.log.timestamp" /></th>
            <th><spring:message code="label.operator" /></th>
            <th><spring:message code="label.log.text" /></th>
            <th><spring:message code="label.unit" /></th>
            <th><spring:message code="label.incident" /></th>
            <th><spring:message code="label.task.state" /></th>
        </tr>
        <!-- ko foreach: loglist -->
        <tr>
            <td data-bind="text: time"></td>
            <td data-bind="text: user.username"></td>
            <td data-bind="text: text" style="white-space: pre-line"></td>
            <td data-bind="if: unit"><a href="#" data-bind="text: unit.call, click: openUnitForm"></a></td>
            <td data-bind="if: incident"><a href="#"  data-bind="text: incident.id, click: openIncidentForm"></a></td>
            <td data-bind="text: state"></td>
        </tr>
        <!-- /ko -->
    </table>
</div>
</body>
</html>
