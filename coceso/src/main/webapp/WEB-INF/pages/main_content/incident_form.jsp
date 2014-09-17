<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML incident form window
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
    <div class="ajax_content incident_form" data-bind="droppable: {drop: assignUnitForm, tolerance: 'pointer'}">
      <form data-bind="submit: save">
        <div class="alert alert-danger" id="error" style="display: none"><strong>Saving failed</strong><br/>Try again or see <em>Debug</em> for further information.</div>

        <div class="clearfix">
          <div class="form-group col-md-8">
            <label class="sr-only"><spring:message code="label.incident.type"/>:</label>
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-default" data-bind="disable: disableTask, click: isTask.set, css: isTask.css">
                <spring:message code="label.incident.type.task"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: disableTransport, click: isTransport.set, css: isTransport.css">
                <spring:message code="label.incident.type.transport"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: disableRelocation, click: isRelocation.set, css: isRelocation.css">
                <spring:message code="label.incident.type.relocation"/>
              </button>
            </div>
          </div>

          <div class="form-group col-md-4 text-right">
            <button type="button" class="btn btn-default btn-sm" data-bind="click: blue.toggle, css: blue.css">
              <spring:message code="label.incident.blue"/>
            </button>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-8">
            <label for="bo" class=""><spring:message code="label.incident.bo"/>:</label>
            <textarea id="bo" name="bo" rows="3" class="form-control" placeholder="<spring:message code="label.incident.bo"/>" autofocus
                      data-bind="disable: disableBO, value: bo.info, valueUpdate: 'input', css: ((bo.info() === '' && !disableBO()) ? 'form-highlight ' : '') + bo.info.css()">
            </textarea>
          </div>

          <div class="form-group btn-group-vertical btn-group-sm col-md-4">
            <!-- TODO data-bind -->
            <button type="button" class="btn btn-default" disabled><spring:message code="label.addressbook"/></button>
            <button type="button" class="btn btn-default" disabled><spring:message code="label.hospitals"/></button>
            <button type="button" class="btn btn-default" disabled><spring:message code="label.favorites"/></button>
          </div>
        </div>

        <div class="form-group col-md-12">
          <label for="caller" class=""><spring:message code="label.incident.caller"/>:</label>
          <input type="text" id="caller" name="caller" class="form-control"
                 placeholder="<spring:message code="label.incident.caller"/>"
                 data-bind="value: caller, valueUpdate: 'input', css: (caller() === '' ? 'form-highlight ' : '') + caller.css()"/>
        </div>

        <div class="form-group col-md-12">
          <label for="info" class=""><spring:message code="label.incident.info"/>:</label>
          <div class="alert alert-warning" data-bind="visible: info.serverChange">
            Field has changed on server!<br>
            New Value: <a href="#" title="Apply new value" data-bind="text: info.serverChange, click: info.reset"></a>
          </div>
          <textarea id="info" name="info" rows="3" class="form-control" placeholder="<spring:message code="label.incident.info"/>"
                    data-bind="value: info, valueUpdate: 'input', css: info.css">
          </textarea>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-8">
            <label for="ao" class=""><spring:message code="label.incident.ao"/>:</label>
            <textarea id="ao" name="ao" rows="3" class="form-control"
                      placeholder="<spring:message code="label.incident.ao"/>"
                      data-bind="value: ao.info, valueUpdate: 'input', css: (highlightAO() ? 'form-highlight ' : '') + ao.info.css()">
            </textarea>
          </div>

          <div class="form-group btn-group-vertical btn-group-sm col-md-4">
            <!-- TODO data-bind -->
            <button type="button" class="btn btn-default" disabled><spring:message code="label.addressbook"/></button>
            <button type="button" class="btn btn-default" disabled><spring:message code="label.hospitals"/></button>
            <button type="button" class="btn btn-default" disabled><spring:message code="label.favorites"/></button>
          </div>
        </div>

        <div class="clearfix">
          <!-- ko if: patient -->
          <div class="col-md-7">
            <strong><spring:message code="text.patient.needs"/></strong>:
            <span data-bind="text: patient().erType"></span>
            <!-- ko if: !patient().isUnknown() -->
            (<span data-bind="text: _('label.patient.sex.' + patient().sex())"></span>)
            <!-- /ko -->
          </div>
          <div class="form-group col-md-5">
            <button type="button" class="btn btn-primary btn-sm" data-bind="click: openPatient"><spring:message code="label.patient.edit"/></button>
          </div>
          <!-- /ko -->
          <!-- ko ifnot: patient -->
          <div class="form-group col-md-5">
            <button type="button" class="btn btn-danger btn-sm" data-bind="click: openPatient, enable: id"><spring:message code="label.patient.add"/></button>
          </div>
          <!-- /ko -->
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6">
            <label for="casus" class=""><spring:message code="label.incident.casus"/>:</label>
            <input type="text" id="casus" name="casus" class="form-control"
                   placeholder="<spring:message code="label.incident.casus"/>"
                   data-bind="value: casusNr, valueUpdate: 'input', css: casusNr.css"/>
          </div>
        </div>

        <%-- Incident State --%>
        <div class="clearfix">
          <div class="form-group col-md-offset-2 col-md-10">
            <label class="sr-only"><spring:message code="label.incident.state"/>:</label>
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-default" data-bind="disable: disableNew, click: isNew.set, css: isNew.css">
                <spring:message code="label.incident.state.new"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isOpen.set, css: isOpen.css">
                <spring:message code="label.incident.state.open"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: disableDispo, click: isDispo.set, css: isDispo.css">
                <spring:message code="label.incident.state.dispo"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: disableWorking, click: isWorking.set, css: isWorking.css">
                <spring:message code="label.incident.state.working"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isDone.set, css: isDone.css">
                <spring:message code="label.incident.state.done"/>
              </button>
            </div>
          </div>
        </div>

        <%-- Assigned Units --%>
        <div class="assigned" data-bind="foreach: units">
          <div class="form-group clearfix">
            <label class="col-md-4 control-label">
              <!-- ko if: $parent.unitCount() > 1 -->
              <span class="glyphicon glyphicon-share" data-bind="click: $parent.duplicate"></span>
              <!-- /ko -->
              <span data-bind="text: unit() && unit().call"></span>
            </label>
            <div class="col-md-8 btn-group btn-group-sm nowrap">
              <button type="button" class="btn btn-default" data-bind="disable: $parent.disableAssigned, click: isAssigned.set, css: isAssigned.css">
                <spring:message code="label.task.state.assigned"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: $parent.disableBO, click: isZBO.set, css: isZBO.css">
                <spring:message code="label.task.state.zbo"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: $parent.disableBO, click: isABO.set, css: isABO.css">
                <spring:message code="label.task.state.abo"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: $parent.disableZAO, click: isZAO.set, css: isZAO.css">
                <spring:message code="label.task.state.zao"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: $parent.disableAAO, click: isAAO.set, css: isAAO.css">
                <spring:message code="label.task.state.aao"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isDetached.set, css: isDetached.css">
                <spring:message code="label.task.state.detached"/>
              </button>
            </div>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-offset-2 col-md-10">
            <button type="button" class="btn btn-success" data-bind="enable: localChange, click: ok">
              <spring:message code="label.ok"/>
            </button>
            <button type="submit" class="btn btn-primary" data-bind="enable: localChange">
              <spring:message code="label.save"/>
            </button>
            <button type="button" class="btn btn-warning" data-bind="enable: localChange, click: reset">
              Reset
            </button>
            <button type="button" class="btn btn-default" data-bind="click: duplicate">
              <spring:message code="label.incident.duplicate"/>
            </button>
          </div>
        </div>
      </form>
    </div>
  </body>
</html>
