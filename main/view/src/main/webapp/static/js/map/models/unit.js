/**
 * CoCeSo
 * Client JS - map/models/unit
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
 * @module map/models/unit
 * @param {module:knockout} ko
 * @param {module:main/models/point} Point
 * @param {module:map/models/task} Task
 */
define(["knockout", "main/models/point", "./task", "ko/extenders/isvalue"],
  function(ko, Point, Task) {
    "use strict";

    /**
     * Single unit
     *
     * @constructor
     * @alias module:map/models/unit
     * @param {Object} data Initial data for the unit
     */
    var Unit = function(data) {
      var self = this;
      data = data || {};

      //Create basic properties
      this.id = data.id;
      this.call = data.call;
      this.portable = data.portable;

      this.home = new Point();
      this.position = new Point();
      this.incidents = ko.observableArray([]);

      /**
       * Method to set data
       *
       * @param {Object} data
       * @returns {void}
       */
      this.setData = function(data) {
        self.home.setData(data.home);
        self.position.setData(data.position);
        if (data.incidents) {
          ko.utils.objectForEach(data.incidents, function(incident, taskState) {
            incident = parseInt(incident);
            if (!incident || isNaN(incident)) {
              return;
            }
            var task = ko.utils.arrayFirst(self.incidents(), function(item) {
              return (item.incident_id === incident);
            });
            if (task) {
              //Item exists, just set the new TaskState
              task.taskState(taskState);
            } else {
              //Create new Task model
              self.incidents.push(new Task(taskState, incident, self.id));
            }
          });
          //Remove detached units
          self.incidents.remove(function(task) {
            return (!data.incidents[task.incident_id]);
          });
        } else {
          self.incidents([]);
        }
      };

      //Set data
      this.setData(data);

      /**
       * Return the position to show in map
       * Moving units are displayed by corresponding incidents
       *
       * @function
       * @type ko.pureComputed
       * @returns {Point}
       */
      this.mapPosition = ko.pureComputed(function() {
        if (!this.portable) {
          return this.home;
        }
        if (ko.utils.arrayFirst(this.incidents(), function(task) {
          return !task.isAssigned();
        })) {
          return null;
        }
        return this.position;
      }, this);

      /**
       * Return the number of assigned incidents
       *
       * @function
       * @type ko.pureComputed
       * @returns {Integer}
       */
      this.incidentCount = ko.pureComputed(function() {
        return this.incidents().length;
      }, this);

      /**
       * Last known position is home
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isHome = this.position.id.extend({isValue: this.home.id});

      /**
       * Unit is "free" (not at home, no Incident assigned)
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.isFree = ko.computed(function() {
        return (this.portable && this.incidentCount() <= 0 && !this.isHome() && !this.isAD());
      }, this);
    };
    Unit.prototype = Object.create({}, /** @lends Unit.prototype */ {
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

    return Unit;
  }
);