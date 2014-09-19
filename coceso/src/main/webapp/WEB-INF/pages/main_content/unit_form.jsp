<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%--
/**
 * CoCeSo
 * Client HTML unit form window
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
    <div class="ajax_content unit_form">
      <form data-bind="submit: save">
        <div class="alert alert-danger hidden" id="error"><strong>Saving failed</strong><br/>Try again or see <em>Debug</em> for further information.</div>

        <div class="clearfix">
          <div class="form-group col-md-6">
            <label for="call"><spring:message code="label.unit.call"/>:</label>
            <input type="text" id="call" class="form-control" name="call" data-bind="value: model() && model().call" readonly/>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6">
            <label for="position"><spring:message code="label.unit.position"/>:</label>
            <a href="#" class="pull-right" data-bind="click: isHome.set">Set to home</a>
            <textarea id="position" name="position" rows="3" class="form-control"data-bind="value: position.info, valueUpdate: 'input', css: position.info.css"></textarea>
          </div>

          <div class="form-group col-md-6">
            <label for="home"><spring:message code="label.unit.home"/>:</label>
            <textarea id="home" name="home" rows="3" class="form-control" data-bind="value: model() && model().home.info" readonly></textarea>
          </div>
        </div>

        <div class="form-group col-md-12">
          <label for="info" class="sr-only"><spring:message code="label.unit.info"/>:</label>
          <div class="alert alert-warning" data-bind="visible: info.serverChange">
            Field has changed on server!<br>
            New Value: <a href="#" title="Apply new value" data-bind="text: info.serverChange, click: info.reset"></a>
          </div>
          <textarea id="info" name="info" rows="3" class="form-control" placeholder="<spring:message code="label.unit.info"/>"
                    data-bind="value: info, valueUpdate: 'input', css: info.css">
          </textarea>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-offset-4 col-md-8">
            <label class="sr-only"><spring:message code="label.unit.state"/>:</label>
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-default" data-bind="click: isEB.set, css: isEB.css">
                <spring:message code="label.unit.state.eb"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isNEB.set, css: isNEB.css">
                <spring:message code="label.unit.state.neb"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isAD.set, css: isAD.css">
                <spring:message code="label.unit.state.ad"/>
              </button>
            </div>
          </div>
        </div>

        <div class="assigned" data-bind="foreach: incidents">
          <div class="form-group clearfix" data-bind="if: incident">
            <label class="col-md-4 control-label" data-bind="html: incident().assignedTitle"></label>
            <div class="col-md-8 btn-group btn-group-sm">
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableAssigned, click: isAssigned.set, css: isAssigned.css">
                <spring:message code="label.task.state.assigned"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableBO, click: isZBO.set, css: isZBO.css">
                <spring:message code="label.task.state.zbo"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableBO, click: isABO.set, css: isABO.css">
                <spring:message code="label.task.state.abo"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableZAO, click: isZAO.set, css: isZAO.css">
                <spring:message code="label.task.state.zao"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableAAO, click: isAAO.set, css: isAAO.css">
                <spring:message code="label.task.state.aao"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isDetached.set, css: isDetached.css">
                <spring:message code="label.task.state.detached"/>
              </button>
            </div>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-12">
            <label><spring:message code="label.crew" />:</label>
            <div class="table-responsive">
              <table class="table table-striped table-condensed">
                <thead>
                  <tr>
                    <th>
                      <spring:message code="label.person.dnr"/>
                    </th>
                    <th>
                      <spring:message code="label.person.name"/>
                    </th>
                    <th></th>
                  </tr>
                </thead>

                <!-- ko if: model -->
                <tbody data-bind="foreach: model().crew"> <!-- TODO: make it working -->
                  <tr>
                    <td data-bind="text: dNr"></td>
                    <td>
                      <strong data-bind="text: sur_name" ></strong> <span data-bind="text: given_name" ></span>
                    </td>
                    <td>
                      <span class="glyphicon glyphicon-info-sign"></span>
                    </td>
                  </tr>
                </tbody>
                <!-- /ko -->
              </table>
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
          </div>
        </div>
      </form>
    </div>
  </body>
</html>
