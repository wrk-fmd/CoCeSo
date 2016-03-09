/**
 * CoCeSo
 * Client JS - data/store/units
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
 * @module data/store/units
 * @param {module:jquery} $
 * @param {module:knockout} ko
 */
define(["jquery", "knockout"], function($, ko) {
  "use strict";

  /**
   * @alias module:data/store/units
   * @type Object
   */
  var store = {
    models: ko.observable({}),
    get: function(id) {
      return store.models()[id] || null;
    },
    filter: null
  };

  store.list = ko.computed(function() {
    var filter = ko.utils.unwrapObservable(store.filter);
    return $.map(store.models(), function(u) {
      return (!filter || !u.section || u.section === filter) ? u : null;
    });
  });

  store.unfiltered = ko.pureComputed(function() {
    return $.map(store.models(), function(u) {
      return u;
    });
  });

  return store;
});
