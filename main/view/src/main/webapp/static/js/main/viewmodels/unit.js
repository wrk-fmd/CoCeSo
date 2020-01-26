/**
 * CoCeSo
 * Client JS - main/viewmodels/unit
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
 * @module main/viewmodels/unit
 * @param {module:knockout} ko
 * @param {module:main/viewmodels/form} Form
 * @param {module:main/models/task} Task
 * @param {module:main/models/unit} Unit
 * @param {module:data/save} save
 * @param {module:data/store/units} store
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define(["knockout", "./form", "../models/task", "../models/unit",
  "data/save", "data/store/units", "utils/destroy","utils/i18n",
  "ko/bindings/point", "ko/extenders/isvalue", "ko/extenders/changes", "ko/extenders/arrayChanges"],
  function(ko, Form, Task, Unit, save, store, destroy, _) {
    "use strict";

    /**
     * Single unit
     *
     * @alias module:main/viewmodels/unit
     * @constructor
     * @extends module:main/models/unit
     * @extends module:main/viewmodels/form
     * @param {Object} data
     */
    var UnitVM = function(data) {
      var self = this;

      /**
       * Used Model (reference)
       *
       * @function
       * @type ko.observable
       * @returns {module:main/models/unit}
       */
      this.model = ko.observable(null);

      var title = _("unit.edit");
      this.dialogTitle = ko.computed(function() {
        if (!this.model()) {
          return title;
        }
        return {dialog: title + ": " + this.model().call, button: this.model().call};
      }, this);

      //Call parent constructors
      Unit.call(this, {id: data.id});
      Form.call(this);

      //Initialize change detection
      this.position = ko.observable("").extend({observeChanges: {}});
      this.home = ko.observable("").extend({observeChanges: {}});
      this.info.extend({observeChanges: {keepChanges: true}});
      this.state.extend({observeChanges: {}});
      this.incidents.extend({arrayChanges: {}});
      this.form.push(this.position, this.info, this.state, this.incidents);

      this.isHome = this.position.extend({isValue: this.home});

      /**
       * "Virtual" computed observable:
       * Serves as callback on changing the id or the list of models
       *
       * @function
       * @type ko.computed
       */
      this.modelChange = ko.computed(function() {
        var newModel = store.get(this.id),
          oldModel = this.model.peek();

        if (newModel === null) {
          if (oldModel === null) {
            //No model exists (not loaded yet or empty form), so create a dummy one
            this.model(new Unit());
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
      this.load = ko.computed(function() {
        //Subscribe to change of model
        this.model();

        //Update server reference for change detection
        this.position.server(this.model().position.info);
        this.home.server(this.model().home.info);
        this.info.server(this.model().info);
        this.state.server(this.model().state);

        //Set initial data
        if (ko.computedContext.isInitial()) {
          if (data.position) {
            this.position(data.position.info);
          }
          if (typeof data.info !== "undefined") {
            this.info(data.info);
          }
          if (typeof data.state !== "undefined") {
            this.state(data.state);
          }
          if (data.incidents) {
            var incident;
            for (incident in data.incidents) {
              this.setTaskState(incident, data.incidents[incident]);
            }
          }
        }
      }, this);

      /**
       * "Virtual" computed observable:
       * Update the associated incidents
       *
       * @function
       * @type ko.computed
       */
      this.setAssociatedIncidents = ko.computed(function() {
        ko.utils.arrayForEach(this.model().incidents(), function(task) {
          var local = ko.utils.arrayFirst(self.incidents.peek(), function(item) {
            return (item.incident_id === task.incident_id);
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
            local = new Task(task.taskState.peek(), task.incident_id, task.unit_id, task.stateChangedAt.peek());
            local.taskState.extend({observeChanges: {server: task.taskState}});
            local.changed = local.taskState.changed;
            local.reset = local.taskState.reset;
            self.incidents.push(local);
          }
        });
        //Remove detached incidents
        this.incidents.remove(function(task) {
          if (!task.taskState.orig()) {
            return false;
          }
          return (!ko.utils.arrayFirst(self.model().incidents(), function(item) {
            return (task.incident_id === item.incident_id);
          }));
        });
      }, this);

      /**
       * Callback after saving
       */
      this.afterSave = function() {
        self.error(false);
      };
    };
    UnitVM.prototype = Object.create(Unit.prototype, /** @lends Unit.prototype */ {
      /**
       * Save the form
       *
       * @function
       * @returns {boolean}
       */
      save: {
        value: function() {
          var data = {
            id: this.id,
            position: this.position(),
            info: this.info(),
            state: this.state(),
            incidents: {}
          };

          var incidents = this.incidents, remove = [];
          ko.utils.arrayForEach(incidents(), function(task) {
            if (task.taskState.changed()) {
              data.incidents[task.incident_id] = task.taskState();
              if (!task.taskState.orig() && task.isDetached()) {
                incidents.remove(task);
              }
            }
          });
          ko.utils.arrayForEach(remove, function(task) {
            incidents.remove(task);
          });

          save(JSON.stringify(data), "unit/update.json", this.afterSave, this.saveError, this.httpError, this.form.saving);
        }
      },
      /**
       * Destroy the ViewModel
       *
       * @function
       */
      destroy: {
        value: function() {
          destroy(this);
        }
      }
    });

    return UnitVM;
  }
);
