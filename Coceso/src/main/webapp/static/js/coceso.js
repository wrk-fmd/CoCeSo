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
    //Initialize window management
    $("#taskbar").winman();

    //Preload incidents and units
    Coceso.Ajax.getAll("incidents", "incident/getAll.json", Coceso.Conf.interval);
    Coceso.Ajax.getAll("units", "unit/getAll.json", Coceso.Conf.interval);
  },
  /**
   * Contains all the models
   *
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
      type: null
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
      incidents: {}
    }
  },
  /**
   * Contains all ViewModels (including baseclasses)
   *
   * @type Object
   */
  ViewModels: {},
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
      var id = $("#taskbar").winman("addWindow", title, src, function(el) {
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
      viewmodel = new Coceso.ViewModels.Units();
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
      viewmodel = new Coceso.ViewModels.Incidents();
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
      viewmodel = new Coceso.ViewModels.Incident(data || {});
      this.openWindow(title, Coceso.Conf.contentBase + src, viewmodel);
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
    save: function(data, url, callback) {
      $.ajax({
        type: "POST",
        url: Coceso.Conf.jsonBase + url,
        dataType: "json",
        contentType: "application/json",
        data: data,
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
  //Set options
  this.options = options || {};

  //Create and populate observables
  ko.mapping.fromJS(data, this.mappingOptions, this);

  //Subscribe to updates
  if (this.options.reload && this.dataType && (typeof this.setData === "function")) {
    Coceso.Ajax.subscribe(this.dataType, this.setData);
  }
};

Coceso.ViewModels.ViewModel.prototype = Object.create({}, {
  dataType: {value: null},
  mappingOptions: {value: {}},
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
  }
});

/**
 * Base class for all list style ViewModels
 *
 * @constructor
 * @extends Coceso.ViewModel
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

Coceso.ViewModels.ViewModelList.prototype = Object.create(Coceso.ViewModels.ViewModel.prototype, {});

/**
 * Base class for all single element ViewModels
 *
 * @constructor
 * @extends Coceso.ViewModel
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
      //Don't autoload list of children
      initial: false,
      reload: false,
      children: {
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
        for (var i in data) {
          var viewItem = ko.utils.unwrapObservable(self[i]);
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
      self.orig(newOrig);
      self.remoteChanges(remoteChanges);
    }
  };

  Coceso.ViewModels.ViewModel.call(this, data, options);

  /**
   * Original data to recognize local changes
   *
   * @type ko.observable (of Object)
   */
  this.orig = ko.observable(orig);

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
};

Coceso.ViewModels.ViewModelSingle.prototype = Object.create(Coceso.ViewModels.ViewModel.prototype, {
  model: {value: null},
  save: {
    value: function() {
      if (!this.options.writeable || !this.saveUrl) {
        return false;
      }

      var data = ko.mapping.toJS(this);
      data.units = undefined;
      data.aCase = undefined;
      data = ko.toJSON(data);
      console.log(data);
      Coceso.Ajax.save(data, this.saveUrl);

      return true;
    }
  },
  compare: {value: {}}
});

/**
 * List of incidents
 *
 * @constructor
 * @extends Coceso.ViewModelList
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Incidents = function(data, options) {
  //Call super constructor
  Coceso.ViewModels.ViewModelList.call(this, data, options);

  var self = this;

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
    ]
  };

  /**
   * The selected tab in the incidents list (corresponds to a key in filters.tabs)
   *
   * @type ko.observable
   */
  this.selectedTab = ko.observable((typeof this.options.selectedTab !== "undefined") ? options.selectedTab : "0");

  /**
   * Generate a list of active filters
   *
   * @type ko.computed
   * @return {Object}
   */
  this.activeFilters = ko.computed(function() {
    return {
      filter: [
        filters.tabs[this.selectedTab()]
      ]
    };
  }, this);

  //Filtered view of the incidents array
  this.filtered = this.incidents.extend({filtered: this.activeFilters});

  /**
   * The accordion view options
   * Has to depend on the used data array for the accordion to update correctly
   *
   * @type ko.computed
   * @returns {Object}
   */
  this.accordionOptions = ko.computed(function() {
    return {active: false, collapsible: true, subscribe: this.filtered()};
  }, this);
};

Coceso.ViewModels.Incidents.prototype = Object.create(Coceso.ViewModels.ViewModelList.prototype, {
  dataType: {value: "incidents"},
  mappingOptions: {
    value: {
      incidents: {
        key: function(data) {
          return ko.utils.unwrapObservable(data.id);
        },
        create: function(options) {
          return new Coceso.ViewModels.Incident(options.data, options.parent.options.children ? options.parent.options.children : {});
        }
      }
    }
  }
});


/**
 * Single incident
 *
 * @constructor
 * @extends Coceso.ViewModelSingle
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Incident = function(data, options) {
  Coceso.ViewModels.ViewModelSingle.call(this, data, options);

  var self = this;

  this.priority = this.priority.extend({integer: true});

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
};

Coceso.ViewModels.Incident.prototype = Object.create(Coceso.ViewModels.ViewModelSingle.prototype, {
  dataType: {value: "incidents"},
  model: {value: Coceso.Models.Incident},
  saveUrl: {value: "incident/update"},
  mappingOptions: {
    value: {
      units: {
        create: function(options) {
          if (!options.parent.options.assigned) {
            return options.data;
          }
          var units = [], i;
          for (i in options.data) {
            units.push({id: parseInt(i), taskState: options.data[i]});
          }
          return new Coceso.ViewModels.Units({units: units}, options.parent.options.children
            ? options.parent.options.children
            : {children: {assigned: false}}
          );
        }
      }
    }
  },
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
  }
});
$.extend(Coceso.ViewModels.Incident.prototype, {
  dragOptions: {
    helper: "clone",
    appendTo: "body",
    cursor: "move",
    zIndex: 1500
  }
});

/**
 * List of units
 *
 * @constructor
 * @extends Coceso.ViewModelList
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Units = function(data, options) {
  Coceso.ViewModels.ViewModelList.call(this, data, options);
};

Coceso.ViewModels.Units.prototype = Object.create(Coceso.ViewModels.ViewModelList.prototype, {
  dataType: {value: "units"},
  mappingOptions: {
    value: {
      units: {
        key: function(data) {
          return ko.utils.unwrapObservable(data.id);
        },
        create: function(options) {
          return new Coceso.ViewModels.Unit(options.data, options.parent.options.children ? options.parent.options.children : {});
        }
      }
    }
  }
});

/**
 * Single unit
 *
 * @constructor
 * @extends Coceso.ViewModelSingle
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Unit = function(data, options) {
  Coceso.ViewModels.ViewModelSingle.call(this, data, options);

  /**
   * CSS class based on the unit's state
   *
   * @type ko.computed
   * @return {string} The CSS class
   */
  this.stateCss = ko.computed(function() {
    return "unit_state_" + this.state().toLowerCase();
  }, this);
};

Coceso.ViewModels.Unit.prototype = Object.create(Coceso.ViewModels.ViewModelSingle.prototype, {
  dataType: {value: "units"},
  model: {value: Coceso.Models.Unit},
  mappingOptions: {
    value: {
      incidents: {
        create: function(options) {
          if (!options.parent.options.assigned) {
            return options.data;
          }
          var incidents = [], i;
          for (i in options.data) {
            incidents.push({id: parseInt(i), taskState: options.data[i]});
          }
          return new Coceso.ViewModels.Incidents({incidents: incidents}, options.parent.options.children
            ? options.parent.options.children
            : {children: {assigned: false}});
        }
      }
    }
  },
  compare: {value: {}}
});
$.extend(Coceso.ViewModels.Unit.prototype, {
  dragOptions: {
    helper: "clone",
    appendTo: "body",
    cursor: "move",
    zIndex: 1500
  }
});
