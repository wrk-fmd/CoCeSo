/**
 * CoCeSo
 * Client JS - patadmin/viewmodel/info-list
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define([
    "jquery", "knockout", "utils/conf",
    "patadmin/store/patadmin-patients",
    "patadmin/store/patadmin-incidents",
    "patadmin/store/patadmin-units"
  ],
  function($, ko, conf, patients, incidents, units) {
    "use strict";

    var vm = {};

    vm.infoViewUrl = conf.get("infoViewUrl");

    // All active patients with group (from Treatment inc) and hospital (from Transport inc ao).
    // Note: only in-treatment and in-transport patients are in the store.
    vm.infoPatients = ko.pureComputed(function() {
      var result = [];
      $.each(patients.models(), function(id, patient) {
        var groupName = "";
        var hospital = "";
        $.each(incidents.models(), function(iid, inc) {
          if (inc.patient() != patient.id) {
            return;
          }
          if (inc.type() === "Treatment" && inc.state() !== "Done") {
            $.each(inc.units(), function(unitId) {
              var unit = units.get(parseInt(unitId));
              if (unit) {
                groupName = unit.call();
              }
            });
          }
          if (inc.type() === "Transport" && inc.state() !== "Done") {
            var ao = inc.ao();
            if (ao) {
              hospital = ao.info || "";
            }
          }
        });
        result.push({patient: patient, groupName: groupName, hospital: hospital});
      });
      result.sort(function(a, b) { return b.patient.id - a.patient.id; });
      return result;
    });

    return vm;
  }
);
