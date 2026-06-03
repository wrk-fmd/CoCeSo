/**
 * CoCeSo
 * Client JS - patadmin/store/patadmin-units
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "knockout", "data/load", "utils/conf"],
  function($, ko, load, conf) {
    "use strict";

    var PatadminUnit = function(data) {
      var self = this;
      data = data || {};

      this.id = data.id;
      this.call = ko.observable(data.call || "");

      this.setData = function(d) {
        self.call(d.call || "");
      };

      this.destroy = function() {};
    };

    var fetching = {};

    var store = {
      models: ko.observable({}),
      get: function(id) {
        return store.models()[id] || null;
      },
      fetchUnknown: function(id) {
        if (!id || store.models()[id] || fetching[id]) {
          return;
        }
        fetching[id] = true;
        $.ajax({
          dataType: "json",
          url: conf.get("jsonBase") + "patadmin/unit?id=" + id,
          success: function(data) {
            delete fetching[id];
            if (data && data.id) {
              var current = store.models();
              if (!current[data.id]) {
                current[data.id] = new PatadminUnit(data);
                store.models.valueHasMutated();
              }
            }
          },
          error: function() {
            delete fetching[id];
          }
        });
      }
    };

    // Initial load: Treatment + Triage units from registration/groups.
    // Subscribe to /topic/unit/main/{c} so ambulances added via fetchUnknown
    // also receive live updates. Both use EntityEventHandler<Unit> sequences.
    load({
      url: "patadmin/registration/groups",
      stomp: "/topic/unit/main/{c}",
      model: PatadminUnit,
      store: store.models
    });

    return store;
  }
);
