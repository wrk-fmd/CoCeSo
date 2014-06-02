/**
 * CoCeSo
 * Client JS
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @link          https://sourceforge.net/projects/coceso/
 * @package       coceso.client.js
 * @since         Rev. 1
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 *
 * Dependencies:
 *	jquery.js
 *	knockout.js
 *	coceso.client.winman
 */

/**
 * Alias for the localization method
 *
 * @function
 * @param {String} key
 * @return {String}
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
    // 32: Space
    openIncidentKey: 32,
    // 89: Y, 74: J
    yesKey: 89,
    // 78: N
    noKey: 78
  },
  confirmStatusUpdate: true,
  listURLs: {
    units: "unit/getAll.json",
    incidents: "incident/getAllRelevant.json",
    patients: "patient/getAll.json"
  },
  incidentText: {
    task_blue: "E",
    task_non_blue: "A",
    transport: "T",
    relocation: "V",
    tohome: "Einr"
  }
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
 * Global vars
 *
 * @type {Object}
 */
Coceso.Global = {
  notificationViewModel: {},
  dialogViewModel: {
    title: ko.observable(""),
    info_text: ko.observable(""),
    elements: ko.observableArray([])
  },
  patients: {}
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
    language: Coceso.Conf.language
  });

  //ViewModel for notifications
  Coceso.Global.notificationViewModel = new (function() {
    var self = this;

    //Clock data
    this.clock_time = ko.observable("00:00:00");
    this.clock_offset = 0;

    //Update clock
    this.clock_update = function() {
      var currentTime = new Date(new Date() - self.clock_offset);
      var currentHours = currentTime.getHours( );
      var currentMinutes = currentTime.getMinutes( );
      var currentSeconds = currentTime.getSeconds( );

      currentMinutes = (currentMinutes < 10 ? "0" : "") + currentMinutes;
      currentSeconds = (currentSeconds < 10 ? "0" : "") + currentSeconds;

      self.clock_time(currentHours + ":" + currentMinutes + ":" + currentSeconds);
    };

    //Start clock
    $.get(Coceso.Conf.jsonBase + "timestamp", function(data) {
      if (data.time) {
        self.clock_offset = new Date() - data.time;
      }
    });
    setInterval(this.clock_update, 1000);

    this.connectionError = ko.observable(false);

    this.incidents = Coceso.Data.incidents.list.extend({filtered: {filters: {filter: {
            type: {val: [Coceso.Constants.Incident.type.task, Coceso.Constants.Incident.type.transport, Coceso.Constants.Incident.type.relocation]},
            state: {val: [Coceso.Constants.Incident.state.new, Coceso.Constants.Incident.state.open]}
          }}}});

    this.openIncidentCounter = ko.computed(function() {
      return this.incidents().length;
    }, this);
    this.openTransportCounter = ko.computed(function() {
      return ko.utils.arrayFilter(this.incidents(), function(i) {
        return (i.isTransport());
      }).length;
    }, this);

    this.radioCounter = ko.computed(function() {
      return Coceso.Data.units.list.extend({filtered: {filters: {filter: {hasAssigned: true}}}}).length;
    }, this);

    this.freeUnits = new Coceso.ViewModels.Units({filter: ['free']});
    this.freeCounter = ko.computed(function() {
      return Coceso.Data.units.list.extend({filtered: {filters: {filter: {isFree: true}}}}).length;
    }, this);

    this.getCss = function(count) {
      return count >= 1 ? "notification-highlight" : "notification-ok";
    };

    this.cssOpen = ko.computed(function() {
      return this.getCss(this.openIncidentCounter());
    }, this);
    this.cssTransport = ko.computed(function() {
      return this.getCss(this.openTransportCounter());
    }, this);
    this.cssRadio = ko.computed(function() {
      return this.getCss(this.radioCounter());
    }, this);
    this.cssFree = ko.computed(function() {
      return this.getCss(this.freeCounter());
    }, this);
    this.cssError = ko.computed(function() {
      return this.connectionError() ? "connection-error" : "connection-ok";
    }, this);
  })();

  //Initialize window management
  $("#taskbar").winman();

  $(document).on("show.bs.dropdown", ".ui-dialog .dropdown", function(event) {
    $(event.target).find(".dropdown-menu").css({top: 0, left: 0}).position({at: "left bottom", my: "left top", of: $(event.target).find(".dropdown-toggle").first()});
    return true;
  });

  //Preload incidents and units
  Coceso.Ajax.load("incidents");
  Coceso.Ajax.load("units");
  Coceso.Ajax.load("patients");

  Coceso.Helper.initializeModalKeyHandler('next-state-confirm');

  //Load Bindings for Notifications
  ko.applyBindings(Coceso.Global.notificationViewModel, $("#nav-notifications")[0]);

  // Load Bindings for Confirmation Dialog
  ko.applyBindings(Coceso.Global.dialogViewModel, $("#next-state-confirm")[0]);
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
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Incidents(options), $.extend({position: {at: "left+70% top+30%"}}, dialog));
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
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Incident(data || {}), {position: {at: "left+30% top+10%"}});
    return false;
  },
  /**
   * For internal usage with given model
   * @param title
   * @param src
   * @param model
   */
  openIncidentInternally: function(title, src, model) {
    //this.openWindow(title, Coceso.Conf.contentBase + src, model, { position: {at: "left+40% top+20%"}});
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
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Units(options || {}), $.extend({position: {at: "left+20% bottom"}}, dialog));
    return false;
  },
  /**
   * Open the units overview with hierarchical View
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} options
   * @param {Object} dialog Dialog options
   */
  openHierarchyUnits: function(title, src, options, dialog) {
    var sUnits = new Coceso.ViewModels.Units({}, options || {});
    var contVM = new ContainerViewModel(sUnits.filtered);
    contVM.load();
    this.openWindow(title, Coceso.Conf.contentBase + src, contVM, $.extend({position: {at: "left top"}}, dialog));
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
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Unit(data || {}), {position: {at: "left+10% top+20%"}});
    return false;
  },
  /**
   * Open Add-Log Window
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} data
   * @return {boolean}
   */
  openLogAdd: function(title, src, data) {
//        this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.CustomLogEntry(data || {}), { position: {at: "left+20% top+20%"}});
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
//    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Logs({}, options || {}), { position: {at: "left+30% center"}});
    return false;
  },
  openPatient: function(title, src, data) {
    this.openWindow(title, Coceso.Conf.contentBase + src, new Coceso.ViewModels.Patient(data || {}), {position: {at: "left+40% top+30%"}});
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
//    this.openWindow(title, Coceso.Conf.contentBase + src, this.Debug, { position: {at: "left+30% center"}});
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
//    this.openWindow(title, Coceso.Conf.contentBase + src, {}, { position: {at: "left+30% center"}});
    return false;
  },
  /*
   * src has to be a full URL!
   */
  openExternalStatic: function(title, src) {
//    this.openWindow(title, src, {}, { position: {at: "left+30% center"}});
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
  loadOptions: {
    units: {
      url: Coceso.Conf.listURLs.units,
      model: "Unit",
      interval: null,
      id: null
    },
    incidents: {
      url: Coceso.Conf.listURLs.incidents,
      model: "Incident",
      interval: null,
      id: null
    },
    patients: {
      url: Coceso.Conf.listURLs.patients,
      model: "Patient",
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
  load: function(type) {
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
        if (status !== "notmodified" && Coceso.Models[options.model] instanceof Function) {
          var mutated = false;
          ko.utils.arrayForEach(data, function(item) {
            if (!item.id) {
              return;
            }

            if (Coceso.Data[type].models[item.id] instanceof Coceso.Models[options.model]) {
              Coceso.Data[type].models[item.id].setData(item);
            } else {
              Coceso.Data[type].models[item.id] = new Coceso.Models[options.model](item);
              mutated = true;
            }
          });
          if (mutated) {
            Coceso.Data[type].list.evaluateImmediate();
          }
        }
        Coceso.Global.notificationViewModel.connectionError(false);
      },
      complete: function() {
        if (options.interval) {
          options.id = window.setTimeout(Coceso.Ajax.load, options.interval, type);
        }
      },
      error: function(xhr) {
        // 404: not found, 0: no connection to server, 200: error is thrown, because response is not a json (not authenticated)
        if (xhr.status === 404 || xhr.status === 0 || xhr.status === 200) {
          Coceso.Global.notificationViewModel.connectionError(true);
        }
      }
    });
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
          Coceso.Ajax.load(i);
        }
      }
    });
  }
};

