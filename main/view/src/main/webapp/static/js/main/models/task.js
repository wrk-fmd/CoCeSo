/**
 * CoCeSo
 * Client JS - models/main/task
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module main/models/task
 * @param {module:knockout} ko
 * @param {module:data/save} save
 * @param {module:data/store/incidents} incidents
 * @param {module:data/store/units} units
 * @param {module:main/confirm} confirm
 * @param {module:main/navigation} navigation
 * @param {module:utils/constants} constants
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define(["knockout", "data/save", "data/store/incidents", "data/store/units",
  "main/confirm", "main/navigation", "utils/constants", "utils/destroy", "utils/i18n", "utils/client-logger", "ko/extenders/isvalue"],
  function(ko, save, incidents, units, confirm, navigation, constants, destroy, _, clientLogger) {
    "use strict";

    /**
     * Task related methods, needed for both incidents and units
     *
     * @alias module:main/models/task
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
       * @returns {module:main/models/incident}
       */
      this.incident = ko.pureComputed(function() {
        return incidents.get(this.incident_id);
      }, this);

      /**
       * Get the associated unit
       *
       * @function
       * @type ko.pureComputed
       * @returns {module:main/models/unit}
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

      this.taskStateDependentTitle = ko.pureComputed(function() {
        const incidentRef = this.incident();

        if ((incidentRef.isTask() || incidentRef.isTransport()) && (this.isZAO || this.isAAO)) {
          return incidentRef.ao.isEmpty() ? incidentRef.assignedTitle() : incidentRef.typeChar() + ": " + incidentRef.ao.info();
        } else {
          return incidentRef.assignedTitle();
        }
      }, this);

      /**
       * Text based on the incident's type
       *
       * @function
       * @type ko.pureComputed
       * @returns {string} The text
       */
      this.taskText = ko.pureComputed(function() {
        const i = this.incident();
        if (i === null) {
          return this.localizedTaskState();
        }
        if (i.isTask() || i.isTransport() || i.isRelocation() || i.isToHome()) {
          return i.typeChar() + ": " + this.localizedTaskState() + " (" + i.stateChange.interval() + "')";
        }

        if (i.isStandby() || i.isHoldPosition()) {
          return i.typeChar() + (this.isAssigned() ? ": " + this.localizedTaskState() : "") + " (" + i.stateChange.interval() + "')";
        }

        return "";
      }, this);
    };
    Task.prototype = Object.create({}, /** @lends task.prototype */ {
      /**
       * Handles the nextState request
       *
       * @function
       * @returns {void}
       */
      nextState: {
        value: function() {
          var incident = this.incident(), unit = this.unit();

          if (!incident || !unit) {
            //Incident or unit not available
            console.error("Task.nextState(): invalid unit or incident!");
            return;
          }

          //Get next state

          /*
           * Allowed states for types
           *
           *  Task, Transport
           *    Assigned, ZBO, ABO, ZAO, AAO, Detached
           *  Relocation, ToHome
           *    Assigned, ZAO, AAO -> Detached
           *  Standby, HoldPosition
           *    Assigned, AAO, Detached
           */

          var task = (incident.isTask() || incident.isTransport()),
            reloc = (incident.isRelocation() || incident.isToHome()),
            hold = (incident.isStandby() || incident.isHoldPosition());

          var s = constants.TaskState;

          var nextState = null, needAO = false;
          if (this.isAssigned()) {
            if (task) {
              nextState = s.zbo;
            } else if (reloc) {
              needAO = incident.isRelocation();
              nextState = s.zao;
            } else if (hold) {
              nextState = s.aao;
            }
          } else if (this.isZBO()) {
            if (task) {
              nextState = s.abo;
            }
          } else if (this.isABO()) {
            if (task) {
              needAO = true;
              nextState = s.zao;
            }
          } else if (this.isZAO()) {
            if (task || reloc) {
              needAO = (!incident.isToHome());
              nextState = s.aao;
            }
          } else if (this.isAAO()) {
            if (task || hold) {
              nextState = s.detached;
            }
          }

          if (nextState === null) {
            //Next state is not defined
            console.error("No next state defined!");
            return;
          }

          if (needAO && !incident.hasAO() && (nextState === s.zao || nextState === s.aao)) {
            clientLogger.debugLog("#userInput #editIncident Open incident form because no destination address is set for incident " + incident.id + ".");
            navigation.openIncident({id: incident.id});
            return;
          }

          var info = _("confirm"), button = _("yes"), elements = [];

          if (incident.isStandby()) {
            if (nextState === s.aao) {
              info = _("confirm.standby.send");
            } else if (nextState === s.detached) {
              info = _("confirm.standby.end");
            }

            elements = [
              {key: _("unit.position"), val: unit.position.info()}
            ];
          } else if (incident.isHoldPosition()) {
            if (nextState === s.aao) {
              info = _("confirm.holdposition.send");
            } else if (nextState === s.detached) {
              info = _("confirm.holdposition.end");
            }

            elements = [
              {key: _("unit.position"), val: incident.ao.info()}
            ];
          } else if (incident.isToHome()) {
            elements = [
              {key: _("incident.bo"), val: incident.bo.info()},
              {key: _("incident.ao"), val: incident.ao.info()}
            ];

            button = (nextState === s.zao) ? _("task.state.zao") : _("task.state.ishome");
          } else if (incident.isRelocation()) {
            elements = [
              {key: _("confirm.current"), val: this.localizedTaskState()},
              {key: _("incident.ao"), val: incident.ao.info()},
              {key: _("incident.blue"), val: (incident.blue() ? _("yes") : _("no"))},
              {key: _("incident.info"), val: incident.info()}
            ];

            button = _("task.state." + nextState.toLowerCase());
          } else {
            elements = [
              {key: _("confirm.current"), val: this.localizedTaskState()},
              {key: _("incident.bo"), val: incident.bo.info()},
              {key: _("incident.ao"), val: incident.ao.info()},
              {key: _("incident.blue"), val: (incident.blue() ? _("yes") : _("no"))},
              {key: _("incident.info"), val: incident.info()},
              {key: _("incident.caller"), val: incident.caller()}
            ];
            if (incident.patient()) {
              var pp = incident.patient();
              elements.push({key: _("patient"), val: pp.firstname() + " " + pp.lastname()});
              elements.push({key: _("patient.insurance"), val: pp.insurance()});
              elements.push({key: _("patient.info"), val: pp.info()});
            }

            button = _("task.state." + nextState.toLowerCase());
          }

          confirm.show({
            title: "<strong>" + unit.call.escapeHTML() + "</strong>" + " - " + incident.typeString(),
            info_text: info, button_text: button, elements: elements,
            save: function() {
              clientLogger.debugLog("#userInput Confirming task state transition for new state '" + nextState + "'.");
              save({incident_id: incident.id, unit_id: unit.id, state: nextState}, "incident/setToState.json");
            }
          });
        }
      },
      /**
       * Destroy the object
       *
       * @function
       * @returns {void}
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
