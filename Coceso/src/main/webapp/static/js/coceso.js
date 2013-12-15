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
 *	knockout.js
 *	knockout.mapping.js
 *	coceso.client.winman
 */


/**
 * Object containing the main code
 *
 * @namespace Coceso
 * @type Object
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
    jsonBase: "data/"
  },
  /**
   * Initialize the application
   *
   * @return {void}
   */
  startup: function() {
    //Initialize window management
    $("#taskbar").winman();

    //Preload incidents and units
    Coceso.Ajax.getAll("incidents", "incident/getAll.json", Coceso.Conf.interval);
    Coceso.Ajax.getAll("units", "unit/getAll.json", Coceso.Conf.interval);
  },
  /**
   * Constants for some values (states, types)
   *
   * @type Object
   */
  Constants: {
    Unit: {
      state: {
        ad: "AD",
        eb: "EB",
        neb: "NEB"
      }
    },
    Incident: {
      type: {
        holdposition: "HoldPosition",
        relocation: "Relocation",
        sendhome: "SendHome",
        standby: "Standby",
        task: "Task"
      },
      state: {
        new : "New",
        open: "Open",
        dispo: "Dispo",
        working: "Working",
        done: "Done"
      }
    },
    TaskState: {
      assigned: "Assigned",
      zbo: "ZBO",
      abo: "ABO",
      zao: "ZAO",
      aao: "AAO",
      detached: "Detached"
    }
  },
  /**
   * Contains all the models
   *
   * @namespace Coceso.Models
   * @type Object
   */
  Models: {
    /**
     * Incident Dummy
     *
     * @type Coceso.Models.Incident
     */
    Incident: {
      id: null,
      state: "New",
      priority: 0,
      blue: false,
      units: {},
      bo: "",
      ao: "",
      casusNr: "",
      info: "",
      caller: "",
      type: null,
      taskState: null
    },
    /**
     * Unit dummy
     *
     * @type Coceso.Models.Unit
     */
    Unit: {
      id: null,
      state: "AD",
      call: null,
      ani: null,
      withDoc: false,
      portable: false,
      transportVehicle: false,
      crew: [],
      info: null,
      position: null,
      home: null,
      incidents: {},
      taskState: null
    },
    /**
     * Point dummy
     *
     * @type Coceso.Models.Point
     */
    Point: {
      id: null,
      info: null,
      longitude: null,
      latitude: null
    }
  },
  /**
   * Contains all ViewModels (including baseclasses)
   *
   * @namespace Coceso.ViewModels
   * @type Object
   */
  ViewModels: {},
  /**
   * Contains UI related functions and data
   *
   * @namespace Coceso.UI
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
      var id = $("#taskbar").winman("addWindow", title, src, function(el, id) {
        viewmodel.ui = id;
        ko.applyBindings(viewmodel, el);
      }, function(el, id) {
        if (typeof viewmodel.destroy === "function") {
          viewmodel.destroy.call(viewmodel);
        }
        ko.cleanNode(el);
        delete Coceso.UI.windows[id];
      });
      this.windows[id] = viewmodel;
    },
    /**
     * Open the units overview
     *
     * @param {String} title
     * @param {String} src
     * @param {Object} options
     * @return {void}
     */
    openUnits: function(title, src, options) {
      this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Units({}, options || {}));
      return false;
    },
    /**
     * Open the incidents overview
     *
     * @param {String} title
     * @param {String} src
     * @param {Object} options
     * @return {void}
     */
    openIncidents: function(title, src, options) {
      this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Incidents({}, options || {}));
      return false;
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
      this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Incident(data || {}));
      return false;
    },
    /**
     * Open a list of log entries
     *
     * @param {String} title
     * @param {String} src
     * @param {Object} options
     * @return {void}
     */
    openLogs: function(title, src, options) {
      this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Logs({}, options || {}));
      return false;
    },
    /**
     * Open static content
     *
     * @param {String} title
     * @param {String} src
     * @return {void}
     */
    openStatic: function(title, src) {
      this.openWindow(title, Coceso.Conf.contentBase + src, {});
      return false;
    }
  },
  /**
   * AJAX related functions and data
   *
   * @namespace Coceso.Ajax
   * @type Object
   */
  Ajax: {
    /**
     * Preloaded data
     *
     * @type Object
     */
    data: {
      incidents: {incidents: []},
      units: {units: []}
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
            ko.utils.arrayForEach(Coceso.Ajax.subscriptions[type], function(item) {
              if (typeof item === "function") {
                item(Coceso.Ajax.data[type]);
              }
            });
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
     * Unsubscribe from loading of specified data
     *
     * @param {String} type The data type
     * @param {Function} func The callback function
     * @return {void}
     */
    unsubscribe: function(type, func) {
      if (this.subscriptions[type]) {
        var subscriptions = this.subscriptions[type];
        ko.utils.arrayForEach(subscriptions, function(item) {
          if (item === func) {
            ko.utils.arrayRemoveItem(subscriptions, item);
          }
        });
      }
    },
    /**
     * Save entries with POST
     *
     * @param {Object} data
     * @param {String} url
     * @param {Function} callback
     * @returns {void}
     */
    save: function(data, url, callback) {
      $.ajax({
        type: "POST",
        url: Coceso.Conf.jsonBase + url,
        dataType: "json",
        contentType: "application/json",
        data: ko.toJSON(data),
        processData: false,
        success: function(data, status) {
          alert("success");
        },
        error: function() {
          alert("error");
        }
      });
    },
    /**
     * Load a single item into a viewmodel
     *
     * NOT USED
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
            //Not used
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


/**
 * Base class for all ViewModels
 *
 * @constructor
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.ViewModel = function(data, options) {
  /**
   * Basic options for the model
   *
   * @type Object
   */
  this.options = options || {};

  //Create and populate observables
  ko.mapping.fromJS(data, this.mappingOptions, this);

  //Subscribe to updates
  if (this.getOption("reload") && this.dataType && (typeof this.setData === "function")) {
    Coceso.Ajax.subscribe(this.dataType, this.setData);
  }
};
Coceso.ViewModels.ViewModel.prototype = Object.create({}, /** @lends Coceso.ViewModels.ViewModel.prototype */ {
  /**
   * The entity used in this model
   *
   * @type String
   */
  dataType: {value: null},
  /**
   * Options for mapping
   *
   * @type Object
   */
  mappingOptions: {value: {}},
  /**
   * Read the specified option value
   *
   * @function
   * @param {String|Array} key The option to get
   * @param {mixed} val The default value
   * @return {mixed}
   */
  getOption: {
    value: function(key, val) {
      if (typeof val === "undefined") {
        val = false;
      }

      if (typeof key === "string") {
        return (typeof this.options[key] !== "undefined") ? this.options[key] : val;
      }

      var i, current = this.options;
      for (i in key) {
        if (typeof current[key[i]] === "undefined") {
          return val;
        }
        current = current[key[i]];
      }
      return current;
    }
  },
  /**
   * Compare to objects
   *
   * @function
   * @param {mixed} a
   * @param {mixed} b
   * @return {boolean}
   */
  objEqual: {
    value: function(a, b) {
      a = ko.utils.unwrapObservable(a);
      b = ko.utils.unwrapObservable(b);

      if ((a instanceof Object) && (b instanceof Object)) {
        var key;
        for (key in a) {
          if (a.hasOwnProperty(key) !== b.hasOwnProperty(key)) {
            return false;
          }
        }
        for (key in b) {
          if (a.hasOwnProperty(key) !== b.hasOwnProperty(key)) {
            return false;
          }
          if (!this.objEqual.call(this, a[key], b[key])) {
            return false;
          }
        }

        return true;
      }

      return (a === b);
    }
  },
  /**
   * Destroy the ViewModel
   *
   * @function
   * @return {void}
   */
  destroy: {
    value: function() {
      //Unsubscribe from updates
      if (this.dataType && (typeof this.setData === "function")) {
        Coceso.Ajax.unsubscribe(this.dataType, this.setData);
      }
    }
  }
});

/**
 * Base class for all list style ViewModels
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModel
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.ViewModelList = function(data, options) {
  var self = this;

  //Set default options: Autoload list, don't load children
  options = $.extend({
    initial: true,
    reload: true,
    children: {
      initial: false,
      reload: false,
      writeable: false
    }
  }, options);

  /**
   * Method to set refreshed data
   *
   * @param {Object} data The refreshed data object
   * @return {void}
   */
  this.setData = function(data) {
    ko.mapping.fromJS(data, self);
  };

  if (options.initial && this.dataType) {
    //Inital loading: take preloaded ajax data
    data = $.extend({}, Coceso.Ajax.data[this.dataType], data);
  }

  //Call super constructor
  Coceso.ViewModels.ViewModel.call(this, data, options);
};
Coceso.ViewModels.ViewModelList.prototype = Object.create(Coceso.ViewModels.ViewModel.prototype, /** @lends Coceso.ViewModels.ViewModelList.prototype */ {});