/**
 * Global data (list of incidents, units, patients)
 *
 * @namespace Coceso.Data
 * @type Object
 */
Coceso.Data = {
  /**
   * Return the model for a specified incident
   *
   * @param {integer} id
   * @returns {Coceso.Models.Incident}
   */
  getIncident: function(id) {
    if (Coceso.Data.incidents.models[id] instanceof Coceso.Models.Incident) {
      return Coceso.Data.incidents.models[id];
    }
    return null;
  },
  /**
   * Return the model for a specified unit
   *
   * @param {integer} id
   * @returns {Coceso.Models.Unit}
   */
  getUnit: function(id) {
    if (Coceso.Data.units.models[id] instanceof Coceso.Models.Unit) {
      return Coceso.Data.units.models[id];
    }
    return null;
  },
  /**
   * Return the model for a specified patient
   *
   * @param {integer} id
   * @returns {Coceso.Models.Patient}
   */
  getPatient: function(id) {
    if (Coceso.Data.patients.models[id] instanceof Coceso.Models.Patient) {
      return Coceso.Data.patients.models[id];
    }
    return null;
  },
  incidents: {models: {}},
  units: {models: {}},
  patients: {models: {}}
};
Coceso.Data.incidents.list = ko.computed(function() {
  return $.map(Coceso.Data.incidents.models, function(v) {
    return v;
  });
});
Coceso.Data.units.list = ko.computed(function() {
  return $.map(Coceso.Data.units.models, function(v) {
    return v;
  });
});
Coceso.Data.patients.list = ko.computed(function() {
  return $.map(Coceso.Data.patients.models, function(v) {
    return v;
  });
});

