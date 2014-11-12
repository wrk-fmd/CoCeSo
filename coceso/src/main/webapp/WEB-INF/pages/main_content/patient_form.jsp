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
      <form data-bind="submit: save">
        <div class="alert alert-danger" data-bind="visible: error">
          <strong><spring:message code="label.error"/>:</strong> <span data-bind="text: errorText"></span>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-12">
            <label for="sex"><spring:message code="label.patient.sex"/>:&nbsp;</label>
            <div class="btn-group" id="sex">
              <button type="button" class="btn btn-default" data-bind="click: isUnknown.set, css: isUnknown.state">
                <spring:message code="label.patient.sex.u"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isMale.set, css: isMale.state">
                <spring:message code="label.patient.sex.m"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isFemale.set, css: isFemale.state">
                <spring:message code="label.patient.sex.f"/>
              </button>
            </div>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6" data-bind="css: sur_name.formcss">
            <label for="sur_name"><spring:message code="label.person.sur_name"/>:</label>
            <input id="sur_name" type="text" class="form-control" name="sur_name" data-bind="value: sur_name, valueUpdate: 'input'" autofocus>
          </div>

          <div class="form-group col-md-6" data-bind="css: given_name.formcss">
            <label for="given_name"><spring:message code="label.person.given_name"/>:</label>
            <input id="given_name" type="text" class="form-control" name="given_name" data-bind="value: given_name, valueUpdate: 'input'">
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6" data-bind="css: insurance_number.formcss">
            <label for="insurance_number"><spring:message code="label.patient.insurance_number"/>:</label>
            <%-- TODO Change type to date, bugfix of sending empty data --%>
            <input id="insurance_number" type="text" class="form-control" name="insurance_number" data-bind="value: insurance_number, valueUpdate: 'input'">
          </div>

          <div class="form-group col-md-6" data-bind="css: externalID.formcss">
            <label for="externalID"><spring:message code="label.patient.externalID"/>:</label>
            <input id="externalID" type="text" class="form-control" name="externalID" data-bind="value: externalID, valueUpdate: 'input'">
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6" data-bind="css: diagnosis.formcss">
            <label for="diagnosis"><spring:message code="label.patient.diagnosis"/>:</label>
            <input id="diagnosis" type="text" class="form-control" name="diagnosis" data-bind="value: diagnosis, valueUpdate: 'input'" disabled>
          </div>

          <div class="form-group col-md-6" data-bind="css: erType.formcss">
            <label for="erType"><spring:message code="label.patient.erType"/>:</label>
            <input id="erType" type="text" class="form-control" name="erType" data-bind="value: erType, valueUpdate: 'input'">
          </div>
        </div>

        <div class="form-group col-md-12" data-bind="css: info.formcss">
          <label for="info" class="sr-only"><spring:message code="label.patient.info"/>:</label>

          <div class="alert alert-warning" data-bind="visible: info.serverChange">
            <spring:message code="label.serverchange"/>
            <a href="#" title="<spring:message code="label.serverchange.apply"/>" data-bind="text: info.serverChange, click: info.reset"></a>
          </div>
          <textarea id="info" name="info" rows="3" class="form-control" placeholder="<spring:message code="label.patient.info"/>"
                    data-bind="value: info, valueUpdate: 'input'">
          </textarea>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-offset-2 col-md-10">
            <button type="button" class="btn btn-success" data-bind="enable: form.enable, click: ok">
              <spring:message code="label.ok"/>
            </button>
            <button type="sumbit" class="btn btn-primary" data-bind="enable: form.enable">
              <spring:message code="label.save"/>
            </button>
            <button type="button" class="btn btn-warning" data-bind="enable: form.changed, click: form.reset">
              <spring:message code="label.reset"/>
            </button>
          </div>
        </div>
      </form>
    </div>
  </body>
</html>