/**
 * Base class for all single element ViewModels
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModel
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.ViewModelSingle = function(data, options) {
  var self = this,
    orig = {};

  options = $.extend({
    initial: true,
    reload: true,
    writeable: true,
    assigned: true,
    children: {
      //Don't autoload list of assigned data
      initial: false,
      reload: false,
      children: {
        //Autoload assigned data, but not another level of assigned data
        initial: true,
        reload: true,
        writeable: false,
        assigned: false
      }
    }
  }, options);

  if (options.initial && this.dataType && data.id) {
    orig = ko.utils.arrayFirst(Coceso.Ajax.data[this.dataType][this.dataType], function(item) {
      return (item.id === data.id);
    }) || {};
  }

  orig = $.extend({}, this.model, orig);
  data = $.extend({}, orig, data);

  /**
   * Method to set refreshed data
   *
   * @param {Object} data The refreshed data object
   * @return {void}
   */
  this.setData = function(data) {
    data = ko.utils.arrayFirst(data[self.dataType], function(item) {
      return (item.id === self.id());
    });

    if (data) {
      var remoteChanges = {},
        newData = $.extend(true, {}, data),
        newOrig = $.extend(true, {}, data);

      if (self.changed()) {
        //Some data was locally edited
        orig = ko.utils.unwrapObservable(self.orig);
        for (var i in self.keepChanges) {
          var viewItem = ko.utils.unwrapObservable(self[i]);
          if ((typeof data[i] !== "undefined") && (typeof orig[i] !== "undefined") && (viewItem !== orig[i]) && (viewItem !== data[i])) {
            newData[i] = viewItem;
            if (data[i] !== orig[i]) {
              newOrig[i] = orig[i];
              remoteChanges[i] = data[i];
            }
          }
        }
      }

      ko.mapping.fromJS(newData, self);
      self.orig(newOrig);
      self.remoteChanges(remoteChanges);
    }
  };

  Coceso.ViewModels.ViewModel.call(this, data, options);

  /**
   * Original data to recognize local changes
   *
   * @type ko.observable
   */
  this.orig = ko.observable(orig);

  /**
   * Conflicting remote changes
   *
   * @type ko.observable
   */
  this.remoteChanges = ko.observable({});

  /**
   * Return if data has been changed by the user
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.changed = ko.computed(function() {
    if (!this.getOption("writeable")) {
      return false;
    }
    orig = ko.utils.unwrapObservable(this.orig);
    for (i in orig) {
      if (!this.objEqual(orig[i], ko.utils.unwrapObservable(this[i]))) {
        if (typeof this.compare[i] === "function") {
          if (this.compare[i].call(this, orig[i], ko.utils.unwrapObservable(this[i]))) {
            return true;
          }
        } else {
          return true;
        }
      }
    }
    return false;
  }, this);

  /**
   * Return if TaskState is "Assigned"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isAssigned = ko.computed(function() {
    return (this.taskState && (this.taskState() === Coceso.Constants.TaskState.assigned));
  }, this);

  /**
   * Return if TaskState is "ZBO"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isZBO = ko.computed(function() {
    return (this.taskState && (this.taskState() === Coceso.Constants.TaskState.zbo));
  }, this);

  /**
   * Return if TaskState is "ABO"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isABO = ko.computed(function() {
    return (this.taskState && (this.taskState() === Coceso.Constants.TaskState.abo));
  }, this);

  /**
   * Return if TaskState is "ZAO"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isZAO = ko.computed(function() {
    return (this.taskState && (this.taskState() === Coceso.Constants.TaskState.zao));
  }, this);

  /**
   * Return if TaskState is "AAO"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isAAO = ko.computed(function() {
    return (this.taskState && (this.taskState() === Coceso.Constants.TaskState.aao));
  }, this);

  /**
   * Return if TaskState is "Detached"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isDetached = ko.computed(function() {
    return (this.taskState && (this.taskState() === Coceso.Constants.TaskState.detached));
  }, this);
};
Coceso.ViewModels.ViewModelSingle.prototype = Object.create(Coceso.ViewModels.ViewModel.prototype, /** @lends Coceso.ViewModels.ViewModelSingle.prototype */ {
  /**
   * The Model used in this ViewModel
   *
   * @type Coceso.Model
   */
  model: {value: null},
  /**
   * The URL to send the POST to
   *
   * @type String
   */
  saveUrl: {value: null},
  /**
   * Local changes to prioritize over server changes
   *
   * @type Object
   */
  keepChanges: {
    value: {
      info: true
    }
  },
  /**
   * Save modified data
   *
   * @function
   * @return {boolean}
   */
  save: {
    value: function() {
      if (!this.getOption("writeable") || !this.saveUrl) {
        return false;
      }

      Coceso.Ajax.save(ko.mapping.toJS(this, {ignore: ["incidents", "units", "taskState"]}), this.saveUrl);
      return true;
    }
  },
  /**
   * Specific comparison functions for some members
   *
   * @type Object
   */
  compare: {value: {}},
  /**
   * Options for draggables
   *
   * @type Object
   */
  dragOptions: {
    value: {
      helper: "clone",
      appendTo: "body",
      cursor: "move",
      zIndex: 1500
    }
  }
});

