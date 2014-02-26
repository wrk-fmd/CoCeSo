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

var _ = $.i18n.prop;

/**
 * Object containing the main code
 *
 * @namespace Coceso
 * @type Object
 */
var Coceso = {};

/**
 * Global vars
 */
Coceso.Global = {
    notificationViewModel: {}
    ,patients: {}
};

/**
 * Initialize the application
 *
 * @return {void}
 */
Coceso.startup = function() {
  //Initialize localization
    $.i18n.properties({
        name: "messages",
        path: Coceso.Conf.langBase,
        mode: "map",
        cache: true,
        // Load Language from spring:message instead of browser
        language: Coceso.Conf.language
    });

    //Set global Notification View Model
    Coceso.Global.notificationViewModel = new Coceso.ViewModels.Notifications();

    //Initialize clock
    Coceso.Clock.start();

    //Initialize window management
    $("#taskbar").winman();

    $(document).on("show.bs.dropdown", ".ui-dialog .dropdown", function(event) {
        $(event.target).find(".dropdown-menu").css({top: 0, left: 0}).position({at: "left bottom", my: "left top", of: $(event.target).find(".dropdown-toggle").first()});
        return true;
    });

    //Preload incidents and units
    Coceso.Ajax.getAll("incidents");
    Coceso.Ajax.getAll("units");
    Coceso.Ajax.getAll("patients");

    //Load Global Patient List
    Coceso.Global.patients = new Coceso.ViewModels.Patients({},{});

    //$( "#other" ).click(function() {
    //    $( "#target" ).keypress();
    //});

    Coceso.Helper.initializeModalKeyHandler('next-state-confirm');

    //Load Bindings for Notifications
    ko.applyBindings(Coceso.Global.notificationViewModel, $("#nav-notifications").get(0));
};

/**
 * Clock functions
 *
 * @type Object
 */
Coceso.Clock = {
  offset: 0,
  start: function() {
    $.get(Coceso.Conf.jsonBase + "timestamp", function(data) {
      if (data.time) {
        Coceso.Clock.offset = new Date() - data.time;
      }
    });
    setInterval(Coceso.Clock.update, 1000);
  },
  update: function() {
    var currentTime = new Date(new Date() - Coceso.Clock.offset);
    var currentHours = currentTime.getHours( );
    var currentMinutes = currentTime.getMinutes( );
    var currentSeconds = currentTime.getSeconds( );

    currentMinutes = (currentMinutes < 10 ? "0" : "") + currentMinutes;
    currentSeconds = (currentSeconds < 10 ? "0" : "") + currentSeconds;

    $("#clock").html(currentHours + ":" + currentMinutes + ":" + currentSeconds);
  }
};

/**
 * Some global settings
 *
 * @type Object
 */
Coceso.Conf = {
  interval: 10000,
  logEntryLimit: 30,
  contentBase: "",
  jsonBase: "",
  langBase: "",
  language: "en",
  debug: false,
  keyboardControl: false,
  keyMapping: {
      openIncidentKey: 32 // 32: Space
      ,yesKey: 89 // 89: Y, 74: J
      ,noKey: 78 // 78: N
  }
  ,confirmStatusUpdate: true
};

/**
 * Constants for some values (states, types)
 *
 * @type Object
 */
Coceso.Constants = {
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
      transport: "Transport",
      tohome: "ToHome",
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
};

/**
 * Contains all the models
 *
 * @namespace Coceso.Models
 * @type Object
 */
Coceso.Models = {
  /**
   * Incident Dummy
   *
   * @type Coceso.Models.Incident
   */
  Incident: {
    id: null,
    state: Coceso.Constants.Incident.state.new ,
    priority: 0,
    blue: false,
    units: {},
    bo: {info: ""},
    ao: {info: ""},
    casusNr: "",
    info: "",
    caller: "",
    type: Coceso.Constants.Incident.type.task,
    taskState: null
  },
  /**
   * Unit dummy
   *
   * @type Coceso.Models.Unit
   */
  Unit: {
    id: null,
    state: Coceso.Constants.Unit.state.ad,
    call: null,
    ani: null,
    withDoc: false,
    portable: false,
    transportVehicle: false,
    crew: [],
    info: null,
    position: {info: ""},
    home: {info: ""},
    incidents: {},
    taskState: null
  },
  /*
   * Patient dummy
   */
  Patient: {
    id: null,
    given_name: "",
    sur_name: "",
    insurance_number: "",
    diagnosis: "",
    erType: "",
    info: "",
    externalID: ""
  }
};

// Little Helper go in here
Coceso.Helper = {
    confirmationDialog: function(elementID, yes) {
        if(!elementID) {
            return;
        }

        var modal = $("#" + elementID);

        modal.modal({
            backdrop: true
            ,keyboard: true
            ,show: true
        });

        var yesHandler = function() {
            $("#" + elementID).modal('hide');
            if(typeof yes === 'function') {
                yes();
            }
        };


        $("#" + elementID + "-yes").bind('click', yesHandler);

        modal.on('hidden.bs.modal', function (e) {
            $("#" + elementID + "-yes").unbind('click', yesHandler);
        })
    }
    ,initializeModalKeyHandler: function(elementID) {
        $("#" + elementID).keyup(function(event) {
            if(event.which === Coceso.Conf.keyMapping.noKey) {
                $("#" + elementID + "-no").click();
            }
            if(event.which === Coceso.Conf.keyMapping.yesKey) {
                $("#" + elementID + "-yes").click();
            }
        });
    }
};

/**
 * Contains UI related functions and data
 *
 * @namespace Coceso.UI
 * @type Object
 */
