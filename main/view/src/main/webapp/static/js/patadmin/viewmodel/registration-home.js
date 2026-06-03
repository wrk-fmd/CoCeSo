/**
 * CoCeSo
 * Client JS - patadmin/viewmodel/registration-home
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

    vm.takeoverUrl = conf.get("takeoverUrl");
    vm.registrationViewUrl = conf.get("registrationViewUrl");
    vm.registrationEditUrl = conf.get("registrationEditUrl");
    vm.groupUrl = conf.get("groupUrl");
    vm.newPatientId = ko.observable(conf.get("newPatientId") || null);

    // Patients in active Treatment incidents, with group name and id for linking.
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
        var groupName = "", groupId = null;
        $.each(inc.units(), function(unitId) {
          var uid = parseInt(unitId);
          var unit = units.get(uid);
          if (unit) {
            groupName = unit.call();
            groupId = uid;
          }
        });
        result.push({patient: patient, groupName: groupName, groupId: groupId});
      });
      result.sort(function(a, b) { return b.patient.id - a.patient.id; });
      return result;
    });

    vm.treatmentCount = conf.get("treatmentCount") || 0;
    vm.transportCount = conf.get("transportCount") || 0;
    vm.countText = (conf.get("countsMsg") || "")
      .replace("|0|", vm.treatmentCount)
      .replace("|1|", vm.transportCount);

    // Incoming: Task/Transport incidents where ao is a treatment unit.
    // Returns enriched objects ready for the template.
    // Uses ko.computed (not pure) because fetchUnknown is a side effect.
    vm.incomingIncidents = ko.computed(function() {
      var result = [];
      $.each(incidents.models(), function(id, inc) {
        var type = inc.type();
        if (type !== "Task" && type !== "Transport") {
          return;
        }
        if (inc.state() === "Done") {
          return;
        }
        var ao = inc.ao();
        if (!ao || ao["@type"] !== "unit") {
          return;
        }
        units.fetchUnknown(ao.id);

        var patient = inc.patient() ? patients.get(inc.patient()) : null;

        var assignedUnits = [];
        $.each(inc.units(), function(unitId, taskState) {
          var unit = units.get(parseInt(unitId));
          assignedUnits.push({
            callsign: unit ? unit.call() : "?",
            taskState: taskState
          });
        });

        result.push({
          incidentId: inc.id,
          patient: patient,
          aoInfo: ao.info || "",
          incidentInfo: inc.info(),
          units: assignedUnits
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