// Little Helper go in here
Coceso.Helper = {
  /**
   * Opens Dialog with ID <code>elementID</code>
   * @param elementID String of ID of element, which should be handled as Dialog
   * @param yes function for Handler in case of "yes"
   * @param viewmodel viewmodel to be applied to Dialog.
   *        Structure: {title: "Title", elements: [ {key: "...", val: "..."}, ...] }
   */
  confirmationDialog: function(elementID, yes, viewmodel) {
    if (!elementID) {
      return;
    }

    var modal = $("#" + elementID);

    viewmodel = $.extend(true, {title: "Title", info_text: "", elements: []}, viewmodel);
    //console.info(viewmodel);

    Coceso.Global.dialogViewModel.title(viewmodel.title);
    Coceso.Global.dialogViewModel.info_text(viewmodel.info_text);
    Coceso.Global.dialogViewModel.elements(viewmodel.elements);

    modal.modal({
      backdrop: true  // Show backdrop, custom css for higher z-index!!
      , keyboard: true // Closable with Escape key
      , show: true
    });

    var yesHandler = function() {
      $("#" + elementID).modal('hide');
      if (typeof yes === 'function') {
        yes();
      }
    };


    $("#" + elementID + "-yes").bind('click', yesHandler);

    modal.on('hidden.bs.modal', function(e) {
      $("#" + elementID + "-yes").unbind('click', yesHandler);
    });
  }
  /**
   * Handles the nextState request
   * @param unit Full model of Unit
   * @param incident Full model of Incident
   * @param task Full model of Task
   */
  , nextState: function(unit, incident, task) {
    if (!unit || !incident) {
      console.error("Coceso.Helper.nextState(): invalid unit or incident!");
      return;
    }

    // Return if AO not given and Action is "set to ZAO" (== Assigned if Relocation, else ==ABO)
    if (incident.ao.info() === "" &&
            (task.isABO() ||
                    (incident.isRelocation() &&
                            task.isAssigned())))
    {
      console.info("No AO set, opening Incident Window");
      Coceso.UI.openIncident(_("label.incident.edit"), "incident_form.html", {id: incident.id()});
      return;
    }

    var save = function() {
      console.info("nextState() triggered on Server");
      Coceso.Ajax.save({incident_id: incident.id, unit_id: unit.id}, "incident/nextState.json");
    };

    if (Coceso.Conf.confirmStatusUpdate) {
      var info = _("text.confirmation");
      var elements = [];

      //console.log(incident.type())
      if (incident.type() === Coceso.Constants.Incident.type.standby) {
        if (task.isAssigned())
          info = _("text.standby.send");
        if (task.isAAO())
          info = _("text.standby.end");

        elements = [
          {key: _("label.unit.position"), val: unit.position.info()}
        ];

      } else if (incident.isToHome()) {
        elements = [
          {key: _("label.incident.bo"), val: incident.bo.info()}
          , {key: _("label.incident.ao"), val: incident.ao.info()}
        ];
      } else if (incident.isRelocation()) {
        elements = [
          {key: _("text.confirmation.current"), val: _("label.task.state." + taskstate.toLowerCase())}
          , {key: _("label.incident.ao"), val: incident.ao.info()}
          , {key: _("label.incident.blue"), val: (incident.blue() ? _("label.yes") : _("label.no"))}
          , {key: _("label.incident.info"), val: incident.info()}
        ];
      } else if (incident.isTransport()) {
        elements = [
          {key: _("label.incident.bo"), val: incident.bo.info()}
          , {key: _("label.incident.ao"), val: incident.ao.info()}
          , {key: _("label.incident.blue"), val: (incident.blue() ? _("label.yes") : _("label.no"))}
          , {key: _("label.incident.info"), val: incident.info()}
          , {key: _("label.incident.caller"), val: incident.caller()}
        ];
        if (incident.patient()) {
          var p = incident.patient();
          elements.push({key: _("label.patient"), val: p.given_name() + " " + p.sur_name()});
          elements.push({key: _("label.patient.insurance_number"), val: p.insurance_number()});
          elements.push({key: _("label.patient.info"), val: p.info()});
        }
      } else if (incident.isHoldPosition()) {
        elements = [
          {key: _("label.unit.position"), val: incident.ao.info()}
        ];
      } else {
        elements = [
          {key: _("text.confirmation.current"), val: _("label.task.state." + task.taskState().toLowerCase())}
          , {key: _("label.incident.bo"), val: incident.bo.info()}
          , {key: _("label.incident.ao"), val: incident.ao.info()}
          , {key: _("label.incident.blue"), val: (incident.blue() ? _("label.yes") : _("label.no"))}
          , {key: _("label.incident.info"), val: incident.info()}
          , {key: _("label.incident.caller"), val: incident.caller()}
        ];
        if (incident.patient()) {
          var pp = incident.patient();
          elements.push({key: _("label.patient"), val: pp.given_name() + " " + pp.sur_name()});
          elements.push({key: _("label.patient.insurance_number"), val: pp.insurance_number()});
          elements.push({key: _("label.patient.info"), val: pp.info()});
        }
      }

      var viewmodel = {
        title: "<strong>" + unit.call + "</strong>" + " - " + incident.localizedType()
        , info_text: info
        , elements: elements
      };

      Coceso.Helper.confirmationDialog('next-state-confirm', save, viewmodel);
    } else {
      save();
    }
  }
  , initializeModalKeyHandler: function(elementID) {
    $("#" + elementID).keyup(function(event) {
      if (event.which === Coceso.Conf.keyMapping.noKey) {
        $("#" + elementID + "-no").click();
      }
      if (event.which === Coceso.Conf.keyMapping.yesKey) {
        $("#" + elementID + "-yes").click();
      }
    });
  }
};

/**
 * Contains all the models
 *
 * @namespace Coceso.Models
 * @type Object
 */
Coceso.Models = {};

/**
 * Helper function to set all task related methods, which are needed for both
 * incidents and units
 *
 * @constructor
 * @param {ko.observable} taskState The TaskState
 * @param {integer} incident The incident to use
 * @param {integer} unit The use to use
 */
Coceso.Models.Task = function(taskState, incident, unit) {
  this.taskState = ko.observable(taskState);

  this.incident = function() {
    return Coceso.Data.getIncident(incident);
  };

  this.unit = function() {
    return Coceso.Data.getUnit(unit);
  };

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
   * Return the localized taskState
   *
   * @function
   * @type ko.computed
   * @return {String}
   */
  this.localizedTaskState = ko.computed(function() {
    if (this.taskState && this.taskState() !== null) {
      return _("label.task.state." + this.taskState().toLowerCase());
    }
    return "";
  }, this);
};