Coceso.UI = {
  /**
   * A list of all opened windows
   *
   * @type Object
   */
  windows: {},
  /**
   * Debugging information, such as HTTP errors
   *
   * @type Coceso.ViewModels.Debug
   */
  Debug: null,
  /**
   * Add a window to the UI
   *
   * @param {String} title The title of the window
   * @param {String} src The source to load the HTML from
   * @param {Object} viewmodel The viewmodel to bind with
   * @param {Object} options
   * @return {void}
   */
  openWindow: function(title, src, viewmodel, options) {
    var id = $("#taskbar").winman("addWindow", title, src, options, function(el, id) {
      viewmodel.ui = id;
      ko.applyBindings(viewmodel, el);
    }, function(el, id) {
      if (viewmodel.destroy instanceof Function) {
        viewmodel.destroy.call(viewmodel);
      }
      ko.cleanNode(el);
      delete Coceso.UI.windows[id];
    });
    this.windows[id] = viewmodel;
  },
  /**
   * Open the incidents overview
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} options
   * @param {Object} dialog Dialog options
   * @return {boolean}
   */
  openIncidents: function(title, src, options, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Incidents({}, options || {}), $.extend({ position: {at: "left+70% top+30%"}}, dialog));
    return false;
  },
  /**
   * Open a specific incident
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} data Additional incident data
   * @return {boolean}
   */
  openIncident: function(title, src, data) {
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Incident( data || {} ), { position: {at: "left+30% top+10%"}});
    return false;
  },
    /**
     * For internal usage with given model
     * @param title
     * @param src
     * @param model
     */
    openIncidentInternally: function(title, src, model) {
        this.openWindow(title, Coceso.Conf.contentBase + src, model, { position: {at: "left+30% top+10%"}});
        return false;
    },
  /**
   * Open the units overview
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} options
   * @param {Object} dialog Dialog options
   * @return {boolean}
   */
  openUnits: function(title, src, options, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Units({}, options || {}), $.extend({ position: {at: "left+20% bottom"}}, dialog));
    return false;
  },
    /**
     * Open the units overview with hierarchical View
     *
     */
    openHierarchyUnits: function(title, src, options, dialog) {
        var sUnits = new Coceso.ViewModels.Units({}, options || {});
        var contVM = new ContainerViewModel(sUnits.filtered);
        contVM.load();
        this.openWindow(title, Coceso.Conf.contentBase + src, contVM, $.extend({ position: {at: "left top"}}, dialog));
        return false;
    },
  /**
   * Open the unit edit Window
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} data
   * @return {boolean}
   */
  openUnit: function(title, src, data) {
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Unit(data || {}), { position: {at: "left+10% top+20%"}});
    return false;
  },
    /**
     * Open Add-Log Window
     */
    openLogAdd: function(title, src, data) {
        this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.CustomLogEntry(data || {}), { position: {at: "left+20% top+20%"}});
        return false;
    },
  /**
   * Open a list of log entries
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} options
   * @return {boolean}
   */
  openLogs: function(title, src, options) {
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Logs({}, options || {}), { position: {at: "left+30% center"}});
    return false;
  },
  openPatient: function(title, src, data) {
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Patient(data || {}), { position: {at: "left+40% top+30%"}});
    return false;
  },
  /**
   * Open debug window
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} options
   * @return {boolean}
   */
  openDebug: function(title, src, options) {
    this.openWindow(title, Coceso.Conf.contentBase + src, this.Debug, { position: {at: "left+30% center"}});
    return false;
  },
  /**
   * Open static content
   *
   * @param {String} title
   * @param {String} src
   * @return {boolean}
   */
  openStatic: function(title, src) {
    this.openWindow(title, Coceso.Conf.contentBase + src, {}, { position: {at: "left+30% center"}});
    return false;
  },
    /*
     * src has to be a full URL!
     */
  openExternalStatic: function(title, src) {
    this.openWindow(title, src, {}, { position: {at: "left+30% center"}});
    return false;
  }
};

/**
 * AJAX related functions and data
 *
 * @namespace Coceso.Ajax
 * @type Object
 */
