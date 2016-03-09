/**
 * CoCeSo
 * Client JS - patadmin/triage/form
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module patadmin/triage/form
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 */
define(["jquery", "knockout", "utils/conf", "bootstrap/modal", "ko/bindings/patient"], function($, ko, conf) {
  "use strict";

  /**
   * @alias module:patadmin/triage/form
   * @constructor
   */
  var Form = function() {
    var self = this;

    this.patient = ko.observable();
    this.medinfo = ko.observable();
    this.lastname = ko.observable();
    this.firstname = ko.observable();
    this.externalId = ko.observable();
    this.birthday = ko.observable();
    this.group = ko.observable();
    this.naca = ko.observable();
    this.diagnosis = ko.observable();
    this.info = ko.observable();

    var initial = conf.get("initial");
    this.types = [];
    if (!initial.patient) {
      this.types.push("patients");
    }
    if (!initial.medinfo) {
      this.types.push("medinfos");
    }

    this.callback = function(event, data) {
      if (data.lastname) {
        self.lastname(data.lastname);
      }
      if (data.firstname) {
        self.firstname(data.firstname);
      }
      if (data.externalId) {
        self.externalId(data.externalId);
      }
      if (data.birthday) {
        self.birthday(data.birthday);
      }

      if (typeof data.group !== "undefined") {
        // Group field exists, so this is a patient
        if (data.id) {
          self.patient(data.id);
        }
        if (data.group && data.group.length > 0) {
          self.group(data.group[0].id);
        }
        if (data.naca) {
          self.naca(data.naca);
        }
        if (data.diagnosis) {
          self.diagnosis(data.diagnosis);
        }
        if (data.info) {
          self.info(data.info);
        }
        if (data.medinfo && data.medinfo.id) {
          self.medinfo(data.medinfo.id);
        }
      } else {
        // Group field does not exist, so this is a medinfo
        if (data.id) {
          self.medinfo(data.id);
        }
      }
    };

    var loadedMedinfo = null;
    this.openMedinfo = function() {
      var medinfo = this.medinfo();
      if (!medinfo) {
        return;
      }

      if (medinfo !== loadedMedinfo) {
        $("#medinfo-modal-content").empty()
          .load(conf.get("medinfoUrl") + medinfo + " #medinfo-content", function() {
            loadedMedinfo = medinfo;
          });
      }
      $("#medinfo-modal").modal();
    };
  };

  return Form;
});
