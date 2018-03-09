/**
 * CoCeSo
 * Client JS - map/models/incident
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
 * @module map/models/incident
 * @param {module:nockout} ko
 * @param {module:main/models/point} Point
 * @param {module:map/models/task} Task
 * @param {module:utils/constants} constants
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 * @returns {Incident}
 */
define(["knockout", "main/models/point", "./task", "utils/constants", "utils/destroy", "utils/i18n", "ko/extenders/isvalue"],
  function(ko, Point, Task, constants, destroy, _) {
    "use strict";

    /**
     * Single incident
     *
     * @constructor
     * @alias module:map/models/incident
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
      this.state = ko.observable(constants.Incident.state.open);
      this.type = ko.observable(constants.Incident.type.task);

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
        self.state(data.state || constants.Incident.state.open);
        self.type(data.type || constants.Incident.type.task);
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
       * If true, Incident is marked red and counted in the Notification Area
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.isHighlighted = ko.computed(function() {
        return (this.isOpen() || this.isDemand());
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
    };
    Incident.prototype = Object.create({}, /** @lends Incident.prototype */ {
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