/**
 * List of incidents
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModelList
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Incidents = function(data, options) {
  //Call super constructor
  Coceso.ViewModels.ViewModelList.call(this, data, options);

  /**
   * Available filters
   *
   * @type Object
   */
  var filters = {
    tabs: [
      {
        filter: {
          type: "Task",
          blue: true
        }
      },
      {
        filter: {
          type: "Task",
          blue: false
        }
      },
      {
        filter: {
          type: "Relocation"
        }
      }
    ],
    option: {
      overview: {
        filter: {
          type: {val: ["Task", "Relocation"]}
        }
      },
      active: {
        filter: {
          state: {op: "not", val: "Done"}
        }
      },
      "new": {
        filter: {
          state: "New"
        }
      },
      open: {
        filter: {
          state: "Open"
        }
      },
      completed: {
        filter: {
          state: "Done"
        }
      }
    }
  };

  /**
   * Whether to show tabs
   *
   * @type boolean
   */
  this.showTabs = this.getOption("showTabs", true);

  /**
   * The selected tab in the incidents list (corresponds to a key in filters.tabs)
   *
   * @type ko.observable
   */
  this.selectedTab = ko.observable(this.getOption("selectedTab", "0"));

  /**
   * Generate a list of active filters
   *
   * @function
   * @type ko.computed
   * @return {Object}
   */
  this.activeFilters = ko.computed(function() {
    var activeFilters = {filter: []},
    filterOption = this.getOption("filter", []);

    if (this.showTabs && filters.tabs[this.selectedTab()]) {
      activeFilters.filter.push(filters.tabs[this.selectedTab()]);
    }

    var i;
    for (i in filterOption) {
      if (filters.option[filterOption[i]]) {
        activeFilters.filter.push(filters.option[filterOption[i]]);
      }
    }

    return activeFilters;
  }, this);

  /**
   * Filtered view of the incidents array
   *
   * @function
   * @type ko.computed
   * @return {Array}
   */
  this.filtered = this.incidents.extend({filtered: this.activeFilters});

  /**
   * The accordion view options
   * Has to depend on the used data array for the accordion to update correctly
   *
   * @function
   * @type ko.computed
   * @return {Object}
   */
  this.accordionOptions = ko.computed(function() {
    return {active: false, collapsible: true, heightStyle: "content"};
  }, this);
};
Coceso.ViewModels.Incidents.prototype = Object.create(Coceso.ViewModels.ViewModelList.prototype, /** @lends Coceso.ViewModels.Incidents.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#dataType
   * @override
   */
  dataType: {value: "incidents"},
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      incidents: {
        key: function(data) {
          return ko.utils.unwrapObservable(data.id);
        },
        create: function(options) {
          return new Coceso.ViewModels.Incident(options.data, options.parent.getOption("children", {}));
        }
      }
    }
  }
});

