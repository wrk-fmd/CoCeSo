/**
 * CoCeSo
 * Client JS - ko/extenders/form
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "./arrayChanges"], function(ko) {
  "use strict";

  /**
   * Form related methods
   *
   * @param {ko.observableArray} target
   * @param {Object} options
   * @returns {ko.observableArray}
   */
  ko.extenders.form = function(target) {
    //Enable change detection
    target.extend({arrayChanges: true});

    target.saving = ko.observable(false);

    target.valid = ko.pureComputed(function() {
      var items = ko.utils.unwrapObservable(this);
      if (!items instanceof Array) {
        return false;
      }

      return !ko.utils.arrayFirst(items, function(item) {
        return typeof item.valid === "undefined" ? false : !ko.utils.unwrapObservable(item.valid);
      });
    }, target);

    target.enable = ko.pureComputed(function() {
      return (!this.saving() && this.changed() && this.valid());
    }, target);

    target.reset = function() {
      var items = ko.utils.unwrapObservable(target);
      if (!items instanceof Array) {
        return;
      }
      ko.utils.arrayForEach(items, function(item) {
        if (item.reset instanceof Function) {
          item.reset();
        }
      });
    };

    return target;
  };
});
