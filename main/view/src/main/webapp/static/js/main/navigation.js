/**
 * CoCeSo
 * Client JS - main/navigation
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
    $("#taskbar").winman();

    /**
     * @exports main/navigation
     */
    var nav = {
      /**
       * Add a window to the UI
       *
       * @param {string} src The source to load the HTML from
       * @param {Object} viewmodel The viewmodel to bind with
       * @param {Object} options
       * @param {string} title The title of the window
       */
      openWindow: function(src, viewmodel, options, title) {
        $("#taskbar").winman("addWindow", src, options, viewmodel, title);
      },
      /**
       * Open the incidents overview
       *
       * @param {Object} options
       * @param {Object} dialog Dialog options
       */
      openIncidents: function(options, dialog) {
        require(["main/viewmodels/incidents"], function(Incidents) {
          nav.openWindow(conf.get("contentBase") + "incident.html", new Incidents(options || {}), $.extend({position: {at: "left+70% top+30%"}}, dialog));
        });
      },
      /**
       * Open a specific incident
       *
       * @param {Object} data Additional incident data
       * @param {Object} dialog Dialog options
       */
      openIncident: function(data, dialog) {
        require(["main/viewmodels/incident"], function(Incident) {
          nav.openWindow(conf.get("contentBase") + "incident_form.html", new Incident(data || {}), $.extend({position: {at: "left+30% top+10%"}}, dialog));
        });
      },
      /**
       * Open the units overview
       *
       * @param {Object} options Viewmodel options
       * @param {Object} dialog Dialog options
       */
      openUnits: function(options, dialog) {
        require(["main/viewmodels/units"], function(Units) {
          nav.openWindow(conf.get("contentBase") + "unit.html", new Units(options || {}), $.extend({position: {at: "left+20% bottom"}}, dialog));
        });
      },
      /**
       * Open the units overview with hierarchical View
       *
       * @param {Object} dialog Dialog options
       */
      openHierarchyUnits: function(dialog) {
        require(["main/viewmodels/hierarchy"], function(Hierarchy) {
          nav.openWindow(conf.get("contentBase") + "unit_hierarchy.html", new Hierarchy(), $.extend({position: {at: "left top"}}, dialog));
        });
      },
      /**
       * Open the unit edit Window
       *
       * @param {Object} data Additional unit data
       * @param {Object} dialog Dialog options
       */
      openUnit: function(data, dialog) {
        require(["main/viewmodels/unit"], function(Unit) {
          nav.openWindow(conf.get("contentBase") + "unit_form.html", new Unit(data || {}), $.extend({position: {at: "left+10% top+20%"}}, dialog));
        });
      },
      /**
       * Open the unit edit Window
       *
       * @param {integer} id Unit id
       * @param {Object} dialog Dialog options
       */
      openUnitDetail: function(id, dialog) {
        if (id) {
          require(["main/viewmodels/unitdetail"], function(UnitDetail) {
            nav.openWindow(conf.get("contentBase") + "unit_detail.html", new UnitDetail(id), $.extend({position: {at: "left+10% top+20%"}}, dialog));
          });
        }
      },
      /**
       * Open Add-Log Window
       *
       * @param {Object} data Additional log data
       * @param {Object} dialog Dialog options
       */
      openLogAdd: function(data, dialog) {
        require(["main/viewmodels/customlog"], function(CustomLogEntry) {
          nav.openWindow(conf.get("contentBase") + "log_add.html", new CustomLogEntry(data || {}), $.extend({position: {at: "left+20% top+20%"}}, dialog));
        });
      },
      /**
       * Open a list of log entries
       *
       * @param {Object} options Viewmodel options
       * @param {Object} dialog Dialog options
       */
      openLogs: function(options, dialog) {
        require(["main/viewmodels/logs"], function(Logs) {
          nav.openWindow(conf.get("contentBase") + "log.html", new Logs(options || {}), $.extend({position: {at: "left+30% top+10%"}}, dialog));
        });
      },
      /**
       * Open the patient list Window
       *
       * @param {Object} options Additional options
       * @param {Object} dialog Dialog options
       */
      openPatients: function(options, dialog) {
        require(["main/viewmodels/patients"], function(Patients) {
          nav.openWindow(conf.get("contentBase") + "patient.html", new Patients(options || {}), $.extend({position: {at: "left+40% top+30%"}}, dialog));
        });
      },
      /**
       * Open the patient edit Window
       *
       * @param {Object} data Additional patient data
       * @param {Object} dialog Dialog options
       */
      openPatient: function(data, dialog) {
        require(["main/viewmodels/patient"], function(Patient) {
          nav.openWindow(conf.get("contentBase") + "patient_form.html", new Patient(data || {}), $.extend({position: {at: "left+40% top+30%"}}, dialog));
        });
      },
      /**
       * Open the radio Window
       *
       * @param {Object} dialog Dialog options
       */
      openRadio: function(dialog) {
        require(["main/viewmodels/radio"], function(Radio) {
          nav.openWindow(conf.get("contentBase") + "radio.html", new Radio(), $.extend({position: {at: "left+40% top+10%"}}, dialog));
        });
      },
      /**
       * Open the map window
       *
       * @param {Object} options Viewmodel options
       * @param {Object} dialog Dialog options
       */
      openMap: function(options, dialog) {
        require(["map/viewmodel"], function(Map) {
          nav.openWindow(conf.get("contentBase") + "map.html", new Map(options || {}), $.extend({position: {at: "left+10% top"}}, dialog));
        });
      },
      /**
       * Open static content
       *
       * @param {string} title
       * @param {string} src
       * @param {Object} dialog Dialog options
       */
      openStatic: function(title, src, dialog) {
        nav.openWindow(conf.get("contentBase") + src, {}, $.extend({position: {at: "left+10% top+20%"}}, dialog), title);
      },
      /**
       * Open external static content
       *
       * @param {string} title
       * @param {string} src Full URL to content
       * @param {Object} dialog Dialog options
       */
      openExternalStatic: function(title, src, dialog) {
        nav.openWindow(src, {}, $.extend({position: {at: "left+30% top+10%"}}, dialog), title);
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
    $(document).on("click", ".panel-toggle", function() {
      $(this).parent(".panel").children(".panel-body").slideToggle();
      $(this).find(".glyphicon").toggleClass("glyphicon-chevron-down glyphicon-chevron-up");
    });

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
          nav.openIncident();
        }
      });
    }

    return nav;
  }
);