/**
 * Single incident
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModelSingle
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Incident = function(data, options) {
  var self = this;

  Coceso.ViewModels.ViewModelSingle.call(this, data, options);

  /**
   * Force priority to be an integer
   *
   * @function
   * @type ko.computed
   * @return {Integer}
   */
  this.priority = this.priority.extend({integer: true});

  /**
   * Incident is of type "Task"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isTask = ko.computed(function() {
    return (this.type() === Coceso.Constants.Incident.type.task);
  }, this);

  /**
   * Incident is of type "Relocation"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isRelocation = ko.computed(function() {
    return (this.type() === Coceso.Constants.Incident.type.relocation);
  }, this);

  /**
   * Incident has state "New"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isNew = ko.computed(function() {
    return (this.state() === Coceso.Constants.Incident.state.new );
  }, this);

  /**
   * Incident has state "Open"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isOpen = ko.computed(function() {
    return (this.state() === Coceso.Constants.Incident.state.open);
  }, this);

  /**
   * Incident has state "Dispo"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isDispo = ko.computed(function() {
    return (this.state() === Coceso.Constants.Incident.state.dispo);
  }, this);

  /**
   * Incident has state "Working"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isWorking = ko.computed(function() {
    return (this.state() === Coceso.Constants.Incident.state.working);
  }, this);

  /**
   * Incident has state "Done"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isDone = ko.computed(function() {
    return (this.state() === Coceso.Constants.Incident.state.done);
  }, this);

  /**
   * Enable the "Task" type button
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.enableTask = ko.computed(function() {
    return (this.getOption("writeable") && (!this.id() || this.isTask()));
  }, this);

  /**
   * Enable the "Relocation" type button
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.enableRelocation = ko.computed(function() {
    return (this.getOption("writeable") && (!this.id() || this.isRelocation()));
  }, this);

  /**
   * Enable BO field
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.enableBO = ko.computed(function() {
    return !this.isRelocation();
  }, this);

  /**
   * Allow IncidentState New
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.enableNew = ko.computed(function() {
    return (this.getOption("writeable") && (!this.id() || (this.orig().state === Coceso.Constants.Incident.state.new )));
  }, this);

  /**
   * Allow IncidentState Dispo
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.enableDispo = ko.computed(function() {
    if (!this.getOption("writeable") || !this.units.units) {
      return false;
    }

    return (ko.utils.arrayFirst(this.units.units(), function(unit) {
      return (unit.isAssigned() || unit.isZBO());
    }) !== null);
  }, this);

  /**
   * Allow IncidentState Working
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.enableWorking = ko.computed(function() {
    if (!this.getOption("writeable") || !this.units.units) {
      return false;
    }

    return (ko.utils.arrayFirst(this.units.units(), function(unit) {
      return (unit.isABO() || unit.isZAO() || unit.isAAO());
    }) !== null);
  }, this);

  /**
   * Assign a unit within the form
   *
   * @param {Event} event The jQuery Event (unused)
   * @param {Object} ui jQuery UI properties
   * @return {void}
   */
  this.assignUnitForm = function(event, ui) {
    var viewmodel = ko.dataFor(ui.draggable.context);
    if (!viewmodel instanceof Coceso.ViewModels.Unit) {
      return;
    }
    var unitid = ko.utils.unwrapObservable(viewmodel.id);
    if (unitid && self.units.units) {
      var assigned = ko.utils.arrayFirst(self.units.units(), function(unit) {
        return (unit.id() === unitid);
      });
      if (assigned === null) {
        self.units.units.push(new Coceso.ViewModels.Unit({id: unitid, taskState: "Assigned"}, self.getOption(["children", "children"], {assigned: false, writeable: false})));
      }
    }
  };

  /**
   * The droppable handler
   *
   * @param {Event} event The jQuery Event (unused)
   * @param {Object} ui jQuery UI properties
   * @return {void}
   */
  this.assignUnitList = function(event, ui) {
    var unit = ko.dataFor(ui.draggable.context);

    if ((unit instanceof Coceso.ViewModels.Unit) && self.id() && unit.id()) {
      Coceso.Ajax.save({}, "assignUnit/" + self.id() + "/" + unit.id());
    }
  };

  /**
   * Open in a form
   *
   * @return {void}
   */
  this.openForm = function() {
    Coceso.UI.openIncident("Edit Incident", "incident_form.html", {id: self.id()});
  };
};
Coceso.ViewModels.Incident.prototype = Object.create(Coceso.ViewModels.ViewModelSingle.prototype, /** @lends Coceso.ViewModels.Incident.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#dataType
   * @override
   */
  dataType: {value: "incidents"},
  /**
   * @see Coceso.ViewModels.ViewModelSingle#model
   * @override
   */
  model: {value: Coceso.Models.Incident},
  /**
   * @see Coceso.ViewModels.ViewModelSingle#saveUrl
   * @override
   */
  saveUrl: {value: "incident/update"},
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      ignore: ["concern"],
      bo: {
        create: function(options) {
          return ko.mapping.fromJS($.extend({}, Coceso.Models.Point, options.data));
        }
      },
      ao: {
        create: function(options) {
          return ko.mapping.fromJS($.extend({}, Coceso.Models.Point, options.data));
        }
      },
      units: {
        create: function(options) {
          if (!options.parent.getOption("assigned")) {
            return options.data;
          }
          var units = [], i;
          for (i in options.data) {
            units.push({id: parseInt(i), taskState: options.data[i]});
          }
          return new Coceso.ViewModels.Units({units: units}, options.parent.getOption("children", {children: {assigned: false}}));
        }
      }
    }
  },
  /**
   * @see Coceso.ViewModels.ViewModelSingle#compare
   * @override
   */
  compare: {
    value: {
      units: function(a, b) {
        var i, units = {};
        if (!(b instanceof Coceso.ViewModels.Units)) {
          return false;
        }
        for (i in b.units()) {
          units[b.units()[i].id()] = b.units()[i].taskState();
        }
        return !this.objEqual(a, units);
      }
    }
  },
  /**
   * @see Coceso.ViewModels.ViewModelSingle#save
   * @override
   */
