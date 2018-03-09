/**
 * CoCeSo
 * Client JS - ko/bindings/valueinit
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
 * @module ko/bindings/valueinit
 * @param {module:knockout} ko
 */
define(["knockout"], function(ko) {
  "use strict";

  ko.bindingHandlers.valueInit = {
    init: function(element, valueAccessor) {
      var property = valueAccessor(),
        value = element.value;

      if (!ko.isWriteableObservable(property)) {
        throw new Error('Knockout "initValue" binding expects an observable.');
      }

      property(value);
      ko.applyBindingsToNode(element, {value: valueAccessor()});
    }
  };
});
