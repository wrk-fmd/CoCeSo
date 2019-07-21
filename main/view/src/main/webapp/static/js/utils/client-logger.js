/**
 * CoCeSo
 * Client JS - utils/client-logger
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2019 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module utils/client-logger
 */
define(["jquery", "utils/conf"], function ($, conf) {
  "use strict";

  function sendLogMessage(message, logLevel) {
    $.ajax({
      type: "POST",
      url: conf.get("jsonBase") + "clientLogger",
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify({
        message: message,
        url: window.location.href,
        logLevel: logLevel
      }),
      processData: false
    });
  }

  var utilFunctions = {};

  utilFunctions.debugLog = function (message) {
    sendLogMessage(message, "debug")
  };

  utilFunctions.infoLog = function (message) {
    sendLogMessage(message, "info")
  };

  utilFunctions.warnLog = function (message) {
    sendLogMessage(message, "warning")
  };

  utilFunctions.errorLog = function (message) {
    sendLogMessage(message, "error")
  };

  return utilFunctions;
});
