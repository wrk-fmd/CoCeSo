<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<!--
/**
* CoCeSo
* Client HTML Unit list content
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
    <title><spring:message code="label.units" /> / <spring:message code="label.main.list" /></title>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="<c:url value="/static/css/coceso.css"/>" type="text/css" />
  </head>
  <body>
    <div class="alert alert-danger"><spring:message code="label.main.error.no_direct_access" /></div>

    <div class="ajax_content">
      <ul class="unit_list" data-bind="foreach: filtered">
        <li class="dropdown">
          <a href="#" class="unit_state dropdown-toggle" data-bind="text: call, css: stateCss, draggable: dragOptions" data-toggle="dropdown" oncontextmenu="this.click(); return false;"></a>
          <ul class="dropdown-menu">
            <li class="dropdown-header"><spring:message code="label.unit.state_set" /></li>
            <li data-bind="css: {disabled: isNEB}"><a href="#" title="<spring:message code="label.set" />: <spring:message code="label.unit.state.neb" />" data-bind="click: setNEB"><spring:message code="label.unit.state.neb" /></a></li>
            <li data-bind="css: {disabled: isEB}"><a href="#" title="<spring:message code="label.set" />: <spring:message code="label.unit.state.eb" />" data-bind="click: setEB"><spring:message code="label.unit.state.eb" /></a></li>
            <li data-bind="css: {disabled: isAD}"><a href="#" title="<spring:message code="label.set" />: <spring:message code="label.unit.state.ad" />" data-bind="click: setAD"><spring:message code="label.unit.state.ad" /></a></li>

            <li class="divider"></li>
            <li class="dropdown-header"><spring:message code="label.actions" /></li>
            <li><a href="#" title="Send Home" data-bind="click: sendHome"><spring:message code="label.unit.send_home" /></a></li>
            <li><a href="#" title="Standby" data-bind="click: standby"><spring:message code="label.incident.type.standby" /></a></li>
            <li><a href="#" title="Hold Position" data-bind="click: holdPosition"><spring:message code="label.incident.type.hold_position" /></a></li>

            <li class="divider"></li>
            <li><a href="#" title="Edit Unit" data-bind="click: openForm"><spring:message code="label.unit.edit" /></a></li>
          </ul>
        </li>
      </ul>
    </div>
  </body>
</html>
