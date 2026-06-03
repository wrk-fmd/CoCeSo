/**
 * CoCeSo
 * Client JS - patadmin_registration
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
    "data/load", "data/store/units",
    "patadmin/registration/group",
    "patadmin/store/patadmin-patients",
    "patadmin/store/patadmin-incidents",
    "patadmin/store/patadmin-units",
    "patadmin/viewmodel/registration-home",
    "bootstrap/collapse", "bootstrap/dropdown"
  ],
  function($, ko, conf, load, groupStore, Group, patients, incidents, units, registrationHomeVm) {
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

    // Existing groups display (Treatment/Triage group overview with capacity icons)
    load({
      url: "patadmin/registration/groups",
      stomp: "/topic/patadmin/groups/{c}",
      model: Group,
      store: groupStore.models
    });
    ko.applyBindings(groupStore, $("#treatment_groups")[0]);

    // New KO sections driven by WebSocket stores
    ko.applyBindings(registrationHomeVm, $("#patadmin-counts")[0]);
    ko.applyBindings(registrationHomeVm, $("#incoming-list")[0]);
    ko.applyBindings(registrationHomeVm, $("#treatment-list")[0]);

    $(".autofocus").first().focus();

    // Scroll to and briefly highlight newly added patient.
    // newPatientId is cleared after the timeout so KO removes the CSS class on re-render.
    var highlightTimeout = null;
    registrationHomeVm.treatmentPatients.subscribe(function() {
      if (!registrationHomeVm.newPatientId()) {
        return;
      }
      var $newlyAdded = $(".newly-added-patient");
      if ($newlyAdded.length > 0 && !highlightTimeout) {
        $("html, body").animate({scrollTop: $newlyAdded.offset().top - 100}, 800);
        highlightTimeout = setTimeout(function() {
          registrationHomeVm.newPatientId(null);
          highlightTimeout = null;
        }, 10000);
      }
    });
  });
});
