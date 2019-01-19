/**
 * CoCeSo
 * Client JS - main/viewmodels/incident
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
 * @module main/viewmodels/incident
 * @param {module:knockout} ko
 * @param {module:main/viewmodels/form} Form
 * @param {module:main/models/incident} Incident
 * @param {module:main/models/task} Task
 * @param {module:main/models/unit} Unit
 * @param {module:main/navigation} navigation
 * @param {module:data/save} save
 * @param {module:data/store/incidents} store
 * @param {module:data/store/sections} sections
 * @param {module:utils/constants} constants
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define([
    "knockout",
    "./form",
    "../models/incident",
    "../models/task",
    "../models/unit",
    "../navigation",
    "data/save",
    "data/store/incidents",
    "data/store/sections",
    "utils/constants",
    "utils/destroy",
    "utils/i18n",
    "ko/bindings/point",
    "ko/extenders/boolean",
    "ko/extenders/changes",
    "ko/extenders/arrayChanges"],
  function (ko, Form, Incident, Task, Unit, navigation, save, store, sections, constants, destroy, _) {
    "use strict";

    /**
     * Single incident
     *
     * @constructor
     * @extends module:main/models/incident
     * @extends module:main/viewmodels/form
     * @param {Object} data
     */
    var IncidentVM = function (data) {
      var self = this;

      this.sections = sections;

      /**
       * Observable for the ID (needed to update form after initial saving)
       *
       * @function
       * @type ko.observable
       * @returns {Integer}
       */
      this.idObs = ko.observable(data.id || null);

      /**
       * Used Model (reference)
       *
       * @function
       * @type ko.observable
       * @returns {module:main/models/incident}
       */
      this.model = ko.observable(null);

      this.dialogTitle = ko.computed(function () {
        if (!this.idObs()) {
          return _("incident.add");
        }
        if (!this.model()) {
          return _("incident.edit");
        }
        return {dialog: _("incident.edit") + ": " + this.model().title(), button: this.model().assignedTitle()};
      }, this);

      //Call parent constructors
      Incident.call(this, {id: this.idObs()});
      Form.call(this);

      //Initialize change detection
      this.ao = ko.observable("").extend({observeChanges: {}});
      this.blue = this.blue.extend({"boolean": true, observeChanges: {}});
      this.priority = this.priority.extend({"boolean": true, observeChanges: {}});
      this.bo = ko.observable("").extend({observeChanges: {}});
      this.caller.extend({observeChanges: {}});
      this.casusNr.extend({observeChanges: {}});
      this.info.extend({observeChanges: {keepChanges: true}});
      this.state.extend({observeChanges: {}});
      this.type.extend({observeChanges: {}});
      this.section.extend({observeChanges: {}});
      this.patientId.extend({observeChanges: {}});
      this.units.extend({arrayChanges: {}});
      this.form.push(this.units, this.type, this.blue, this.priority, this.bo, this.ao, this.info, this.caller, this.casusNr, this.state, this.section);

      /**
       * "Virtual" computed observable:
       * Serves as callback on changing the id or the list of models
       *
       * @function
       * @type ko.computed
       * @returns {void}
       */
      this.modelChange = ko.computed(function () {
        var newModel = store.get(this.idObs()),
          oldModel = this.model.peek();

        if (newModel === null) {
          if (oldModel === null) {
            //No model exists (not loaded yet or empty form), so create a dummy one
            this.model(new Incident());
          }
        } else if (newModel !== oldModel) {
          //Model has changed
          this.model(newModel);
        }
      }, this);

      /**
       * "Virtual" computed observable:
       * Load the local data from Model
       *
       * @function
       * @type ko.computed
       */
      this.load = ko.computed(function () {
        //Subscribe to change of model
        this.model();

        //Update server reference for change detection
        this.ao.server(this.model().ao.info);
        this.blue.server(this.model().blue);
        this.priority.server(this.model().priority);
        this.bo.server(this.model().bo.info);
        this.caller.server(this.model().caller);
        this.casusNr.server(this.model().casusNr);
        this.info.server(this.model().info);
        this.state.server(this.model().state);
        this.type.server(this.model().type);
        this.section.server(this.model().section);
        this.patientId.server(this.model().patientId);

        //Set initial data
        if (ko.computedContext.isInitial()) {
          if (data.ao) {
            this.ao(data.ao.info);
          }
          if (typeof data.blue !== "undefined") {
            this.blue(data.blue);
          }
          if (typeof data.priority !== "undefined") {
            this.blue(data.priority);
          }
          if (typeof data.bo !== "undefined") {
            this.bo(data.bo.info);
          }
          if (typeof data.caller !== "undefined") {
            this.caller(data.caller);
          }
          if (typeof data.casusNr !== "undefined") {
            this.casusNr(data.casusNr);
          }
          if (typeof data.info !== "undefined") {
            this.info(data.info);
          }
          if (typeof data.state !== "undefined") {
            this.state(data.state);
          }
          if (typeof data.type !== "undefined") {
            this.type(data.type);
          }
          if (typeof data.section !== "undefined") {
            this.section(data.section);
          }
          if (data.units) {
            var unit;
            for (unit in data.units) {
              this.setTaskState(unit, data.units[unit]);
            }
          }
        }
      }, this);

      /**
       * "Virtual" computed observable:
       * Update the associated units
       *
       * @function
       * @type ko.computed
       */
      this.setAssociatedUnits = ko.computed(function () {
        ko.utils.arrayForEach(this.model().units(), function (task) {
          var local = ko.utils.arrayFirst(self.units.peek(), function (item) {
            return (item.unit_id === task.unit_id);
          });
          if (local) {
            if (local.taskState.server !== task.taskState) {
              //Local element exists, but does not match global model: Recreate local taskState observable
              local.taskState.server(task.taskState);
              local.changed = local.taskState.changed;
              local.reset = local.taskState.reset;
            }
          } else {
            //Local task does not exist at all: Create complete Task model
            local = new Task(task.taskState.peek(), task.incident_id, task.unit_id);
            local.taskState.extend({observeChanges: {server: task.taskState}});
            local.changed = local.taskState.changed;
            local.reset = local.taskState.reset;
            self.units.push(local);
          }
        });
        //Remove detached units
        this.units.remove(function (task) {
          if (!task.taskState.orig()) {
            return false;
          }
          return (!ko.utils.arrayFirst(self.model().units(), function (item) {
            return (task.unit_id === item.unit_id);
          }));
        });
      }, this);

      if (data.autoSave) {
        //Saving results in reloading the data, but reloading is not possible before loading is completed
        window.setTimeout(function () {
          self.save.call(self);
        }, 100);
      }

      /**
       * Incident has an AO
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.hasAO = ko.computed(function () {
        return !!this.ao();
      }, this);

      /**
       * Disable "AAO" state
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableAAO = ko.computed(function () {
        return !this.hasAO();
      }, this);

      /**
       * Disable "ZAO" state
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableZAO = ko.computed(function () {
        return (this.isStandby() || this.isHoldPosition() || !this.hasAO());
      }, this);

      /**
       * Disable the "Task" type button
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableTask = ko.computed(function () {
        return (this.idObs() && !this.isTask() && !this.isTransport());
      }, this);

      /**
       * Disable the "Relocation" type button
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableRelocation = ko.computed(function () {
        return (this.idObs() && !this.isRelocation());
      }, this);

      /**
       * Disable IncidentState InProgress
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.disableInProgress = ko.computed(function () {
        return (ko.utils.arrayFirst(this.units(), function (task) {
          return !task.isDetached();
        }) === null);
      }, this);

      this.isAlarmTextSendingEnabled = ko.computed(function () {
        return !!this.idObs() && !this.form.changed()
      }, this);

      /**
       * Highlight AO Field if empty and minimum of 1 Unit is ABO
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.highlightAO = ko.computed(function () {
        if (this.unitCount() > 0) {
          return (!this.hasAO() && ko.utils.arrayFilter(this.units(), function (task) {
            return task.isABO();
          }).length >= 1);
        }
        return false;
      }, this);

      /**
       * Assign a unit within the form
       *
       * @param {Event} event The jQuery Event (unused)
       * @param {Object} ui jQuery UI properties
       */
      this.assignUnitForm = function (event, ui) {
        var viewmodel = ko.dataFor(ui.draggable[0]);
        if (!(viewmodel instanceof Unit)) {
          return;
        }
        self.setTaskState.call(self, ko.utils.unwrapObservable(viewmodel.id));
      };

      /**
       * Duplicate the incident
       *
       * @function
       * @param {module:main/models/task} task Optional task to remove from current and bind to new incident
       */
      this.duplicate = function (task) {
        var data = {
          caller: self.caller(),
          bo: {info: self.bo()},
          ao: {info: self.ao()},
          info: self.info(),
          blue: self.blue(),
          priority: self.priority(),
          type: self.type(),
          section: self.section()
        };
        if (task instanceof Task) {
          data.units = {};
          data.units[task.unit_id] = task.taskState();
          if (ko.utils.unwrapObservable(task.taskState.server()) && self.id) {
            // Unit is assigned on server, so we have to move it on server
            data.autoSave = true;
            save({
              incident_id: self.id,
              unit_id: task.unit_id,
              state: constants.TaskState.detached
            }, "incident/setToState.json", self.afterSave, self.saveError, self.httpError);
          } else {
            // Just remove it locally
            self.units.remove(task);
          }
        }
        navigation.openIncident(data);
      };

      /**
       * Callback after saving
       *
       * @param {Object} data The data returned from server
       */
      this.afterSave = function (data) {
        self.error(false);
        if (data.incident_id && data.incident_id !== self.id) {
          //ID has changed
          self.id = data.incident_id;
          self.idObs(self.id);
        }
      };
    };
    IncidentVM.prototype = Object.create(Incident.prototype, /** @lends Incident.prototype */ {
      /**
       * Set TaskState for unit
       *
       * @function
       */
      setTaskState: {
        value: function (unit, taskState) {
          if (unit) {
            var assigned = ko.utils.arrayFirst(this.units(), function (task) {
              return (task.unit_id === unit);
            });
            if (assigned === null) {
              assigned = new Task(taskState ? taskState : constants.TaskState.assigned, this.id, unit);
              assigned.taskState.extend({observeChanges: {server: null, orig: null}});
              assigned.changed = assigned.taskState.changed;
              assigned.reset = assigned.taskState.reset;
              this.units.push(assigned);
            } else if (taskState && assigned.taskState() !== taskState) {
              assigned.taskState(taskState);
            }
          }
        }
      },
      /**
       * Save the form
       *
       * @function
       * @returns {boolean}
       */
      save: {
        value: function () {
          var data = {
            id: this.id,
            ao: this.ao(),
            bo: this.bo(),
            blue: this.blue(),
            priority: this.priority(),
            caller: this.caller(),
            casusNr: this.casusNr(),
            info: this.info(),
            state: this.state(),
            type: this.type(),
            section: this.section() || null,
            units: {}
          };

          var units = this.units, remove = [];
          ko.utils.arrayForEach(units(), function (task) {
            if (task.taskState.changed()) {
              data.units[task.unit_id] = task.taskState();
              if (!task.taskState.orig() && task.isDetached()) {
                remove.push(task);
              }
            }
          });
          ko.utils.arrayForEach(remove, function (task) {
            units.remove(task);
          });

          save(JSON.stringify(data), "incident/update.json", this.afterSave, this.saveError, this.httpError, this.form.saving);
        }
      },
      /**
       * Destroy the ViewModel
       *
       * @function
       */
      destroy: {
        value: function () {
          destroy(this);
        }
      }
    });

    return IncidentVM;
  });