//  save: {
//    value: function() {
//
//
//      if (!this.getOption("writeable") || !this.saveUrl) {
//        return false;
//      }
//
//      var data = ko.mapping.toJS(this, {ignore: ["units"]});
//
//      console.log(data);
//
//      if (!data.id) {
//
//      }
//
//      var units = (typeof this.units.units !== "undefined") ? ko.utils.arrayMap(this.units.units(), function(unit) {
//        return "incident/setToState/" + data.id + "/" + unit.id() + "/" + unit.taskState();
//      }) : [];
//
//      console.log(data.units);
//      console.log(units);
//
//      data.units = undefined;
//
//
//      data = ko.toJSON(data);
//      console.log(data);
//      Coceso.Ajax.save(data, this.saveUrl);
//
//      return true;
//    }
//  }
});

/**
 * List of units
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModelList
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Units = function(data, options) {
  Coceso.ViewModels.ViewModelList.call(this, data, options);

  /**
   * Available filters
   *
   * @type Object
   */
  var filters = {
    option: {
      radio: {
        filter: {
          hasAssigned: true
        }
      }
    }
  };

  /**
   * Generate a list of active filters
   *
   * @function
   * @type ko.computed
   * @return {Object}
   */
  this.activeFilters = ko.computed(function() {
    var activeFilters = {filter: []},
    filterOption = this.getOption("filter", []);

    var i;
    for (i in filterOption) {
      if (filters.option[filterOption[i]]) {
        activeFilters.filter.push(filters.option[filterOption[i]]);
      }
    }

    return activeFilters;
  }, this);

  /**
   * Filtered view of the incidents array
   *
   * @function
   * @type ko.computed
   * @return {Array}
   */
  this.filtered = this.units.extend({filtered: this.activeFilters});


};
Coceso.ViewModels.Units.prototype = Object.create(Coceso.ViewModels.ViewModelList.prototype, /** @lends Coceso.ViewModels.Units.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#dataType
   * @override
   */
  dataType: {value: "units"},
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      units: {
        key: function(data) {
          return ko.utils.unwrapObservable(data.id);
        },
        create: function(options) {
          return new Coceso.ViewModels.Unit(options.data, options.parent.getOption("children", {}));
        }
      }
    }
  }
});

