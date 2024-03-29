/**
 * CoCeSo
 * Client JS - utils/misc
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module utils/misc
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 */
define(["jquery", "knockout", "utils/conf"], function ($, ko, conf) {
  "use strict";

  /**
   * Set error handling
   *
   * @param {string} message Error msg
   * @param {string} url URL of JS file
   * @param {integer} line Line with error
   * @param {integer} col Column with error
   * @param {Object} error Error information
   * @returns {boolean}
   */
  window.onerror = function (message, url, line, col, error) {
    $.ajax({
      type: "POST",
      url: conf.get("jsonBase") + "clientLogger",
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify({
        message: message,
        url: url,
        codeLine: line,
        codeColumn: col,
        stack: error ? error.stack : null,
        logLevel: "warning"
      }),
      processData: false
    });

    return !conf.get("debug");
  };

  // Helper to read object or array from LocalStorage
  $.fn.getLocalStorage = function (key, defaultValue) {
    var json = localStorage.getItem(key);
    if (json) {
      try {
        return JSON.parse(json) || defaultValue;
      } catch (e) {
        console.log(e);
      }
    }
    return defaultValue;
  };

  ko.observable.fn.equalityComparer = ko.dependentObservable.fn.equalityComparer = function (a, b) {
    return a === b;
  };

  RegExp.escape = function (s) {
    return s.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
  };

  String.escapeChars = {
    "&": "amp",
    "<": "lt",
    ">": "gt"
  };

  String.prototype.escapeHTML = function () {
    return this.replace(/[&<>]/g, function (m) {
      return '&' + String.escapeChars[m] + ';';
    });
  };

  String.prototype.normalizeAccents = function () {
    // From https://stackoverflow.com/questions/5700636/using-javascript-to-perform-text-matches-with-without-accented-characters
    return this.normalize('NFD').replace(/[\u0300-\u036f]/g, "");
  };
});
