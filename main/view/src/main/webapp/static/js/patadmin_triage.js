/**
 * CoCeSo
 * Client JS - patadmin_form
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
  require(["jquery", "knockout", "data/load", "data/store/units", "patadmin/triage/group", "bootstrap/collapse", "bootstrap/dropdown"],
    function($, ko, load, store, Group) {
      "use strict";

      load({
        url: "patadmin/triage/groups.json",
        stomp: "/topic/patadmin/groups/{c}",
        model: Group,
        store: store.models
      });

      ko.applyBindings(store, $("#treatment_groups")[0]);
    }
  );
});