/**
 * Single unit
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModelSingle
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Unit = function(data, options) {
  Coceso.ViewModels.ViewModelSingle.call(this, data, options);

  /**
   * CSS class based on the unit's state
   *
   * @function
   * @type ko.computed
   * @return {string} The CSS class
   */
  this.stateCss = ko.computed(function() {
    return "unit_state_" + this.state().toLowerCase();
  }, this);

  /**
   * Unit has incident with TaskState "Assigned"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.hasAssigned = ko.computed(function() {
    if (!this.incidents.incidents) {
      return false;
    }

    return (ko.utils.arrayFirst(this.incidents.incidents(), function(incident) {
      return incident.isAssigned();
    }) !== null);
  }, this);

};
Coceso.ViewModels.Unit.prototype = Object.create(Coceso.ViewModels.ViewModelSingle.prototype, /** @lends Coceso.ViewModels.Unit.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#dataType
   * @override
   */
  dataType: {value: "units"},
  /**
   * @see Coceso.ViewModels.ViewModelSingle#model
   * @override
   */
  model: {value: Coceso.Models.Unit},
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      ignore: ["concern"],
      incidents: {
        create: function(options) {
          if (!options.parent.getOption("assigned")) {
            return options.data;
          }
          var incidents = [], i;
          for (i in options.data) {
            incidents.push({id: parseInt(i), taskState: options.data[i]});
          }
          return new Coceso.ViewModels.Incidents({incidents: incidents}, options.parent.getOption("children", {children: {assigned: false, writeable: false}}));
        }
      }
    }
  },
  /**
   * @see Coceso.ViewModels.ViewModelSingle#compare
   * @override
   */
  compare: {value: {}}
});

