/**
 * CoCeSo
 * Client JS - patadmin_treatment
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
    "patadmin/viewmodel/treatment-home",
    "bootstrap/collapse", "bootstrap/dropdown"
  ],
  function($, ko, conf, patients, incidents, units, treatmentHomeVm) {
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

    ko.applyBindings(treatmentHomeVm, $("#save-alert")[0]);
    ko.applyBindings(treatmentHomeVm, $("#patadmin-counts")[0]);
    ko.applyBindings(treatmentHomeVm, $("#treatment-list")[0]);

    $(".autofocus").first().focus();
  });
});
