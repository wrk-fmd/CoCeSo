/**
 * CoCeSo
 * Client JS - main/navigation
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
 * @module main/navigation
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:data/store/incidents} incidents
 * @param {module:data/store/units} units
 * @param {module:data/store/sections} sections
 * @param {module:utils/conf} conf
 * @param {module:utils/constants} constants
 * @param {module:utils/clock} clock
 */
define(["jquery", "knockout", "data/store/incidents", "data/store/units", "data/store/sections",
  "utils/conf", "utils/constants", "utils/clock",
  "./winman", "bootstrap/tooltip", "ko/bindings/draggdroppable", "ko/bindings/popover", "ko/extenders/list"],
  function($, ko, incidents, units, sections, conf, constants, clock) {
    "use strict";

    //Initialize window management
    var taskbar = $("#taskbar").winman();

    /**
     * @exports main/navigation
     */
    var nav = {
      /**
       * Open the incidents overview
       *
       * @param {Object} options
       * @param {String} position
       */
      openIncidents: function(options, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "incident.html",
          pos: position || "left+70% top+30%",
          model: "main/viewmodels/incidents",
          options: options || {},
          save: true
        });
      },
      /**
       * Open a specific incident
       *
       * @param {Object} data Additional incident data
       * @param {String} position
       */
      openIncident: function(data, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "incident_form.html",
          pos: position || "left+30% top+10%",
          model: "main/viewmodels/incident",
          options: data || {}
        });
      },
      /**
       * Open the units overview
       *
       * @param {Object} options Viewmodel options
       * @param {String} position
       */
      openUnits: function(options, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "unit.html",
          pos: position || "left+20% bottom",
          model: "main/viewmodels/units",
          options: options || {},
          save: true
        });
      },
      /**
       * Open the units overview with hierarchical View
       *
       * @param {String} position
       */
      openHierarchyUnits: function(position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "unit_hierarchy.html",
          pos: position || "left top",
          model: "main/viewmodels/hierarchy",
          save: true
        });
      },
      /**
       * Open the unit edit Window
       *
       * @param {Object} data Additional unit data
       * @param {String} position
       */
      openUnit: function(data, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "unit_form.html",
          pos: position || "left+10% top+20%",
          model: "main/viewmodels/unit",
          options: data || {}
        });
      },
      /**
       * Open the unit edit Window
       *
       * @param {integer} id Unit id
       * @param {String} position
       */
      openUnitDetail: function(id, position) {
        if (id) {
          taskbar.winman("addWindow", {
            src: conf.get("contentBase") + "unit_detail.html",
            pos: position || "left+10% top+20%",
            model: "main/viewmodels/unitdetail",
            options: id
          });
        }
      },
      /**
       * Open Add-Log Window
       *
       * @param {Object} data Additional log data
       * @param {String} position
       */
      openLogAdd: function(data, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "log_add.html",
          pos: position || "left+20% top+20%",
          model: "main/viewmodels/customlog",
          options: data || {}
        });
      },
      /**
       * Open a list of log entries
       *
       * @param {Object} options Viewmodel options
       * @param {String} position
       */
      openLogs: function(options, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "log.html",
          pos: position || "left+30% top+10%",
          model: "main/viewmodels/logs",
          options: options || {},
          save: true
        });
      },
      /**
       * Open the patient list Window
       *
       * @param {Object} options Additional options
       * @param {String} position
       */
      openPatients: function(options, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "patient.html",
          pos: position || "left+40% top+30%",
          model: "main/viewmodels/patients",
          options: options || {},
          save: true
        });
      },
      /**
       * Open the patient edit Window
       *
       * @param {Object} data Additional patient data
       * @param {String} position
       */
      openPatient: function(data, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "patient_form.html",
          pos: position || "left+40% top+30%",
          model: "main/viewmodels/patient",
          options: data || {}
        });
      },

      openAlarmText: function(alarmTextData, windowPosition) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "alarm_text_form.html",
          pos: windowPosition || "left+25% top+35%",
          model: "main/viewmodels/alarm",
          options: alarmTextData
        });
      },

      /**
       * Open the radio Window
       *
       * @param {String} position
       */
      openRadio: function(position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "radio.html",
          pos: position || "left+40% top+10%",
          model: "main/viewmodels/radio",
          save: true
        });
      },
      /**
       * Open the map window
       *
       * @param {Object} options Viewmodel options
       * @param {String} position
       */
      openMap: function(options, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + "map.html",
          pos: position || "left+10% top",
          model: "map/viewmodel",
          options: options || {},
          save: true
        });
      },
      /**
       * Open static content
       *
       * @param {string} title
       * @param {string} src
       * @param {String} position
       */
      openStatic: function(title, src, position) {
        taskbar.winman("addWindow", {
          src: conf.get("contentBase") + src,
          pos: position || "left+10% top+20%",
          options: {title: title},
          save: true
        });
      },
      /**
       * Open external static content
       *
       * @param {string} title
       * @param {string} src Full URL to content
       * @param {String} position
       */
      openExternalStatic: function(title, src, position) {
        taskbar.winman("addWindow", {
          src: src,
          pos: position || "left+30% top+10%",
          options: {title: title},
          save: true
        });
      },
      /**
       * Resize the workspace (scrollable dialog container)
       *
       * @param {float} factor
       */
      resizeWorkspace: function(factor) {
        var cont = $("#dialog_container");
        cont.height(factor ? cont.height() * factor : "");
        cont.width(factor ? cont.width() * factor : "");
      }
    };

    nav.sections = sections;

    incidents.filter = sections.filter;
    units.filter = sections.filter;

    /**
     * The current time
     *
     * @function
     * @type ko.computed
     * @returns {string}
     */
    nav.clock_time = clock.timestamp.formatted;

    /**
     * Connection error
     *
     * @function
     * @type ko.observable
     * @returns {boolean}
     */
    nav.connectionError = ko.observable(false);

    var incidentsFiltered = incidents.list.extend({list: {filter: {
          type: [constants.Incident.type.task, constants.Incident.type.transport, constants.Incident.type.relocation],
          isHighlighted: true
        }}});

    /**
     * Open incidents
     *
     * @function
     * @type ko.computed
     * @returns {integer}
     */
    nav.openIncidentCounter = ko.computed(function() {
      return incidentsFiltered().length;
    });

    /**
     * Open transports
     *
     * @function
     * @type ko.computed
     * @returns {integer}
     */
    nav.openTransportCounter = ko.computed(function() {
      return ko.utils.arrayFilter(incidentsFiltered(), function(i) {
        return i.isTransport();
      }).length;
    });

    /**
     * Number of units with TaskState "assigned"
     *
     * @function
     * @type ko.computed
     * @returns {integer}
     */
    nav.radioCounter = ko.computed(function() {
      return ko.utils.arrayFilter(units.list(), function(u) {
        return u.hasAssigned();
      }).length;
    });

    /**
     * Number of "free" units
     *
     * @function
     * @type ko.computed
     * @returns {integer}
     */

    nav.freeCounter = ko.computed(function() {
      return ko.utils.arrayFilter(units.list(), function(u) {
        return u.isFree();
      }).length;
    });

    /**
     * Generate warning CSS
     *
     * @param {integer} count
     * @returns {string}
     */
    nav.getCss = function(count) {
      return count >= 1 ? "notification-highlight" : "notification-ok";
    };

    /**
     * CSS for open incidents
     *
     * @function
     * @type ko.computed
     * @returns {string}
     */
    nav.cssOpen = ko.computed(function() {
      return this.getCss(this.openIncidentCounter());
    }, nav);

    /**
     * CSS for open transports
     *
     * @function
     * @type ko.computed
     * @returns {string}
     */
    nav.cssTransport = ko.computed(function() {
      return this.getCss(this.openTransportCounter());
    }, nav);

    /**
     * CSS for units with TaskState "Assigned"
     *
     * @function
     * @type ko.computed
     * @returns {string}
     */
    nav.cssRadio = ko.computed(function() {
      return this.getCss(this.radioCounter());
    }, nav);

    /**
     * CSS for "free" units
     *
     * @function
     * @type ko.computed
     * @returns {string}
     */
    nav.cssFree = ko.computed(function() {
      return this.getCss(this.freeCounter());
    }, nav);

    /**
     * CSS for connection status
     *
     * @function
     * @type ko.computed
     * @returns {string}
     */
    nav.cssError = ko.computed(function() {
      return this.connectionError() ? "connection-error" : "connection-ok";
    }, nav);

    //Fix dropdowns
    $(document).on("show.bs.dropdown", ".ui-dialog .dropdown", function(event) {
      $(event.target).find(".dropdown-menu").css({top: 0, left: 0}).position({at: "left bottom", my: "left top", of: $(event.target).find(".dropdown-toggle").first()});
      return true;
    });

    $(".tooltipped").tooltip();

    //Initialize key handler
    if (conf.get("keyboardControl")) {
      var keyMapping = conf.get("keyMapping");
      $("#next-state-confirm").keyup(function(event) {
        if (event.which === keyMapping.noKey) {
          $("#next-state-confirm-no").click();
        }
        if (event.which === keyMapping.yesKey) {
          $("#next-state-confirm-yes").click();
        }
      });
      $("body").keyup(function(event) {
        var t = event.target.tagName;
        if (t === "INPUT" || t === "TEXTAREA" || t === "SELECT" || t === "BUTTON") {
          return;
        }
        if (event.which === keyMapping.openIncidentKey) {
          nav.openIncident({section: sections.filter()});
        }
      });
    }

    return nav;
  }
);
