<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@taglib uri="coceso" prefix="t" %>
<%--
/**
 * CoCeSo
 * QR code generation page
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2020 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<html>
<head>
  <script type="text/javascript">
    var CocesoConf = {
      jsonBase: "<c:url value="/"/>",
      langBase: "<c:url value="/static/i18n/"/>",
      staticBase: "<c:url value="/static/"/>",
      language: "<spring:message code="this.languageCode"/>",
      plugins: ${cocesoConfig.jsPlugins},
      activeConcernId: ${concernId},
      publicGeobrokerUrl: "${publicGeobrokerUrl}"
    };
  </script>
  <t:head title="concern.qr.codes" entry="qr_codes"/>
  <style>
    .qr {
      margin: 1cm;
      display: block;
      float: right;
    }

    .link-paragraph {
      width: 100%;
    }

    .line-wrap {
      white-space: pre-wrap; /* css-3 */
      white-space: -moz-pre-wrap; /* Mozilla, since 1999 */
      word-wrap: break-word; /* Internet Explorer 5.5+ */
    }

    .info-template {
      display: none;
    }

    @media print {
      .noprint {
        display: none;
      }

      .unit {
        margin: 0 1.5cm; /* cannot insert vertical space after page-break*/
        page-break-after: always;
      }

      .unit::before {
        content: " ";
        height: 1.5cm;
        display: block;
      }

      @page {
        margin: 0;
      }
    }
  </style>
</head>
<body>
<div class="unit-selection noprint">
  <div class="form-group">
    <label for="unit_call_contains">Unit Name ('contains')</label>
    <input type="text" id="unit_call_contains" class="form-control" data-bind="value: call, valueUpdate: 'input'"/>

    <label for="label_to_print">Label (optional)</label>
    <input type="text" id="label_to_print" class="form-control" data-bind="value: label, valueUpdate: 'input'"/>
  </div>

</div>
<div class="filtered-units">
  <!-- ko foreach: filteredUnits -->
  <div class="unit" data-bind="descendantsComplete: paintQrCode()">
    <div class="qr" data-bind="attr: { id: qrId }"></div>
    <h1 data-bind="text: unitHeader"></h1>
    <p class="link-paragraph">
      <a class="line-wrap" data-bind="text: generatedUrl, attr: { href: generatedUrl }"></a>
    </p>
    <div class="info-content" data-bind="html: infoContent"></div>
  </div>
  <!-- /ko -->
</div>
<div id="info-template" class="info-template">Loading info sheet &#8230;</div>
</body>
</html>