Coceso.Ajax = {
  /**
   * Preloaded data
   *
   * @type Object
   */
  data: {
    incidents: {incidentlist: []}
    ,units: {unitlist: []}
    ,patients: {patientlist: []}
  },
  /**
   * Subscriptions to data loading
   *
   * @type Object
   */
  subscriptions: {
    incidents: [],
    units: [],
    patients: []
  },
  loadOptions: {
    units: {
      list: "unitlist",
      url: "unit/getAll.json",
      interval: null,
      id: null
    },
    incidents: {
      list: "incidentlist",
      url: "incident/getAll.json",
      interval: null,
      id: null
    }
    ,patients: {
      list: "patientlist",
      url: "patient/getAll.json",
      interval: null,
      id: null
    }
  },
  /**
   * Load the specified data
   *
   * @param {String} type The data type
   * @return {void}
   */
  getAll: function(type) {
    if (!Coceso.Ajax.loadOptions[type]) {
      return false;
    }

    var options = Coceso.Ajax.loadOptions[type];
    if (options.id) {
      window.clearTimeout(options.id);
      options.id = null;
    }
    if (options.interval === null) {
      options.interval = Coceso.Conf.interval;
    }

    $.ajax({
      dataType: "json",
      url: Coceso.Conf.jsonBase + options.url,
      ifModified: true,
      success: function(data, status) {
        if (status !== "notmodified") {
          Coceso.Ajax.data[type][options.list] = data;
          ko.utils.arrayForEach(Coceso.Ajax.subscriptions[type], function(item) {
            if (item instanceof Function) {
              item(Coceso.Ajax.data[type]);
            }
          });
        }
        Coceso.Global.notificationViewModel.connectionError(false);
      },
      complete: function() {
        if (options.interval) {
          options.id = window.setTimeout(Coceso.Ajax.getAll, options.interval, type);
        }
      },
      error: function(xhr) {
        // 404: not found, 0: no connection to server, 200: error is thrown, because response is not a json (not authenticated)
        if(xhr.status === 404 || xhr.status === 0 || xhr.status === 200) {
          Coceso.Global.notificationViewModel.connectionError(true);
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
   * @param {Function} success
   * @param {Function} error
   * @param {Function} httperror
   * @returns {void}
   */
  save: function(data, url, success, error, httperror) {
    $.ajax({
      type: "POST",
      url: Coceso.Conf.jsonBase + url,
      dataType: "json",
      contentType: (typeof data === "string") ? "application/json" : "application/x-www-form-urlencoded",
      data: data,
      processData: (typeof data !== "string"),
      success: function(data) {
        if (data.success) {
          if (success instanceof Function) {
            success(data);
          }
        } else {
          if (error instanceof Function) {
            error(data);
          }
        }
      },
      error: function(jqXHR) {
        Coceso.UI.Debug.pushHttpError(jqXHR, url, data);
        if (httperror instanceof Function) {
          httperror(jqXHR);
        }
      },
      complete: function() {
        var i;
        for (i in Coceso.Ajax.loadOptions) {
          Coceso.Ajax.getAll(i);
        }
      }
    });
  },
  /**
   * Load a single item into a viewmodel
   *
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
};

/**
 * Contains all ViewModels (including baseclasses)
 *
 * @namespace Coceso.ViewModels
 * @type Object
 */
Coceso.ViewModels = {};

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
  if (this.getOption("reload") && this.dataType && (this.setData instanceof Function)) {
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
   * @param {Object} val The default value
   * @return {Object}
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
   * Destroy the ViewModel
   *
   * @function
   * @return {void}
   */
  destroy: {
    value: function() {
      //Unsubscribe from updates
      if (this.dataType && (this.setData instanceof Function)) {
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
  var self = this, orig = {};

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
    orig = ko.utils.arrayFirst(Coceso.Ajax.data[this.dataType][this.dataList], function(item) {
      return (item.id === data.id);
    }) || {};
  }

  orig = this.replaceNull($.extend(true, {}, this.model, orig));
  data = this.replaceNull($.extend(true, {}, orig, data));

  this.mappingOptions.orig = orig;

  /**
   * Method to set refreshed data
   *
   * @param {Object} data The refreshed data object
   * @return {void}
   */
  this.setData = function(data) {
    if (data[self.dataList] instanceof Array) {
      data = ko.utils.arrayFirst(data[self.dataList], function(item) {
        return (item.id === self.id());
      });
    }

    data = self.replaceNull($.extend(true, {}, data));

    if (data) {
      ko.mapping.fromJS(data, self);
    }
  };

  //Call super constructor
  Coceso.ViewModels.ViewModel.call(this, data, options);

  /**
   * Watch dependencies
   *
   * @type ko.observableArray
   */
  this.dependencies = ko.observableArray().extend({arrayChanges: {}});

  if (typeof this.taskState !== "undefined") {
    this.dependencies.push(this.taskState);
  }

  /**
   * Return if data has been changed by the user
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.localChange = ko.computed(function() {
    return this.dependencies.localChange();
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

  /**
   * Reset the form to its original state
   *
   * @return {void}
   */
  this.reset = function() {
    self.dependencies.reset();
  };

  /**
   * Callback on error saving
   *
   * @return {void}
   */
  this.saveError = function() {
    $("#" + self.ui + "-error").stop(true).show().fadeOut(7000);
  };
};
Coceso.ViewModels.ViewModelSingle.prototype = Object.create(Coceso.ViewModels.ViewModel.prototype, /** @lends Coceso.ViewModels.ViewModelSingle.prototype */ {
  /**
   * The Model used in this ViewModel
   *
   * @type Coceso.Model
   */
  model: {value: null},
  /**
   * The list name
   *
   * @type String
   */
  dataList: {value: null},
  /**
   * The URL to send the POST to
   *
   * @type String
   */
  saveUrl: {value: null},
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

      var data = ko.mapping.toJS(this, {ignore: ["incidents", "units", "taskState"]});

      if (this.beforeSave instanceof Function) {
        data = this.beforeSave(data);
      }

      Coceso.Ajax.save(ko.toJSON(data), this.saveUrl, this.afterSave, this.saveError, this.saveError);
      return true;
    }
  },
  /**
   * Save modified data and close the window
   *
   * @function
   * @return {boolean}
   */
  ok: {
    value: function() {
      var result = this.save();
      if (result) {
        $("#" + this.ui).dialog("destroy");
      }
      return result;
    }
  },
  /**
   * Replace null values with empty objects
   *
   * @function
   * @param {Object} data
   * @return {Object}
   */
  replaceNull: {
    value: function(data) {
      if (!this.model) {
        return data;
      }

      var i;
      for (i in data) {
        if ((data[i] === null) && (typeof this.model[i] === "object") && (this.model[i] !== null)) {
          data[i] = $.extend(true, {}, this.model[i]);
        }
      }
      return data;
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
    overview: {
      filter: {
        type: {val: [Coceso.Constants.Incident.type.task, Coceso.Constants.Incident.type.transport, Coceso.Constants.Incident.type.relocation]}
      }
    },
    active: {
      disable: {state: {done: true}},
      filter: {
        state: {op: "not", val: Coceso.Constants.Incident.state.done}
      }
    },
    "new": {
      disable: {state: true},
      filter: {
        state: Coceso.Constants.Incident.state.new
      }
    },
    open: {
      disable: {state: true},
      filter: {
        state: Coceso.Constants.Incident.state.open
      }
    },
    new_or_open: {
        disable: {state: true},
        filter: {
            state: {val: [Coceso.Constants.Incident.state.new, Coceso.Constants.Incident.state.open]}
        }
    },
    completed: {
      disable: {state: true},
      filter: {
        state: Coceso.Constants.Incident.state.done
      }
    }
  };

  var filterOption = this.getOption("filter", []);

  this.disableFilter = {};
  for (var i in filterOption) {
    if (filters[filterOption[i]] && filters[filterOption[i]].disable) {
      this.disableFilter = $.extend(true, this.disable, filters[filterOption[i]].disable);
    }
  }

  /**
   * The selected filters
   *
   * @type Object
   */
  this.filter = {
    type: ko.observableArray(),
    blue: ko.observableArray(),
    state: ko.observableArray()
  };

  /**
   * Generate a list of active filters
   *
   * @function
   * @type ko.computed
   * @return {Object}
   */
  this.activeFilters = ko.computed(function() {
    var activeFilters = {filter: []};

    //Filters selected in user interface
    var i, filter = {};
    for (i in this.filter) {
      var unwrapped = ko.utils.unwrapObservable(this.filter[i]);
      if (unwrapped.length) {
        filter[i] = {val: unwrapped};
      }
    }
    activeFilters.filter.push({
      filter: filter
    });

    //Filters from options
    for (i in filterOption) {
      if (filters[filterOption[i]]) {
        activeFilters.filter.push(filters[filterOption[i]]);
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
  this.filtered = this.incidentlist.extend({filtered: {filters: this.activeFilters}});
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
      incidentlist: {
        key: function(data) {
          return ko.utils.unwrapObservable(data.id);
        },
        create: function(options) {
          return new Coceso.ViewModels.Incident(options.data, options.parent.getOption("children", {}));
        },
        update: function(options) {
          options.target.setData(options.data);
          return options.target;
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

  //Call parent constructor
  Coceso.ViewModels.ViewModelSingle.call(this, data, options);

  //Detect changes
  this.dependencies.push(this.type, this.priority, this.blue, this.bo.info, this.ao.info, this.info, this.caller, this.casusNr, this.state);
  if (this.units && this.units.unitlist) {
    this.dependencies.push(this.units.unitlist);
  }

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
   * Incident is of type "Relocation"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isTransport = ko.computed(function() {
    return (this.type() === Coceso.Constants.Incident.type.transport);
  }, this);

  /**
   * Incident is of type "ToHome"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isToHome = ko.computed(function() {
    return (this.type() === Coceso.Constants.Incident.type.tohome);
  }, this);

  /**
   * Incident is of type "HoldPosition"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isHoldPosition = ko.computed(function() {
    return (this.type() === Coceso.Constants.Incident.type.holdposition);
  }, this);

  /**
   * Incident is of type "Standby"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isStandby = ko.computed(function() {
    return (this.type() === Coceso.Constants.Incident.type.standby);
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

    /*
     * True, if current Incident will be cancelled on next assign
     */
    this.isInterruptible = ko.computed(function() {
        return (this.isStandby() || this.isToHome() || this.isHoldPosition() || this.isRelocation());
    }, this);

  /**
   * Disable the "Task" type button
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableTask = ko.computed(function() {
    return (!this.getOption("writeable") || (this.id() && !this.isTask()));
  }, this);

  /**
   * Disable the "Relocation" type button
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableRelocation = ko.computed(function() {
    return (!this.getOption("writeable") || (this.id() && !this.isRelocation()));
  }, this);

  /**
   * If true, Incident is marked red and counted in the Notification Area
   * @type {boolean}
   */
  self.notifyOpen = ko.computed(function() {
      return (self.isOpen() || self.isNew());
  });
  /**
   * Disable the "Transport" type button
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableTransport = ko.computed(function() {
    return (!this.getOption("writeable") || (this.id() && !this.isTransport()));
  }, this);

  /**
   * Disable BO field
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableBO = ko.computed(function() {
    return (!this.isTask() && !this.isTransport());
  }, this);

  /**
   * Disable "Assigned" state
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableAssigned = ko.computed(function() {
    return (this.isStandby() || this.isHoldPosition());
  }, this);

  /**
   * Disable "AAO" state
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableAAO = ko.computed(function() {
    return (this.ao.info() === "");
  }, this);

  /**
   * Disable "ZAO" state
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableZAO = ko.computed(function() {
    return (this.isStandby() || this.isHoldPosition() || this.ao.info() === "");
  }, this);

  /**
   * Disable IncidentState New
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableNew = ko.computed(function() {
    return (!this.getOption("writeable") || (this.id() && this.state.orig() !== Coceso.Constants.Incident.state.new ));
  }, this);

  /**
   * Disable IncidentState Dispo
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableDispo = ko.computed(function() {
    if (!this.getOption("writeable") || !this.units.unitlist) {
      return true;
    }

    return (ko.utils.arrayFirst(this.units.unitlist(), function(unit) {
      return (unit.isAssigned() || unit.isZBO());
    }) === null);
  }, this);

  /**
   * Disable IncidentState Working
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableWorking = ko.computed(function() {
    if (!this.getOption("writeable") || !this.units.unitlist) {
      return true;
    }

    return (ko.utils.arrayFirst(this.units.unitlist(), function(unit) {
      return (unit.isABO() || unit.isZAO() || unit.isAAO());
    }) === null);
  }, this);

  /**
   * Return the title string
   *
   * @function
   * @type ko.computed
   * @return {String}
   */
  this.title = ko.computed(function() {
    if (!this.disableBO()) {
      return (this.bo.info() === "") ? "No BO" : this.bo.info();
    }
    return (this.ao.info() === "") ? "No AO" : this.ao.info();
  }, this);

  /**
   * Return a one-letter representation of type
   *
   * @function
   * @type ko.computed
   * @return {String}
   */
  // TODO Move Definitions to Conf
  this.typeString = ko.computed(function() {
    if (this.isTask()) {
      return this.blue() ? "E" : "A";
    }
    if (this.isTransport()) {
      return "T";
    }
    if (this.isRelocation()) {
      return "V";
    }
    if (this.isToHome()) {
      return "Einr";
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
   * @type ko.computed
   * @return {String}
   */
  this.assignedTitle = ko.computed(function() {
    if (this.isTask() || this.isTransport() || this.isRelocation()) {
      return this.typeString() + ": " + this.title().split("\n")[0];
    }
    if (this.isToHome() || this.isStandby() || this.isHoldPosition()) {
      return this.typeString();
    }
    return this.title();
  }, this);

  /**
   * Set TaskState to next state
   *
   * @param {Integer} unitid
   * @return {void}
   */
  this.nextState = function(unitid) {
      if (unitid && self.id()) {
          var save = function() {
              Coceso.Ajax.save({incident_id: self.id(), unit_id: unitid}, "incident/nextState.json");
          };

          if(Coceso.Conf.confirmStatusUpdate) {
              Coceso.Helper.confirmationDialog('next-state-confirm', save);
          } else {
              save();
          }
      }
  };

  /**
   * Assign a unit within the form
   *
   * @param {Event} event The jQuery Event (unused)
   * @param {Object} ui jQuery UI properties
   * @return {void}
   */
  this.assignUnitForm = function(event, ui) {
    var viewmodel = ko.dataFor(ui.draggable.context);
    if (!(viewmodel instanceof Coceso.ViewModels.Unit)) {
      return;
    }
    var unitid = ko.utils.unwrapObservable(viewmodel.id);
    if (unitid && self.units.unitlist) {
      var assigned = ko.utils.arrayFirst(self.units.unitlist(), function(unit) {
        return (unit.id() === unitid);
      });
      if (assigned === null) {
        self.units.unitlist.push(new Coceso.ViewModels.Unit({id: unitid, taskState: "Assigned"}, self.getOption(["children", "children"], {assigned: false, writeable: false})));
      }
    }
  };

  /**
   * Assign a unit to an incident in the list
   *
   * @param {Event} event The jQuery Event (unused)
   * @param {Object} ui jQuery UI properties
   * @return {void}
   */
  this.assignUnitList = function(event, ui) {
    var unit = ko.dataFor(ui.draggable.context);

    if ((unit instanceof Coceso.ViewModels.Unit) && self.id() && unit.id()) {
      Coceso.Ajax.save({incident_id: self.id(), unit_id: unit.id()}, "assignUnit.json");
    }
  };

  /**
   * Open in a form
   *
   * @return {void}
   */
  this.openForm = function() {
    Coceso.UI.openIncident(_("label.incident.edit"), "incident_form.html", {id: self.id()});
  };

  /**
   * Open log
   *
   * @return {void}
   */
  this.openLog = function() {
    if (self.id()) {
      Coceso.UI.openLogs("Incident-Log", "log.html", {url: "log/getByIncident/" + self.id()});
    }
  };

    self.patient = ko.computed(function() {
        return ko.utils.arrayFirst(Coceso.Global.patients.filtered(), function(item) {
            return self.id() === item.id();
        });
    });

    self.openPatient = function() {
        // If Patient doesn't exist, open Window for new Patient with ID from Incident given
        // Otherwise existing Patient will be loaded
        if(self.id()) {
            Coceso.UI.openPatient(_("label.patient.edit"), "patient_form.html", {id: self.id()});
        }

    };

  /**
   * Callback after saving
   *
   * @param {Object} data The data returned from server
   * @return {void}
   */
  this.afterSave = function(data) {
    if (data.incident_id) {
      self.id(data.incident_id);
    }

    if (self.id() && (typeof self.units.unitlist !== "undefined")) {
      ko.utils.arrayForEach(self.units.unitlist(), function(unit) {
        if (unit.taskState.localChange()) {
          if (unit.taskState.orig() === null) {
            Coceso.Ajax.save({incident_id: self.id(), unit_id: unit.id()}, "assignUnit.json", function() {
              Coceso.Ajax.save({incident_id: self.id(), unit_id: unit.id(), state: unit.taskState()}, "incident/setToState.json");
            });
          } else {
            Coceso.Ajax.save({incident_id: self.id(), unit_id: unit.id(), state: unit.taskState()}, "incident/setToState.json");
          }
        }
      });
    }
  };
};
Coceso.ViewModels.Incident.prototype = Object.create(Coceso.ViewModels.ViewModelSingle.prototype, /** @lends Coceso.ViewModels.Incident.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#dataType
   * @override
   */
  dataType: {value: "incidents"},
  /**
   * @see Coceso.ViewModels.ViewModel#dataList
   * @override
   */
  dataList: {value: "incidentlist"},
  /**
   * @see Coceso.ViewModels.ViewModelSingle#model
   * @override
   */
  model: {value: Coceso.Models.Incident},
  /**
   * @see Coceso.ViewModels.ViewModelSingle#saveUrl
   * @override
   */
  saveUrl: {value: "incident/update.json"},
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      ignore: ["concern"],
      keepChanges: {
        info: true
      },
      units: {
        create: function(options) {
          if (!options.parent.getOption("assigned")) {
            return options.data;
          }
          return new Coceso.ViewModels.Units({unitlist: []}, options.parent.getOption("children", {children: {assigned: false}}));
        },
        update: function(options) {
          if (!options.parent.getOption("assigned")) {
            return options.target;
          }
          var units = [], i;
          for (i in options.data) {
            units.push({id: parseInt(i), taskState: options.data[i]});
          }

          ko.utils.arrayForEach(options.target.unitlist(), function(unit) {
            if ((unit.taskState.orig() === null) && (typeof options.data[unit.id()] === "undefined")) {
              units.push({id: unit.id()});
            }
          });

          options.target.setData({unitlist: units});
          return options.target;
        }
      }
    }
  },
  /**
   * Data manipulation before saving
   *
   * @function
   * @param {Object} data The data to save
   * @return {Object} The manipulated data
   */
  beforeSave: {
    value: function(data) {
      delete data.ao.id;
      delete data.bo.id;

      return data;
    }
  }
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
    radio: {
      filter: {
        hasAssigned: true
      }
    },
    free: {
      filter: {
        isFree: true
      }
    },
    available: {
      filter: {
        isAvailable: true
      }
    }
  };

  var filterOption = this.getOption("filter", []);

  /**
   * The selected filters
   *
   * @type Object
   */
  this.filter = {};

  /**
   * Generate a list of active filters
   *
   * @function
   * @type ko.computed
   * @return {Object}
   */
  this.activeFilters = ko.computed(function() {
    var activeFilters = {filter: []};

    //Filters selected in user interface
    var i, filter = {};
    for (i in this.filter) {
      var unwrapped = ko.utils.unwrapObservable(this.filter[i]);
      if (unwrapped.length) {
        filter[i] = {val: unwrapped};
      }
    }
    activeFilters.filter.push({
      filter: filter
    });

    //Filters from options
    for (i in filterOption) {
      if (filters[filterOption[i]]) {
        activeFilters.filter.push(filters[filterOption[i]]);
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
  this.filtered = this.unitlist.extend({filtered: {filters: this.activeFilters}});
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
      unitlist: {
        key: function(data) {
          return ko.utils.unwrapObservable(data.id);
        },
        create: function(options) {
          return new Coceso.ViewModels.Unit(options.data, options.parent.getOption("children", {}));
        },
        update: function(options) {
          options.target.setData(options.data);
          return options.target;
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
  var self = this;

  Coceso.ViewModels.ViewModelSingle.call(this, data, options);

  //Detect changes
  this.dependencies.push(this.position.info, this.info, this.state);
  if (this.incidents && this.incidents.incidentlist) {
    this.dependencies.push(this.incidents.incidentlist);
  }

  /**
   * Return the number of assigned incidents
   *
   * @function
   * @type ko.computed
   * @return {Integer}
   */
  this.incidentCount = ko.computed(function() {
    if (!this.incidents || !this.incidents.incidentlist) {
      return -1;
    }

    return this.incidents.incidentlist().length;
  }, this);

  /**
   * Home is set
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.hasHome = ko.computed(function() {
    return (this.home.info() !== "");
  }, this);

  /**
   * Last known position is home
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isHome = ko.computed(function() {
    return (this.hasHome() && this.position.info() === this.home.info());
  }, this);

  /**
   * Unit is 'free' (not at home, no Incident assigned
   */
  this.isFree = ko.computed(function() {
    return (this.incidentCount() <= 0) && this.hasHome() && !this.isHome() && this.portable();
  }, this);


  /**
   * Unit has state "AD"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isAD = ko.computed(function() {
    return (this.state() === Coceso.Constants.Unit.state.ad);
  }, this);

  /**
   * Unit has state "EB"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isEB = ko.computed(function() {
    return (this.state() === Coceso.Constants.Unit.state.eb);
  }, this);

  /**
   * Unit has state "NEB"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isNEB = ko.computed(function() {
    return (this.state() === Coceso.Constants.Unit.state.neb);
  }, this);

  /**
   * Unit has incident with TaskState "Assigned"
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.hasAssigned = ko.computed(function() {
    if (this.incidentCount() <= 0) {
      return false;
    }

    return (ko.utils.arrayFirst(this.incidents.incidentlist(), function(incident) {
      return incident.isAssigned();
    }) !== null);
  }, this);

    /**
     * Unit is available for a new Incident and 'EB'
     * @type {boolean}
     */
    this.isAvailable = ko.computed(function() {
        return (this.portable() && this.isEB() && (this.incidentCount() <= 0 || this.incidents.incidentlist()[0].isInterruptible()));
    }, this);

    /**
   * Disable the send home method
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableSendHome = ko.computed(function() {
    if (!this.hasHome() || this.isHome() || (this.incidentCount() > 1)) {
      return true;
    }
    if (this.incidentCount() <= 0) {
      return false;
    }
    return (!this.incidents.incidentlist()[0].isHoldPosition() && !this.incidents.incidentlist()[0].isStandby());
  }, this);

  /**
   * Disable the standby method
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableStandby = ko.computed(function() {
    if (this.incidentCount() === 1) {
      return !this.incidents.incidentlist()[0].isHoldPosition();
    }
    return (this.incidentCount() > 1);
  }, this);

  /**
   * Disable the hold position method
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableHoldPosition = ko.computed(function() {
    if (this.isHome() || (this.position.info() === "") || (this.incidentCount() > 1)) {
      return true;
    }
    if (this.incidentCount() <= 0) {
      return false;
    }
    return !this.incidents.incidentlist()[0].isStandby();
  }, this);

  /**
   * CSS class based on the unit's state
   *
   * @function
   * @type ko.computed
   * @return {string} The CSS class
   */
  this.stateCss = ko.computed(function() {
    if (this.isEB()) {
      return (this.incidentCount() === 1 && this.incidents.incidentlist()[0].isStandby()) ? "unit_state_standby" : "unit_state_eb";
    }
    return this.isNEB() ? "unit_state_neb" : "unit_state_ad";
  }, this);

  /**
   * Text based on the unit's assigned tasks
   *
   * @function
   * @type ko.computed
   * @return {string} The text
   */
  this.taskText = ko.computed(function() {
    if (this.incidentCount() < 0) {
      return "";
    }
    if (this.incidentCount() > 1) {
      return "<span class='glyphicon glyphicon-plus'></span>";
    }
    if (this.incidentCount() === 0) {
      return "<span class='glyphicon glyphicon-" + (this.hasHome() ? (this.isHome() ? "home" : "exclamation-sign") : "ok-sign") + "'></span>";
    }

    var incident = this.incidents.incidentlist()[0];
    if (incident.isTask() || incident.isTransport() || incident.isRelocation() || incident.isToHome()) {
      return incident.typeString() + ": " + _("label.task.state." + incident.taskState().toLowerCase());
    }

    if (incident.isStandby() || incident.isHoldPosition()) {
      return incident.typeString();
    }

    return "";
  }, this);

  /**
   * CSS class based on the unit's task
   *
   * @function
   * @type ko.computed
   * @return {string} The CSS class
   */
  this.taskCss = ko.computed(function() {
    if (this.incidentCount() < 0) {
      return "";
    }
    if (this.incidentCount() > 1) {
      return "unit_state_multiple";
    }
    if (this.incidentCount() === 0) {
      return this.isFree() ? "unit_state_free" : this.stateCss() ;
    }

    var incident = this.incidents.incidentlist()[0];
    if (incident.isTask() || incident.isTransport()) {
      return (incident.blue()) ? "unit_state_task_blue" : "unit_state_task";
    }
    if (incident.isRelocation()) {
      return "unit_state_relocation";
    }
    if (incident.isHoldPosition()) {
      return "unit_state_holdposition";
    }
    if (incident.isToHome()) {
      return "unit_state_tohome";
    }
    if (incident.isStandby()) {
      return "unit_state_standby";
    }

    return "";
  }, this);

  /**
   * Options for the tooltip
   *
   * @function
   * @type ko.computed
   * @return {Object} The popover options
   */
  this.popover = ko.computed(function() {
    // Bugfix orphaned Popovers (Ticket #17)
    var content = "<div onmouseout=\"$('.popover').remove();\">";
    if (this.hasHome()) {
      content += "<p><span class='key'>" + _("label.unit.home") + "</span><span>" + this.home.info() + "</span></p>";
    }
    content += "<p><span class='key'>" + _("label.unit.position") + "</span><span>" + this.position.info() + "</span></p>";

    if (this.incidentCount() > 0) {
      ko.utils.arrayForEach(this.incidents.incidentlist(), function(inc) {
        content += "<p><span class='key'>" + inc.assignedTitle() + "</span><span>" + _("label.task.state." + inc.taskState().toLowerCase()) + "</span></p>";
      });
    }
    content += "</div>";

    return {
      trigger: 'hover focus',
      placement: 'auto right',
      html: true,
      container: 'body',
      title: this.call(),
      content: content
    };
  }, this);

  /**
   * Set TaskState to next state
   *
   * @param {Integer} incid
   * @return {void}
   */
  this.nextState = function(incid) {
    if (typeof incid === "undefined" && self.incidentCount() === 1) {
      incid = self.incidents.incidentlist()[0].id();
    }

    if (incid && self.id()) {
        var save = function() {
            Coceso.Ajax.save({incident_id: incid, unit_id: self.id()}, "incident/nextState.json");
        };

        if(Coceso.Conf.confirmStatusUpdate) {
            Coceso.Helper.confirmationDialog('next-state-confirm', save);
        } else {
            save();
        }
    }
  };

  /**
   * Assign a incident within the form
   *
   * @param {Event} event The jQuery Event (unused)
   * @param {Object} ui jQuery UI properties
   * @return {void}
   */
  this.assignIncidentForm = function(event, ui) {
    var viewmodel = ko.dataFor(ui.draggable.context);
    if (!(viewmodel instanceof Coceso.ViewModels.Incident)) {
      return;
    }

    var incid = ko.utils.unwrapObservable(viewmodel.id);
    if (incid && self.incidents.incidentlist) {
      var assigned = ko.utils.arrayFirst(self.incidents.incidentlist(), function(incident) {
        return (incident.id() === incid);
      });
      if (assigned === null) {
        self.incidents.incidentlist.push(new Coceso.ViewModels.Incident({id: incid, taskState: Coceso.Constants.TaskState.assigned}, self.getOption(["children", "children"], {assigned: false, writeable: false})));
      }
    }
  };

  /**
   * Open incident form with Unit attached
   *
   * @return {void}
   */
  this.addIncident = function() {
    options = {units: {}};
      var model = new Coceso.ViewModels.Incident(options);
      if(model.units.unitlist) {
          model.units.unitlist.push(new Coceso.ViewModels.Unit({id: self.id(), taskState: "Assigned"}, self.getOption(["children", "children"], {assigned: false, writeable: false})));
      }
      Coceso.UI.openIncidentInternally(_("label.unit.new_incident"), "incident_form.html", model);
  };

    /**
     * Open incident form with Unit as caller
     * BO is set to Position of Unit, BlueLight is true by default
     *
     * @return {void}
     */
    this.reportIncident = function() {
        options = {caller: self.call()};
        if(self.portable()) {
            options = $.extend(options, { bo: self.position, blue: true, units: {} });
        }
        var model = new Coceso.ViewModels.Incident(options);
        if(self.portable() && model.units.unitlist) {
            model.units.unitlist.push(new Coceso.ViewModels.Unit({id: self.id(), taskState: "ABO"}, self.getOption(["children", "children"], {assigned: false, writeable: false})));
        }
        Coceso.UI.openIncidentInternally(_("label.unit.report_incident"), "incident_form.html", model);
    };

  /**
   * Open in a form
   *
   * @return {void}
   */
  this.openForm = function() {
    Coceso.UI.openUnit(_("label.unit.edit"), "unit_form.html", {id: self.id()});
  };

  /**
   * Open Log of this Unit
   *
   * @return {void}
   */
  this.openLog = function() {
    if (self.id()) {
      Coceso.UI.openLogs("Unit-Log", "log.html", {url: "log/getByUnit/" + self.id()});
    }
  };

  /**
   * Callback after saving
   *
   * @param {Object} data The data returned from server
   * @return {void}
   */
  this.afterSave = function(data) {
    if (data.unit_id) {
      self.id(data.unit_id);
    }

    if (self.id() && self.incidentCount() > 0) {
      ko.utils.arrayForEach(self.incidents.incidentlist(), function(inc) {
        if (inc.taskState.localChange()) {
          if (inc.taskState.orig() === null) {
            Coceso.Ajax.save({unit_id: self.id(), incident_id: inc.id()}, "assignUnit.json", function() {
              Coceso.Ajax.save({unit_id: self.id(), incident_id: inc.id(), state: inc.taskState()}, "incident/setToState.json");
            });
          } else {
            Coceso.Ajax.save({unit_id: self.id(), incident_id: inc.id(), state: inc.taskState()}, "incident/setToState.json");
          }
        }
      });
    }
  };
};
Coceso.ViewModels.Unit.prototype = Object.create(Coceso.ViewModels.ViewModelSingle.prototype, /** @lends Coceso.ViewModels.Unit.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#dataType
   * @override
   */
  dataType: {value: "units"},
  /**
   * @see Coceso.ViewModels.ViewModel#dataList
   * @override
   */
  dataList: {value: "unitlist"},
  /**
   * @see Coceso.ViewModels.ViewModelSingle#model
   * @override
   */
  model: {value: Coceso.Models.Unit},
  /**
   * @see Coceso.ViewModels.ViewModel#saveUrl
   * @override
   */
  saveUrl: {value: "unit/update.json"},
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      ignore: ["concern"],
      keepChanges: {
        info: true
      },
      incidents: {
        create: function(options) {
          if (!options.parent.getOption("assigned")) {
            return options.data;
          }
          return new Coceso.ViewModels.Incidents({incidentlist: []}, options.parent.getOption("children", {children: {assigned: false}}));
        },
        update: function(options) {
          if (!options.parent.getOption("assigned")) {
            return options.target;
          }
          var incidents = [], i;
          for (i in options.data) {
            incidents.push({id: parseInt(i), taskState: options.data[i]});
          }

          ko.utils.arrayForEach(options.target.incidentlist, function(incident) {
            if ((incident.taskState.orig() === null) && (typeof options.data[incident.id()] === "undefined")) {
              incidents.push({id: incident.id()});
            }
          });

          options.target.setData({incidentlist: incidents});
          return options.target;
        }
      }
    }
  },
  /**
   * Data manipulation before saving
   *
   * @function
   * @param {Object} data The data to save
   * @return {Object} The manipulated data
   */
  beforeSave: {
    value: function(data) {
      delete data.call;
      delete data.ani;
      delete data.withDoc;
      delete data.portable;
      delete data.transportVehicle;
      delete data.crew;
      delete data.position.id;
      delete data.home;

      return data;
    }
  },
  /**
   * Set unit state to "AD"
   *
   * @function
   * @return {void}
   */
  setAD: {
    value: function() {
      if (!this.isAD() && this.id()) {
        Coceso.Ajax.save(ko.toJSON({id: this.id(), state: Coceso.Constants.Unit.state.ad}), "unit/update.json");
      }
    }
  },
  /**
   * Set unit state to "EB"
   *
   * @function
   * @return {void}
   */
  setEB: {
    value: function() {
      if (!this.isEB() && this.id()) {
        Coceso.Ajax.save(ko.toJSON({id: this.id(), state: Coceso.Constants.Unit.state.eb}), "unit/update.json");
      }
    }
  },
  /**
   * Set unit state to "NEB"
   *
   * @function
   * @return {void}
   */
  setNEB: {
    value: function() {
      if (!this.isNEB() && this.id()) {
        Coceso.Ajax.save(ko.toJSON({id: this.id(), state: Coceso.Constants.Unit.state.neb}), "unit/update.json");
      }
    }
  },
  /**
   * Send unit home
   *
   * @function
   * @return {void}
   */
  sendHome: {
    value: function() {
      if (this.id() && !this.disableSendHome()) {
        Coceso.Ajax.save({id: this.id()}, "unit/sendHome.json");
      }
    }
  },
  /**
   * Send unit home
   *
   * @function
   * @return {void}
   */
  standby: {
    value: function() {
      if (this.id() && !this.disableStandby()) {
        Coceso.Ajax.save({id: this.id()}, "unit/standby.json");
      }
    }
  },
  /**
   * Send unit home
   *
   * @function
   * @return {void}
   */
  holdPosition: {
    value: function() {
      if (this.id() && !this.disableHoldPosition()) {
        Coceso.Ajax.save({id: this.id()}, "unit/holdPosition.json");
      }
    }
  }
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
  this.loglist = ko.observableArray();
//options.assigned = false;
  //var start = Date.now();
  Coceso.ViewModels.ViewModelList.call(this, data, options);
  //var end = Date.now();

  //console.log(end - start);

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
          self.setData({loglist: data});
        }
      },
      complete: function() {
        if (interval) {
          window.setTimeout(function() {
            self.load(url, interval);
          }, interval);
        }
      }
    });
  };

  if (this.getOption("initial")) {
    this.load(this.getOption("url", "log/getLast/"+Coceso.Conf.logEntryLimit), this.getOption("autoload") ? Coceso.Conf.interval : false);
  }

};
Coceso.ViewModels.Logs.prototype = Object.create(Coceso.ViewModels.ViewModelList.prototype, /** @lends Coceso.ViewModels.Logs.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      loglist: {
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
      incident: {
        create: function(options) {
          if (!options.parent.getOption("assigned")) {
            return options.data;
          }

          return new Coceso.ViewModels.Incident(options.data, options.parent.getOption("children", {}));
        }
      },
      unit: {
        create: function(options) {
          if (!options.parent.getOption("assigned")) {
            return options.data;
          }

          return new Coceso.ViewModels.Unit(options.data, options.parent.getOption("children", {}));
        }
      },
      json: {
        create: function(options) {
          var data = JSON.parse(options.data);
          //var data = null;
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
  }
});

/**
 * List of Patients
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModelList
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Patients = function(data, options) {
    //Call super constructor
    Coceso.ViewModels.ViewModelList.call(this, data, options);


    /**
     * Available filters
     *
     * @type Object
     */
    var filters = {};

    var filterOption = this.getOption("filter", []);

    /**
     * The selected filters
     *
     * @type Object
     */
    this.filter = {};

    /**
     * Generate a list of active filters
     *
     * @function
     * @type ko.computed
     * @return {Object}
     */
    this.activeFilters = ko.computed(function() {
        var activeFilters = {filter: []};

        //Filters selected in user interface
        var i, filter = {};
        for (i in this.filter) {
            var unwrapped = ko.utils.unwrapObservable(this.filter[i]);
            if (unwrapped.length) {
                filter[i] = {val: unwrapped};
            }
        }
        activeFilters.filter.push({
            filter: filter
        });

        //Filters from options
        for (i in filterOption) {
            if (filters[filterOption[i]]) {
                activeFilters.filter.push(filters[filterOption[i]]);
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
    this.filtered = this.patientlist.extend({filtered: {filters: this.activeFilters}});
};
Coceso.ViewModels.Patients.prototype = Object.create(Coceso.ViewModels.ViewModelList.prototype, /** @lends Coceso.ViewModels.Incidents.prototype */ {
    /**
     * @see Coceso.ViewModels.ViewModel#dataType
     * @override
     */
    dataType: {value: "patients"},
    /**
     * @see Coceso.ViewModels.ViewModel#mappingOptions
     * @override
     */
    mappingOptions: {
        value: {
            patientlist: {
                key: function(data) {
                    return ko.utils.unwrapObservable(data.id);
                },
                create: function(options) {
                    return new Coceso.ViewModels.Patient(options.data, options.parent.getOption("children", {}));
                },
                update: function(options) {
                    options.target.setData(options.data);
                    return options.target;
                }
            }
        }
    }
});

/**
 * Single Patient
 *
 * @constructor
 * @extends Coceso.ViewModels.ViewModelSingle
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Patient = function(data, options) {
    Coceso.ViewModels.ViewModelSingle.call(this, data, options);

    //Detect changes
    this.dependencies.push(this.given_name, this.sur_name, this.insurance_number, this.externalID, this.diagnosis,
                            this.erType, this.info);

};
Coceso.ViewModels.Patient.prototype = Object.create(Coceso.ViewModels.ViewModelSingle.prototype, /** @lends Coceso.ViewModels.Log.prototype */ {
    /**
     * @see Coceso.ViewModels.ViewModel#dataType
     * @override
     */
    dataType: {value: "patients"},
    /**
     * @see Coceso.ViewModels.ViewModel#dataList
     * @override
     */
    dataList: {value: "patientlist"},
    /**
     * @see Coceso.ViewModels.ViewModelSingle#model
     * @override
     */
    model: {value: Coceso.Models.Patient},
    /**
     * @see Coceso.ViewModels.ViewModelSingle#saveUrl
     * @override
     */
    saveUrl: {value: "patient/update.json"},
    /**
     * @see Coceso.ViewModels.ViewModel#mappingOptions
     * @override
     */
    mappingOptions: {
        value: {
            keepChanges: {
                info: true
            }
        }
    }
});

/**
 * Debug viewmodel (contains errors)
 *
 * @returns {undefined}
 */
Coceso.UI.Debug = new function() {
  var self = this;

  this.errors = ko.observableArray();

  this.filtered = this.errors.extend({
    filtered: {
      sort: function(a, b) {
        return (b.timestamp - a.timestamp);
      }
    }
  });

  this.pushHttpError = function(jqXHR, url, data) {
    var time = new Date();
    self.errors.push({
      timestamp: time.getTime(),
      time: time.toLocaleString(),
      type: "HTTP-Error",
      status: jqXHR.status,
      message: jqXHR.statusText,
      url: url,
      data: data
    });
  };
};

/**
 * ViewModel for Notifications
 */
Coceso.ViewModels.Notifications = function() {
    var self = this;

    self.connectionError = ko.observable(false);

    self.incidents = new Coceso.ViewModels.Incidents({},{filter: ['overview', 'new_or_open']});
    self.openIncidentCounter = ko.computed(function() {
        return self.incidents.filtered().length;
    });
    self.openTransportCounter = ko.computed(function() {
        var openTransports = ko.utils.arrayFilter(self.incidents.filtered(), function(inc) {
            return inc.type() === Coceso.Constants.Incident.type.transport;
        });
        return openTransports.length;
    });

    self.radioUnits = new Coceso.ViewModels.Units({}, {filter: ['radio']});
    self.radioCounter = ko.computed(function() {
        return self.radioUnits.filtered().length;
    });

    self.freeUnits = new Coceso.ViewModels.Units({}, {filter: ['free']});
    self.freeCounter = ko.computed(function() {
        return self.freeUnits.filtered().length;
    });

    self.getCss = function(count) {
        return count >= 1 ? "notification-highlight" : "notification-ok";
    };

    self.cssOpen = ko.computed(function() {
        return self.getCss(self.openIncidentCounter());
    });
    self.cssTransport = ko.computed(function() {
        return self.getCss(self.openTransportCounter());
    });
    self.cssRadio = ko.computed(function() {
        return self.getCss(self.radioCounter());
    });
    self.cssFree = ko.computed(function() {
        return self.getCss(self.freeCounter());
    });
    self.cssError = ko.computed(function() {
        return self.connectionError() ? "connection-error" : "connection-ok";
    });
};

/**
 * ViewModel for Custom Log Entry (only used to create a new one)
 */
Coceso.ViewModels.CustomLogEntry = function(options) {
    var self = this;

    var defOptions = {
        text: "",
        unit: 0
    };
    options = $.extend({}, defOptions, options);
    self.text = ko.observable(options.text);
    self.unit = ko.observable(options.unit);

    // No auto-update needed
    //TODO self.unitList = ko.observableArray(Coceso.Ajax.data.unitlist);

    self.error = ko.observable(false);

    self.ok = function() {
        Coceso.Ajax.save(ko.toJSON($.extend({id: 0, unit: null, incident: null},self), function(key, value){
            // Filter field error and ui
            if(key === "error" || key === "ui" || key === "unitList") {
                return;
            }
            // If unit id is given, create anonymous object
            if(key === "unit") {
                return value === 0 ? null : {id: value}
            }
            // All other elements return by default
            return value;
        }),
            "log/add.json", self.afterSave, self.saveError, self.saveError);
    };

    self.saveError = function() {
        self.error(true);
    };

    self.afterSave = function() {
        $("#" + self.ui).dialog("destroy");
    };
};

/**
 * Model and ViewModel for hierarchical View in Unit Window
 */
function Container(data, filtered) {
    var cont = this;

    cont.name = ko.observable(data.name);

    cont.subContainer = ko.observableArray($.map(data.subContainer, function(u) { return new Container(u, filtered)}));
    cont.unitIds = ko.observableArray(data.unitIds);

    cont.filtered = filtered;

    // Contain all Units (Full Object) from filtered, that id is in unitIds
    cont.units = ko.computed(function() {
        return ko.utils.arrayFilter(cont.filtered(), function(unit) {
            return cont.unitIds().indexOf(unit.id()) >= 0;
        }).sort(function(a,b) {
            var t = cont.unitIds();
            return t.indexOf(a.id()) === t.indexOf(b.id()) ? 0 : (t.indexOf(a.id()) < t.indexOf(b.id()) ? -1 : 1);
        });
    });

    cont.availableCounter = ko.computed(function() {
        var count = ko.utils.arrayFilter(cont.units(), function(unit) {
            return unit.isAvailable() || (!unit.portable() && unit.isEB());
        }).length;
        for(var i = 0; i < cont.subContainer().length; i++) {
            count += cont.subContainer()[i].availableCounter();
        }
        return count;
    });

    cont.totalCounter = ko.computed(function() {
        var count = cont.units().length;
        for(var i = 0; i < cont.subContainer().length; i++) {
            count += cont.subContainer()[i].totalCounter();
        }
        return count;
    });
}

function ContainerViewModel(filtered) {
    var self = this;

    self.filtered = filtered;
    self.top = ko.observable(new Container({name: "Loading...", unitIds:[], subContainer: []}, ko.observableArray([])));

    self.load = function() {
        $.getJSON(Coceso.Conf.jsonBase+"unitContainer/getSlim", function(topContainer) {
            self.top(new Container(topContainer, self.filtered));

            // Bind Toggle after data loading
            $('.unit-view-toggle').click(function(){
                //TODO Change this definition if another element is used for toggle
                $(this).parent('.panel').children('.panel-body').slideToggle();
            });
        });
    };
}


