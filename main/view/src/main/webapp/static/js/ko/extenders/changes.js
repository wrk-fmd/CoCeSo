/**
 * CoCeSo
 * Client JS - ko/extenders/changes
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module ko/extenders/changes
 * @param {module:knockout} ko
 */
define(["knockout"], function(ko) {
  "use strict";

  /**
   * Allow change detection on observable
   *
   * @param {ko.observable} target
   * @param {Object} options
   * @returns {ko.observable}
   */
  ko.extenders.observeChanges = function(target, options) {
    target.server = ko.observable(options.server);
    target.orig = (typeof options.orig !== "undefined") ? ko.observable(options.orig) : ko.observable(ko.utils.unwrapObservable(target.server()));

    target.changed = ko.pureComputed(function() {
      return (typeof this.orig() !== "undefined" && this() !== this.orig());
    }, target);

    if (options.keepChanges) {
      target.serverChange = ko.pureComputed(function() {
        var server = ko.utils.unwrapObservable(this.server()), orig = this.orig();
        if (typeof server !== "undefined" && server !== orig) {
          return server;
        }
        return null;
      }, target);
    }

    target.valid = options.validate instanceof Function ? ko.pureComputed(options.validate, target) : function() {
      return true;
    };

    target.formcss = ko.pureComputed(function() {
      if (!this.valid()) {
        return "has-error";
      }
      if (this.changed()) {
        return "has-change";
      }
      return "";
    }, target);

    target.reset = function() {
      var server = ko.utils.unwrapObservable(target.server()), orig = target.orig();
      if (options.keepChanges && typeof server !== "undefined" && server !== orig) {
        target(server);
        target.orig(server);
      } else if (target.changed()) {
        target(orig);
      }
    };

    target.tmp = ko.computed(function() {
      var server = ko.utils.unwrapObservable(this.server()), orig = this.orig();
      if (typeof server !== "undefined" && server !== orig) {
        if (!options.keepChanges || !this.changed() || server === this()) {
          this.orig(server);
          this(server);
        }
      }
    }, target);

    return target;
  };
});
