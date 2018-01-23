/**
 * CoCeSo
 * Client JS - ko/extenders/paginate
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "./boolean"], function(ko) {
  "use strict";

  /**
   * Create additional fields to allow pagination
   *
   * @param {ko.observable} target
   * @param {Object} options
   * @returns {ko.observable}
   */
  ko.extenders.paginate = function(target, options) {
    target.page = ko.observable(1).extend({rateLimit: 200});
    target.isFirst = ko.observable(true);
    target.isLast = ko.observable(true);
    target.total = ko.observable(1);
    target.field = ko.observable(options.field || null);
    target.asc = ko.observable(typeof options.asc === "undefined" ? true : options.asc).extend({"boolean": true});
    target.filter = ko.observable("").extend({rateLimit: 500});

    target.sort = function(field) {
      if (target.field() === field) {
        target.asc.toggle();
      } else {
        target.field(field);
        target.asc(true);
      }
    };
    target.getSort = function(field) {
      return function() {
        target.sort(field);
      };
    };
    target.icon = function(f) {
      if (target.field() === f) {
        return target.asc() ? "glyphicon-sort-by-attributes" : "glyphicon-sort-by-attributes-alt";
      }
      return "glyphicon-sort";
    };

    var update = ko.computed(function() {
      this.page();
      this.field();
      this.asc();
      this.filter();
      options.callback(this);
    }, target);

    target.prev = function() {
      if (!target.isFirst()) {
        target.page(target.page() - 1);
      }
    };

    target.next = function() {
      if (!target.isLast()) {
        target.page(target.page() + 1);
      }
    };

    return target;
  };
});
