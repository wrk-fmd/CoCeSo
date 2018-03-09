/**
 * CoCeSo
 * Client JS - data/store/patients
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "knockout"], function($, ko) {
  "use strict";

  var store = {
    models: ko.observable({}),
    get: function(id) {
      return store.models()[id] || null;
    }
  };
  store.list = ko.computed(function() {
    return $.map(store.models(), function(v) {
      return v;
    });
  });

  return store;
});
