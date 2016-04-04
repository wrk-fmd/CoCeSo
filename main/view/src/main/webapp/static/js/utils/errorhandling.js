/**
 * CoCeSo
 * Client JS - utils/errorhandling
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Function */

/**
 * @module {Object} utils/errorhandling
 * @param {module:knockout} ko
 * @param {module:utils/i18n} _
 */
define(["knockout", "./i18n"], function(ko, _) {
  "use strict";

  function errorText() {
    var error = this.error();

    if (error >= 1 && error <= 8) {
      return _("error." + error);
    }

    return "";
  }

  /**
   * Create error observables
   *
   * @exports module:utils/errorhandling
   * @param {Object} obj The object to add the properties to
   * @param {Integer} error Initialize with error
   * @param {Function} load Reloading function called on error
   * @returns {void}
   */
  var initErrorHandling = function(obj, error, load) {
    obj.error = ko.observable(error || false);
    obj.errorText = ko.pureComputed(errorText, obj);

    obj.saveError = function(response) {
      obj.error(response.error || 8);
      if (load instanceof Function) {
        load.call(obj);
      }
    };

    obj.httpError = function() {
      obj.error(7);
      if (load instanceof Function) {
        load.call(obj);
      }
    };
  };

  return initErrorHandling;
});
