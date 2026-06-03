/**
 * CoCeSo
 * Client JS - patadmin/store/patadmin-patients
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "data/load", "patadmin/registration/patient"],
  function(ko, load, Patient) {
    "use strict";

    var store = {
      models: ko.observable({}),
      get: function(id) {
        return store.models()[id] || null;
      }
    };

    load({
      url: "patadmin/patients",
      stomp: "/topic/patient/main/{c}",
      model: Patient,
      store: store.models
    });

    return store;
  }
);
