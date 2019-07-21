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
 * @copyright Copyright (c) 2015 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
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
          <strong><spring:message code="error"/>:</strong> <span data-bind="text: errorText"></span>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-12">
            <label><spring:message code="patient.sex"/>:&nbsp;</label>
            <div class="btn-group" id="sex">
              <button type="button" class="btn btn-default" data-bind="click: isUnknown.set, css: isUnknown.state">
                <spring:message code="patient.sex.u"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isMale.set, css: isMale.state">
                <spring:message code="patient.sex.male"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isFemale.set, css: isFemale.state">
                <spring:message code="patient.sex.female"/>
              </button>
            </div>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6" data-bind="css: lastname.formcss">
            <label for="lastname"><spring:message code="patient.lastname"/>:</label>
            <input id="lastname" type="text" class="form-control" name="lastname" data-bind="value: lastname, valueUpdate: 'input'">
          </div>

          <div class="form-group col-md-6" data-bind="css: firstname.formcss">
            <label for="firstname"><spring:message code="patient.firstname"/>:</label>
            <input id="firstname" type="text" class="form-control" name="firstname" data-bind="value: firstname, valueUpdate: 'input'" autofocus>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6" data-bind="css: externalId.formcss">
            <label for="externalId"><spring:message code="patient.externalId"/>:</label>
            <input id="externalId" type="text" class="form-control" name="externalId" data-bind="value: externalId, valueUpdate: 'input'">
          </div>

          <div class="form-group col-md-3" data-bind="css: insurance.formcss">
            <label for="insurance"><spring:message code="patient.insurance"/>:</label>
            <input id="insurance" type="text" class="form-control" name="insurance" data-bind="value: insurance, valueUpdate: 'input'">
          </div>

          <div class="form-group col-md-3" data-bind="css: birthday.formcss">
            <label for="birthday"><spring:message code="patient.birthday"/>:</label>
            <input id="birthday" type="date" class="form-control" name="birthday"
                   min="1900-01-01" max="<%=java.time.LocalDate.now().toString()%>"
                   data-bind="date: birthday, valueUpdate: 'input'">
            <label><spring:message code="patient.age"/>: <span data-bind="text: ageInYears"></span></label>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6" data-bind="css: diagnosis.formcss">
            <label for="diagnosis"><spring:message code="patient.diagnosis"/>:</label>
            <input id="diagnosis" type="text" class="form-control" name="diagnosis" data-bind="value: diagnosis, valueUpdate: 'input'">
          </div>

          <div class="form-group col-md-6" data-bind="css: ertype.formcss">
            <label for="ertype"><spring:message code="patient.ertype"/>:</label>
            <input id="ertype" type="text" class="form-control" name="ertype" data-bind="value: ertype, valueUpdate: 'input', ertype: true">
          </div>
        </div>

        <div class="form-group col-md-12" data-bind="css: info.formcss">
          <label for="info" class="sr-only"><spring:message code="patient.info"/>:</label>

          <div class="alert alert-warning" data-bind="visible: info.serverChange">
            <spring:message code="serverchange"/>
            <a href="#" title="<spring:message code="serverchange.apply"/>" data-bind="text: info.serverChange, click: info.reset"></a>
          </div>
          <textarea id="info" name="info" rows="3" class="form-control" placeholder="<spring:message code="patient.info"/>"
                    data-bind="value: info, valueUpdate: 'input'">
          </textarea>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-offset-2 col-md-10">
            <button type="button" class="btn btn-success" data-bind="click: ok">
              <spring:message code="ok"/>
            </button>
            <button type="button" class="btn btn-warning" data-bind="click: closeForm">
              <spring:message code="cancel"/>
            </button>
            <button type="submit" class="btn btn-primary" data-bind="enable: form.enable">
              <spring:message code="apply"/>
            </button>
          </div>
        </div>
      </form>
    </div>
  </body>
</html>
