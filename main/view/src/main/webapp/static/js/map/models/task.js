/**
 * CoCeSo
 * Client JS - map/models/task
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
 * @module map/models/task
 * @param {module:knockout} ko
 * @param {module:data/store/incidents} incidents
 * @param {module:data/store/units} units
 * @param {module:utils/constants} constants
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define(["knockout", "data/store/incidents", "data/store/units", "utils/constants", "utils/destroy", "utils/i18n", "ko/extenders/isvalue"],
  function(ko, incidents, units, constants, destroy, _) {
    "use strict";

    /**
     * Task related methods, needed for both incidents and units
     *
     * @alias module:map/models/task
     * @constructor
     * @param {String} taskState The TaskState
     * @param {Integer} incident The incident to use
     * @param {Integer} unit The use to use
     */
    var Task = function(taskState, incident, unit) {
      incident = parseInt(incident);
      unit = parseInt(unit);

      /**
       * The TaskState
       *
       * @function
       * @type ko.observable
       * @returns {String}
       */
      this.taskState = ko.observable(taskState);

      /**
       * The incident id
       *
       * @type Integer
       */
      this.incident_id = (incident && !isNaN(incident)) ? incident : null;

      /**
       * The unit id
       *
       * @type Integer
       */
      this.unit_id = (unit && !isNaN(unit)) ? unit : null;

      /**
       * Get the associated incident
       *
       * @function
       * @type ko.pureComputed
       * @returns {module:map/models/incident}
       */
      this.incident = ko.pureComputed(function() {
        return incidents.get(this.incident_id);
      }, this);

      /**
       * Get the associated unit
       *
       * @function
       * @type ko.pureComputed
       * @returns {module:map/models/unit}
       */
      this.unit = ko.pureComputed(function() {
        return units.get(this.unit_id);
      }, this);

      /**
       * Return if TaskState is "Assigned"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isAssigned = this.taskState.extend({isValue: constants.TaskState.assigned});

      /**
       * Return if TaskState is "ZBO"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isZBO = this.taskState.extend({isValue: constants.TaskState.zbo});

      /**
       * Return if TaskState is "ABO"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isABO = this.taskState.extend({isValue: constants.TaskState.abo});

      /**
       * Return if TaskState is "ZAO"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isZAO = this.taskState.extend({isValue: constants.TaskState.zao});

      /**
       * Return if TaskState is "AAO"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isAAO = this.taskState.extend({isValue: constants.TaskState.aao});

      /**
       * Return if TaskState is "Detached"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isDetached = this.taskState.extend({isValue: constants.TaskState.detached});

      /**
       * Return the localized taskState
       *
       * @function
       * @type ko.pureComputed
       * @returns {String}
       */
      this.localizedTaskState = ko.pureComputed(function() {
        if (this.taskState()) {
          return _("task.state." + this.taskState().toLowerCase().escapeHTML());
        }
        return "";
      }, this);

      /**
       * Text based on the incident's type
       *
       * @function
       * @type ko.pureComputed
       * @returns {string} The text
       */
      this.taskText = ko.pureComputed(function() {
        var i = this.incident();
        if (i === null) {
          return this.localizedTaskState();
        }
        if (i.isTask() || i.isTransport() || i.isRelocation() || i.isToHome()) {
          return i.typeChar() + ": " + this.localizedTaskState();
        }

        if (i.isStandby() || i.isHoldPosition()) {
          return i.typeChar() + (this.isAssigned() ? ": " + this.localizedTaskState() : "");
        }

        return "";
      }, this);
    };
    Task.prototype = Object.create({}, /** @lends Task.prototype */ {
      /**
       * Destroy the object
       *
       * @function
       */
      destroy: {
        value: function() {
          destroy(this);
        }
      }
    });

    return Task;
  }
);