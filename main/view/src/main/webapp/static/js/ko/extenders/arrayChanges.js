/**
 * CoCeSo
 * Client JS - ko/extenders/form
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
   * Allow change detection on array
   *
   * @param {ko.observableArray} target
   * @param {Object} options
   * @returns {ko.observableArray}
   */
  ko.extenders.arrayChanges = function(target, options) {
    //Include those for matching interface with observeChanges
    target.orig = ko.observable(null);
    target.serverChange = ko.observable(null);

    target.changed = ko.pureComputed(function() {
      var items = ko.utils.unwrapObservable(this);
      if (!items instanceof Array) {
        return false;
      }
      return !!ko.utils.arrayFirst(items, function(item) {
        return ko.utils.unwrapObservable(item.changed);
      });
    }, target);

    return target;
  };
});