/**
 * Single incident
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.Incident = function(data) {
  var self = this;

  //Create basic properties
  this.id = data.id;
  this.ao = {info: ko.observable("")};
  this.bo = {info: ko.observable("")};
  this.units = ko.observableArray([]);
  this.blue = ko.observable(false);
  this.caller = ko.observable(null);
  this.casusNr = ko.observable(null);
  this.info = ko.observable(null);
  this.state = ko.observable(Coceso.Constants.Incident.state.new);
  this.type = ko.observable(Coceso.Constants.Incident.type.task);

  /**
   * Method to set data
   *
   * @param {Object} data
   * @return {void}
   */
  this.setData = function(data) {
    if (data.ao) {
      self.ao.info(data.ao.info);
    }
    if (data.bo) {
      self.bo.info(data.bo.info);
    }
    if (data.units) {
      self.units($.map(data.units, function(taskState, unit) {
        return new Coceso.Models.Task(taskState, null, unit);
      }));
    }
    self.blue(data.blue || false);
    self.caller(data.caller || null);
    self.casusNr(data.casusNr || null);
    self.info(data.info || null);
    self.state(data.state || Coceso.Constants.Incident.state.new);
    self.type(data.type || Coceso.Constants.Incident.type.task);
  };

  //Set data
  this.setData(data);

  /**
   * Generate plain JS for saving
   *
   * @return {Object}
   */
  this.toJS = function() {
    return {
      id: ko.utils.unwrapObservable(self.id),
      ao: {info: self.ao.info()},
      bo: {info: self.bo.info()},
      blue: self.blue(),
      caller: self.caller(),
      casusNr: self.casusNr(),
      info: self.info(),
      state: self.state(),
      type: self.type()
    };
  };

  /**
   * is$Type methods
   */
  {
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
  }

  /**
   * is$State methods
   */
  {
    /**
     * Incident has state "New"
     *
     * @function
     * @type ko.computed
     * @return {boolean}
     */
    this.isNew = ko.computed(function() {
      return (this.state() === Coceso.Constants.Incident.state.new);
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
  }

  /**
   * Return the type as localized string
   *
   * @function
   * @type ko.computed
   * @return {String}
   */
  this.localizedType = ko.computed(function() {
    if (this.type() && this.type() !== null) {
      if (this.isTask() && this.blue()) {
        return _("label.incident.type.task.blue");
      }
      return _("label.incident.type." + this.type().toLowerCase());
    }
    return "";
  }, this);

  /*
   * True, if current Incident will be cancelled on next assign
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.isInterruptible = ko.computed(function() {
    return (this.isStandby() || this.isToHome() || this.isHoldPosition() || this.isRelocation());
  }, this);

  /**
   * If true, Incident is marked red and counted in the Notification Area
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.notifyOpen = ko.computed(function() {
    return (this.isOpen() || this.isNew());
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
  this.typeString = ko.computed(function() {
    if (this.isTask()) {
      return this.blue() ? Coceso.Conf.incidentText.task_blue : Coceso.Conf.incidentText.task_non_blue;
    }
    if (this.isTransport()) {
      return Coceso.Conf.incidentText.transport;
    }
    if (this.isRelocation()) {
      return Coceso.Conf.incidentText.relocation;
    }
    if (this.isToHome()) {
      return Coceso.Conf.incidentText.tohome;
    }
    if (this.isStandby()) {
      return "<span class='glyphicon glyphicon-pause'></span>";
    }
    if (this.isHoldPosition()) {
      return "<span class='glyphicon glyphicon-record'></span>";
    }
    return "";
  }, this);

  this.dropdownTitle = ko.computed(function() {
    var title = this.title();

    if (!title) {
      title = "";
    }

    if (title.length > 30) {
      title = title.substring(0, 30) + "...";
    }
    return "<span class='incident_type_text" + (this.blue() ? " incident_blue" : "")
            + "'>" + this.typeString() + "</span>" + title.split("\n")[0];
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
    if (typeof unitid === "undefined" && self.unitCount() === 1) {
      unitid = self.units.unitlist()[0].id();
    }

    if (unitid && self.id()) {
      var unit = ko.utils.arrayFirst(self.units.unitlist(), function(item) {
        return item.id() === unitid;
      });

      if (unit.id() !== unitid) {
        console.error("Coceso.ViewModel.Incident.nextState(): something went wrong on Unit filtering");
        return;
      }

      Coceso.Helper.nextState(unit, self);

    } else {
      console.warn("called nextState() without valid unit reference!");
      console.warn("Stack:\n" + (new Error()).stack);
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

  this.duplicate = function(unit) {
    options = {caller: self.caller(), bo: {info: self.bo.info()}, ao: {info: self.ao.info()}, info: self.info(), blue: self.blue(), type: self.type()};
    var model = new Coceso.ViewModels.Incident(options);
    if (unit && model.units.unitlist) {
      model.units.unitlist.push(new Coceso.ViewModels.Unit({id: unit.id(), taskState: unit.taskState()}, self.getOption(["children", "children"], {assigned: false, writeable: false})));
      unit.taskState(Coceso.Constants.TaskState.detached);
    }
    Coceso.UI.openIncidentInternally(_("label.incident"), "incident_form.html", model);
    // If unit is duplicated, save new Form immediately to avoid Anomalies in Unit List
    if (unit) {
      model.save();
    }
    self.save();
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
    Coceso.UI.openIncident(_("label.incident.edit"), "incident_form.html", {id: self.id});
  };

  this.unitCount = ko.computed(function() {
    if (!this.units || !this.units.unitlist) {
      return -1;
    }

    return this.units.unitlist().length;
  }, this);

  /**
   * Highlight AO Field if empty an minimum of 1 Unit is ABO
   */
  this.highlightAO = ko.computed(function() {
    if (this.unitCount() > 0) {
      return this.ao.info() === "" && ko.utils.arrayFilter(this.units.unitlist(), function(unit) {
        return unit.isABO();
      }).length >= 1;
    }
    return false;
  }, this);

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
    return ko.utils.arrayFirst(Coceso.Data.patients.list(), function(item) {
      return self.id() === item.id();
    });
  });

  this.openPatient = function() {
    // If Patient doesn't exist, open Window for new Patient with ID from Incident given
    // Otherwise existing Patient will be loaded
    if (self.id()) {
      Coceso.UI.openPatient(_("label.patient.edit"), "patient_form.html", {id: self.id()});
    }

  };
};

