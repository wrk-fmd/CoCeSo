/**
 * CoCeSo
 * Client JS - ko/extenders/boolean
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
   * Helper for boolean values
   *
   * @param {ko.observable} target
   * @returns {ko.pureComputed}
   */
  ko.extenders["boolean"] = function(target) {
    var ret = ko.pureComputed({
      read: target,
      write: function(val) {
        target(!!val);
      }
    });

    ret.state = ko.pureComputed(function() {
      return this() ? "active" : "";
    }, ret);

    ret.toggle = function() {
      target(!target());
    };

    ret.set = function() {
      target(true);
    };

    ret.unset = function() {
      target(false);
    };

    return ret;
  };
});
