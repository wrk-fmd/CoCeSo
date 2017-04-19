/**
 * CoCeSo
 * Client JS - models/main/incident
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
 *
 * @module main/models/incident
 * @param {module:nockout} ko
 * @param {module:main/models/point} Point
 * @param {module:main/models/task} Task
 * @param {module:main/models/unit} Unit
 * @param {module:main/navigation} navigation
 * @param {module:data/save} save
 * @param {module:data/store/patients} patients
 * @param {module:utils/constants} constants
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 * @returns {Incident}
 */
define(["knockout", "./point", "./task", "./unit", "../navigation", "data/save", "data/store/patients",
  "utils/constants", "utils/destroy", "utils/i18n", "ko/extenders/isvalue", "ko/extenders/timeinterval"],
  function(ko, Point, Task, Unit, navigation, save, patients, constants, destroy, _) {
    "use strict";

    /**
     * Single incident
     *
     * @constructor
     * @alias module:main/models/incident
     * @param {Object} data
     */
    var Incident = function(data) {
      var self = this;
      data = data || {};

      //Create basic properties
      this.id = data.id;
      this.ao = new Point();
      this.bo = new Point();
      this.units = ko.observableArray([]);
      this.blue = ko.observable(false);
      this.priority = ko.observable(false);
      this.caller = ko.observable("");
      this.casusNr = ko.observable("");
      this.info = ko.observable("");
      this.state = ko.observable(constants.Incident.state.open);
      this.type = ko.observable(constants.Incident.type.task);
      this.section = ko.observable("");
      this.created = ko.observable(null).extend({timeinterval: true});
      this.arrival = ko.observable(null);
      this.stateChange = ko.observable(null).extend({timeinterval: true});
      this.ended = ko.observable(null);
      this.patientId = ko.observable(null);

      /**
       * Method to set data
       *
       * @param {Object} data
       * @returns {void}
       */
      this.setData = function(data) {
        self.ao.setData(data.ao);
        self.bo.setData(data.bo);
        if (data.units) {
          ko.utils.objectForEach(data.units, function(unit, taskState) {
            unit = parseInt(unit);
            if (!unit || isNaN(unit)) {
              return;
            }
            var task = ko.utils.arrayFirst(self.units(), function(item) {
              return (item.unit_id === unit);
            });
            if (task) {
              //Item exists, just set the new TaskState
              task.taskState(taskState);
            } else {
              //Create new Task model
              self.units.push(new Task(taskState, self.id, unit));
            }
          });
          //Remove detached units
          self.units.remove(function(task) {
            return (!data.units[task.unit_id]);
          });
        } else {
          self.units([]);
        }
        self.blue(data.blue || false);
        self.priority(data.priority || false);
        self.caller(data.caller || "");
        self.casusNr(data.casusNr || "");
        self.info(data.info || "");
        self.state(data.state || constants.Incident.state.open);
        self.type(data.type || constants.Incident.type.task);
        self.section(data.section || "");
        self.created(data.created);
        self.arrival(data.arrival || null);
        self.stateChange(data.stateChange || null);
        self.ended(data.ended || null);
        self.patientId(data.patient);
      };

      //Set data
      this.setData(data);

      /**
       * Incident is of type "Task"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isTask = this.type.extend({isValue: constants.Incident.type.task});

      /**
       * Incident is of type "Relocation"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isRelocation = this.type.extend({isValue: constants.Incident.type.relocation});

      /**
       * Incident is of type "Transport"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isTransport = this.type.extend({isValue: constants.Incident.type.transport});

      /**
       * Incident is of type "ToHome"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isToHome = this.type.extend({isValue: constants.Incident.type.tohome});

      /**
       * Incident is of type "HoldPosition"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isHoldPosition = this.type.extend({isValue: constants.Incident.type.holdposition});

      /**
       * Incident is of type "Standby"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isStandby = this.type.extend({isValue: constants.Incident.type.standby});

      /**
       * Incident has state "Open"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isOpen = this.state.extend({isValue: constants.Incident.state.open});

      /**
       * Incident has state "Demand"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isDemand = this.state.extend({isValue: constants.Incident.state.demand});

      /**
       * Incident has state "InProgress"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isInProgress = this.state.extend({isValue: constants.Incident.state.inprogress});

      /**
       * Incident has state "Done"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isDone = this.state.extend({isValue: constants.Incident.state.done});

      /**
       * @function
       * @type ko.pureComputed
       * @returns {Integer} Time since the creation/last taskstate change in minutes
       */
      this.timer = ko.pureComputed(function() {
        return this.arrival() ? this.stateChange.interval() : this.created.interval();
      }, this);

      /**
       * @function
       * @type ko.pureComputed
       * @returns {String} CSS class for the timer based on elapsed time
       */
      this.timerCss = ko.pureComputed(function() {
        var timer = this.timer();
        if (this.arrival()) {
          if (timer >= 35) {
            return "timer-danger";
          }
          if (timer >= 25) {
            return "timer-warning";
          }
        } else {
          if (timer >= 15) {
            return "timer-danger";
          }
          if (timer >= 10) {
            return "timer-warning";
          }
        }
        return "";
      }, this);

      /**
       * The associated patient
       *
       * @function
       * @type ko.pureComputed
       * @returns {Patient}
       */
      this.patient = ko.pureComputed(function() {
        return patients.get(this.patientId());
      }, this);

      /**
       * True, if current Incident will be cancelled on next assign
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.isInterruptible = ko.computed(function() {
        return (this.isStandby() || this.isToHome() || this.isHoldPosition() || this.isRelocation());
      }, this);


      /**
       * Incident has an AO
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.hasAO = ko.computed(function() {
        return !this.ao.isEmpty();
      }, this);

      /**
       * If true, Incident is marked red and counted in the Notification Area
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.isHighlighted = ko.computed(function() {
        return (this.isOpen() || this.isDemand() || (this.isTransport() && !this.hasAO()));
      }, this);

      /**
       * Disable BO field
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableBO = ko.computed(function() {
        return (!this.isTask() && !this.isTransport());
      }, this);

      /**
       * Disable "Assigned" state
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableAssigned = ko.computed(function() {
        return (this.isStandby() || this.isHoldPosition());
      }, this);

      /**
       * Disable "AAO" state
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableAAO = ko.computed(function() {
        return !this.hasAO();
      }, this);

      /**
       * Disable "ZAO" state
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableZAO = ko.computed(function() {
        return (this.isStandby() || this.isHoldPosition() || !this.hasAO());
      }, this);

      /**
       * Number of assigned units
       *
       * @function
       * @type ko.pureComputed
       * @returns {integer}
       */
      this.unitCount = ko.pureComputed(function() {
        return this.units().length;
      }, this);

      /**
       * Return the title string
       *
       * @function
       * @type ko.pureComputed
       * @returns {String}
       */
      this.title = ko.pureComputed(function() {
        if (!this.disableBO()) {
          return (this.bo.isEmpty()) ? _("incident.nobo") : this.bo.info();
        }
        return (this.ao.isEmpty()) ? _("incident.noao") : this.ao.info();
      }, this);

      /**
       * Return the type as localized string
       *
       * @function
       * @type ko.pureComputed
       * @returns {String}
       */
      this.typeString = ko.pureComputed(function() {
        if (this.type()) {
          if (this.isTask() && this.blue()) {
            return _("incident.type.task.blue");
          }
          return _("incident.type." + this.type().toLowerCase().escapeHTML());
        }
        return "";
      }, this);

      /**
       * Return a one-letter representation of type
       *
       * @function
       * @type ko.pureComputed
       * @returns {String}
       */
      this.typeChar = ko.pureComputed(function() {
        if (this.isTask()) {
          return this.blue() ? _("incident.stype.task.blue") : _("incident.stype.task");
        }
        if (this.isTransport()) {
          return _("incident.stype.transport");
        }
        if (this.isRelocation()) {
          return _("incident.stype.relocation");
        }
        if (this.isToHome()) {
          return _("incident.stype.tohome");
        }
        if (this.isStandby()) {
          return "<span class='glyphicon glyphicon-pause'></span>";
        }
        if (this.isHoldPosition()) {
          return "<span class='glyphicon glyphicon-record'></span>";
        }
        return "";
      }, this);

      /**
       * CSS class based on the incident's type
       *
       * @function
       * @type ko.pureComputed
       * @returns {string} The CSS class
       */
      this.typeCss = ko.pureComputed(function() {
        if (this.isTask() || this.isTransport()) {
          return (this.blue()) ? "unit_state_task_blue" : "unit_state_task";
        }
        if (this.isRelocation()) {
          return "unit_state_relocation";
        }
        if (this.isHoldPosition()) {
          return "unit_state_holdposition";
        }
        if (this.isToHome()) {
          return "unit_state_tohome";
        }
        if (this.isStandby()) {
          return "unit_state_standby";
        }

        return "";
      }, this);

      /**
       * Title in unit dropdown
       *
       * @function
       * @type ko.pureComputed
       * @returns {String}
       */
      this.dropdownTitle = ko.pureComputed(function() {
        var titleVal = this.title();
        if (!titleVal) {
          titleVal = "";
        }
        if (titleVal.length > 30) {
          titleVal = titleVal.substring(0, 30) + "...";
        }
        return "<span class='incident_type_text" + (this.blue() ? " incident_blue" : "")
          + "'>" + this.typeChar() + "</span>: " + titleVal.split("\n")[0].escapeHTML();
      }, this);

      /**
       * Title for unit form
       *
       * @function
       * @type ko.pureComputed
       * @returns {String}
       */
      this.assignedTitle = ko.pureComputed(function() {
        if (this.isTask() || this.isTransport() || this.isRelocation()) {
          return this.typeChar() + ": " + this.title().split("\n")[0].escapeHTML();
        }
        if (this.isToHome() || this.isStandby() || this.isHoldPosition()) {
          return this.typeChar();
        }
        return this.title().escapeHTML();
      }, this);

      /**
       * Assign a unit to an incident in the list
       *
       * @param {Event} event The jQuery Event (unused)
       * @param {Object} ui jQuery UI properties
       * @returns {void}
       */
      this.assignUnitList = function(event, ui) {
        var unit = ko.dataFor(ui.draggable[0]);

        if ((unit instanceof Unit) && self.id && unit.id) {
          save({incident_id: self.id, unit_id: unit.id}, "assignUnit.json");
        }
      };
    };
    Incident.prototype = Object.create({}, /** @lends Incident.prototype */ {
      /**
       * Open in a form
       *
       * @function
       * @returns {void}
       */
      openForm: {
        value: function() {
          var id;
          if (this instanceof Incident) {
            id = this.id;
          } else if (this instanceof Task) {
            id = this.incident_id;
          }
          if (id) {
            navigation.openIncident({id: id});
          }
        }
      },
      /**
       * Add log entry for this incident
       *
       * @function
       * @returns {void}
       */
      addLog: {
        value: function() {
          if (this.id) {
            navigation.openLogAdd({incident: this.id});
          }
        }
      },
      /**
       * Open log
       *
       * @returns {void}
       */
      openLog: {
        value: function() {
          if (this.id) {
            navigation.openLogs({url: "log/getByIncident/" + this.id, title: "Incident-Log"});
          }
        }
      },
      /**
       * Open patient
       *
       * @returns {void}
       */
      openPatient: {
        value: function() {
          navigation.openPatient({id: this.patientId(), incident: !this.patientId() && this.id});
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

    return Incident;
  });

