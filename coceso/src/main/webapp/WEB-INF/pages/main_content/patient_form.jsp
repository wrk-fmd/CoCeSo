<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML patient form window
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<html>
  <head>
    <title>No direct access</title>
  </head>
  <body style="display: none">
    <div class="ajax_content patient_form">
      <div class="alert alert-danger" id="error" style="display: none"><strong>Saving failed</strong><br/>Try again or see
        <em>Debug</em> for further information.
      </div>


      <div class="clearfix">
        <div class="form-group col-md-12">
          <label for="sex"><spring:message code="label.patient.sex"/>:&nbsp;</label>
          <div class="btn-group" id="sex">
            <button class="btn btn-default" data-bind="click: setSexUnknown, css: {active: isUnknown}">
              <spring:message code="label.patient.sex.u"/>
            </button>
            <button class="btn btn-default" data-bind="click: setSexMale, css: {active: isMale}">
              <spring:message code="label.patient.sex.m"/>
            </button>
            <button class="btn btn-default" data-bind="click: setSexFemale, css: {active: isFemale}">
              <spring:message code="label.patient.sex.f"/>
            </button>
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
          <%-- TODO Change type to date, bugfix of sending empty data --%>
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
                  placeholder="<spring:message code="label.patient.info"/>"
                  data-bind="value: info, css: {'form-changed': info.localChange}, valueUpdate: 'input'"></textarea>
      </div>

      <div class="clearfix">
        <div class="form-group col-md-offset-2 col-md-10">
          <input type="button" class="btn btn-success" value="<spring:message code="label.ok"/>"
                 data-bind="enable: localChange, click: ok"/>
          <input type="button" class="btn btn-primary" value="<spring:message code="label.save"/>"
                 data-bind="enable: localChange, click: save"/>
          <input type="button" class="btn btn-warning" value="Reset" data-bind="enable: localChange, click: reset"/>
        </div>
      </div>
    </div>
  </body>
</html>