/**
 * List of Logs
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModelList
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Logs = function(data, options) {
  var self = this;

  /**
   * List of Logs
   *
   * @function
   * @type ko.observableArray
   * @return {Array}
   */
  this.logs = ko.observableArray();

  Coceso.ViewModels.ViewModelList.call(this, data, options);

  /**
   * Load the specified data
   *
   * @param {String} url The URL to load from
   * @param {int} interval The interval to reload. 0 or false for no autoload.
   * @return {void}
   */
  this.load = function(url, interval) {
    $.ajax({
      dataType: "json",
      url: Coceso.Conf.jsonBase + url,
      ifModified: true,
      success: function(data, status) {
        if (status !== "notmodified") {
          self.setData({logs: data});
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
  };

  if (this.getOption("initial")) {
    this.load(this.getOption("url", "log/getAll"), this.getOption("autoload") ? Coceso.Conf.interval : false);
  }

};
Coceso.ViewModels.Logs.prototype = Object.create(Coceso.ViewModels.ViewModelList.prototype, /** @lends Coceso.ViewModels.Logs.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      logs: {
        key: function(data) {
          return ko.utils.unwrapObservable(data.id);
        },
        create: function(options) {
          return new Coceso.ViewModels.Log(options.data, options.parent.getOption("children", {}));
        }
      }
    }
  }
});

/**
 * Single log entry
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModelSingle
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Log = function(data, options) {


  options = $.extend({
    children: {
      initial: false,
      reload: false,
      assigned: false,
      writeable: false
    }
  }, options);

  Coceso.ViewModels.ViewModelSingle.call(this, data, options);

  /**
   * Convert Timestamp
   *
   * @function
   * @type ko.computed
   * @return {string}
   */
  this.time = ko.computed(function() {
    var time = new Date(this.timestamp());
    return time.toLocaleString();
  }, this);
};
Coceso.ViewModels.Log.prototype = Object.create(Coceso.ViewModels.ViewModelSingle.prototype, /** @lends Coceso.ViewModels.Log.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      ignore: ["concern"],
      json: {
        create: function(options) {
          var data = JSON.parse(options.data);
          if (!options.parent.getOption("assigned")) {
            return data;
          }

          if (!data) {
            data = [null, null];
          }

          return {
            unit: new Coceso.ViewModels.Unit(data[0], options.parent.getOption("children", {})),
            incident: new Coceso.ViewModels.Incident(data[1], options.parent.getOption("children", {}))
          };
        }
      }
    }
  },
  /**
   * @see Coceso.ViewModels.ViewModelSingle#compare
   * @override
   */
  compare: {value: {}}
});