/**
 * Single unit
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.Unit = function(data) {
  var self = this;

  //Create basic properties
  this.id = data.id;
  this.ani = data.ani;
  this.call = data.call;
  this.crew = data.crew;
  this.portable = data.portable;
  this.transportVehicle = data.transportVehicle;
  this.withDoc = data.withDoc;

  this.home = {info: ko.observable("")};
  this.position = {info: ko.observable("")};
  this.incidents = ko.observableArray([]);
  this.info = ko.observable(null);
  this.state = ko.observable(Coceso.Constants.Unit.state.ad);

  /**
   * Method to set data (loaded with AJAX)
   *
   * @param {Object} data
   * @returns {void}
   */
  this.setData = function(data) {
    if (data.home) {
      self.home.info(data.home.info);
    }
    if (data.position) {
      self.position.info(data.position.info);
    }
    if (data.incidents) {
      self.incidents($.map(data.incidents, function(taskState, incident) {
        return new Coceso.Models.Task(taskState, incident, null);
      }));
    }

    self.info(data.info);
    self.state(data.state);
  };

  //Set data
  this.setData(data);

  /**
   * Generate plain JS for saving
   *
   * @return {Object}
   */
  this.toJS = function() {
    return {
      id: ko.utils.unwrapObservable(self.id),
      position: {info: self.position.info()},
      info: self.info(),
      state: self.state()
    };
  };

  /**
   * Return the number of assigned incidents
   *
   * @function
   * @type ko.computed
   * @return {Integer}
   */
  this.incidentCount = ko.computed(function() {
    if (!this.incidents) {
      return -1;
    }

    return this.incidents().length;
  }, this);


  this.dropdownIncidents = ko.computed(function() {
    if (!this.incidentCount() <= 0) {
      return [];
    }

    return ko.utils.arrayFilter(this.incidents(), function(item) {
      var i = item.incident();
      return ((i !== null) && (i.isTask() || i.isTransport() || i.isRelocation()));
    });
  }, this);

  /**
   * Returns true or false; If 1 or more incident of Type Task, Transport or Relocation is in incidentlist
   * @type {*}
   */
  this.dropdownActive = ko.computed(function() {
    return (this.dropdownIncidents().length > 0);
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

    return (ko.utils.arrayFirst(this.incidents(), function(item) {
      return (item.isAssigned());
    }) !== null);
  }, this);

  /**
   * Unit is 'free' (not at home, no Incident assigned)
   */
  this.isFree = ko.computed(function() {
    return (this.portable && this.incidentCount() <= 0 && !this.isHome() && !this.isAD());
  }, this);

  /**
   * Unit is available for a new Incident and 'EB'
   * @type {boolean}
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

    var i = this.incidents()[0].incident();
    return (i !== null && !i.isHoldPosition() && !i.isStandby());
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
      var i = this.incidents()[0].incident();
      return (i !== null && !i.isHoldPosition());
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
    return false;
    if (this.isHome() || (this.position.info() === "") || (this.incidentCount() > 1)) {
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
   * @return {string} The CSS class
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
      return "<span class='glyphicon glyphicon-" + (this.isHome() ? "home" : "exclamation-sign") + "'></span>";
    }


    var i = this.incidents()[0].incident();
    if (i === null) {
      return "";
    }
    if (i.isTask() || i.isTransport() || i.isRelocation() || i.isToHome()) {
      return i.typeString() + ": " + _("label.task.state." + this.incidents()[0].taskState().toLowerCase());
    }

    if (i.isStandby() || i.isHoldPosition()) {
      return i.typeString() + (this.incidents()[0].isAssigned() ? ": " + _("label.task.state.assigned") : "");
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
      return this.isFree() ? "unit_state_free" : this.stateCss();
    }

    var i = this.incidents()[0].incident();
    if (i === null) {
      return "";
    }
    if (i.isTask() || i.isTransport()) {
      return (i.blue()) ? "unit_state_task_blue" : "unit_state_task";
    }
    if (i.isRelocation()) {
      return "unit_state_relocation";
    }
    if (i.isHoldPosition()) {
      return "unit_state_holdposition";
    }
    if (i.isToHome()) {
      return "unit_state_tohome";
    }
    if (i.isStandby()) {
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
    if (this.ani !== "") {
      content += "<p><span class='key'>" + _("label.unit.ani") + "</span><span>" + this.ani + "</span></p>";
    }
    if (this.hasHome()) {
      content += "<p><span class='key'> <span class='glyphicon glyphicon-home'></span> </span><span>" + this.home.info() + "</span></p>";
    }
    content += "<p><span class='key'> <span class='glyphicon glyphicon-map-marker'></span> </span><span>" +
            (this.position.info() === "" ? "N/A" : this.position.info()) + "</span></p>";

    content += "<hr>";

    if (this.incidentCount() > 0) {
      var inc = this.incidents();
      for (id in inc) {
        var i = Coceso.Data.getIncident(id);
        if (i !== null) {
          content += "<p><span class='key'>" + i.assignedTitle() + "</span><span>" + _("label.task.state." + inc[id].toLowerCase()) + "</span></p>";
        }
      }
      ;
    }
    content += "</div>";

    return {
      trigger: 'hover focus',
      placement: 'auto right',
      html: true,
      container: 'body',
      title: this.call,
      content: content
    };
  }, this);

  /**
   * Set TaskState to next state
   *
   * @param {int} incid
   * @return {void}
   */
  this.nextState = function(incid) {
    if (typeof incid === "undefined" && self.incidentCount() === 1) {
      incid = self.incidents()[0].incident().id;
    }

    if (incid && self.id) {
      var task = ko.utils.arrayFirst(self.incidents(), function(item) {
        return item.incident().id === incid;
      });

      if (task.incident().id !== incid) {
        console.error("Coceso.ViewModel.Unit.nextState(): something went wrong on Incident filtering");
        return;
      }

      Coceso.Helper.nextState(self, task.incident(), task);

    } else {
      console.warn("called nextState() without valid incident reference! (Unit: " + self.call + ")");
      console.warn("Stack:\n" + (new Error()).stack);
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
    if (model.units.unitlist) {
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
    options = {caller: self.call};
    if (self.portable) {
      options = $.extend(options, {bo: {info: self.position.info()}, blue: true});
    }
    var model = new Coceso.ViewModels.Incident(options);
    if (self.portable && model.units.unitlist) {
      model.units.unitlist.push(new Coceso.ViewModels.Unit({id: self.id, taskState: "ABO"}, self.getOption(["children", "children"], {assigned: false, writeable: false})));
    }
    Coceso.UI.openIncidentInternally(_("label.unit.report_incident"), "incident_form.html", model);
  };

  /**
   * Open in a form
   *
   * @return {void}
   */
  this.openForm = function() {
    Coceso.UI.openUnit(self.call + " - " + _("label.unit.edit"), "unit_form.html", {id: self.id});
  };

  /**
   * Open Log of this Unit
   *
   * @return {void}
   */
  this.openLog = function() {
    if (self.id) {
      Coceso.UI.openLogs("Unit-Log", "log.html", {url: "log/getLastByUnit/" + self.id + "/" + Coceso.Conf.logEntryLimit});
    }
  };
};
Coceso.Models.Unit.prototype = Object.create({}, /** @lends Coceso.Models.Unit.prototype */ {
  /**
   * Set unit state to "AD"
   *
   * @function
   * @return {void}
   */
  setAD: {
    value: function() {
      if (!this.isAD() && this.id) {
        Coceso.Ajax.save(ko.toJSON({id: this.id, state: Coceso.Constants.Unit.state.ad}), "unit/update.json");
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
      if (!this.isEB() && this.id) {
        Coceso.Ajax.save(ko.toJSON({id: this.id, state: Coceso.Constants.Unit.state.eb}), "unit/update.json");
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
      if (!this.isNEB() && this.id) {
        Coceso.Ajax.save(ko.toJSON({id: this.id, state: Coceso.Constants.Unit.state.neb}), "unit/update.json");
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
      if (this.id && !this.disableSendHome()) {
        Coceso.Ajax.save({id: this.id}, "unit/sendHome.json");
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
      if (this.id && !this.disableStandby()) {
        Coceso.Ajax.save({id: this.id}, "unit/standby.json");
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
      if (this.id && !this.disableHoldPosition()) {
        Coceso.Ajax.save({id: this.id}, "unit/holdPosition.json");
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
      cursor: "move",
      zIndex: 1500
    }
  }

});

/**
 * Single patient
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.Patient = function(data) {
  var self = this;

  data = data || {};

  //Create basic properties
  this.id = data.id;
  this.given_name = ko.observable(null);
  this.sur_name = ko.observable(null);
  this.insurance_number = ko.observable(null);
  this.diagnosis = ko.observable(null);
  this.erType = ko.observable(null);
  this.info = ko.observable(null);
  this.externalID = ko.observable(null);
  this.sex = ko.observable("u");

  /**
   * Method to set data (loaded with AJAX)
   *
   * @param {Object} data
   * @returns {void}
   */
  this.setData = function(data) {
    self.given_name(data.given_name);
    self.sur_name(data.sur_name);
    self.insurance_number(data.insurance_number);
    self.diagnosis(data.diagnosis);
    self.erType(data.erType);
    self.info(data.info);
    self.externalID(data.externalID);
    self.sex(data.sex);
  };

  //Set data
  this.setData(data);

  /**
   * Generate plain JS for saving
   *
   * @return {Object}
   */
  this.toJS = function() {
    return {
      id: ko.utils.unwrapObservable(self.id),
      given_name: self.given_name(),
      sur_name: self.sur_name(),
      insurance_number: self.insurance_number(),
      diagnosis: self.diagnosis(),
      erType: self.erType(),
      info: self.info(),
      externalID: self.externalID(),
      sex: self.sex()
    };
  };
};

/**
 * Contains all ViewModels (including baseclasses)
 *
 * @namespace Coceso.ViewModels
 * @type Object
 */
Coceso.ViewModels = {};

/**
 * Filterable models
 *
 * @constructor
 * @param {Object} options
 */
Coceso.ViewModels.Filterable = function(options) {
  var filterOption = options.filter;

  this.disableFilter = {};
  for (var i in filterOption) {
    if (this.filters[filterOption[i]] && this.filters[filterOption[i]].disable) {
      this.disableFilter = $.extend(true, this.disableFilter, this.filters[filterOption[i]].disable);
    }
  }

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
      if (this.filters[filterOption[i]]) {
        activeFilters.filter.push(this.filters[filterOption[i]]);
      }
    }

    return activeFilters;
  }, this);
};

/**
 * List of incidents
 *
 * @constructor
 * @extends Coceso.ViewModels.Filterable
 * @param {Object} options
 */
Coceso.ViewModels.Incidents = function(options) {
  /**
   * Available filters
   *
   * @type Object
   */
  this.filters = {
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

  //Call parent constructor
  Coceso.ViewModels.Filterable.call(this, options);

  /**
   * Filtered view of the incidents array
   *
   * @function
   * @type ko.computed
   * @return {Array}
   */
  this.filtered = Coceso.Data.incidents.list.extend({filtered: {filters: this.activeFilters}});
};

/**
 * List of units
 *
 * @constructor
 * @extends Coceso.ViewModels.Filterable
 * @param {Object} options
 */
Coceso.ViewModels.Units = function(options) {
  /**
   * Available filters
   *
   * @type Object
   */
  this.filters = {
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

  /**
   * The selected filters
   *
   * @type Object
   */
  this.filter = {};

  //Call parent constructor
  Coceso.ViewModels.Filterable.call(this, options);

  /**
   * Filtered view of the incidents array
   *
   * @function
   * @type ko.computed
   * @return {Array}
   */
  this.filtered = Coceso.Data.units.list.extend({filtered: {filters: this.activeFilters}});
};

/**
 * Base class for all Form ViewModels
 *
 * @constructor
 */
Coceso.ViewModels.Form = function() {
  var self = this;

  /**
   * Watch dependencies
   *
   * @type ko.observableArray
   */
  this.dependencies = ko.observableArray().extend({arrayChanges: {}});

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
Coceso.ViewModels.Form.prototype = Object.create({}, /** @lends Coceso.ViewModels.Form.prototype */ {
  /**
   * Save modified data
   *
   * @function
   * @return {boolean}
   */
  save: {
    value: function() {
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
  }
});


/**
 * Single incident
 *
 * @constructor
 * @extends Coceso.Models.Incident
 * @extends Coceso.ViewModels.Form
 * @param {Object} data
 * @param {Object} options
 */
Coceso.ViewModels.Incident = function(data, options) {
  var self = this;

  this.model = null;

  if (data.id) {
    this.model = Coceso.Data.getIncident(data.id);
  }

  if (this.model === null) {
    data.id = null;
    this.model = new Coceso.Models.Incident(data);
  }

  data = $.extend(true, this.model.toJS(), data);

  //Call parent constructors
  Coceso.Models.Incident.call(this, data);
  Coceso.ViewModels.Form.call(this);

  this.id = ko.observable(this.id);

  //Detect changes
  this.ao.info.extend({observeChanges: {server: this.model.ao.info}});
  this.blue.extend({observeChanges: {server: this.model.blue}});
  this.bo.info.extend({observeChanges: {server: this.model.bo.info}});
  this.caller.extend({observeChanges: {server: this.model.caller}});
  this.casusNr.extend({observeChanges: {server: this.model.casusNr}});
  this.info.extend({observeChanges: {server: this.model.info, keepChanges: true}});
  this.state.extend({observeChanges: {server: this.model.state}});
  this.type.extend({observeChanges: {server: this.model.type}});

  this.dependencies.push(this.type, this.blue, this.bo.info, this.ao.info, this.info, this.caller, this.casusNr, this.state);
  if (this.units && this.units.unitlist) {
    this.dependencies.push(this.units.unitlist);
  }

  /**
   * Disable the "Task" type button
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableTask = ko.computed(function() {
    return (this.id() && !this.isTask());
  }, this);

  /**
   * Disable the "Relocation" type button
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableRelocation = ko.computed(function() {
    return (this.id() && !this.isRelocation());
  }, this);

  /**
   * Disable the "Transport" type button
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableTransport = ko.computed(function() {
    return (this.id() && !this.isTransport());
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
    return (this.id() && this.state.orig() !== Coceso.Constants.Incident.state.new);
  }, this);

  /**
   * Disable IncidentState Dispo
   *
   * @function
   * @type ko.computed
   * @return {boolean}
   */
  this.disableDispo = ko.computed(function() {
    return false;
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
    return false;
    if (!this.getOption("writeable") || !this.units.unitlist) {
      return true;
    }

    return (ko.utils.arrayFirst(this.units.unitlist(), function(unit) {
      return (unit.isABO() || unit.isZAO() || unit.isAAO());
    }) === null);
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

  this.duplicate = function(unit) {
    options = {caller: self.caller(), bo: {info: self.bo.info()}, ao: {info: self.ao.info()}, info: self.info(), blue: self.blue(), type: self.type()};
    var model = new Coceso.ViewModels.Incident(options);
    if (unit && model.units.unitlist) {
      model.units.unitlist.push(new Coceso.ViewModels.Unit({id: unit.id(), taskState: unit.taskState()}, self.getOption(["children", "children"], {assigned: false, writeable: false})));
      unit.taskState(Coceso.Constants.TaskState.detached);
    }
    Coceso.UI.openIncidentInternally(_("label.incident"), "incident_form.html", model);
    // If unit is duplicated, save new Form immediately to avoid Anomalies in Unit List
    if (unit) {
      model.save();
    }
    self.save();
  };

  this.unitCount = ko.computed(function() {
    if (!this.units || !this.units.unitlist) {
      return -1;
    }

    return this.units.unitlist().length;
  }, this);

  /**
   * Highlight AO Field if empty an minimum of 1 Unit is ABO
   */
  this.highlightAO = ko.computed(function() {
    if (this.unitCount() > 0) {
      return this.ao.info() === "" && ko.utils.arrayFilter(this.units.unitlist(), function(unit) {
        return unit.isABO();
      }).length >= 1;
    }
    return false;
  }, this);

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
    return ko.utils.arrayFirst(Coceso.Data.patients.list(), function(item) {
      return self.id() === item.id();
    });
  });

  self.openPatient = function() {
    // If Patient doesn't exist, open Window for new Patient with ID from Incident given
    // Otherwise existing Patient will be loaded
    if (self.id()) {
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
Coceso.ViewModels.Incident.prototype = Object.create(Coceso.ViewModels.Form.prototype, /** @lends Coceso.ViewModels.Incident.prototype */ {
  /**
   * @see Coceso.ViewModels.Form#save
   * @override
   *
   * @function
   * @return {boolean}
   */
  save: {
    value: function() {
      var data = this.toJS();

      if (data.ao.info === "") {
        data.ao.id = -2;
      }
      if (data.bo.info === "") {
        data.bo.id = -2;
      }

      Coceso.Ajax.save(ko.toJSON(data), "incident/update.json", this.afterSave, this.saveError, this.saveError);
      return true;
    }
  }
});

/**
 * Single unit
 *
 * @constructor
 * @extends Coceso.ViewModels.Form
 * @param {Object} data
 */
Coceso.ViewModels.Unit = function(data) {
  var self = this;

  this.model = null;

  if (data.id) {
    this.model = Coceso.Data.getUnit(data.id);
  }

  if (this.model === null) {
    data.id = null;
    this.model = new Coceso.Models.Unit(data);
  }

  data = $.extend(true, this.model.toJS(), data);

  data.incidents = {};
  ko.utils.arrayForEach(this.model.incidents(), function(item) {
    data.incidents[item.incident().id] = item.taskState();
  });

  //Call parent constructors
  Coceso.Models.Unit.call(this, data);
  Coceso.ViewModels.Form.call(this);

  this.id = ko.observable(this.id);

  //Detect changes
  this.position.info.extend({observeChanges: {server: this.model.position.info}});
  this.info.extend({observeChanges: {server: this.model.info, keepChanges: true}});
  this.state.extend({observeChanges: {server: this.model.state}});
  this.incidents.extend({arrayChanges: {}});
  ko.utils.arrayForEach(this.incidents(), function(item) {
    item.taskState.extend({observeChanges: {server: this}});
    item.localChange = item.taskState.localChange;
  });

  this.dependencies.push(this.position.info, this.info, this.state, this.incidents);

  /**
   * Assign an incident within the form
   *
   * @param {Event} event The jQuery Event (unused)
   * @param {Object} ui jQuery UI properties
   * @return {void}
   */
  this.assignIncidentForm = function(event, ui) {
    var viewmodel = ko.dataFor(ui.draggable.context);
    if (!(viewmodel instanceof Coceso.Models.Incident)) {
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
Coceso.ViewModels.Unit.prototype = Object.create(Coceso.ViewModels.Form.prototype, /** @lends Coceso.ViewModels.Unit.prototype */ {
  /**
   * @see Coceso.ViewModels.Form#save
   * @override
   *
   * @function
   * @return {boolean}
   */
  save: {
    value: function() {
      var data = this.toJS();

      if (data.position.info === "") {
        data.ao.id = -2;
      }

      Coceso.Ajax.save(ko.toJSON(data), "unit/update.json", this.afterSave, this.saveError, this.saveError);
      return true;
    }
  }
});

/**
 * Single Patient
 *
 * @constructor
 * @extends Coceso.ViewModels.Form
 * @param {Object} data
 */
Coceso.ViewModels.Patient = function(data) {
  this.model = null;

  if (data.id) {
    this.model = Coceso.Data.getPatient(data.id);
  }

  if (this.model === null) {
    this.model = new Coceso.Models.Patient({id: data.id});
  }

  data = $.extend(true, this.model.toJS(), data);

  //Call parent constructors
  Coceso.Models.Patient.call(this, data);
  Coceso.ViewModels.Form.call(this);

  this.id = ko.observable(this.id);

  //Detect changes
  this.given_name.extend({observeChanges: {server: this.model.given_name}});
  this.sur_name.extend({observeChanges: {server: this.model.sur_name}});
  this.insurance_number.extend({observeChanges: {server: this.model.insurance_number}});
  this.externalID.extend({observeChanges: {server: this.model.externalID}});
  this.diagnosis.extend({observeChanges: {server: this.model.diagnosis}});
  this.erType.extend({observeChanges: {server: this.model.erType}});
  this.info.extend({observeChanges: {server: this.model.info, keepChanges: true}});
  this.sex.extend({observeChanges: {server: this.model.sex}});

  this.dependencies.push(this.given_name, this.sur_name, this.insurance_number,
          this.externalID, this.diagnosis, this.erType, this.info, this.sex);
};
Coceso.ViewModels.Patient.prototype = Object.create(Coceso.ViewModels.Form.prototype, /** @lends Coceso.ViewModels.Patient.prototype */ {
  /**
   * @see Coceso.ViewModels.Form#save
   * @override
   *
   * @function
   * @return {boolean}
   */
  save: {
    value: function() {
      Coceso.Ajax.save(ko.toJSON(this.toJS()), "patient/update.json", null, this.saveError, this.saveError);
      return true;
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
    this.load(this.getOption("url", "log/getLast/" + Coceso.Conf.logEntryLimit), this.getOption("autoload") ? Coceso.Conf.interval : false);
  }

};
Coceso.ViewModels.Logs.prototype = Object.create({}, /** @lends Coceso.ViewModels.Logs.prototype */ {
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

  var self = this;

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

  self.openUnitForm = function() {
    if (self.unit && self.unit.id !== null) {
      Coceso.UI.openUnit(_("label.unit.edit"), "unit_form.html", {id: self.unit.id});
    }
  };

  self.openIncidentForm = function() {
    if (self.incident && self.incident.id !== null) {
      Coceso.UI.openIncident(_("label.incident.edit"), "incident_form.html", {id: self.incident.id});
    }
  };

};
Coceso.ViewModels.Log.prototype = Object.create(Coceso.ViewModels.Form.prototype, /** @lends Coceso.ViewModels.Log.prototype */ {
  /**
   * @see Coceso.ViewModels.ViewModel#mappingOptions
   * @override
   */
  mappingOptions: {
    value: {
      ignore: ["concern", "json"],
      incident: {
        create: function(options) {
          return $.extend(true, {id: null}, options.data);
        }
      },
      unit: {
        key: function(data) {
          return ko.utils.unwrapObservable(data.id);
        },
        create: function(options) {
          return $.extend(true, {id: null, call: null}, options.data);
        }
      }
      /*,json: {
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
       }*/
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
 * ViewModel for Custom Log Entry (only used to create a new one)
 *
 * @param {Object} options
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
  self.unitList = ko.observableArray(Coceso.Data.units.unitlist);

  self.error = ko.observable(false);

  self.ok = function() {

    Coceso.Ajax.save(ko.toJSON($.extend({id: 0, unit: null, incident: null}, self), function(key, value) {
      // Filter field error and ui
      if (key === "error" || key === "ui" || key === "unitList") {
        return;
      }
      // If unit id is given, create anonymous object
      if (key === "unit") {
        return (!value) ? null : {id: value};
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
 *
 * @param {Object} data
 * @param {Object} filtered
 */
function Container(data, filtered) {
  var cont = this;

  cont.name = ko.observable(data.name);

  cont.subContainer = ko.observableArray($.map(data.subContainer, function(u) {
    return new Container(u, filtered);
  }));
  cont.unitIds = ko.observableArray(data.unitIds);

  cont.filtered = filtered;

  // Contain all Units (Full Object) from filtered, that id is in unitIds
  cont.units = ko.computed(function() {
    return ko.utils.arrayFilter(cont.filtered(), function(unit) {
      return cont.unitIds().indexOf(unit.id) >= 0;
    }).sort(function(a, b) {
      var t = cont.unitIds();
      return t.indexOf(a.id) === t.indexOf(b.id) ? 0 : (t.indexOf(a.id) < t.indexOf(b.id) ? -1 : 1);
    });
  });

  cont.availableCounter = ko.computed(function() {
    var count = ko.utils.arrayFilter(cont.units(), function(unit) {
      return unit.isAvailable() || (!unit.portable && unit.isEB());
    }).length;
    for (var i = 0; i < cont.subContainer().length; i++) {
      count += cont.subContainer()[i].availableCounter();
    }
    return count;
  });

  cont.totalCounter = ko.computed(function() {
    var count = cont.units().length;
    for (var i = 0; i < cont.subContainer().length; i++) {
      count += cont.subContainer()[i].totalCounter();
    }
    return count;
  });
}

function ContainerViewModel(filtered) {
  var self = this;

  self.filtered = filtered;
  self.top = ko.observable(new Container({name: "Loading...", unitIds: [], subContainer: []}, ko.observableArray([])));

  self.load = function() {
    $.getJSON(Coceso.Conf.jsonBase + "unitContainer/getSlim", function(topContainer) {
      self.top(new Container(topContainer, self.filtered));

      // Bind Toggle after data loading
      $('.unit-view-toggle').click(function() {
        //TODO Change this definition if another element is used for toggle
        $(this).parent('.panel').children('.panel-body').slideToggle();
      });
    });
  };
}


