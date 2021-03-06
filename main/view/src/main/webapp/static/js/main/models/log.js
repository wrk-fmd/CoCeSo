/**
 * CoCeSo
 * Client JS - models/main/log
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["data/store/incidents", "data/store/units", "utils/i18n"], function(incidents, units, _) {
  /**
   * Single log entry
   *
   * @constructor
   * @param {Object} data
   */
  var Log = function(data) {
    var self = this;
    data = data || {};

    //Create basic properties
    this.id = data.id;
    this.unit = data.unit ? units.get(data.unit) : null;
    this.incident = data.incident ? incidents.get(data.incident) : null;
    this.user = data.user;
    this.timestamp = data.timestamp;
    this.state = data.state;
    this.type = data.type;
    this.autoGenerated = data.autoGenerated;
    this.text = this.type === "CUSTOM" ? data.text : _("log.type." + this.type);
    this.changes = data.changes;

    this.time = (new Date(this.timestamp)).toLocaleString();

    this.openUnitForm = function() {
      if (self.unit) {
        self.unit.openForm();
      }
    };

    this.openIncidentForm = function() {
      if (self.incident) {
        self.incident.openForm();
      }
    };
  };

  return Log;
});