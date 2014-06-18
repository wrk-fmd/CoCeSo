<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--
/**
* CoCeSo
* Client HTML Incident form content
*
* Licensed under The MIT License
* For full copyright and license information, please see the LICENSE.txt
* Redistributions of files must retain the above copyright notice.
*
* @link https://sourceforge.net/projects/coceso/
* @package coceso.client.html
* @since Rev. 1
* @license MIT License (http://www.opensource.org/licenses/mit-license.php)
*
* Dependencies:
* coceso.client.css
*/
-->
<html lang="en">
<head>
    <title><spring:message code="label.log"/> / <spring:message code="label.main.form"/></title>

    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <link rel="stylesheet" href="<c:url value='/static/css/coceso.css'/>" type="text/css"/>
</head>
<body>
<div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access"/></div>

<%-- MODEL: Coceso.ViewModels.CustomLogEntry --%>
<div class="ajax_content">
    <div>
        <div class="col-md-8">
                <textarea class="form-control" data-bind="value: text, valueUpdate: 'input'" rows="3" autofocus></textarea>
        </div>
        <div class="col-md-3">
            <select class="form-control" data-bind="options: unitList, optionsText: 'call', optionsValue: 'id', value: unit, optionsCaption: '<spring:message code="label.unit.select"/>'"></select>
        </div>
        <div class="col-md-2">
            <input type="submit" class="btn btn-success" data-bind="click: ok, enable: text().trim()" value="<spring:message code="label.ok" />">
            <span class="glyphicon glyphicon-warning-sign tooltipped" style="color: #ff0000; font-size: x-large" data-bind="visible: error"
                  title="<spring:message code="label.error.2" />" data-toggle="tooltip" data-placement="top"></span>
        </div>
    </div>
</div>
</body>
</html>
