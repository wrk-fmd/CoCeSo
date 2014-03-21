<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
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
    <title><spring:message code="label.patient"/> / <spring:message code="label.main.form"/></title>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <link rel="stylesheet" href="<c:url value='/static/css/coceso.css'/>" type="text/css"/>
</head>
<body>
<div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access"/></div>

<div class="ajax_content patient_form">
    <div class="alert alert-danger" id="error" style="display: none"><strong>Saving failed</strong><br/>Try again or see
        <em>Debug</em> for further information.
    </div>


    <div class="clearfix">
        <div class="form-group col-md-12">
            <div class="btn-group">
                <button type="button" class="btn btn-default" value="u"
                        data-bind="click: function() {sex('u') }, css: {active: sex() === 'u'}">
                    <spring:message code="label.patient.sex.u" />
                </button>
                <button type="button" class="btn btn-default" value="m"
                        data-bind="click: function() {sex('m') }, css: {active: sex() === 'm'}">
                    <spring:message code="label.patient.sex.m" />
                </button>
                <button type="button" class="btn btn-default" value="f"
                        data-bind="click: function() {sex('f') }, css: {active: sex() === 'f'}">
                    <spring:message code="label.patient.sex.f" />
                </button>
                <%--label class="btn btn-default">
                    <input type="radio" name="sex" value="u" data-bind="checked: sex"><spring:message code="label.patient.sex.u" />
                </label>
                <label class="btn btn-default">
                    <input type="radio" name="sex" value="m" data-bind="checked: sex"><spring:message code="label.patient.sex.u" />
                </label>
                <label class="btn btn-default">
                    <input type="radio" name="sex" value="f" data-bind="checked: sex"><spring:message code="label.patient.sex.u" />
                </label--%>
            </div>
        </div>
    </div>

    <div class="clearfix">
        <div class="form-group col-md-6">
            <label for="sur_name"><spring:message code="label.person.sur_name"/>:</label>
            <input id="sur_name" type="text" class="form-control" name="sur_name" data-bind="value: sur_name, valueUpdate: 'input'" autofocus>
        </div>

        <div class="form-group col-md-6">
            <label for="given_name"><spring:message code="label.person.given_name"/>:</label>
            <input id="given_name" type="text" class="form-control" name="given_name" data-bind="value: given_name, valueUpdate: 'input'">
        </div>
    </div>

    <div class="clearfix">
        <div class="form-group col-md-6">
            <label for="insurance_number"><spring:message code="label.patient.insurance_number"/>:</label>
            <input id="insurance_number" type="text" class="form-control" name="insurance_number" data-bind="value: insurance_number, valueUpdate: 'input'">
        </div>

        <div class="form-group col-md-6">
            <label for="externalID"><spring:message code="label.patient.externalID"/>:</label>
            <input id="externalID" type="text" class="form-control" name="externalID" data-bind="value: externalID, valueUpdate: 'input'">
        </div>
    </div>

    <div class="clearfix">
        <div class="form-group col-md-6">
            <label for="diagnosis"><spring:message code="label.patient.diagnosis"/>:</label>
            <input id="diagnosis" type="text" class="form-control" name="diagnosis" data-bind="value: diagnosis, valueUpdate: 'input'" disabled>
        </div>

        <div class="form-group col-md-6">
            <label for="erType"><spring:message code="label.patient.erType"/>:</label>
            <input id="erType" type="text" class="form-control" name="erType" data-bind="value: erType, valueUpdate: 'input'">
        </div>
    </div>

    <div class="form-group col-md-12">
        <label for="info" class="sr-only"><spring:message code="label.patient.info"/>:</label>

        <div class="alert alert-warning" data-bind="visible: info.serverChange">
            Field has changed on server!<br>
            New Value: <a href="#" title="Apply new value" data-bind="text: info.serverChange, click: info.reset"></a>
        </div>
        <textarea id="info" name="info" rows="3" class="form-control"
                  placeholder="<spring:message code='label.patient.info' />"
                  data-bind="value: info, css: {'form-changed': info.localChange}, valueUpdate: 'input'"></textarea>
    </div>

    <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
            <input type="button" class="btn btn-success" value="<spring:message code='label.ok' />"
                   data-bind="enable: localChange, click: ok"/>
            <input type="button" class="btn btn-primary" value="<spring:message code='label.save' />"
                   data-bind="enable: localChange, click: save"/>
            <input type="button" class="btn btn-warning" value="Reset" data-bind="enable: localChange, click: reset"/>
        </div>
    </div>
</div>
</body>
</html>
