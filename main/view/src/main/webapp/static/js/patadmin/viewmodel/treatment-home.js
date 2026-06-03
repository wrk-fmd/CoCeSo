/**
 * CoCeSo
 * Client JS - patadmin/viewmodel/treatment-home
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

    vm.treatmentViewUrl = conf.get("treatmentViewUrl");
    vm.treatmentEditUrl = conf.get("treatmentEditUrl");
    vm.dischargeUrl = conf.get("dischargeUrl");
    vm.transportUrl = conf.get("transportUrl");
    vm.transportedUrl = conf.get("transportedUrl");

    vm.savedPatientId = conf.get("savedPatientId") || null;
    vm.showSavedAlert = ko.observable(!!vm.savedPatientId);

    vm.treatmentPatients = ko.pureComputed(function() {
      var result = [], seen = {};
      $.each(incidents.models(), function(id, inc) {
        if (inc.type() !== "Treatment" || inc.state() === "Done" || !inc.patient()) {
          return;
        }
        var patientId = inc.patient();
        if (seen[patientId]) {
          return;
        }
        seen[patientId] = true;
        var patient = patients.get(patientId);
        if (!patient) {
          return;
        }
        var groupName = "";
        $.each(inc.units(), function(unitId) {
          var unit = units.get(parseInt(unitId));
          if (unit) {
            groupName = unit.call();
          }
        });
        result.push({patient: patient, groupName: groupName});
      });
      result.sort(function(a, b) { return b.patient.id - a.patient.id; });
      return result;
    });

    vm.treatmentCount = conf.get("treatmentCount") || 0;
    vm.transportCount = conf.get("transportCount") || 0;
    vm.countText = (conf.get("countsMsg") || "")
      .replace("|0|", vm.treatmentCount)
      .replace("|1|", vm.transportCount);

    // Returns true if the patient has an active hospital transport (bo = unit).
    vm.hasHospitalTransport = function(patientId) {
      var found = false;
      $.each(incidents.models(), function(id, inc) {
        if (inc.type() !== "Transport" || inc.state() === "Done" || inc.patient() != patientId) {
          return;
        }
        var bo = inc.bo();
        if (bo && bo["@type"] === "unit") {
          found = true;
          return false;
        }
      });
      return found;
    };

    return vm;
  }
);
