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
        <div class="alert alert-danger" data-bind="visible: error">
          <strong><spring:message code="error"/>:</strong> <span data-bind="text: errorText"></span>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6">
            <label for="call"><spring:message code="unit.call"/>:</label>
            <input type="text" id="call" class="form-control" name="call" data-bind="value: model() && model().call" readonly/>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-6" data-bind="css: position.formcss">
            <label for="position"><spring:message code="unit.position"/>:</label>
            <a href="#" class="pull-right" data-bind="click: isHome.set"><spring:message code="unit.settohome"/></a>
            <textarea id="position" name="position" rows="3" class="form-control"
                      data-bind="value: position, valueUpdate: 'input', point: true"></textarea>
          </div>

          <div class="form-group col-md-6">
            <label for="home"><spring:message code="unit.home"/>:</label>
            <textarea id="home" name="home" rows="3" class="form-control"
                      data-bind="value: model() && model().home.info()" readonly></textarea>
          </div>
        </div>

        <div class="form-group col-md-12" data-bind="css: info.formcss">
          <label for="info" class="sr-only"><spring:message code="unit.info"/>:</label>
          <div class="alert alert-warning" data-bind="visible: info.serverChange">
            <spring:message code="serverchange"/>
            <a href="#" title="<spring:message code="serverchange.apply"/>" data-bind="text: info.serverChange, click: info.reset"></a>
          </div>
          <textarea id="info" name="info" rows="3" class="form-control" placeholder="<spring:message code="unit.info"/>"
                    data-bind="value: info, valueUpdate: 'input'">
          </textarea>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-offset-4 col-md-8">
            <label class="sr-only"><spring:message code="unit.state"/>:</label>
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-default" data-bind="click: isEB.set, css: isEB.state">
                <spring:message code="unit.state.eb"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isNEB.set, css: isNEB.state">
                <spring:message code="unit.state.neb"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isAD.set, css: isAD.state">
                <spring:message code="unit.state.ad"/>
              </button>
            </div>
          </div>
        </div>

        <div class="assigned" data-bind="foreach: incidents">
          <div class="form-group clearfix" data-bind="if: incident">
            <label class="col-md-4 control-label" data-bind="html: incident().assignedTitle"></label>
            <div class="col-md-8 btn-group btn-group-sm">
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableAssigned, click: isAssigned.set, css: isAssigned.state">
                <spring:message code="task.state.assigned"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableBO, click: isZBO.set, css: isZBO.state">
                <spring:message code="task.state.zbo"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableBO, click: isABO.set, css: isABO.state">
                <spring:message code="task.state.abo"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableZAO, click: isZAO.set, css: isZAO.state">
                <spring:message code="task.state.zao"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="disable: incident().disableAAO, click: isAAO.set, css: isAAO.state">
                <spring:message code="task.state.aao"/>
              </button>
              <button type="button" class="btn btn-default" data-bind="click: isDetached.set, css: isDetached.state">
                <spring:message code="task.state.detached"/>
              </button>
            </div>
          </div>
        </div>

        <div class="clearfix">
          <div class="form-group col-md-offset-2 col-md-10">
            <button type="button" class="btn btn-success" data-bind="enable: form.enable, click: ok">
              <spring:message code="ok"/>
            </button>
            <button type="submit" class="btn btn-primary" data-bind="enable: form.enable">
              <spring:message code="save"/>
            </button>
            <button type="button" class="btn btn-warning" data-bind="enable: form.changed, click: form.reset">
              <spring:message code="reset"/>
            </button>
          </div>
        </div>
      </form>
    </div>
  </body>
</html>
