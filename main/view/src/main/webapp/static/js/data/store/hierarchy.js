/**
 * CoCeSo
 * Client JS - data/store/hierarchy
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
 * @module data/store/hierarchy
 * @param {module:jquery} $
 * @param {module:knockout} ko
 */
define(["jquery", "knockout"], function($, ko) {
  "use strict";

  /**
   * @alias module:data/store/hierarchy
   * @type Object
   */
  var store = {
    models: ko.observable({}),
    get: function(id) {
      return store.models()[id] || null;
    },
    count: 0
  };

  store.list = ko.pureComputed(function() {
    return $.map(store.models(), function(i) {
      return i;
    });
  });

  store.root = ko.computed(function() {
    return ko.utils.arrayFirst(store.list(), function(container) {
      return !container.parent();
    });
  });

  return store;
});
