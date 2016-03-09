/**
 * CoCeSo
 * Client JS - main/viewmodels/customlog
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "knockout", "data/save", "data/store/incidents", "data/store/units", "utils/errorhandling", "utils/i18n"],
    function($, ko, save, incidents, units, initErrorHandling, _) {
      "use strict";

      /**
       * ViewModel for Custom Log Entry (only used to create a new one)
       *
       * @constructor
       * @param {Object} data
       */
      var CustomLog = function(data) {
        var self = this;

        initErrorHandling(this);
        this.dialogTitle = _("log.add");

        this.text = ko.observable(data.text || "");
        this.unit = ko.observable(data.unit || 0);
        this.incident = data.incident || null;
        this.incidentTitle = ko.computed(function() {
          if (!this.incident) {
            return null;
          }
          var incident = incidents.get(this.incident);
          return incident ? incident.assignedTitle() : null;
        }, this);
        this.unitList = units.list;

        this.ok = function() {
          save(JSON.stringify({
            text: this.text(),
            unit: this.unit() ? {id: this.unit()} : null,
            incident: this.incident ? {id: this.incident} : null
          }), "log/add.json", this.afterSave, this.saveError, this.httpError);
        };

        this.afterSave = function() {
          $("#" + self.ui).dialog("destroy");
        };
      };

      return CustomLog;
    });
