/**
 * CoCeSo
 * Client JS - patadmin_transport_list
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

require(["config"], function() {
  require([
    "jquery", "knockout", "utils/conf",
    "patadmin/store/patadmin-patients",
    "patadmin/store/patadmin-incidents",
    "patadmin/store/patadmin-units",
    "patadmin/viewmodel/transport-list",
    "bootstrap/collapse", "bootstrap/dropdown"
  ],
  function($, ko, conf, patients, incidents, units, transportListVm) {
    "use strict";

    var connectionError = ko.observable(false);
    conf.set("error", connectionError);
    ko.applyBindings({
      wsIconClass: ko.pureComputed(function() {
        return {
          "glyphicon-signal": !connectionError(),
          "glyphicon-exclamation-sign": connectionError(),
          "text-success": !connectionError(),
          "text-danger": connectionError()
        };
      })
    }, $("#patadmin-navbar-status")[0]);

    ko.applyBindings(transportListVm, $("#transport-list")[0]);
  });
});
