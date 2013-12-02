/**
 * CoCeSo
 * Client JS
 * Copyright (c) WRK\Daniel Rohr
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright (c) 2013 Daniel Rohr
 * @link          https://sourceforge.net/projects/coceso/
 * @package       coceso.client.js
 * @since         Rev. 1
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 *
 * Dependencies:
 *	jquery.js
 *	jquery.ui.menubar.js
 *	jquery.ui.winman.js
 *	knockout-2.3.0.js
 */

var Coceso = {
  /**
   * Some global settings
   *
   * @type Object
   */
  Conf: {
    interval: 10000,
    contentBase: "content/",
    jsonBase: "../data/"
  },
  /**
   * Initialize the application
   *
   * @return {void}
   */
  startup: function() {
    //Initialize the menubar and window management
    $("#menubar").menubar();
    $("#taskbar").winman();

    //Preload incidents and units
    Coceso.Ajax.getAll("incidents", "incident/getAll.json", Coceso.Conf.interval);
    Coceso.Ajax.getAll("units", "unit/getAll.json", Coceso.Conf.interval);
  },
  /**
   * Contains all the viewmodels
   *
   * @type Object
   */
  ViewModels: {
    /**
     * List of units
     *
     * @constructor
     * @param {Object} data An object containing the units
     */
    Units: function(data) {
      var self = this;

      //Create and populate observables
      ko.mapping.fromJS(data, {
        units: {
          key: function(data) {
            return ko.utils.unwrapObservable(data.id);
          },
          create: function(options) {
            return new Coceso.ViewModels.Unit(options.data);
          }
        }
      }, this);

      /**
       * Renew the units
       *
       * @param {Object} data An object containing the units
       * @return {void}
       */
      this.setData = function(data) {
        ko.mapping.fromJS(data, self);
      };
    },
    /**
     * A single unit
     *
     * @constructor
     * @param {Object} data An object containing the unit's properties
     * @param {boolean} writeable
     */
    Unit: function(data, writeable) {
      //Create and populate observables
      ko.mapping.fromJS(data, {}, this);

      /**
       * CSS class based on the unit's state
       *
       * @type ko.computed
       * @return {string} The CSS class
       */
      this.stateCss = ko.computed(function() {
        return "unit_state_" + this.state().toLowerCase();
      }, this);

      /**
       * Options for dragging
       *
       * @type Object
       */
      this.dragOptions = {
        helper: "clone",
        appendTo: "body",
        cursor: "move",
        zIndex: 1500
      };
    },
    /**
     * List of incidents
     *
     * @constructor
     * @param {Object} data An object containing the incidents
     */
    Incidents: function(data) {
      var self = this;

      //Create and populate observables
      ko.mapping.fromJS(data, {
        incidents: {
          key: function(data) {
            return ko.utils.unwrapObservable(data.id);
          },
          create: function(options) {
            return new Coceso.ViewModels.Incident(options.data);
          }
        }
      }, this);



      this.tabs = [
        {
          title: "Emergency",
          filter: {
            type: "Task",
            blue: true
          }
        },
        {
          title: "Task",
          filter: {
            type: "Task",
            blue: false
          }
        },
        {
          title: "Relocation",
          filter: {
            type: "Relocation"
          }
        }
      ];

      this.selectedTab = ko.observable();

      this.tabOptions = {
        create: function(event, ui) {
          self.selectedTab($(ui.tab).data("tabindex"));
        },
        beforeActivate: function(event, ui) {
          self.selectedTab($(ui.newTab).data("tabindex"));
        }
      };

      this.filtered = ko.computed(function() {
        var filters = self.tabs[self.selectedTab()] ? self.tabs[self.selectedTab()].filter : {};
        incidents = this.incidents();
        return ko.utils.arrayFilter(incidents, function(incident) {
          for (i in filters) {
            if (incident[i] && incident[i]() !== filters[i]) {
              return false;
            }
          }
          return true;
        });
      }, this);

      this.accordionOptions = ko.computed(function() {
        console.log("bla");
        return {active: false, collapsible: true, subscribe: this.filtered()};
      }, this);

      /**
       * Renew the incidents
       *
       * @param {Object} data An object containing the incidents
       * @return {void}
       */
      this.setData = function(data) {
        ko.mapping.fromJS(data, self);
      };
    },
    /**
     * A single incident
     *
     * @constructor
     * @param {Object} data An object containing the incident's properties
     * @param {Object} options Additional options
     */
    Incident: function(data, options) {
      var self = this;
      this.options = options || {};

      //Create and populate observables
      ko.mapping.fromJS(data, {}, this);

      /**
       * Original data to recognize local changes
       *
       * @type ko.observable (of Object)
       */
      this.orig = ko.observable(data);

      /**
       * Conflicting remote changes
       *
       * @type ko.observable (of Object)
       */
      this.remoteChanges = ko.observable({});

      /**
       * Return if data has been changed by the user
       *
       * @type ko.computed
       * @return {boolean}
       */
      this.changed = ko.computed(function() {
        if (!this.options.writeable) {
          return false;
        }
        orig = ko.utils.unwrapObservable(this.orig);
        for (i in orig) {
          if (orig[i] !== ko.utils.unwrapObservable(this[i])) {
            return true;
          }
        }
        return false;
      }, this);

      /**
       * Generate the incident's title
       *
       * @type ko.computed
       * @return {String}
       */
      this.title = ko.computed(function() {
        return this.type() + " " + this.bo();
      }, this);

      /**
       * The droppable handler
       *
       * @param {Event} event The jQuery Event (unused)
       * @param {Object} ui jQuery UI properties
       * @return {void}
       */
      this.drop = function(event, ui) {
        if ($(ui.draggable.context).data("unitid")) {
          //TODO
          alert("Assign Unit " + $(ui.draggable.context).data("unitid") + " to Incident " + self.id());
        }
      };

      /**
       * The droppable options
       *
       * @type Object
       */
      this.dropOptions = {
        drop: self.drop
      };

      /**
       * Open in a form
       *
       * @return {void}
       */
      this.openForm = function() {
        Coceso.UI.openIncident("Edit Incident", "incident_form.html", {id: self.id()});
      };

      /**
       * Load the incident data
       *
       * @param {int} interval The interval to reload. 0 or false for no autoload.
       * @return {void}
       */
      this.load = function(interval) {
        id = ko.utils.unwrapObservable(self.id);
        if (id !== null) {
          Coceso.Ajax.get(self, "incident/get/" + id + ".json", interval);
        }
      };

      /**
       * Save the modified incident data
       *
       * @return {void}
       */
      this.save = function() {
        //TODO
      };

      if (this.options.autoload) {
        this.load(this.options.autoload);
      }
    }
  },
  /**
   * Contains UI related functions and data
   *
   * @type Object
   */
  UI: {
    /**
     * A list of all opened windows
     *
     * @type Object
     */
    windows: {},
    /**
     * Add a window to the UI
     *
     * @param {String} title The title of the window
     * @param {String} src The source to load the HTML from
     * @param {ViewModel} viewmodel The viewmodel to bind with
     * @return {void}
     */
    openWindow: function(title, src, viewmodel) {
      id = $("#taskbar").winman("addWindow", title, src, function(el) {
        ko.applyBindings(viewmodel, el);
      });
      this.windows[id] = viewmodel;
    },
    /**
     * Open the units overview
     *
     * @param {String} title
     * @param {String} src
     * @return {void}
     */
    openUnits: function(title, src) {
      viewmodel = new Coceso.ViewModels.Units(Coceso.Ajax.data.units);
      Coceso.Ajax.subscribe("units", viewmodel.setData);
      this.openWindow(title, Coceso.Conf.contentBase + src, viewmodel);
    },
    /**
     * Open the incidents overview
     *
     * @param {String} title
     * @param {String} src
     * @return {void}
     */
    openIncidents: function(title, src) {
      viewmodel = new Coceso.ViewModels.Incidents(Coceso.Ajax.data.incidents);
      Coceso.Ajax.subscribe("incidents", viewmodel.setData);
      this.openWindow(title, Coceso.Conf.contentBase + src, viewmodel);
    },
    /**
     * Open a specific incident
     *
     * @param {String} title
     * @param {String} src
     * @param {Object} data Additional incident data
     * @return {void}
     */
    openIncident: function(title, src, data) {
      data = $.extend(true, {
        id: null,
        state: "New",
        priority: 10,
        blue: false,
        units: {},
        bo: null,
        ao: null,
        casusNr: null,
        info: null,
        caller: null,
        type: null
      }, data || {});
      viewmodel = new Coceso.ViewModels.Incident(data, {writeable: true, autoload: Coceso.Conf.interval});
      this.openWindow(title, Coceso.Conf.contentBase + src, viewmodel);
    }
  },
  /**
   * AJAX related functions and data
   *
   * @type Object
   */
  Ajax: {
    /**
     * Preloaded data
     *
     * @type Object
     */
    data: {
      incidents: {},
      units: {}
    },
    /**
     * Subscriptions to data loading
     *
     * @type Object
     */
    subscriptions: {
      incidents: [],
      units: []
    },
    /**
     * Load the specified data
     *
     * @param {String} type The data type
     * @param {String} url The URL to load from
     * @param {int} interval The interval to reload. 0 or false for no autoload.
     * @return {void}
     */
    getAll: function(type, url, interval) {
      $.ajax({
        dataType: "json",
        url: Coceso.Conf.jsonBase + url,
        ifModified: true,
        success: function(data, status) {
          if (status !== "notmodified") {
            Coceso.Ajax.data[type][type] = data;
            for (var i = 0; i < Coceso.Ajax.subscriptions[type].length; i++) {
              Coceso.Ajax.subscriptions[type][i](Coceso.Ajax.data[type]);
            }
          }
        },
        complete: function() {
          if (interval) {
            window.setTimeout(function() {
              Coceso.Ajax.getAll(type, url, interval);
            }, interval);
          }
        }
      });
    },
    /**
     * Subscribe to the loading of specified data
     *
     * @param {String} type The data type
     * @param {Function} func The callback function
     * @return {void}
     */
    subscribe: function(type, func) {
      if (this.subscriptions[type]) {
        this.subscriptions[type].push(func);
      }
    },
    /**
     * Load a single item into a viewmodel
     *
     * @param {ViewModel} viewmodel
     * @param {String} url The URL to load from
     * @param {int} interval The interval to reload. 0 or false for no autoload.
     * @return {void}
     */
    get: function(viewmodel, url, interval) {
      $.ajax({
        dataType: "json",
        url: Coceso.Conf.jsonBase + url,
        ifModified: true,
        success: function(data, status) {
          if (status !== "notmodified") {
            remoteChanges = {};
            newData = $.extend(true, {}, data);
            newOrig = $.extend(true, {}, data);
            if (viewmodel.changed()) {
              //Some data was locally edited
              orig = ko.utils.unwrapObservable(viewmodel.orig);
              for (var i in data) {
                viewItem = ko.utils.unwrapObservable(viewmodel[i]);
                if ((typeof orig[i] !== "undefined") && (viewItem !== orig[i]) && (viewItem !== data[i])) {
                  newData[i] = viewItem;
                  if (data[i] !== orig[i]) {
                    newOrig[i] = orig[i];
                    remoteChanges[i] = data[i];
                  }
                }
              }
            }

            ko.mapping.fromJS(newData, {}, viewmodel);
            viewmodel.orig(newOrig);
            viewmodel.remoteChanges(remoteChanges);
          }
        },
        complete: function() {
          if (interval) {
            window.setTimeout(function() {
              Coceso.Ajax.get(viewmodel, url, interval);
            }, interval);
          }
        }
      });
    }
  }
};
