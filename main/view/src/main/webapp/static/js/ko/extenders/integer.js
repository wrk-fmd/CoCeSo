/**
 * CoCeSo
 * Client JS - ko/extenders/integer
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout"], function(ko) {
  "use strict";

  /**
   * Force value to be an integer
   *
   * @param {ko.observable} target
   * @param {Integer} length
   * @returns {ko.pureComputed}
   */
  ko.extenders.integer = function(target, length) {
    var ret = ko.pureComputed({
      read: target,
      write: function(newValue) {
        var current = target(),
            newValueInt = (newValue && !isNaN(newValue)) ? parseInt(newValue) : 0;

        if (newValue === null || newValue === "") {
          newValueInt = null;
        } else if (length) {
          newValueInt = newValueInt.toString();
          while (newValueInt.length < length) {
            newValueInt = "0" + newValueInt;
          }
        }

        if (newValueInt !== current) {
          target(newValueInt);
        } else if (newValue !== current) {
          target.notifySubscribers(newValueInt);
        }
      }
    }).extend({notify: 'always'});

    ret.valueHasMutated = target.valueHasMutated;
    ret(target());

    return ret;
  };
});
