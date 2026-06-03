/**
 * CoCeSo
 * Client JS - patadmin/viewmodel/transport-list
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
    "jquery", "knockout",
    "patadmin/store/patadmin-patients",
    "patadmin/store/patadmin-incidents",
    "patadmin/store/patadmin-units"
  ],
  function($, ko, patients, incidents, units) {
    "use strict";

    var vm = {};

    // One row per active hospital-transport incident (bo = unit).
    // Uses ko.computed (not pure) because fetchUnknown is a side effect.
    vm.hospitalTransportRows = ko.computed(function() {
      var result = [];
      var allUnits = units.models(); // explicit dependency so re-runs when any unit is added

      $.each(incidents.models(), function(id, inc) {
        if (inc.type() !== "Transport" || inc.state() === "Done") {
          return;
        }
        var bo = inc.bo();
        if (!bo || bo["@type"] !== "unit") {
          return;
        }

        var patientId = inc.patient();
        var patient = patientId ? patients.get(patientId) : null;

        // Group name from the patient's active Treatment incident
        var groupName = "";
        if (patientId) {
          $.each(incidents.models(), function(id2, inc2) {
            if (inc2.type() !== "Treatment" || inc2.state() === "Done" || inc2.patient() != patientId) {
              return;
            }
            $.each(inc2.units(), function(unitId) {
              var unit = units.get(parseInt(unitId));
              if (unit) {
                groupName = unit.call();
              }
            });
          });
        }

        // Ambulances assigned to this transport
        var transportUnits = [];
        $.each(inc.units(), function(unitId, taskState) {
          var uid = parseInt(unitId);
          var unit = allUnits[uid] || null;
          if (!unit) {
            units.fetchUnknown(uid);
          }
          transportUnits.push({
            callsign: unit ? unit.call() : "...",
            taskState: taskState
          });
        });

        var ao = inc.ao();
        result.push({
          patient: patient,
          groupName: groupName,
          casusNr: inc.casusNr(),
          hospital: ao ? ao.info : "",
          transportInfo: inc.info(),
          transportUnits: transportUnits
        });
      });

      result.sort(function(a, b) {
        var aId = a.patient ? a.patient.id : 0;
        var bId = b.patient ? b.patient.id : 0;
        return bId - aId;
      });
      return result;
    });

    return vm;
  }
);
