/**
 * CoCeSo
 * Client JS - models/main/unit
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
 * @module main/models/unit
 * @param {module:knockout} ko
 * @param {module:main/models/point} Point
 * @param {module:main/models/task} Task
 * @param {module:main/navigation} navigation
 * @param {module:data/save} save
 * @param {module:utils/constants} constants
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define([
  "knockout",
    "./point",
    "./task",
    "../navigation",
    "data/save",
    "data/store/sections",
    "utils/constants",
    "utils/destroy",
    "utils/i18n",
    "utils/client-logger",
    "ko/extenders/isvalue"],
  function(ko, Point, Task, navigation, save, sectionsStore, constants, destroy, _, clientLogger) {
    "use strict";

    /**
     * Single unit
     *
     * @constructor
     * @alias module:models/main/unit
     * @param {Object} data Initial data for the unit
     */
    var Unit = function(data) {
      var self = this;
      data = data || {};

      //Create basic properties
      this.id = data.id;
      this.ani = data.ani;
      this.call = data.call;
      this.crew = data.crew;
      this.portable = data.portable;
      this.transportVehicle = data.transportVehicle;
      this.withDoc = data.withDoc;
      this.section = data.section;

      this.home = new Point();
      this.position = new Point();
      this.incidents = ko.observableArray([]);
      this.info = ko.observable("");
      this.state = ko.observable(constants.Unit.state.ad);

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
          ko.utils.objectForEach(data.incidents, function(incidentId, taskState) {
            incidentId = parseInt(incidentId);
            if (!incidentId || isNaN(incidentId)) {
              return;
            }
            var task = ko.utils.arrayFirst(self.incidents(), function(item) {
              return (item.incident_id === incidentId);
            });

            var stateChangedAt = data.incidentStateChangeTimestamps[incidentId];
            if (task) {
              //Item exists, just set the new TaskState
              task.taskState(taskState);
              task.stateChangedAt(stateChangedAt);
            } else {
              //Create new Task model
              self.incidents.push(new Task(taskState, incidentId, self.id, stateChangedAt));
            }
          });

          //Remove detached units
          self.incidents.remove(function(task) {
            return (!data.incidents[task.incident_id]);
          });
        } else {
          self.incidents([]);
        }
        self.info(data.info || "");
        self.state(data.state || constants.Unit.state.ad);

        self.ani = data.ani;
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
       * List of incidents showing up in dropdown
       *
       * @function
       * @type ko.pureComputed
       * @returns {Array}
       */
      this.dropdownIncidents = ko.pureComputed(function() {
        if (this.incidentCount() <= 0) {
          return [];
        }
        return ko.utils.arrayFilter(this.incidents(), function(task) {
          var i = task.incident();
          return ((i !== null) && (i.isTask() || i.isTransport() || i.isRelocation()));
        });
      }, this);

      /**
       * Returns if some incidents should show up in dropdown
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.dropdownActive = ko.pureComputed(function() {
        return (this.dropdownIncidents().length > 0);
      }, this);

      /**
       * Home is set
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.hasHome = ko.pureComputed(function() {
        return !this.home.isEmpty();
      }, this);

      /**
       * Last known position is home
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isHome = this.position.info.extend({isValue: this.home.info});

      /**
       * Unit has state "AD"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isAD = this.state.extend({isValue: constants.Unit.state.ad});

      /**
       * Unit has state "EB"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isEB = this.state.extend({isValue: constants.Unit.state.eb});

      /**
       * Unit has state "NEB"
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isNEB = this.state.extend({isValue: constants.Unit.state.neb});

      /**
       * Unit has incident with TaskState "Assigned"
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.hasAssigned = ko.computed(function() {
        if (!this.portable || this.incidentCount() <= 0) {
          return false;
        }

        return (!!ko.utils.arrayFirst(this.incidents(), function(task) {
          return (task.isAssigned());
        }));
      }, this);

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

      /**
       * Unit is available for a new Incident and "EB"
       *
       * @function
       * @type ko.computed
       * @returns {boolean}
       */
      this.isAvailable = ko.computed(function() {
        //Non portable or non EB units are not available
        if (!this.portable || !this.isEB()) {
          return false;
        }

        //No incidents: Unit is available
        if (this.incidentCount() <= 0) {
          return true;
        }

        //Incident allows interruption: Unit is available
        var i = this.incidents()[0].incident();
        return (i === null || i.isInterruptible());
      }, this);

      /**
       * Disable the send home method
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.disableSendHome = ko.pureComputed(function() {
        if (!this.hasHome() || this.isHome() || (this.incidentCount() > 1)) {
          return true;
        }
        if (this.incidentCount() <= 0) {
          return false;
        }

        var i = this.incidents()[0].incident();
        return (i !== null && !i.isHoldPosition() && !i.isStandby());
      }, this);

      /**
       * Disable the standby method
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.disableStandby = ko.pureComputed(function() {
        if (this.incidentCount() === 1) {
          var i = this.incidents()[0].incident();
          return (i !== null && !i.isHoldPosition());
        }
        return (this.incidentCount() > 1);
      }, this);

      /**
       * Disable the hold position method
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.disableHoldPosition = ko.pureComputed(function() {
        if (this.isHome() || this.position.isEmpty() || (this.incidentCount() > 1)) {
          return true;
        }
        if (this.incidentCount() <= 0) {
          return false;
        }

        var i = this.incidents()[0].incident();
        return (i !== null && !i.isStandby());
      }, this);

      /**
       * CSS class based on the unit's state
       *
       * @function
       * @type ko.computed
       * @returns {string} The CSS class
       */
      this.stateCss = ko.computed(function() {
        if (this.isEB()) {
          if (this.incidentCount() === 1) {
            var i = this.incidents()[0].incident();
            if (i !== null && i.isStandby()) {
              return "unit_state_standby";
            }
          }
          return "unit_state_eb";
        }
        return this.isNEB() ? "unit_state_neb" : "unit_state_ad";
      }, this);

      /**
       * Options for the tooltip
       *
       * @function
       * @type ko.pureComputed
       * @returns {Object} The popover options
       */
      this.popover = ko.pureComputed(function() {
        // Bugfix orphaned Popovers (Ticket #17)
        var content = "<div onmouseout=\"$('.popover').remove();\"><dl class='dl-horizontal list-narrower'>";
        // The ANI brings no operational benefit in the popover.
        // if (this.ani) {
        //   content += "<dt>" + _("unit.ani") + "</dt><dd>" + this.ani.escapeHTML() + "</dd>";
        // }

        // Having two different positions in the popover is quite confusing. The home location is no longer shown, in order to read the current position faster.
        // if (this.hasHome()) {
        //   content += "<dt><span class='glyphicon glyphicon-home'></span></dt><dd><span class='pre'>" + this.home.info().escapeHTML() + "</span></dd>";
        // }

        content += "<dt><span class='glyphicon glyphicon-map-marker'></span></dt><dd><span class='pre'>" +
          (this.position.isEmpty() ? "N/A" : this.position.info().escapeHTML()) + "</span></dd>";

        content += "</dl><hr/><dl class='dl-horizontal'>";

        if (this.incidentCount() > 0) {
          ko.utils.arrayForEach(this.incidents(), function(task) {
            if (task.incident() !== null) {
              content += "<dt>" + task.taskStateDependentTitle() + "</dt><dd>" + task.localizedTaskState() + "</dd>";
            }
          });
        }
        content += "</dl></div>";

        return {
          trigger: "hover focus",
          placement: "auto left",
          html: true,
          container: "body",
          title: this.call.escapeHTML(),
          content: content
        };
      }, this);

      /**
       * Open incident form with Unit attached
       *
       * @returns {void}
       */
      this.addIncident = function() {
        var data = {
          units: {},
          section: sectionsStore.filter()
        };
        data.units[self.id] = constants.TaskState.assigned;

        clientLogger.debugLog("#userInput #createIncident Creating a new task/transport for unit from #contextmenu.");
        navigation.openIncident(data);
      };

      /**
       * Open incident form with new relocation and current Unit attached.
       *
       * @returns {void}
       */
      this.addRelocation = function() {
        var data = {
          units: {},
          section: sectionsStore.filter(),
          type: constants.Incident.type.relocation
        };
        data.units[self.id] = constants.TaskState.assigned;

        clientLogger.debugLog("#userInput #createIncident Creating a new relocation for unit from #contextmenu.");
        navigation.openIncident(data);
      };

      /**
       * Open incident form with Unit as caller
       * BO is set to Position of Unit, BlueLight is true by default
       *
       * @returns {void}
       */
      this.reportIncident = function() {
        var data = {
          caller: self.call
        };
        if (this.portable) {
          data.bo = {info: self.position.info()};
          data.blue = true;
          data.units = {};
          data.units[self.id] = constants.TaskState.abo;
          data.section = sectionsStore.filter();
        }

        clientLogger.debugLog("#userInput #createIncident Creating a new reported task for unit from #contextmenu.");
        navigation.openIncident(data);
      };
    };
    Unit.prototype = Object.create({}, /** @lends Unit.prototype */ {
      /**
       * Set unit state to "AD"
       *
       * @function
       * @returns {void}
       */
      setAD: {
        value: function() {
          if (this.id && !this.isAD()) {
            save(JSON.stringify({id: this.id, state: constants.Unit.state.ad}), "unit/update");
          }
        }
      },
      /**
       * Set unit state to "EB"
       *
       * @function
       * @returns {void}
       */
      setEB: {
        value: function() {
          if (this.id && !this.isEB()) {
            save(JSON.stringify({id: this.id, state: constants.Unit.state.eb}), "unit/update");
          }
        }
      },
      /**
       * Set unit state to "NEB"
       *
       * @function
       * @returns {void}
       */
      setNEB: {
        value: function() {
          if (this.id && !this.isNEB()) {
            save(JSON.stringify({id: this.id, state: constants.Unit.state.neb}), "unit/update");
          }
        }
      },
      /**
       * Send unit home
       *
       * @function
       * @returns {void}
       */
      sendHome: {
        value: function() {
          if (this.id && !this.disableSendHome()) {
            save({id: this.id}, "unit/sendHome");
          }
        }
      },
      /**
       * Send unit home
       *
       * @function
       * @returns {void}
       */
      standby: {
        value: function() {
          if (this.id && !this.disableStandby()) {
            save({id: this.id}, "unit/standby");
          }
        }
      },
      /**
       * Send unit home
       *
       * @function
       * @returns {void}
       */
      holdPosition: {
        value: function() {
          if (this.id && !this.disableHoldPosition()) {
            save({id: this.id}, "unit/holdPosition");
          }
        }
      },
      /**
       * Send selcall to unit
       *
       * @function
       * @returns {void}
       */
      sendCall: {
        value: function() {
          if (this.ani) {
            save(JSON.stringify({ani: this.ani}), "radio/send");
          }
        }
      },
      /**
       * Set TaskState to next state
       *
       * @function
       * @returns {void}
       */
      nextState: {
        value: function() {
          if (this.incidentCount() === 1) {
            this.incidents()[0].nextState();
          }
        }
      },
      /**
       * Open in a form
       *
       * @function
       * @returns {void}
       */
      openForm: {
        value: function() {
          navigation.openUnit({id: this.id});
        }
      },
      /**
       * Open details
       *
       * @function
       * @returns {void}
       */
      openDetails: {
        value: function() {
          navigation.openUnitDetail(this.id);
        }
      },
      /**
       * Add log entry for this unit
       *
       * @function
       * @returns {void}
       */
      addLog: {
        value: function() {
          if (this.id) {
            navigation.openLogAdd({unit: this.id});
          }
        }
      },
      /**
       * Open Log of this Unit
       *
       * @function
       * @returns {void}
       */
      openLog: {
        value: function() {
          if (this.id) {
            navigation.openLogs({url: "log/getLastByUnit/" + this.id + "/5", title: "Unit-Log"});
          }
        }
      },
      /**
       * Options for draggables
       *
       * @type Object
       */
      dragOptions: {
        value: {
          helper: "clone",
          appendTo: "body",
          cursor: "move"
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

    return Unit;
  });
