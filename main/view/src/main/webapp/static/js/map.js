/**
 * CoCeSo
 * Client JS - map
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

require(["config"], function() {
  require(["knockout", "data/load", "data/store/incidents", "data/store/units",
    "map/models/incident", "map/models/unit", "map/viewmodel", "utils/lock", "utils/misc"],
    function(ko, load, incidentsStore, unitsStore, Incident, Unit, Map, lock) {
      "use strict";

      lock.lock();

      load({
        url: "incident/main.json",
        stomp: "/topic/incident/main/{c}",
        model: Incident,
        store: incidentsStore.models
      });
      load({
        url: "unit/main.json",
        stomp: "/topic/unit/main/{c}",
        model: Unit,
        store: unitsStore.models
      });

      var options = {};
      if (location.search) {
        var query = location.search.replace(/&+/g, '&').replace(/^\?*&*|&+$/g, '');
        if (query.length) {
          var splits = query.split('&');
          var length = splits.length;
          var v, name, value;

          for (var i = 0; i < length; i++) {
            v = splits[i].split('=');
            name = decodeURIComponent(v.shift().replace(/\+/g, '%20'));
            value = v.length ? decodeURIComponent(v.join('=').replace(/\+/g, '%20')) : null;

            if (options.hasOwnProperty(name)) {
              if (typeof options[name] === 'string') {
                options[name] = [options[name]];
              }

              options[name].push(value);
            } else {
              options[name] = value;
            }
          }
        }
      }

      //Load Map ViewModel
      var viewModel = new Map(options);
      ko.applyBindings(viewModel);
      viewModel.init();
    }
  );
});
