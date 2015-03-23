/**
 * CoCeSo
 * Client JS - main
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Coceso, ko, L */

/**
 * Some global settings
 *
 * @type Object
 */
Coceso.Conf.listURLs = {
  units: "unit/getAll.json",
  incidents: "incident/getAllRelevant.json",
  patients: "patient/getAll.json"
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
      "new": "New",
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
  },
  Patient: {
    sex: {
      unknown: "u",
      male: "m",
      female: "f"
    }
  }
};

/**
 * Initialize the application
 *
 * @returns {void}
 */
Coceso.startup = function() {
  //Initialize localization
  Coceso.initi18n();

  //Lock concern changing
  Coceso.Lock.lock();

  //Initialize window management
  $("#taskbar").winman();

  $(document).on("show.bs.dropdown", ".ui-dialog .dropdown", function(event) {
    $(event.target).find(".dropdown-menu").css({top: 0, left: 0}).position({at: "left bottom", my: "left top", of: $(event.target).find(".dropdown-toggle").first()});
    return true;
  });

  //Preload incidents, units and patients
  Coceso.Ajax.load("incidents");
  Coceso.Ajax.load("units");
  Coceso.Ajax.load("patients");

  //Initialize key handler
  if (Coceso.Conf.keyboardControl) {
    $("#next-state-confirm").keyup(function(event) {
      if (event.which === Coceso.Conf.keyMapping.noKey) {
        $("#next-state-confirm-no").click();
      }
      if (event.which === Coceso.Conf.keyMapping.yesKey) {
        $("#next-state-confirm-yes").click();
      }
    });
    $("body").keyup(function(event) {
      var t = event.target.tagName;
      if (t === "INPUT" || t === "TEXTAREA" || t === "SELECT" || t === "BUTTON") {
        return;
      }
      if (event.which === Coceso.Conf.keyMapping.openIncidentKey) {
        Coceso.UI.openIncident();
      }
    });
  }

  $(".tooltipped").tooltip();
  $(document).on("click", ".panel-toggle", function() {
    $(this).parent(".panel").children(".panel-body").slideToggle();
    $(this).find(".glyphicon").toggleClass("glyphicon-chevron-down glyphicon-chevron-up");
  });

  //Load Bindings for Notifications
  Coceso.UI.Notifications = new Coceso.ViewModels.Notifications();
  ko.applyBindings(Coceso.UI.Notifications, $("#nav-notifications")[0]);

  //Load Bindings for status confirmation window
  ko.applyBindings(Coceso.UI.Dialog, $("#next-state-confirm")[0]);

  //Initialize autocomplete handler
  Coceso.initAutocomplete();
};

/**
 * Initialize only map
 *
 * @returns {void}
 */
Coceso.startupMap = function() {
  //Initialize localization
  Coceso.initi18n();

  //Preload incidents and units
  Coceso.Ajax.load("incidents");
  Coceso.Ajax.load("units");

  var options = {};
  if (location.search) {
    var query = location.search.replace(/&+/g, '&').replace(/^\?*&*|&+$/g, '');
    if (query.length) {
      var splits = query.split('&');
      var length = splits.length;
      var v, name, value;

      for (var i = 0; i < length; i++) {
        v = splits[i].split('=');
        name = decodeURIComponent(v.shift().replace(/\+/g, '%20'));
        value = v.length ? decodeURIComponent(v.join('=').replace(/\+/g, '%20')) : null;

        if (options.hasOwnProperty(name)) {
          if (typeof options[name] === 'string') {
            options[name] = [options[name]];
          }

          options[name].push(value);
        } else {
          options[name] = value;
        }
      }
    }
  }

  //Load Map ViewModel
  var viewModel = new Coceso.ViewModels.Map(options);
  ko.applyBindings(viewModel);
  viewModel.init();
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
   * Only used for debugging (global access to all viewmodels)
   *
   * @type Object
   */
  windows: {},
  /**
   * Notifications
   *
   * @type Coceso.ViewModels.Notifications
   */
  Notifications: null,
  /**
   * Confirmation dialog data
   *
   * @function
   * @type ko.observable
   * @returns {Object}
   */
  Dialog: ko.observable({title: "", info_text: "", button_text: "", elements: [], save: null}),
  /**
   * Add a window to the UI
   *
   * @param {String} src The source to load the HTML from
   * @param {Object} viewmodel The viewmodel to bind with
   * @param {Object} options
   * @param {String} title The title of the window
   * @returns {void}
   */
  openWindow: function(src, viewmodel, options, title) {
    var id = $("#taskbar").winman("addWindow", src, options, viewmodel, title, function(el, id) {
      delete Coceso.UI.windows[id];
    });
    this.windows[id] = viewmodel;
  },
  /**
   * Open the incidents overview
   *
   * @param {Object} options
   * @param {Object} dialog Dialog options
   * @returns {boolean} false
   */
  openIncidents: function(options, dialog) {
    this.openWindow(Coceso.Conf.contentBase + "incident.html", new Coceso.ViewModels.Incidents(options || {}), $.extend({position: {at: "left+70% top+30%"}}, dialog));
    return false;
  },
  /**
   * Open a specific incident
   *
   * @param {Object} data Additional incident data
   * @param {Object} dialog Dialog options
   * @returns {boolean} false
   */
  openIncident: function(data, dialog) {
    this.openWindow(Coceso.Conf.contentBase + "incident_form.html", new Coceso.ViewModels.Incident(data || {}), $.extend({position: {at: "left+30% top+10%"}}, dialog));
    return false;
  },
  /**
   * Open the units overview
   *
   * @param {Object} options Viewmodel options
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openUnits: function(options, dialog) {
    this.openWindow(Coceso.Conf.contentBase + "unit.html", new Coceso.ViewModels.Units(options || {}), $.extend({position: {at: "left+20% bottom"}}, dialog));
    return false;
  },
  /**
   * Open the units overview with hierarchical View
   *
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openHierarchyUnits: function(dialog) {
    this.openWindow(Coceso.Conf.contentBase + "unit_hierarchy.html", new Coceso.ViewModels.Hierarchical(), $.extend({position: {at: "left top"}}, dialog));
    return false;
  },
  /**
   * Open the unit edit Window
   *
   * @param {Object} data Additional unit data
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openUnit: function(data, dialog) {
    this.openWindow(Coceso.Conf.contentBase + "unit_form.html", new Coceso.ViewModels.Unit(data || {}), $.extend({position: {at: "left+10% top+20%"}}, dialog));
    return false;
  },
  /**
   * Open the unit edit Window
   *
   * @param {Integer} id Unit id
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openUnitDetail: function(id, dialog) {
    if (id) {
      this.openWindow(Coceso.Conf.contentBase + "unit_detail.html", new Coceso.ViewModels.UnitDetail(id), $.extend({position: {at: "left+10% top+20%"}}, dialog));
    }
    return false;
  },
  /**
   * Open Add-Log Window
   *
   * @param {Object} data Additional log data
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openLogAdd: function(data, dialog) {
    this.openWindow(Coceso.Conf.contentBase + "log_add.html", new Coceso.ViewModels.CustomLogEntry(data || {}), $.extend({position: {at: "left+20% top+20%"}}, dialog));
    return false;
  },
  /**
   * Open a list of log entries
   *
   * @param {Object} options Viewmodel options
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openLogs: function(options, dialog) {
    this.openWindow(Coceso.Conf.contentBase + "log.html", new Coceso.ViewModels.Logs(options || {}), $.extend({position: {at: "left+30% top+10%"}}, dialog));
    return false;
  },
  /**
   * Open the patient edit Window
   *
   * @param {Object} data Additional patient data
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openPatient: function(data, dialog) {
    this.openWindow(Coceso.Conf.contentBase + "patient_form.html", new Coceso.ViewModels.Patient(data || {}), $.extend({position: {at: "left+40% top+30%"}}, dialog));
    return false;
  },
  /**
   * Open the patient edit Window
   *
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openRadio: function(dialog) {
    this.openWindow(Coceso.Conf.contentBase + "radio.html", new Coceso.ViewModels.Radio(), $.extend({position: {at: "left+40% top+10%"}}, dialog));
    return false;
  },
  /**
   * Open the map window
   *
   * @param {Object} options Viewmodel options
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openMap: function(options, dialog) {
    this.openWindow(Coceso.Conf.contentBase + "map.html", new Coceso.ViewModels.Map(options || {}), $.extend({position: {at: "left+10% top"}}, dialog));
    return false;
  },
  /**
   * Open static content
   *
   * @param {String} title
   * @param {String} src
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openStatic: function(title, src, dialog) {
    this.openWindow(Coceso.Conf.contentBase + src, {}, $.extend({position: {at: "left+10% top+20%"}}, dialog), title);
    return false;
  },
  /**
   * Open external static content
   *
   * @param {String} title
   * @param {String} src Full URL to content
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openExternalStatic: function(title, src, dialog) {
    this.openWindow(src, {}, $.extend({position: {at: "left+30% top+10%"}}, dialog), title);
    return false;
  },
  /**
   * Resize the workspace (scrollable dialog container)
   *
   * @param {Float} factor
   * @returns {boolean}
   */
  resizeWorkspace: function(factor) {
    var cont = $("#dialog_container");
    cont.height(factor ? cont.height() * factor : "");
    cont.width(factor ? cont.width() * factor : "");
    return false;
  }
};

/**
 * AJAX options
 *
 * @type Object
 */
Coceso.Ajax.loadOptions = {
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
    if (Coceso.Data.incidents.models()[id] instanceof Coceso.Models.Incident) {
      return Coceso.Data.incidents.models()[id];
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
    if (Coceso.Data.units.models()[id] instanceof Coceso.Models.Unit) {
      return Coceso.Data.units.models()[id];
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
    if (Coceso.Data.patients.models()[id] instanceof Coceso.Models.Patient) {
      return Coceso.Data.patients.models()[id];
    }
    return null;
  },
  incidents: {models: ko.observable({})},
  units: {models: ko.observable({})},
  patients: {models: ko.observable({})}
};

/**
 * Dynamically generated list of incidents
 *
 * @function
 * @type ko.computed
 * @returns {Array}
 */
Coceso.Data.incidents.list = ko.computed(function() {
  return $.map(Coceso.Data.incidents.models(), function(v) {
    return v;
  });
});

/**
 * Dynamically generated list of units
 *
 * @function
 * @type ko.computed
 * @returns {Array}
 */
Coceso.Data.units.list = ko.computed(function() {
  return $.map(Coceso.Data.units.models(), function(v) {
    return v;
  });
});

/**
 * Dynamically generated list of patients
 *
 * @function
 * @type ko.computed
 * @returns {Array}
 */
Coceso.Data.patients.list = ko.computed(function() {
  return $.map(Coceso.Data.patients.models(), function(v) {
    return v;
  });
});

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
  incident = parseInt(incident);
  unit = parseInt(unit);

  /**
   * The TaskState
   *
   * @function
   * @type ko.observable
   * @returns {String}
   */
  this.taskState = ko.observable(taskState);

  /**
   * The incident id
   *
   * @type Integer
   */
  this.incident_id = (incident && !isNaN(incident)) ? incident : null;

  /**
   * The unit id
   *
   * @type Integer
   */
  this.unit_id = (unit && !isNaN(unit)) ? unit : null;

  /**
   * Get the associated incident
   *
   * @function
   * @type ko.pureComputed
   * @returns {Coceso.Models.Incident}
   */
  this.incident = ko.pureComputed(function() {
    return Coceso.Data.getIncident(this.incident_id);
  }, this);

  /**
   * Get the associated unit
   *
   * @function
   * @type ko.pureComputed
   * @returns {Coceso.Models.Unit}
   */
  this.unit = ko.pureComputed(function() {
    return Coceso.Data.getUnit(this.unit_id);
  }, this);

  /**
   * Return if TaskState is "Assigned"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isAssigned = this.taskState.extend({isValue: Coceso.Constants.TaskState.assigned});

  /**
   * Return if TaskState is "ZBO"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isZBO = this.taskState.extend({isValue: Coceso.Constants.TaskState.zbo});

  /**
   * Return if TaskState is "ABO"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isABO = this.taskState.extend({isValue: Coceso.Constants.TaskState.abo});

  /**
   * Return if TaskState is "ZAO"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isZAO = this.taskState.extend({isValue: Coceso.Constants.TaskState.zao});

  /**
   * Return if TaskState is "AAO"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isAAO = this.taskState.extend({isValue: Coceso.Constants.TaskState.aao});

  /**
   * Return if TaskState is "Detached"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isDetached = this.taskState.extend({isValue: Coceso.Constants.TaskState.detached});

  /**
   * Return the localized taskState
   *
   * @function
   * @type ko.pureComputed
   * @returns {String}
   */
  this.localizedTaskState = ko.pureComputed(function() {
    if (this.taskState()) {
      return _("label.task.state." + this.taskState().toLowerCase().escapeHTML());
    }
    return "";
  }, this);

  /**
   * Text based on the incident's type
   *
   * @function
   * @type ko.pureComputed
   * @returns {string} The text
   */
  this.taskText = ko.pureComputed(function() {
    var i = this.incident();
    if (i === null) {
      return this.localizedTaskState();
    }
    if (i.isTask() || i.isTransport() || i.isRelocation() || i.isToHome()) {
      return i.typeChar() + ": " + this.localizedTaskState();
    }

    if (i.isStandby() || i.isHoldPosition()) {
      return i.typeChar() + (this.isAssigned() ? ": " + this.localizedTaskState() : "");
    }

    return "";
  }, this);
};
Coceso.Models.Task.prototype = Object.create({}, /** @lends Coceso.Models.Task.prototype */ {
  /**
   * Handles the nextState request
   *
   * @function
   * @returns {void}
   */
  nextState: {
    value: function() {
      var incident = this.incident(), unit = this.unit();

      if (!incident || !unit) {
        //Incident or unit not available
        console.error("Coceso.Models.TaskState.nextState(): invalid unit or incident!");
        return;
      }

      //Get next state

      /*
       * Allowed states for types
       *
       *  Task, Transport
       *    Assigned, ZBO, ABO, ZAO, AAO, Detached
       *  Relocation, ToHome
       *    Assigned, ZAO, AAO -> Detached
       *  Standby, HoldPosition
       *    Assigned, AAO, Detached
       */

      var task = (incident.isTask() || incident.isTransport()),
          reloc = (incident.isRelocation() || incident.isToHome()),
          hold = (incident.isStandby() || incident.isHoldPosition());

      var s = Coceso.Constants.TaskState;

      var nextState = null, needAO = false;
      if (this.isAssigned()) {
        if (task) {
          nextState = s.zbo;
        } else if (reloc) {
          needAO = incident.isRelocation();
          nextState = s.zao;
        } else if (hold) {
          nextState = s.aao;
        }
      } else if (this.isZBO()) {
        if (task) {
          nextState = s.abo;
        }
      } else if (this.isABO()) {
        if (task) {
          needAO = true;
          nextState = s.zao;
        }
      } else if (this.isZAO()) {
        if (task || reloc) {
          needAO = (!incident.isToHome());
          nextState = s.aao;
        }
      } else if (this.isAAO()) {
        if (task || hold) {
          nextState = s.detached;
        }
      }

      if (nextState === null) {
        //Next state is not defined
        console.error("No next state defined!");
        return;
      }

      if (needAO && !incident.ao.id() && (nextState === s.zao || nextState === s.aao)) {
        console.info("No AO set, opening Incident Window");
        Coceso.UI.openIncident({id: incident.id});
        return;
      }

      if (Coceso.Conf.confirmStatusUpdate) {
        var info = _("text.confirmation"), button = _("label.confirmation.yes"), elements = [];

        if (incident.isStandby()) {
          if (nextState === s.aao) {
            info = _("text.standby.send");
          } else if (nextState === s.detached) {
            info = _("text.standby.end");
          }

          elements = [
            {key: _("label.unit.position"), val: unit.position.info()}
          ];
        } else if (incident.isHoldPosition()) {
          if (nextState === s.aao) {
            info = _("text.holdposition.send");
          } else if (nextState === s.detached) {
            info = _("text.holdposition.end");
          }

          elements = [
            {key: _("label.unit.position"), val: incident.ao.info()}
          ];
        } else if (incident.isToHome()) {
          elements = [
            {key: _("label.incident.bo"), val: incident.bo.info()},
            {key: _("label.incident.ao"), val: incident.ao.info()}
          ];

          button = (nextState === s.zao) ? _("label.task.state.zao") : _("label.task.state.ishome");
        } else if (incident.isRelocation()) {
          elements = [
            {key: _("text.confirmation.current"), val: this.localizedTaskState()},
            {key: _("label.incident.ao"), val: incident.ao.info()},
            {key: _("label.incident.blue"), val: (incident.blue() ? _("label.yes") : _("label.no"))},
            {key: _("label.incident.info"), val: incident.info()}
          ];

          button = _("label.task.state." + nextState.toLowerCase());
        } else {
          elements = [
            {key: _("text.confirmation.current"), val: this.localizedTaskState()},
            {key: _("label.incident.bo"), val: incident.bo.info()},
            {key: _("label.incident.ao"), val: incident.ao.info()},
            {key: _("label.incident.blue"), val: (incident.blue() ? _("label.yes") : _("label.no"))},
            {key: _("label.incident.info"), val: incident.info()},
            {key: _("label.incident.caller"), val: incident.caller()}
          ];
          if (incident.patient()) {
            var pp = incident.patient();
            elements.push({key: _("label.patient"), val: pp.given_name() + " " + pp.sur_name()});
            elements.push({key: _("label.patient.insurance_number"), val: pp.insurance_number()});
            elements.push({key: _("label.patient.info"), val: pp.info()});
          }

          button = _("label.task.state." + nextState.toLowerCase());
        }

        Coceso.UI.Dialog({
          title: "<strong>" + unit.call.escapeHTML() + "</strong>" + " - " + incident.typeString(),
          info_text: info, button_text: button, elements: elements,
          save: function() {
            console.info("nextState() triggered on Server");
            Coceso.Ajax.save({incident_id: incident.id, unit_id: unit.id, state: nextState}, "incident/setToState.json");
          }
        });

        $("#next-state-confirm").modal({backdrop: true, keyboard: true, show: true});
      } else {
        console.info("nextState() triggered on Server");
        Coceso.Ajax.save({incident_id: incident.id, unit_id: unit.id, state: nextState}, "incident/setToState.json");
      }
    }
  }
});

/**
 * Point
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.Point = function(data) {
  this.id = ko.observable(null);
  this.info = ko.observable("");
  this.lat = ko.observable(null);
  this.lng = ko.observable(null);

  this.setData = function(data) {
    data = data || {};
    this.id(data.id || null);
    this.info(data.info || "");
    this.lat(typeof data.latitude === "undefined" ? null : data.latitude || null);
    this.lng(typeof data.longitude === "undefined" ? null : data.longitude || null);
  };

  /**
   * Get static representation of the point
   *
   * @function
   * @type ko.computed
   * @returns {Object}
   */
  this.getStatic = ko.pureComputed(function() {
    return {
      id: this.id(),
      info: this.info(),
      lat: this.lat(),
      lng: this.lng()
    };
  }, this).extend({rateLimit: 5});

  if (data) {
    this.setData(data);
  }
};

/**
 * Single incident
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.Incident = function(data) {
  var self = this;
  data = data || {};

  //Create basic properties
  this.id = data.id;
  this.ao = new Coceso.Models.Point();
  this.bo = new Coceso.Models.Point();
  this.units = ko.observableArray([]);
  this.blue = ko.observable(false);
  this.caller = ko.observable("");
  this.casusNr = ko.observable("");
  this.info = ko.observable("");
  this.state = ko.observable(Coceso.Constants.Incident.state["new"]);
  this.type = ko.observable(Coceso.Constants.Incident.type.task);

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
          self.units.push(new Coceso.Models.Task(taskState, self.id, unit));
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
    self.caller(data.caller || "");
    self.casusNr(data.casusNr || "");
    self.info(data.info || "");
    self.state(data.state || Coceso.Constants.Incident.state["new"]);
    self.type(data.type || Coceso.Constants.Incident.type.task);
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
  this.isTask = this.type.extend({isValue: Coceso.Constants.Incident.type.task});

  /**
   * Incident is of type "Relocation"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isRelocation = this.type.extend({isValue: Coceso.Constants.Incident.type.relocation});

  /**
   * Incident is of type "Transport"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isTransport = this.type.extend({isValue: Coceso.Constants.Incident.type.transport});

  /**
   * Incident is of type "ToHome"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isToHome = this.type.extend({isValue: Coceso.Constants.Incident.type.tohome});

  /**
   * Incident is of type "HoldPosition"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isHoldPosition = this.type.extend({isValue: Coceso.Constants.Incident.type.holdposition});

  /**
   * Incident is of type "Standby"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isStandby = this.type.extend({isValue: Coceso.Constants.Incident.type.standby});

  /**
   * Incident has state "New"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isNew = this.state.extend({isValue: Coceso.Constants.Incident.state["new"]});

  /**
   * Incident has state "Open"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isOpen = this.state.extend({isValue: Coceso.Constants.Incident.state.open});

  /**
   * Incident has state "Dispo"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isDispo = this.state.extend({isValue: Coceso.Constants.Incident.state.dispo});

  /**
   * Incident has state "Working"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isWorking = this.state.extend({isValue: Coceso.Constants.Incident.state.working});

  /**
   * Incident has state "Done"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isDone = this.state.extend({isValue: Coceso.Constants.Incident.state.done});

  /**
   * The associated patient
   *
   * @function
   * @type ko.pureComputed
   * @returns {Coceso.Model.Patient}
   */
  this.patient = ko.pureComputed(function() {
    return Coceso.Data.getPatient(this.id);
  }, this);

  /**
   * True, if current Incident will be cancelled on next assign
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isInterruptible = ko.computed(function() {
    return (this.isStandby() || this.isToHome() || this.isHoldPosition() || this.isRelocation());
  }, this);

  /**
   * If true, Incident is marked red and counted in the Notification Area
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isNewOrOpen = ko.computed(function() {
    return (this.isOpen() || this.isNew());
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
   * Disable "Assigned" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableAssigned = ko.computed(function() {
    return (this.isStandby() || this.isHoldPosition());
  }, this);

  /**
   * Incident has an AO
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.hasAO = ko.computed(function() {
    return !!this.ao.id();
  }, this);

  /**
   * Disable "AAO" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableAAO = ko.computed(function() {
    return !this.hasAO();
  }, this);

  /**
   * Disable "ZAO" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableZAO = ko.computed(function() {
    return (this.isStandby() || this.isHoldPosition() || !this.hasAO());
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
      return (this.bo.id()) ? this.bo.info() : _("label.incident.nobo");
    }
    return (this.ao.id()) ? this.ao.info() : _("label.incident.noao");
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
        return _("label.incident.type.task.blue");
      }
      return _("label.incident.type." + this.type().toLowerCase().escapeHTML());
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
      return this.blue() ? _("label.incident.stype.task.blue") : _("label.incident.stype.task");
    }
    if (this.isTransport()) {
      return _("label.incident.stype.transport");
    }
    if (this.isRelocation()) {
      return _("label.incident.stype.relocation");
    }
    if (this.isToHome()) {
      return _("label.incident.stype.tohome");
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
   * CSS class based on the incident's type
   *
   * @function
   * @type ko.pureComputed
   * @returns {string} The CSS class
   */
  this.typeCss = ko.pureComputed(function() {
    if (this.isTask() || this.isTransport()) {
      return (this.blue()) ? "unit_state_task_blue" : "unit_state_task";
    }
    if (this.isRelocation()) {
      return "unit_state_relocation";
    }
    if (this.isHoldPosition()) {
      return "unit_state_holdposition";
    }
    if (this.isToHome()) {
      return "unit_state_tohome";
    }
    if (this.isStandby()) {
      return "unit_state_standby";
    }

    return "";
  }, this);

  /**
   * Title in unit dropdown
   *
   * @function
   * @type ko.pureComputed
   * @returns {String}
   */
  this.dropdownTitle = ko.pureComputed(function() {
    var title = this.title();
    if (!title) {
      title = "";
    }
    if (title.length > 30) {
      title = title.substring(0, 30) + "...";
    }
    return "<span class='incident_type_text" + (this.blue() ? " incident_blue" : "")
        + "'>" + this.typeChar() + "</span>" + title.split("\n")[0].escapeHTML();
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

  /**
   * Assign a unit to an incident in the list
   *
   * @param {Event} event The jQuery Event (unused)
   * @param {Object} ui jQuery UI properties
   * @returns {void}
   */
  this.assignUnitList = function(event, ui) {
    var unit = ko.dataFor(ui.draggable.context);

    if ((unit instanceof Coceso.Models.Unit) && self.id && unit.id) {
      Coceso.Ajax.save({incident_id: self.id, unit_id: unit.id}, "assignUnit.json");
    }
  };
};
Coceso.Models.Incident.prototype = Object.create({}, /** @lends Coceso.Models.Incident.prototype */ {
  /**
   * Open in a form
   *
   * @function
   * @returns {void}
   */
  openForm: {
    value: function() {
      var id;
      if (this instanceof Coceso.Models.Incident) {
        id = this.id;
      } else if (this instanceof Coceso.Models.Task) {
        id = this.incident_id;
      }
      if (id) {
        Coceso.UI.openIncident({id: id});
      }
    }
  },
  /**
   * Add log entry for this incident
   *
   * @function
   * @returns {void}
   */
  addLog: {
    value: function() {
      if (this.id) {
        Coceso.UI.openLogAdd({incident: this.id});
      }
    }
  },
  /**
   * Open log
   *
   * @returns {void}
   */
  openLog: {
    value: function() {
      if (this.id) {
        Coceso.UI.openLogs({url: "log/getByIncident/" + this.id, title: "Incident-Log"});
      }
    }
  },
  /**
   * Open patient
   *
   * @returns {void}
   */
  openPatient: {
    value: function() {
      Coceso.UI.openPatient({id: this.id});
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
      Coceso.Helpers.destroyComputed(this);
    }
  }
});

/**
 * Single unit
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.Unit = function(data) {
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

  this.home = new Coceso.Models.Point();
  this.position = new Coceso.Models.Point();
  this.incidents = ko.observableArray([]);
  this.info = ko.observable("");
  this.state = ko.observable(Coceso.Constants.Unit.state.ad);

  /**
   * Method to set data (loaded with AJAX)
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
          self.incidents.push(new Coceso.Models.Task(taskState, incident, self.id));
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
    self.state(data.state || Coceso.Constants.Unit.state.ad);
  };

  //Set data
  this.setData(data);

  /**
   * Return the position to show in map
   * Moving units are displayed by corresponding incidents
   *
   * @function
   * @type ko.pureComputed
   * @returns {Coceso.Models.Point}
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
    return !!this.home.id();
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
   * Unit has state "AD"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isAD = this.state.extend({isValue: Coceso.Constants.Unit.state.ad});

  /**
   * Unit has state "EB"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isEB = this.state.extend({isValue: Coceso.Constants.Unit.state.eb});

  /**
   * Unit has state "NEB"
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isNEB = this.state.extend({isValue: Coceso.Constants.Unit.state.neb});

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

    return (ko.utils.arrayFirst(this.incidents(), function(task) {
      return (task.isAssigned());
    }) !== null);
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
    if (this.isHome() || !this.position.id() || (this.incidentCount() > 1)) {
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
    if (this.ani) {
      content += "<dt>" + _("label.unit.ani") + "</dt><dd>" + this.ani.escapeHTML() + "</dd>";
    }
    if (this.hasHome()) {
      content += "<dt><span class='glyphicon glyphicon-home'></span></dt><dd><span class='pre'>" + this.home.info().escapeHTML() + "</span></dd>";
    }
    content += "<dt><span class='glyphicon glyphicon-map-marker'></span></dt><dd><span class='pre'>" +
        (this.position.id() ? this.position.info().escapeHTML() : "N/A") + "</span></dd>";

    content += "</dl><hr/><dl class='dl-horizontal'>";

    if (this.incidentCount() > 0) {
      ko.utils.arrayForEach(this.incidents(), function(task) {
        if (task.incident() !== null) {
          content += "<dt>" + task.incident().assignedTitle() + "</dt><dd>" + task.localizedTaskState() + "</dd>";
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
    var data = {units: {}};
    data.units[this.id] = Coceso.Constants.TaskState.assigned;
    Coceso.UI.openIncident(data);
  };

  /**
   * Open incident form with Unit as caller
   * BO is set to Position of Unit, BlueLight is true by default
   *
   * @returns {void}
   */
  this.reportIncident = function() {
    var data = {caller: self.call};
    if (this.portable) {
      data.bo = {info: self.position.info()};
      data.blue = true;
      data.units = {};
      data.units[self.id] = Coceso.Constants.TaskState.abo;
    }
    Coceso.UI.openIncident(data);
  };
};
Coceso.Models.Unit.prototype = Object.create({}, /** @lends Coceso.Models.Unit.prototype */ {
  /**
   * Set unit state to "AD"
   *
   * @function
   * @returns {void}
   */
  setAD: {
    value: function() {
      if (this.id && !this.isAD()) {
        Coceso.Ajax.save(JSON.stringify({id: this.id, state: Coceso.Constants.Unit.state.ad}), "unit/update.json");
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
        Coceso.Ajax.save(JSON.stringify({id: this.id, state: Coceso.Constants.Unit.state.eb}), "unit/update.json");
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
        Coceso.Ajax.save(JSON.stringify({id: this.id, state: Coceso.Constants.Unit.state.neb}), "unit/update.json");
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
        Coceso.Ajax.save({id: this.id}, "unit/sendHome.json");
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
        Coceso.Ajax.save({id: this.id}, "unit/standby.json");
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
        Coceso.Ajax.save({id: this.id}, "unit/holdPosition.json");
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
        Coceso.Ajax.save(JSON.stringify({ani: this.ani}), "radio/send.json");
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
      Coceso.UI.openUnit({id: this.id});
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
      Coceso.UI.openUnitDetail(this.id);
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
        Coceso.UI.openLogAdd({unit: this.id});
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
        Coceso.UI.openLogs({url: "log/getLastByUnit/" + this.id + "/" + Coceso.Conf.logEntryLimit, title: "Unit-Log"});
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
      Coceso.Helpers.destroyComputed(this);
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
  this.given_name = ko.observable("");
  this.sur_name = ko.observable("");
  this.insurance_number = ko.observable("");
  this.diagnosis = ko.observable("");
  this.erType = ko.observable("");
  this.info = ko.observable("");
  this.externalID = ko.observable("");
  this.sex = ko.observable(Coceso.Constants.Patient.sex.unknown);

  /**
   * Method to set data (loaded with AJAX)
   *
   * @param {Object} data
   * @returns {void}
   */
  this.setData = function(data) {
    self.given_name(data.given_name || "");
    self.sur_name(data.sur_name || "");
    self.insurance_number(data.insurance_number || "");
    self.diagnosis(data.diagnosis || "");
    self.erType(data.erType || "");
    self.info(data.info || "");
    self.externalID(data.externalID || "");
    self.sex(data.sex || Coceso.Constants.Patient.sex.unknown);
  };

  //Set data
  this.setData(data);

  /**
   * Patient is male
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isMale = this.sex.extend({isValue: Coceso.Constants.Patient.sex.male});

  /**
   * Patient is female
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isFemale = this.sex.extend({isValue: Coceso.Constants.Patient.sex.female});

  /**
   * Patient's sex is unknown
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isUnknown = ko.pureComputed(function() {
    return (!this.isMale() && !this.isFemale());
  }, this);

  /**
   * Set sex to undefined
   *
   * @returns {void}
   */
  this.isUnknown.set = function() {
    this.sex(Coceso.Constants.Patient.sex.unknown);
  };

  /**
   * State css for undefined sex
   *
   * @function
   * @type ko.pureComputed
   * @returns {String}
   */
  this.isUnknown.state = ko.pureComputed(function() {
    return this() ? "active" : "";
  }, this.isUnknown);

  this.destroy = function() {
  };
};

/**
 * Single log entry
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.Log = function(data) {
  var self = this;

  data = data || {};

  //Create basic properties
  this.id = data.id;
  this.unit = data.unit ? Coceso.Data.getUnit(data.unit) : null;
  this.incident = data.incident ? Coceso.Data.getIncident(data.incident) : null;
  this.user = data.user;
  this.timestamp = data.timestamp;
  this.state = data.state;
  this.type = data.type;
  this.autoGenerated = data.autoGenerated;
  this.text = this.type === "CUSTOM" ? data.text : _("descr." + this.type);
  this.changes = data.changes;

  this.time = (new Date(this.timestamp)).toLocaleString();
  var self = this;

  self.openUnitForm = function() {
    if (self.unit) {
      self.unit.openForm();
    }
  };

  self.openIncidentForm = function() {
    if (self.incident) {
      self.incident.openForm();
    }
  };

};

Coceso.Models.RadioCall = function(data) {
  data = data || {};

  this.timestamp = data.timestamp;
  this.time = Coceso.Helpers.fmtTime(data.timestamp);
  this.ani = data.ani;
  this.port = data.port;
  this.emergency = (data.direction === "RX_EMG");

  this.unit = ko.pureComputed(function() {
    if (Coceso.Data.Radio.aniMap[this.ani]) {
      return Coceso.Data.getUnit(Coceso.Data.Radio.aniMap[this.ani]);
    }
    var id, models = Coceso.Data.units.models();
    for (id in models) {
      if (models[id].ani === this.ani) {
        Coceso.Data.Radio.aniMap[this.ani] = id;
        return models[id];
      }
    }
    return null;
  }, this);

  this.timer = ko.pureComputed(function() {
    return Coceso.Clock.timestamp() - this.timestamp;
  }, this);

  this.fmtTimer = ko.pureComputed(function() {
    return Coceso.Helpers.fmtInterval(this.timer());
  }, this);

};

/**
 * Filterable models
 *
 * @constructor
 * @param {Object} options
 */
Coceso.ViewModels.Filterable = function(options) {
  options = options || {};

  this.disableFilter = {};
  for (var i in options.filter) {
    if (this.filters[options.filter[i]] && this.filters[options.filter[i]].disable) {
      this.disableFilter = $.extend(true, this.disableFilter, this.filters[options.filter[i]].disable);
    }
  }

  /**
   * Generate a list of active filters
   *
   * @type {Array}
   */
  this.activeFilters = [this.filter];

  //Filters from options
  for (var i in options.filter) {
    if (this.filters[options.filter[i]]) {
      this.activeFilters.push(this.filters[options.filter[i]].filter);
    }
  }
};
Coceso.ViewModels.Filterable.prototype = Object.create({}, /** @lends Coceso.ViewModels.Filterable.prototype */ {
  /**
   * Destroy the ViewModel
   *
   * @function
   * @return {void}
   */
  destroy: {
    value: function() {
      Coceso.Helpers.destroyComputed(this);
    }
  }
});

/**
 * List of incidents
 *
 * @constructor
 * @extends Coceso.ViewModels.Filterable
 * @param {Object} options
 */
Coceso.ViewModels.Incidents = function(options) {
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
   * @returns {Array}
   */
  this.filtered = Coceso.Data.incidents.list.extend({list: {filter: this.activeFilters}});

  var title = options.title || _("label.incidents");
  this.dialogTitle = ko.computed(function() {
    var open = ko.utils.arrayFilter(this.filtered(), function(i) {
      return i.isNewOrOpen();
    }).length,
        total = this.filtered().length;

    return {dialog: title + " (" + open + "/" + total + ")", button: title};
  }, this);
};
Coceso.ViewModels.Incidents.prototype = Object.create(Coceso.ViewModels.Filterable.prototype, /** @lends Coceso.ViewModels.Incidents.prototype */ {
  /**
   * Available filters
   *
   * @type Object
   */
  filters: {
    value: {
      overview: {
        filter: {
          type: [Coceso.Constants.Incident.type.task, Coceso.Constants.Incident.type.transport, Coceso.Constants.Incident.type.relocation]
        }
      },
      active: {
        disable: {state: {done: true}},
        filter: {isDone: false}
      },
      "new": {
        disable: {state: true},
        filter: {isNew: true}
      },
      open: {
        disable: {state: true},
        filter: {isOpen: true}
      },
      new_or_open: {
        disable: {state: true},
        filter: {isNewOrOpen: true}
      },
      completed: {
        disable: {state: true},
        filter: {isDone: true}
      },
      transport: {
        disable: {type: true},
        filter: {isTransport: true}
      }
    }
  }
});

/**
 * List of units
 *
 * @constructor
 * @extends Coceso.ViewModels.Filterable
 * @param {Object} options
 */
Coceso.ViewModels.Units = function(options) {
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
   * @returns {Array}
   */
  this.filtered = Coceso.Data.units.list.extend({list: {filter: this.activeFilters}});

  var title = options.title || _("label.units");
  this.dialogTitle = ko.computed(function() {
    return {dialog: title + " (" + this.filtered().length + ")", button: title};
  }, this);
};
Coceso.ViewModels.Units.prototype = Object.create(Coceso.ViewModels.Filterable.prototype, /** @lends Coceso.ViewModels.Units.prototype */ {
  /**
   * Available filters
   *
   * @type Object
   */
  filters: {
    value: {
      radio: {
        filter: {hasAssigned: true}
      },
      free: {
        filter: {isFree: true}
      },
      available: {
        filter: {isAvailable: true}
      }
    }
  }
});

/**
 * ViewModel for hierarchical view in Unit Window
 *
 * @constructor
 */
Coceso.ViewModels.Hierarchical = function() {
  var self = this;

  this.top = ko.observable(new Coceso.ViewModels.UnitContainer({name: "Loading...", subContainer: []}));

  $.getJSON(Coceso.Conf.jsonBase + "unitContainer/getSlim.json", function(topContainer) {
    self.top(new Coceso.ViewModels.UnitContainer(topContainer));
  });

  var title = _("label.units") + ": " + _("label.main.unit.hierarchy");
  this.dialogTitle = ko.pureComputed(function() {
    return {dialog: title + " (" + this.top().availableCounter() + "/" + this.top().totalCounter() + ")", button: title};
  }, this);
};

/**
 * Container for hierarchical view in Unit Window
 *
 * @constructor
 * @param {Object} data
 */
Coceso.ViewModels.UnitContainer = function(data) {
  this.name = ko.observable(data.name);

  this.subContainer = ko.observableArray($.map(data.subContainer, function(subdata) {
    return new Coceso.ViewModels.UnitContainer(subdata);
  }));

  this.units = Coceso.Data.units.list.extend({
    list: {
      filter: {
        id: data.unitIds && data.unitIds.length ? data.unitIds : false
      },
      sort: function(a, b) {
        var t = data.unitIds;
        return t.indexOf(a.id) === t.indexOf(b.id) ? 0 : (t.indexOf(a.id) < t.indexOf(b.id) ? -1 : 1);
      }
    }
  });

  this.availableCounter = ko.computed(function() {
    var count = ko.utils.arrayFilter(this.units(), function(unit) {
      return unit.isAvailable() || (!unit.portable && unit.isEB());
    }).length;
    ko.utils.arrayForEach(this.subContainer(), function(item) {
      count += item.availableCounter();
    });
    return count;
  }, this);

  this.totalCounter = ko.computed(function() {
    var count = this.units().length;
    ko.utils.arrayForEach(this.subContainer(), function(item) {
      count += item.totalCounter();
    });
    return count;
  }, this);
};
Coceso.ViewModels.UnitContainer.prototype = Object.create({}, /** @lends Coceso.ViewModels.UnitContainer.prototype */ {
  /**
   * Destroy the ViewModel
   *
   * @function
   * @return {void}
   */
  destroy: {
    value: function() {
      ko.utils.arrayForEach(this.subContainer(), function(item) {
        item.destroy();
      });
      Coceso.Helpers.destroyComputed(this);
    }
  }
});

/**
 * Base class for all Form ViewModels
 *
 * @constructor
 */
Coceso.ViewModels.Form = function() {
  /**
   * Watch dependencies
   *
   * @type ko.observableArray
   */
  this.form = ko.observableArray().extend({form: {}});

  Coceso.Helpers.initErrorHandling(this);

  /**
   * Save modified data and close the window
   *
   * @function
   * @returns {boolean}
   */
  this.ok = function() {
    this.save();
    $("#" + this.ui).dialog("destroy");
  };
};

/**
 * Single incident
 *
 * @constructor
 * @extends Coceso.Models.Incident
 * @extends Coceso.ViewModels.Form
 * @param {Object} data
 */
Coceso.ViewModels.Incident = function(data) {
  var self = this;

  /**
   * Observable for the ID (needed to update form after initial saving)
   *
   * @function
   * @type ko.observable
   * @returns {Integer}
   */
  this.idObs = ko.observable(data.id || null);

  /**
   * Used Model (reference)
   *
   * @function
   * @type ko.observable
   * @returns {Coceso.Models.Incident}
   */
  this.model = ko.observable(null);

  this.dialogTitle = ko.computed(function() {
    if (!this.idObs()) {
      return _("label.incident.add");
    }
    if (!this.model()) {
      return _("label.incident.edit");
    }
    return {dialog: _("label.incident.edit") + ": " + this.model().title(), button: this.model().assignedTitle()};
  }, this);

  //Call parent constructors
  Coceso.Models.Incident.call(this, {id: this.idObs()});
  Coceso.ViewModels.Form.call(this);

  //Initialize change detection
  this.ao = ko.observable("").extend({observeChanges: {}});
  this.blue = this.blue.extend({"boolean": true, observeChanges: {}});
  this.bo = ko.observable("").extend({observeChanges: {}});
  this.caller.extend({observeChanges: {}});
  this.casusNr.extend({observeChanges: {}});
  this.info.extend({observeChanges: {keepChanges: true}});
  this.state.extend({observeChanges: {}});
  this.type.extend({observeChanges: {}});
  this.units.extend({arrayChanges: {}});
  this.form.push(this.units, this.type, this.blue, this.bo, this.ao, this.info, this.caller, this.casusNr, this.state);

  /**
   * "Virtual" computed observable:
   * Serves as callback on changing the id or the list of models
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.modelChange = ko.computed(function() {
    var newModel = Coceso.Data.getIncident(this.idObs()),
        oldModel = this.model.peek();

    if (newModel === null) {
      if (oldModel === null) {
        //No model exists (not loaded yet or empty form), so create a dummy one
        this.model(new Coceso.Models.Incident());
      }
    } else if (newModel !== oldModel) {
      //Model has changed
      this.model(newModel);
    }
  }, this);

  /**
   * "Virtual" computed observable:
   * Load the local data from Model
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.load = ko.computed(function() {
    //Subscribe to change of model
    this.model();

    //Update server reference for change detection
    this.ao.server(this.model().ao.info);
    this.blue.server(this.model().blue);
    this.bo.server(this.model().bo.info);
    this.caller.server(this.model().caller);
    this.casusNr.server(this.model().casusNr);
    this.info.server(this.model().info);
    this.state.server(this.model().state);
    this.type.server(this.model().type);

    //Set initial data
    if (ko.computedContext.isInitial()) {
      if (data.ao) {
        this.ao(data.ao.info);
      }
      if (typeof data.blue !== "undefined") {
        this.blue(data.blue);
      }
      if (typeof data.bo !== "undefined") {
        this.bo(data.bo.info);
      }
      if (typeof data.caller !== "undefined") {
        this.caller(data.caller);
      }
      if (typeof data.casusNr !== "undefined") {
        this.casusNr(data.casusNr);
      }
      if (typeof data.info !== "undefined") {
        this.info(data.info);
      }
      if (typeof data.state !== "undefined") {
        this.state(data.state);
      }
      if (typeof data.type !== "undefined") {
        this.type(data.type);
      }
      if (data.units) {
        var unit;
        for (unit in data.units) {
          this.setTaskState(unit, data.units[unit]);
        }
      }
    }
  }, this);

  /**
   * "Virtual" computed observable:
   * Update the associated units
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.setAssociatedUnits = ko.computed(function() {
    ko.utils.arrayForEach(this.model().units(), function(task) {
      var local = ko.utils.arrayFirst(self.units.peek(), function(item) {
        return (item.unit_id === task.unit_id);
      });
      if (local) {
        if (local.taskState.server !== task.taskState) {
          //Local element exists, but does not match global model: Recreate local taskState observable
          local.taskState.server(task.taskState);
          local.changed = local.taskState.changed;
          local.reset = local.taskState.reset;
        }
      } else {
        //Local task does not exist at all: Create complete Task model
        local = new Coceso.Models.Task(task.taskState.peek(), task.incident_id, task.unit_id);
        local.taskState.extend({observeChanges: {server: task.taskState}});
        local.changed = local.taskState.changed;
        local.reset = local.taskState.reset;
        self.units.push(local);
      }
    });
    //Remove detached units
    this.units.remove(function(task) {
      if (!task.taskState.orig()) {
        return false;
      }
      return (!ko.utils.arrayFirst(self.model().units(), function(item) {
        return (task.unit_id === item.unit_id);
      }));
    });
  }, this);

  if (data.autoSave) {
    //Saving results in reloading the data, but reloading is not possible before loading is completed
    window.setTimeout(function() {
      self.save.call(self);
    }, 100);
  }

  /**
   * Incident has an AO
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.hasAO = ko.computed(function() {
    return !!this.ao();
  }, this);

  /**
   * Disable "AAO" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableAAO = ko.computed(function() {
    return !this.hasAO();
  }, this);

  /**
   * Disable "ZAO" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableZAO = ko.computed(function() {
    return (this.isStandby() || this.isHoldPosition() || !this.hasAO());
  }, this);

  /**
   * Disable the "Task" type button
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableTask = ko.computed(function() {
    return (this.idObs() && !this.isTask() && !this.isTransport());
  }, this);

  /**
   * Disable the "Relocation" type button
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableRelocation = ko.computed(function() {
    return (this.idObs() && !this.isRelocation());
  }, this);

  /**
   * Disable IncidentState New
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableNew = ko.computed(function() {
    return (this.idObs() && this.state.orig() !== Coceso.Constants.Incident.state["new"]);
  }, this);

  /**
   * Disable IncidentState Dispo
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableDispo = ko.computed(function() {
    return (ko.utils.arrayFirst(this.units(), function(task) {
      return (task.isAssigned() || task.isZBO());
    }) === null);
  }, this);

  /**
   * Disable IncidentState Working
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableWorking = ko.computed(function() {
    return (ko.utils.arrayFirst(this.units(), function(task) {
      return (task.isABO() || task.isZAO() || task.isAAO());
    }) === null);
  }, this);

  /**
   * Highlight AO Field if empty and minimum of 1 Unit is ABO
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.highlightAO = ko.computed(function() {
    if (this.unitCount() > 0) {
      return (!this.hasAO() && ko.utils.arrayFilter(this.units(), function(task) {
        return task.isABO();
      }).length >= 1);
    }
    return false;
  }, this);

  /**
   * Assign a unit within the form
   *
   * @param {Event} event The jQuery Event (unused)
   * @param {Object} ui jQuery UI properties
   * @returns {void}
   */
  this.assignUnitForm = function(event, ui) {
    var viewmodel = ko.dataFor(ui.draggable.context);
    if (!(viewmodel instanceof Coceso.Models.Unit)) {
      return;
    }
    self.setTaskState.call(self, ko.utils.unwrapObservable(viewmodel.id));
  };

  /**
   * Duplicate the incident
   *
   * @function
   * @param {Coceso.Models.Task} task Optional task to remove from current and bind to new incident
   * @returns {void}
   */
  this.duplicate = function(task) {
    var data = {
      caller: self.caller(),
      bo: {info: self.bo()},
      ao: {info: self.ao()},
      info: self.info(),
      blue: self.blue(),
      type: self.type()
    };
    if (task instanceof Coceso.Models.Task) {
      data.units = {};
      data.units[task.unit_id] = task.taskState();
      if (ko.utils.unwrapObservable(task.taskState.server()) && self.id) {
        // Unit is assigned on server, so we have to move it on server
        data.autoSave = true;
        Coceso.Ajax.save({
          incident_id: self.id,
          unit_id: task.unit_id,
          state: Coceso.Constants.TaskState.detached
        }, "incident/setToState.json", self.afterSave, self.saveError, self.httpError);
      } else {
        // Just remove it locally
        self.units.remove(task);
      }
    }
    Coceso.UI.openIncident(data);
  };

  /**
   * Callback after saving
   *
   * @param {Object} data The data returned from server
   * @returns {void}
   */
  this.afterSave = function(data) {
    self.error(false);
    if (data.incident_id && data.incident_id !== self.id) {
      //ID has changed
      self.id = data.incident_id;
      self.idObs(self.id);
    }
  };
};
Coceso.ViewModels.Incident.prototype = Object.create(Coceso.Models.Incident.prototype, /** @lends Coceso.ViewModels.Incident.prototype */ {
  /**
   * Set TaskState for unit
   *
   * @function
   * @returns {void}
   */
  setTaskState: {
    value: function(unit, taskState) {
      if (unit) {
        var assigned = ko.utils.arrayFirst(this.units(), function(task) {
          return (task.unit_id === unit);
        });
        if (assigned === null) {
          assigned = new Coceso.Models.Task(taskState ? taskState : Coceso.Constants.TaskState.assigned, this.id, unit);
          assigned.taskState.extend({observeChanges: {server: null, orig: null}});
          assigned.changed = assigned.taskState.changed;
          assigned.reset = assigned.taskState.reset;
          this.units.push(assigned);
        } else if (taskState && assigned.taskState() !== taskState) {
          assigned.taskState(taskState);
        }
      }
    }
  },
  /**
   * Save the form
   *
   * @function
   * @returns {boolean}
   */
  save: {
    value: function() {
      var data = {
        id: this.id,
        ao: {info: this.ao()},
        bo: {info: this.bo()},
        blue: this.blue(),
        caller: this.caller(),
        casusNr: this.casusNr(),
        info: this.info(),
        state: this.state(),
        type: this.type(),
        units: {}
      };

      if (!data.ao.info) {
        data.ao = {id: -2};
      }
      if (!data.bo.info) {
        data.bo = {id: -2};
      }

      var units = this.units, remove = [];
      ko.utils.arrayForEach(units(), function(task) {
        if (task.taskState.changed()) {
          data.units[task.unit_id] = task.taskState();
          if (!task.taskState.orig() && task.isDetached()) {
            remove.push(task);
          }
        }
      });
      ko.utils.arrayForEach(remove, function(task) {
        units.remove(task);
      });

      Coceso.Ajax.save(JSON.stringify(data), "incident/update.json", this.afterSave, this.saveError, this.httpError, this.form.saving);
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
      Coceso.Helpers.destroyComputed(this);
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

  /**
   * Used Model (reference)
   *
   * @function
   * @type ko.observable
   * @returns {Coceso.Models.Unit}
   */
  this.model = ko.observable(null);

  var title = _("label.unit.edit");
  this.dialogTitle = ko.computed(function() {
    if (!this.model()) {
      return title;
    }
    return {dialog: title + ": " + this.model().call, button: this.model().call};
  }, this);

  //Call parent constructors
  Coceso.Models.Unit.call(this, {id: data.id});
  Coceso.ViewModels.Form.call(this);

  //Initialize change detection
  this.position = ko.observable("").extend({observeChanges: {}});
  this.home = ko.observable("").extend({observeChanges: {}});
  this.info.extend({observeChanges: {keepChanges: true}});
  this.state.extend({observeChanges: {}});
  this.incidents.extend({arrayChanges: {}});
  this.form.push(this.position, this.info, this.state, this.incidents);

  this.isHome = this.position.extend({isValue: this.home});

  /**
   * "Virtual" computed observable:
   * Serves as callback on changing the id or the list of models
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.modelChange = ko.computed(function() {
    var newModel = Coceso.Data.getUnit(this.id),
        oldModel = this.model.peek();

    if (newModel === null) {
      if (oldModel === null) {
        //No model exists (not loaded yet or empty form), so create a dummy one
        this.model(new Coceso.Models.Unit());
      }
    } else if (newModel !== oldModel) {
      //Model has changed
      this.model(newModel);
    }
  }, this);

  /**
   * "Virtual" computed observable:
   * Load the local data from Model
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.load = ko.computed(function() {
    //Subscribe to change of model
    this.model();

    //Update server reference for change detection
    this.position.server(this.model().position.info);
    this.home.server(this.model().home.info);
    this.info.server(this.model().info);
    this.state.server(this.model().state);

    //Set initial data
    if (ko.computedContext.isInitial()) {
      if (data.position) {
        this.position(data.position.info);
      }
      if (typeof data.info !== "undefined") {
        this.info(data.info);
      }
      if (typeof data.state !== "undefined") {
        this.state(data.state);
      }
      if (data.incidents) {
        var incident;
        for (incident in data.incidents) {
          this.setTaskState(incident, data.incidents[incident]);
        }
      }
    }
  }, this);

  /**
   * "Virtual" computed observable:
   * Update the associated incidents
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.setAssociatedIncidents = ko.computed(function() {
    ko.utils.arrayForEach(this.model().incidents(), function(task) {
      var local = ko.utils.arrayFirst(self.incidents.peek(), function(item) {
        return (item.incident_id === task.incident_id);
      });
      if (local) {
        if (local.taskState.server !== task.taskState) {
          //Local element exists, but does not match global model: Recreate local taskState observable
          local.taskState.server(task.taskState);
          local.changed = local.taskState.changed;
          local.reset = local.taskState.reset;
        }
      } else {
        //Local task does not exist at all: Create complete Task model
        local = new Coceso.Models.Task(task.taskState.peek(), task.incident_id, task.unit_id);
        local.taskState.extend({observeChanges: {server: task.taskState}});
        local.changed = local.taskState.changed;
        local.reset = local.taskState.reset;
        self.incidents.push(local);
      }
    });
    //Remove detached incidents
    this.incidents.remove(function(task) {
      if (!task.taskState.orig()) {
        return false;
      }
      return (!ko.utils.arrayFirst(self.model().incidents(), function(item) {
        return (task.incident_id === item.incident_id);
      }));
    });
  }, this);

  /**
   * Callback after saving
   *
   * @param {Object} data The data returned from server
   * @returns {void}
   */
  this.afterSave = function(data) {
    self.error(false);
  };
};
Coceso.ViewModels.Unit.prototype = Object.create(Coceso.Models.Unit.prototype, /** @lends Coceso.ViewModels.Unit.prototype */ {
  /**
   * Save the form
   *
   * @function
   * @returns {boolean}
   */
  save: {
    value: function() {
      var data = {
        id: this.id,
        position: {info: this.position()},
        info: this.info(),
        state: this.state(),
        incidents: {}
      };

      if (!data.position.info) {
        data.position = {id: -2};
      }

      var incidents = this.incidents, remove = [];
      ko.utils.arrayForEach(incidents(), function(task) {
        if (task.taskState.changed()) {
          data.incidents[task.incident_id] = task.taskState();
          if (!task.taskState.orig() && task.isDetached()) {
            incidents.remove(task);
          }
        }
      });
      ko.utils.arrayForEach(remove, function(task) {
        incidents.remove(task);
      });

      Coceso.Ajax.save(JSON.stringify(data), "unit/update.json", this.afterSave, this.saveError, this.httpError, this.form.saving);
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
      Coceso.Helpers.destroyComputed(this);
    }
  }
});

/**
 * Unit details
 *
 * @constructor
 * @param {Integer} id
 */
Coceso.ViewModels.UnitDetail = function(id) {
  /**
   * Used Model (reference)
   *
   * @function
   * @type ko.observable
   * @returns {Coceso.Models.Unit}
   */
  this.model = ko.computed(function() {
    var model = Coceso.Data.getUnit(id);
    return model === null ? new Coceso.Models.Unit() : model;
  }, this);

  var title = _("label.unit.details");
  this.dialogTitle = ko.computed(function() {
    if (!this.model()) {
      return title;
    }
    return {dialog: title + ": " + this.model().call, button: this.model().call};
  }, this);

};
Coceso.ViewModels.UnitDetail.prototype = Object.create({}, /** @lends Coceso.ViewModels.UnitDetail.prototype */ {
  /**
   * Destroy the ViewModel
   *
   * @function
   * @return {void}
   */
  destroy: {
    value: function() {
      Coceso.Helpers.destroyComputed(this);
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
  var self = this;

  /**
   * Used Model (reference)
   *
   * @function
   * @type ko.observable
   * @returns {Coceso.Models.Incident}
   */
  this.model = ko.observable(null);

  var title = _("label.patient.edit");
  this.dialogTitle = ko.computed(function() {
    if (!this.model()) {
      return title;
    }
    return {dialog: title + ": " + this.model().sur_name() + " " + this.model().given_name(), button: title};
  }, this);

  //Call parent constructors
  Coceso.Models.Patient.call(this, {id: data.id});
  Coceso.ViewModels.Form.call(this);

  //Initialize change detection
  this.given_name.extend({observeChanges: {}});
  this.sur_name.extend({observeChanges: {}});
  this.insurance_number.extend({observeChanges: {}});
  this.externalID.extend({observeChanges: {}});
  this.diagnosis.extend({observeChanges: {}});
  this.erType.extend({observeChanges: {}});
  this.info.extend({observeChanges: {keepChanges: true}});
  this.sex.extend({observeChanges: {}});

  this.form.push(this.given_name, this.sur_name, this.insurance_number,
      this.externalID, this.diagnosis, this.erType, this.info, this.sex);

  /**
   * "Virtual" computed observable:
   * Serves as callback on changing the id or the list of models
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.modelChange = ko.computed(function() {
    var newModel = Coceso.Data.getPatient(this.id),
        oldModel = this.model.peek();

    if (newModel === null) {
      if (oldModel === null) {
        //No model exists (not loaded yet or empty form), so create a dummy one
        this.model(new Coceso.Models.Patient());
      }
    } else if (newModel !== oldModel) {
      //Model has changed
      this.model(newModel);
    }
  }, this);

  /**
   * "Virtual" computed observable:
   * Load the local data from Model
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.load = ko.computed(function() {
    //Subscribe to change of model
    this.model();

    //Update server reference for change detection
    this.given_name.server(this.model().given_name);
    this.sur_name.server(this.model().sur_name);
    this.insurance_number.server(this.model().insurance_number);
    this.externalID.server(this.model().externalID);
    this.diagnosis.server(this.model().diagnosis);
    this.erType.server(this.model().erType);
    this.info.server(this.model().info);
    this.sex.server(this.model().sex);

    //Set initial data
    if (ko.computedContext.isInitial()) {
      if (typeof data.given_name !== "undefined") {
        this.given_name(data.given_name);
      }
      if (typeof data.sur_name !== "undefined") {
        this.sur_name(data.sur_name);
      }
      if (typeof data.insurance_number !== "undefined") {
        this.insurance_number(data.insurance_number);
      }
      if (typeof data.externalID !== "undefined") {
        this.externalID(data.externalID);
      }
      if (typeof data.diagnosis !== "undefined") {
        this.diagnosis(data.diagnosis);
      }
      if (typeof data.erType !== "undefined") {
        this.erType(data.erType);
      }
      if (typeof data.info !== "undefined") {
        this.info(data.info);
      }
      if (typeof data.sex !== "undefined") {
        this.sex(data.sex);
      }
    }
  }, this);

  /**
   * Callback after saving
   *
   * @param {Object} data The data returned from server
   * @returns {void}
   */
  this.afterSave = function(data) {
    self.error(false);
  };
};
Coceso.ViewModels.Patient.prototype = Object.create(Coceso.Models.Patient.prototype, /** @lends Coceso.ViewModels.Patient.prototype */ {
  /**
   * Save the form
   *
   * @function
   * @returns {boolean}
   */
  save: {
    value: function() {
      var data = {
        id: this.id,
        given_name: this.given_name(),
        sur_name: this.sur_name(),
        insurance_number: this.insurance_number(),
        diagnosis: this.diagnosis(),
        erType: this.erType(),
        info: this.info(),
        externalID: this.externalID(),
        sex: this.sex()
      };

      Coceso.Ajax.save(JSON.stringify(data), "patient/update.json", this.afterSave, this.saveError, this.httpError, this.form.saving);
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
      Coceso.Helpers.destroyComputed(this);
    }
  }
});

/**
 * List of Logs
 *
 * @constructor
 * @param {Object} options
 */
Coceso.ViewModels.Logs = function(options) {
  var self = this;

  this.dialogTitle = options.title || _("label.log");

  /**
   * List of Logs
   *
   * @function
   * @type ko.observableArray
   * @returns {Array}
   */
  this.loglist = ko.observableArray();

  /**
   * Timeout ID for reload
   *
   * @type {integer}
   */
  this.timeout = null;

  /**
   * Load the specified data
   *
   * @param {String} url The URL to load from
   * @param {int} interval The interval to reload. 0 or false for no autoload.
   * @returns {void}
   */
  this.load = function(url, interval) {
    $.ajax({
      dataType: "json",
      url: Coceso.Conf.jsonBase + url,
      ifModified: true,
      success: function(data, status) {
        if (status !== "notmodified") {
          self.loglist($.map(data, function(item) {
            return new Coceso.Models.Log(item);
          }));
        }
      },
      complete: function() {
        if (interval) {
          self.timeout = window.setTimeout(self.load, interval, url, interval);
        }
      }
    });
  };

  this.load(options.url || "log/getLast/" + Coceso.Conf.logEntryLimit, options.autoload ? Coceso.Conf.interval : false);
};
Coceso.ViewModels.Logs.prototype = Object.create({}, /** @lends Coceso.ViewModels.Logs.prototype */ {
  /**
   * Destroy the ViewModel
   *
   * @function
   * @return {void}
   */
  destroy: {
    value: function() {
      //Stop reloading timeout
      if (this.timeout) {
        window.clearTimeout(this.timeout);
      }
    }
  }
});

/**
 * ViewModel for Custom Log Entry (only used to create a new one)
 *
 * @constructor
 * @param {Object} data
 */
Coceso.ViewModels.CustomLogEntry = function(data) {
  var self = this;

  Coceso.Helpers.initErrorHandling(this);
  this.dialogTitle = _("label.log.add");

  this.text = ko.observable(data.text || "");
  this.unit = ko.observable(data.unit || 0);
  this.incident = data.incident || null;
  this.incidentTitle = ko.computed(function() {
    if (!this.incident) {
      return null;
    }
    var incident = Coceso.Data.getIncident(this.incident);
    return incident ? incident.assignedTitle() : null;
  }, this);
  this.unitList = Coceso.Data.units.list;

  this.ok = function() {
    Coceso.Ajax.save(
        JSON.stringify({
          text: this.text(),
          unit: this.unit() ? {id: this.unit()} : null,
          incident: this.incident ? {id: this.incident} : null
        }),
        "log/add.json", this.afterSave, this.saveError, this.httpError);
  };

  this.afterSave = function() {
    $("#" + self.ui).dialog("destroy");
  };
};

/**
 * ViewModel for incoming radio calls
 *
 * @constructor
 */
Coceso.ViewModels.Radio = function() {
  var self = this, minutes = 5;

  if (!Coceso.Data.Radio) {
    // Initialize Object
    Coceso.Data.Radio = {
      calls: ko.observableArray(),
      ports: ko.observableArray(),
      aniMap: {},
      count: 0,
      interval: null
    };

    // Load past data
    $.ajax({
      dataType: "json",
      url: Coceso.Conf.jsonBase + "radio/getLast/" + minutes + ".json",
      success: function(data) {
        ko.utils.arrayForEach(data, function(item) {
          Coceso.Data.Radio.calls.unshift(new Coceso.Models.RadioCall(item));
        });
      },
      error: function() {
        if (Coceso.UI && Coceso.UI.Notifications) {
          Coceso.UI.Notifications.connectionError(true);
        }
      }
    });

    // Subscribe to updates
    Coceso.Socket.Client.subscribe('/topic/radio/incoming', function(data) {
      Coceso.Data.Radio.calls.unshift(new Coceso.Models.RadioCall(JSON.parse(data.body)));
    });

    // Load available ports
    (function getPorts() {
      $.ajax({
        dataType: "json",
        url: Coceso.Conf.jsonBase + "radio/ports.json",
        success: function(data) {
          Coceso.Data.Radio.ports(data);
        },
        error: function() {
          // Error loading ports, try again
          window.setTimeout(getPorts, 5000);
        }
      });
    })();

    // Remove all entries older than 10 minutes
    Coceso.Data.Radio.interval = window.setInterval(function() {
      var time = new Date() - minutes * 60000;
      Coceso.Data.Radio.calls.remove(function(call) {
        return call.timestamp < time;
      });
    }, 60000);
  }
  Coceso.Data.Radio.count++;

  var radio = _("label.radio");

  this.port = ko.observable();
  this.dialogTitle = ko.computed(function() {
    var port = this.port();
    return port ? radio + ": " + port : radio;
  }, this);

  this.calls = ko.computed(function() {
    var data = Coceso.Data.Radio.calls().sort(function(a, b) {
      return b.timestamp - a.timestamp;
    }), calls = [], last = null;
    ko.utils.arrayForEach(data, function(item) {
      if (self.port() && item.port && self.port() !== item.port) {
        return;
      }
      if (last && last.call.ani === item.ani) {
        last.additional.push(item);
        if (item.emergency) {
          last.emergency = true;
        }
      } else {
        last = {call: item, additional: [], emergency: item.emergency};
        calls.push(last);
      }
    });
    return calls;
  }, this);

  this.accordionOptions = {
    active: false,
    collapsible: true,
    heightStyle: "content",
    beforeActivate: function(event, ui) {
      return (!ui.newHeader || !ui.newHeader.hasClass("no-open"));
    }
  };
};
Coceso.ViewModels.Radio.prototype = Object.create({}, /** @lends Coceso.ViewModels.Radio.prototype */ {
  /**
   * Destroy the ViewModel
   *
   * @function
   * @return {void}
   */
  destroy: {
    value: function() {
      var store = Coceso.Data.Radio;
      Coceso.Helpers.destroyComputed(this);
      store.count--;
      if (!store.count) {
        delete Coceso.Data.Radio;
        Coceso.Socket.Client.unsubscribe('/topic/radio/incoming');
        window.clearInterval(store.interval);
        ko.utils.arrayForEach(store.calls(), function(call) {
          Coceso.Helpers.destroyComputed(call);
        });
      }
    }
  }
});

/**
 * Constructor for the situation map
 *
 * @constructor
 * @param {Object} options
 */
Coceso.ViewModels.Map = function(options) {
  var self = this;
  options = options || {};
  this.dialogTitle = options.title || _("label.map");

  // Define Layers
  var baseLayers = {}, overlays = {}, names = {
    basemap: _("label.map.basemap"),
    ortho: _("label.map.ortho"),
    hospitals: _("label.map.hospitals"),
    oneway: _("label.map.oneway"),
    defi: _("label.map.defi"),
    "ehs.in": _("label.map.ehs.in"),
    "ehs.out": _("label.map.ehs.out"),
    vcm: _("label.map.vcm")
  };

  baseLayers[names.basemap] = L.tileLayer("https://{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpeg", {
    maxZoom: 19,
    subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
    bounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
    attribution: _("label.map.source") + ": <a href='http://basemap.at' target='_blank'>basemap.at</a>, " +
        "<a href='http://creativecommons.org/licenses/by/3.0/at/deed.de' target='_blank'>CC-BY 3.0</a>"
  });
  baseLayers[names.ortho] = L.layerGroup([
    L.tileLayer("https://{s}.wien.gv.at/basemap/bmaporthofoto30cm/normal/google3857/{z}/{y}/{x}.jpeg", {
      maxZoom: 19,
      subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
      bounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
      attribution: _("label.map.source") + ": <a href='http://basemap.at' target='_blank'>basemap.at</a>, " +
          "<a href='http://creativecommons.org/licenses/by/3.0/at/deed.de' target='_blank'>CC-BY 3.0</a>"
    }),
    L.tileLayer("https://{s}.wien.gv.at/basemap/bmapoverlay/normal/google3857/{z}/{y}/{x}.png", {
      maxZoom: 19,
      subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
      bounds: [[46.358770, 8.782379], [49.037872, 17.189532]]
    })
  ]);

  overlays[names.hospitals] = new L.GeoJSON.WFS("https://data.wien.gv.at/daten/geo", "ogdwien:KRANKENHAUSOGD", {
    pointToLayer: function(feature, latlng) {
      return L.marker(latlng, {
        icon: L.icon({
          iconUrl: 'https://data.wien.gv.at/katalog/images/krankenhaus.png',
          iconSize: [16, 16]
        })
      });
    },
    onEachFeature: function(feature, layer) {
      if (feature.properties) {
        layer.bindPopup(new Coceso.Map.Popup(feature.properties.BEZEICHNUNG, feature.properties.ADRESSE));
      }
    }
  });

  overlays[names.defi] = new L.GeoJSON.WFS("https://data.wien.gv.at/daten/geo", "ogdwien:DEFIBRILLATOROGD", {
    pointToLayer: function(feature, latlng) {
      return L.marker(latlng, {
        icon: L.icon({
          iconUrl: 'https://data.wien.gv.at/katalog/images/defibrillator.png',
          iconSize: [16, 16]
        })
      });
    },
    onEachFeature: function(feature, layer) {
      if (feature.properties) {
        layer.bindPopup(new Coceso.Map.Popup(feature.properties.ADRESSE, feature.properties.INFO));
      }
    }
  });

  overlays[names["ehs.out"]] = L.imageOverlay(Coceso.Conf.layerBase + "ehs_out.png", [[48.202974, 16.416414], [48.213057, 16.427201]]);
  overlays[names["ehs.in"]] = L.imageOverlay(Coceso.Conf.layerBase + "ehs_in.png", [[48.206008, 16.419262], [48.208467, 16.422656]]);
  overlays[names.vcm] = L.tileLayer(Coceso.Conf.layerBase + "vcm/{z}/{y}/{x}.png", {
    minZoom: 11,
    maxZoom: 19,
    bounds: [[48.1837, 16.3141], [48.2377, 16.4395]]
  });

  var layersControl = L.control.layers(baseLayers, overlays);

  // Parse options
  options.b = baseLayers[names[options.b]] ? options.b : "basemap";
  var layers = [baseLayers[names[options.b]]], o = [];
  if (options.o) {
    if (!(options.o instanceof Array)) {
      options.o = [options.o];
    }
    ko.utils.arrayForEach(options.o, function(item) {
      if (overlays[names[item]]) {
        o.push(item);
        layers.push(overlays[names[item]]);
      }
    });
  }
  if (o) {
    options.o = o;
  } else if (options.o) {
    delete options.o;
  }

  var locate = true;
  if (options.c) {
    if (!(options.c instanceof Array)) {
      options.c = options.c.split(",");
    }
    if (options.c.length >= 2) {
      options.c = options.c.slice(0, 2);
      options.c = $.map(options.c, window.parseFloat);
      if (!isNaN(options.c[0]) && !isNaN(options.c[1])) {
        locate = false;
      }
    }
  }
  if (locate) {
    options.c = [48.2, 16.35];
  }
  if (options.z) {
    options.z = parseInt(options.z);
    if (isNaN(options.z)) {
      options.z = 13;
    }
  } else {
    options.z = 13;
  }

  // Create marker layer
  var noCoordsControl = new Coceso.Map.NoCoordsControl(),
      markerLayer = new Coceso.Map.MarkerLayer(noCoordsControl),
      incidentMarkers = {},
      unitMarkers = {};

  // Add markers to layer
  var incidents = Coceso.Data.incidents.list.extend({list: {filter: {isDone: false}}});
  this._updateIncidentList = ko.computed(function() {
    var found = {}, id;
    ko.utils.arrayForEach(incidents(), function(inc) {
      if (!incidentMarkers[inc.id]) {
        incidentMarkers[inc.id] = new Coceso.Map.Incident(inc, markerLayer);
      }
      found[inc.id] = true;
    });
    for (id in incidentMarkers) {
      if (!found[id]) {
        incidentMarkers[id].destroy();
        delete incidentMarkers[id];
      }
    }
  }, this);

  this._updateUnitList = ko.computed(function() {
    var found = {}, id;
    ko.utils.arrayForEach(Coceso.Data.units.list(), function(unit) {
      if (!unitMarkers[unit.id]) {
        unitMarkers[unit.id] = new Coceso.Map.Unit(unit, markerLayer);
      }
      found[unit.id] = true;
    });
    for (id in unitMarkers) {
      if (!found[id]) {
        unitMarkers[id].destroy();
        delete unitMarkers[id];
      }
    }
  }, this);

  // Initalize map after UI is loaded
  var map;

  /**
   * Initialize the ViewModel
   *
   * @returns {void}
   */
  this.init = function() {
    map = L.map(this.ui ? this.ui + "-map-container" : "map-container", {
      center: options.c, zoom: options.z,
      minZoom: 7, maxZoom: 19,
      maxBounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
      layers: layers
    });

    // Center map on current position
    if (locate) {
      map.locate({setView: true});
    }

    // Add layers
    map.addControl(layersControl);
    map.addControl(L.control.scale({imperial: false, maxWidth: 300}));
    map.addControl(new Coceso.Map.Legend());
    map.addControl(noCoordsControl);
    map.addLayer(markerLayer);

    // Listen to resize of UI container
    if (this.ui) {
      $("#" + this.ui).on("dialogresizestop", function() {
        map.invalidateSize();
      });
    }

    // Set link to full version
    var fullLink = $("<a href='" + Coceso.Conf.contentBase + "map' target='_blank'>" + _("label.map.full") + "</a>");
    if (this.ui) {
      map.attributionControl.setPrefix(fullLink.prop("outerHTML"));
    } else {
      map.attributionControl.setPrefix(false);
    }

    function setQuery() {
      for (var key in options) {
        if (!options[key]) {
          delete options[key];
        }
      }
      if (self.ui) {
        fullLink.prop("href", Coceso.Conf.contentBase + "map?" + $.param(options, true));
        map.attributionControl.setPrefix(fullLink.prop("outerHTML"));
      } else {
        window.history.replaceState(null, null, "?" + $.param(options, true));
      }
    }

    function findName(name) {
      for (var key in names) {
        if (names[key] === name) {
          return key;
        }
      }
      return null;
    }

    map.on("moveend", function() {
      var c = map.getCenter();
      options.c = c ? [c.lat, c.lng] : null;
      options.z = map.getZoom();
      setQuery();
    });
    map.on("baselayerchange", function(e) {
      options.b = findName(e.name);
      setQuery();
    });
    map.on("overlayadd", function(e) {
      var name = findName(e.name);
      if (name) {
        if (options.o instanceof Array) {
          options.o.push(name);
        } else {
          options.o = [name];
        }
      }
      setQuery();
    });
    map.on("overlayremove", function(e) {
      if (options.o instanceof Array) {
        var name = findName(e.name);
        if (name) {
          for (var i = 0; i <= options.o.length; i++) {
            if (options.o[i] === name) {
              options.o.splice(i, 1);
            }
          }
        }
      }
      setQuery();
    });
  };

  /**
   * Destroy the viewmodel
   *
   * @returns {void}
   */
  this.destroy = function() {
    Coceso.Helpers.destroyComputed(this);
    var i;
    for (i in incidentMarkers) {
      incidentMarkers[i].destroy();
      delete incidentMarkers[i];
    }
    for (i in unitMarkers) {
      unitMarkers[i].destroy();
      delete unitMarkers[i];
    }

    if (this.ui) {
      $("#" + this.ui).off("dialogresizestop");
    }

    map.remove();
    Coceso.Helpers.cleanObj(map);
    map = layersControl = markerLayer = noCoordsControl = baseLayers = overlays = names = null;
  };
};

/**
 * Layer to show WFS data
 *
 * @constructor
 * @extends L.GeoJSON
 * @param {String} serviceUrl
 * @param {String} featureType
 * @param {GeoJSONOptions} options
 */
L.GeoJSON.WFS = L.GeoJSON.extend({
  initialize: function(serviceUrl, featureType, options) {
    L.GeoJSON.prototype.initialize.call(this, null, options);
    this._featureUrl = serviceUrl + "?service=WFS&request=GetFeature&outputFormat=json&version=1.1.0&srsName=EPSG:4326&typeName=" + featureType;
    this._loaded = false;
  },
  onAdd: function(map) {
    L.LayerGroup.prototype.onAdd.call(this, map);
    if (!this._loaded) {
      var self = this;
      $.ajax({
        dataType: "json",
        url: this._featureUrl,
        success: function(response) {
          if (response.type && response.type === "FeatureCollection") {
            self.addData(response);
            self._loaded = true;
          }
        }
      });
    }
  }
});

/**
 * Contains all map related classes
 *
 * @namespace Coceso.Map
 * @type Object
 */
Coceso.Map = {};

/**
 * Modified Leaflet popup to work with bootstrap and observables
 *
 * @constructor
 * @extends L.Popup
 * @param {String|ko.observable} title
 * @param {String|ko.observable} content
 * @param {PopupOptions} options
 * @param {ILayer} source
 */
Coceso.Map.Popup = L.Popup.extend({
  initialize: function(title, content, options, source) {
    L.Popup.prototype.initialize.call(this, options, source);
    if (ko.isObservable(title)) {
      this.setTitle(title());
      title.subscribe(this.setTitle, this);
    } else {
      this.setTitle(title);
    }
    if (ko.isObservable(content)) {
      this.setContent(content());
      content.subscribe(this.setContent, this);
    } else {
      this.setContent(content);
    }
  },
  setTitle: function(title) {
    this._title = title;
    this.update();
    return this;
  },
  _initLayout: function() {
    var containerClass = "leaflet-popover popover top " + this.options.className + " leaflet-zoom-" + (this._animated ? "animated" : "hide"),
        container = this._container = L.DomUtil.create("div", containerClass),
        closeButton;

    if (this.options.closeButton) {
      closeButton = this._closeButton = L.DomUtil.create('a', 'leaflet-popup-close-button', container);
      closeButton.href = '#close';
      closeButton.innerHTML = '&#215;';
      L.DomEvent.disableClickPropagation(closeButton);
      L.DomEvent.on(closeButton, 'click', this._onCloseButtonClick, this);
    }

    L.DomEvent.disableClickPropagation(container);
    this._tipContainer = L.DomUtil.create("div", "arrow", container);
    this._titleNode = L.DomUtil.create("h3", "popover-title", container);
    this._contentNode = L.DomUtil.create("div", "popover-content", container);

    L.DomEvent.disableScrollPropagation(this._contentNode);
    L.DomEvent.on(container, "contextmenu", L.DomEvent.stopPropagation);
  },
  _updateContent: function() {
    if (this._title) {
      this._titleNode.innerHTML = this._title;
    }
    L.Popup.prototype._updateContent.call(this);
  },
  _updatePosition: function() {
    if (!this._map) {
      return;
    }
    L.Popup.prototype._updatePosition.call(this);
    this._containerBottom = this._containerBottom + 20;
    this._container.style.bottom = this._containerBottom + "px";
  }
});

/**
 * Show legend
 * in part taken from L.Control.Layers
 *
 * @constructor
 * @extends L.Control
 */
Coceso.Map.Legend = L.Control.extend({
  options: {
    collapsed: true,
    position: "bottomright"
  },
  onAdd: function() {
    var className = 'map-legend',
        container = this._container = L.DomUtil.create('div', className);

    //Makes this work on IE10 Touch devices by stopping it from firing a mouseout event when the touch is released
    container.setAttribute('aria-haspopup', true);

    if (!L.Browser.touch) {
      L.DomEvent
          .disableClickPropagation(container)
          .disableScrollPropagation(container);
    } else {
      L.DomEvent.on(container, 'click', L.DomEvent.stopPropagation);
    }
    if (this.options.collapsed) {
      if (!L.Browser.android) {
        L.DomEvent
            .on(container, 'mouseover', this._expand, this)
            .on(container, 'mouseout', this._collapse, this);
      }
      var link = L.DomUtil.create('a', className + '-toggle', container);
      link.href = '#';
      link.title = _("label.main.key");
      L.DomUtil.create("span", "glyphicon glyphicon-question-sign", link);

      if (L.Browser.touch) {
        L.DomEvent
            .on(link, 'click', L.DomEvent.stop)
            .on(link, 'click', this._expand, this);
      } else {
        L.DomEvent.on(link, 'focus', this._expand, this);
      }

      this._map.on('click', this._collapse, this);
    } else {
      this._expand();
    }

    var list = L.DomUtil.create('div', className + '-list', container),
        content = "<ul class='list-unstyled'>";
    ko.utils.arrayForEach(Coceso.Map.Point.prototype.types, function(type) {
      content += "<li class='clearfix'>"
          + "<div class='leaflet-marker-icon leaflet-div-icon icon-" + type + "'></div>"
          + "<div>" + _("label.map.legend." + type) + "</div>"
          + "</li>";
    });
    content += "<li class='clearfix'>"
        + "<div class='leaflet-marker-icon leaflet-div-icon'>"
        + "<span class='glyphicon glyphicon-plus'></span></div>"
        + "<div>" + _("label.map.legend.multiple") + "</div>"
        + "</li>";
    content += "</ul>";
    list.innerHTML = content;

    return container;
  },
  _expand: function() {
    L.DomUtil.addClass(this._container, 'map-legend-expanded');
  },
  _collapse: function() {
    this._container.className = this._container.className.replace(' map-legend-expanded', '');
  }
});

/**
 * Show incident on map
 *
 * @constructor
 * @param {Coceso.Models.Incident} inc
 * @param {Coceso.Map.MarkerLayer} layer
 */
Coceso.Map.Incident = function(inc, layer) {
  this.id = inc.id;

  this.type = inc.type;
  this.blue = inc.blue;
  this.isNewOrOpen = inc.isNewOrOpen;
  this.disableBO = inc.disableBO;

  /**
   * Get the content to show in popup
   *
   * @function
   * @type ko.computed
   * @returns {Array} Content to show in incident and unit lists
   */
  this.popupContent = ko.computed(function() {
    var incidents = "", units = "";

    if (inc.isStandby() || inc.isHoldPosition() || inc.isToHome()) {
      var tasks = inc.units();
      if (tasks.length > 0 && tasks[0].unit()) {
        units += "<li><strong>" + inc.typeChar() + "</strong>: " + tasks[0].unit().call.escapeHTML();
        if (tasks[0].isAssigned()) {
          units += " (" + tasks[0].localizedTaskState() + ")";
        }
        units += "</li>";
      }
    } else {
      incidents += "<li>" + inc.assignedTitle();
      if (inc.unitCount()) {
        incidents += "<dl class='dl-horizontal list-narrower'>";
        ko.utils.arrayForEach(inc.units(), function(task) {
          incidents += "<dt>" + (task.unit() && task.unit().call.escapeHTML()) + "</dt><dd>" + task.localizedTaskState() + "</dd>";
        });
        incidents += "</dl>";
      }
      incidents += "</li>";
    }

    return [incidents, units];
  }, this);

  this._updateBo = ko.computed(function() {
    layer.moveBo(this, inc.bo.getStatic());
  }, this);

  this._updateAo = ko.computed(function() {
    layer.moveAo(this, inc.ao.getStatic());
  }, this);

  var line = new L.Polyline([[0, 0], [0, 0]]);

  this._updateLine = ko.computed(function() {
    var aLat = inc.ao.lat(), aLng = inc.ao.lng(),
        bLat = inc.bo.lat(), bLng = inc.bo.lng();

    if (aLat && aLng && bLat && bLng) {
      line.setLatLngs([[bLat, bLng], [aLat, aLng]]);
      layer.addLayer(line);
    } else {
      layer.removeLayer(line);
    }
  }, this);

  this._updateLineColor = ko.computed(function() {
    if (inc.isTask() || inc.isTransport()) {
      line.setStyle({color: inc.blue() ? "#0064cd" : "#9999ff"});
    } else if (inc.isToHome()) {
      line.setStyle({color: "#99ff99"});
    } else {
      line.setStyle({color: "#03f"});
    }
  }, this);

  /**
   * Destroy the object
   *
   * @returns {void}
   */
  this.destroy = function() {
    Coceso.Helpers.destroyComputed(this);
    layer.removeLayer(line);
    layer.moveBo(this, null);
    layer.moveAo(this, null);
  };
};

/**
 * Show unit on map
 *
 * @void
 * @param {Coceso.Models.Unit} unit
 * @param {Coceso.Map.MarkerLayer} layer
 */
Coceso.Map.Unit = function(unit, layer) {
  this.id = unit.id;

  this.portable = unit.portable;
  this.isFree = unit.isFree;
  this.isHome = unit.isHome;

  /**
   * Get the content to show in popup
   *
   * @function
   * @type ko.computed
   * @returns {String} Content to show in unit list
   */
  this.popupContent = ko.computed(function() {
    var content = "<li>";
    if (unit.portable) {
      if (unit.isFree()) {
        content += "<span class='glyphicon glyphicon-exclamation-sign'></span>";
      } else if (unit.isHome()) {
        content += "<span class='glyphicon glyphicon-home'></span>";
      } else {
        content += "<span class='glyphicon glyphicon-map-marker'></span>";
      }
    } else {
      content += "<span class='glyphicon glyphicon-record'></span>";
    }
    content += ": " + unit.call.escapeHTML() + "</li>";

    return content;
  }, this);

  this._updatePoint = ko.computed(function() {
    var point = unit.mapPosition();
    layer.moveUnit(this, point ? point.getStatic() : null);
  }, this);

  /**
   * Destroy the object
   *
   * @returns {void}
   */
  this.destroy = function() {
    Coceso.Helpers.destroyComputed(this);
    layer.moveUnit(this, null);
  };
};


/**
 * LayerGroup to show the markers on
 *
 * @constructor
 * @extends L.LayerGroup
 * @param {Coceso.Map.NoCoordsControl} noCoordsControl
 */
Coceso.Map.MarkerLayer = L.LayerGroup.extend({
  initialize: function(noCoordsControl) {
    this._layers = {};
    this._points = {};
    this._grid = {};
    this._bo = {};
    this._ao = {};
    this._units = {};
    this._noCoordsControl = noCoordsControl;
  },
  removeLayer: function(layer) {
    if (layer instanceof Coceso.Map.Marker) {
      var x, y;
      for (x in this._grid) {
        for (y in this._grid[x]) {
          ko.utils.arrayRemoveItem(this._grid[x][y], layer);
        }
      }
    }
    L.LayerGroup.prototype.removeLayer.call(this, layer);
  },
  /**
   * Move bo of incident
   *
   * @param {Coceso.Map.Incident} inc
   * @param {Object} point
   * @returns {void}
   */
  moveBo: function(inc, point) {
    var mapPoint = this._getMapPoint(point);
    if (mapPoint) {
      if (this._bo[inc.id] !== mapPoint) {
        this._removeBo(inc);
        mapPoint.pushBo(inc);
        this._bo[inc.id] = mapPoint;
      }
    } else {
      this._removeBo(inc);
    }
  },
  /**
   * Move ao of incident
   *
   * @param {Coceso.Map.Incident} inc
   * @param {Object} point
   * @returns {void}
   */
  moveAo: function(inc, point) {
    var mapPoint = this._getMapPoint(point);
    if (mapPoint) {
      if (this._ao[inc.id] !== mapPoint) {
        this._removeAo(inc);
        mapPoint.pushAo(inc);
        this._ao[inc.id] = mapPoint;
      }
    } else {
      this._removeAo(inc);
    }
  },
  /**
   * Move unit
   *
   * @param {Coceso.Map.Unit} unit
   * @param {Object} point
   * @returns {void}
   */
  moveUnit: function(unit, point) {
    var mapPoint = this._getMapPoint(point);
    if (mapPoint) {
      if (this._units[unit.id] !== mapPoint) {
        this._removeUnit(unit);
        mapPoint.pushUnit(unit);
        this._units[unit.id] = mapPoint;
      }
    } else {
      this._removeUnit(unit);
    }
  },
  /**
   * Get MapPoint for a point object and move the point to correct marker
   *
   * @param {Object} point
   * @returns {Coceso.Map.Point}
   */
  _getMapPoint: function(point) {
    if (!point || !point.id) {
      return null;
    }

    var mapPoint = null;
    if (this._points[point.id]) {
      if (!this._points[point.id].checkLatLng(point)) {
        // Point exists, but has been moved
        mapPoint = this._points[point.id];
        mapPoint.setData(point);
      } else {
        this._points[point.id].info(point.info);
      }
    } else {
      // Point does not yet exist
      mapPoint = this._points[point.id] = new Coceso.Map.Point(point);
    }

    if (mapPoint) {
      // Point is either new or has been moved
      if (point.lat === null || point.lng === null) {
        // Coordinates not set, move to noCoordControl
        mapPoint.moveToMarker(this._noCoordsControl);
      } else {
        // Find near points
        var x = Math.round(point.lat * 5000) / 5000,
            y = Math.round(point.lng * 2500) / 2500,
            x1 = x < point.lat ? x + 0.0002 : x - 0.0002,
            y1 = x < point.lng ? x + 0.0004 : x - 0.0004;

        var gridMarkers = $.merge($.merge(this._getGrid(x, y), this._getGrid(x, y1)), $.merge(this._getGrid(x1, y), this._getGrid(x1, y1)));
        var near = $.map(gridMarkers, function(marker) {
          var distance = marker.getLatLng().distanceTo(point);
          return distance < 10 ? {m: marker, d: distance} : null;
        });

        if (near.length) {
          // Move to nearest existing marker
          near.sort(function(a, b) {
            return a.d - b.d;
          });
          mapPoint.moveToMarker(near[0].m);
        } else {
          // Create a new marker
          var marker = new Coceso.Map.Marker(point, this, {noCoordsControl: this._noCoordsControl});
          if (this._grid[x]) {
            if (this._grid[x][y]) {
              this._grid[x][y].push(marker);
            } else {
              this._grid[x][y] = [marker];
            }
          } else {
            this._grid[x] = {};
            this._grid[x][y] = [marker];
          }
          mapPoint.moveToMarker(marker);
        }
      }
    }
    return this._points[point.id];
  },
  _getGrid: function(x, y) {
    return (this._grid[x] && this._grid[x][y]) ? $.merge([], this._grid[x][y]) : [];
  },
  /**
   * Remove bo for incident
   *
   * @param {Coceso.Map.Incident} inc
   * @returns {void}
   */
  _removeBo: function(inc) {
    if (this._bo[inc.id]) {
      this._bo[inc.id].bo.remove(inc);
      if (!this._bo[inc.id].count()) {
        this._bo[inc.id].destroy();
        delete this._points[this._bo[inc.id].id];
      }
      delete this._bo[inc.id];
    }
  },
  /**
   * Remove ao for incident
   *
   * @param {Coceso.Map.Incident} inc
   * @returns {void}
   */
  _removeAo: function(inc) {
    if (this._ao[inc.id]) {
      this._ao[inc.id].ao.remove(inc);
      if (!this._ao[inc.id].count()) {
        this._ao[inc.id].destroy();
        delete this._points[this._ao[inc.id].id];
      }
      delete this._ao[inc.id];
    }
  },
  /**
   * Remove unit
   *
   * @param {Coceso.Map.Unit} unit
   * @returns {void}
   */
  _removeUnit: function(unit) {
    if (this._units[unit.id]) {
      this._units[unit.id].units.remove(unit);
      if (!this._units[unit.id].count()) {
        this._units[unit.id].destroy();
        delete this._points[this._units[unit.id].id];
      }
      delete this._units[unit.id];
    }
  }
});

/**
 * Point to show on map
 *
 * @constructor
 * @param {Object} point Unwrapped Coceso.Models.Point
 */
Coceso.Map.Point = function(point) {
  this.id = point.id;
  this.bo = ko.observableArray([]);
  this.ao = ko.observableArray([]);
  this.units = ko.observableArray([]);
  this.info = ko.observable(point.info);
  this._latlng = {lat: point.lat, lng: point.lng};
  this._marker = null;

  /**
   * Total count of units and incidents at this point
   *
   * @type ko.computed
   * @returns {Integer}
   */
  this.count = ko.computed(function() {
    return this.units().length + this.bo().length + this.ao().length;
  }, this);

  /**
   * Get types of units and incidents at this point
   *
   * @function
   * @type ko.computed
   * @returns {Integer} Each bit represents a specific type
   * @see Coceso.Map.Point.prototype.types
   */
  this.type = ko.computed(function() {
    var value = 0;
    ko.utils.arrayForEach(this.units(), function(unit) {
      if (unit.portable) {
        if (unit.isFree()) {
          value |= 2;
        }
        if (unit.isHome()) {
          value |= 64;
        }
      } else {
        value |= 4;
      }
    });

    ko.utils.arrayForEach(this.bo(), function(inc) {
      if (inc.isNewOrOpen()) {
        value |= 1;
      }
      switch (inc.type()) {
        case Coceso.Constants.Incident.type.task:
        case Coceso.Constants.Incident.type.transport:
          value |= 32;
          if (inc.blue()) {
            value |= 8;
          }
          break;
        case Coceso.Constants.Incident.type.tohome:
          value |= 512;
          break;
      }
    });

    ko.utils.arrayForEach(this.ao(), function(inc) {
      if (inc.disableBO() && inc.isNewOrOpen()) {
        value |= 1;
      }
      switch (inc.type()) {
        case Coceso.Constants.Incident.type.task:
        case Coceso.Constants.Incident.type.transport:
          value |= 32;
          if (inc.blue()) {
            value |= 8;
          }
          break;
        case Coceso.Constants.Incident.type.relocation:
          value |= 16;
          break;
        case Coceso.Constants.Incident.type.holdposition:
          value |= 128;
          break;
        case Coceso.Constants.Incident.type.standby:
          value |= 256;
          break;
        case Coceso.Constants.Incident.type.tohome:
          value |= 512;
          break;
      }
    });
    return value;
  }, this);

  /**
   * Content for popup
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.popupContent = ko.computed(function() {
    var units = "", incidents = "", content = "";

    function addIncident(inc) {
      var incContent = inc.popupContent();
      incidents += incContent[0];
      units += incContent[1];
    }

    ko.utils.arrayForEach(this.bo(), addIncident);
    ko.utils.arrayForEach(this.ao(), addIncident);
    ko.utils.arrayForEach(this.units(), function(unit) {
      units += unit.popupContent();
    });

    if (incidents) {
      content += "<ul class='list-unstyled'>" + incidents + "</ul>";
    }
    if (units) {
      content += "<ul class='list-unstyled'>" + units + "</ul>";
    }
    return content;
  }, this);

  /**
   * Title for popup
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.popupTitle = ko.computed(function() {
    return "<span class='pre'>" + this.info().escapeHTML() + "</span>";
  }, this);
};
Coceso.Map.Point.prototype = Object.create({}, /** @lends Coceso.Map.Point.prototype */ {
  /**
   * Add incident bo to point
   *
   * @function
   * @param {Coceso.Map.Incident} inc
   * @returns {void}
   */
  pushBo: {
    value: function(inc) {
      if (!ko.utils.arrayFirst(this.bo(), function(item) {
        return item === inc;
      })) {
        this.bo.push(inc);
      }
    }
  },
  /**
   * Add incident ao to point
   *
   * @function
   * @param {Coceso.Map.Incident} inc
   * @returns {void}
   */
  pushAo: {
    value: function(inc) {
      if (!ko.utils.arrayFirst(this.ao(), function(item) {
        return item === inc;
      })) {
        this.ao.push(inc);
      }
    }
  },
  /**
   * Add unit to point
   *
   * @function
   * @param {Coceso.Map.Unit} inc
   * @returns {void}
   */
  pushUnit: {
    value: function(unit) {
      if (!ko.utils.arrayFirst(this.units(), function(item) {
        return item === unit;
      })) {
        this.units.push(unit);
      }
    }
  },
  /**
   * Compare latlngs
   *
   * @function
   * @param {Object} latlng
   * @returns {boolean}
   */
  checkLatLng: {
    value: function(latlng) {
      return this._latlng.lat === latlng.lat && this._latlng.lng === latlng.lng;
    }
  },
  /**
   * Update latlng and info
   *
   * @function
   * @param {Object} point
   * @returns {void}
   */
  setData: {
    value: function(point) {
      this._latlng = {lat: point.lat, lng: point.lng};
      this.info(point.info);
    }
  },
  /**
   * Move point to another marker
   *
   * @function
   * @param {Coceso.Map.Marker|Coceso.Map.NoCoordsMarker} marker
   */
  moveToMarker: {
    value: function(marker) {
      if (this._marker !== marker) {
        if (this._marker) {
          this._marker.removePoint(this);
        }
        if (marker) {
          marker.addPoint(this);
        }
        this._marker = marker;
      }
    }
  },
  /**
   * Types for each bit (2^0 to 2^9) in .type()
   *
   * @type Array
   */
  types: {
    value: [
      "open", "free", "fixed", "blue", "relocation",
      "task", "home", "holdposition", "standby", "tohome"
    ]
  },
  /**
   * Get type for bitwise value
   */
  getType: {
    value: function(value) {
      var i, bit = 1;
      for (i = 0; i < this.types.length; i++) {
        if (value & bit) {
          return this.types[i];

        }
        bit *= 2;
      }
      return null;
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
      if (this._marker) {
        this._marker.removePoint(this);
        this._marker = null;
      }
      Coceso.Helpers.destroyComputed(this);
    }
  }
});

/**
 * Marker for the situation map
 *
 * @constructor
 * @extends L.Marker
 * @param {Object} latlng
 * @param {Coceso.Map.MarkerLayer} layer
 * @param {MarkerOptions} options
 */
Coceso.Map.Marker = L.Marker.extend({
  initialize: function(latlng, layer, options) {
    options.icon = L.divIcon({iconSize: [18, 18]});
    L.Marker.prototype.initialize.call(this, latlng, options);

    this._layer = layer;
    this._points = ko.observableArray([]);

    /**
     * Get count of all units and incidents and set marker content accordingly
     *
     * @function
     * @type ko.computed
     * @returns {Integer}
     */
    this.count = ko.computed(function() {
      var count = 0;
      ko.utils.arrayForEach(this._points(), function(item) {
        count += item.count();
      });
      this.options.icon.options.html = count > 1 ? "<span class='glyphicon glyphicon-plus'></span>" : false;
      this.setIcon(this.options.icon);
      return count;
    }, this);

    /**
     * Set marker color according to units and incidents
     *
     * @function
     * @type ko.computed
     * @returns {void}
     */
    this._type = ko.computed(function() {
      var value = 0;

      ko.utils.arrayForEach(this._points(), function(item) {
        value |= item.type();
      });

      this.options.icon.options.className = "leaflet-div-icon icon-" + Coceso.Map.Point.prototype.getType(value);
      this.setIcon(this.options.icon);
    }, this);

    /**
     * Get popup content
     *
     * @function
     * @type ko.computed
     * @returns {String}
     */
    this.popupContent = ko.computed(function() {
      if (!this._points().length) {
        return "";
      }
      if (this._points().length === 1) {
        return this._points()[0].popupContent();
      }
      var content = "";
      ko.utils.arrayForEach(this._points(), function(item) {
        content += "<h4>" + item.popupTitle() + "</h4>"
            + item.popupContent();
      });
      return content;
    }, this);

    /**
     * Get popup title
     *
     * @function
     * @type ko.computed
     * @returns {String}
     */
    this.popupTitle = ko.computed(function() {
      return this._points().length ? this._points()[0].popupTitle() : "";
    }, this);

    this.bindPopup(new Coceso.Map.Popup(this.popupTitle, this.popupContent));
  },
  /**
   * Add a point to the marker
   *
   * @param {Coceso.Map.Point} point
   * @returns {void}
   */
  addPoint: function(point) {
    this._points.push(point);
    this._layer.addLayer(this);
  },
  /**
   * Remove point from the marker
   *
   * @param {Coceso.Map.Point} point
   * @returns {void}
   */
  removePoint: function(point) {
    this._points.remove(point);
    if (!this._points().length) {
      this.destroy();
    }
  },
  /**
   * Destroy the object
   *
   * @returns {void}
   */
  destroy: function() {
    this._layer.removeLayer(this);
    Coceso.Helpers.destroyComputed(this);
  }
});

/**
 * Show points with unknown coordinates
 *
 * @constructor
 * @extends L.Control
 * @param {ControlOptions} options
 */
Coceso.Map.NoCoordsControl = L.Control.extend({
  options: {
    position: "bottomleft"
  },
  initialize: function(options) {
    L.Control.prototype.initialize.call(this, options);
    this._points = {};
    this._ul = L.DomUtil.create("ul", "list-unstyled");
  },
  onAdd: function() {
    this._container = L.DomUtil.create("div", "map-nocoords");
    L.DomUtil.create("h3", '', this._container).innerText = _("label.map.nocoords");
    this._container.appendChild(this._ul);
    this._container.style.display = this._ul.firstChild ? "" : "none";
    return this._container;
  },
  /**
   * Add point to list
   *
   * @param {Coceso.Map.Point} point
   * @returns {void}
   */
  addPoint: function(point) {
    if (!this._points[point.id]) {
      this._points[point.id] = new Coceso.Map.NoCoordsMarker(point);
      this._ul.appendChild(this._points[point.id].el);
      if (this._container) {
        this._container.style.display = "";
      }
    }
  },
  /**
   * Remove point from list
   *
   * @param {Coceso.Map.Point} point
   * @returns {void}
   */
  removePoint: function(point) {
    var item = this._points[point.id];
    if (item) {
      this._ul.removeChild(item.el);
      item.destroy();
      delete this._points[point.id];
      if (!this._ul.firstChild) {
        this._container.style.display = "none";
      }
    }
  }
});

/**
 * Marker item for the unknown coordinates list
 *
 * @constructor
 * @param {Coceso.Map.Point} point
 */
Coceso.Map.NoCoordsMarker = function(point) {
  this.el = L.DomUtil.create("li", "clearfix");

  var icon = L.divIcon({iconSize: [18, 18]}).createIcon();
  this.el.appendChild(icon);
  var title = L.DomUtil.create('div', '', this.el);

  this._updateTitle = ko.computed(function() {
    title.innerHTML = point.popupTitle();
  });
  this._updateType = ko.computed(function() {
    icon.className = "leaflet-marker-icon leaflet-div-icon icon-"
        + point.getType(point.type());
  });
  this._updateCount = ko.computed(function() {
    icon.innerHTML = point.count() > 1 ? "<span class='glyphicon glyphicon-plus'></span>" : "";
  });
};
Coceso.Map.NoCoordsMarker.prototype = Object.create({}, /** @lends Coceso.Map.NoCoordsMarker.prototype */ {
  /**
   * Destroy the object
   *
   * @function
   * @returns {void}
   */
  destroy: {
    value: function() {
      Coceso.Helpers.destroyComputed(this);
    }
  }
});

/**
 * Constructor for the notification viewmodel
 *
 * @constructor
 */
Coceso.ViewModels.Notifications = function() {
  Coceso.Clock.init();

  this.clock_time = Coceso.Clock.time;

  /**
   * Connection error
   *
   * @function
   * @type ko.observable
   * @returns {boolean}
   */
  this.connectionError = ko.observable(false);

  var incidents = Coceso.Data.incidents.list.extend({list: {filter: {
        type: [Coceso.Constants.Incident.type.task, Coceso.Constants.Incident.type.transport, Coceso.Constants.Incident.type.relocation],
        isNewOrOpen: true
      }}});

  /**
   * Open incidents
   *
   * @function
   * @type ko.computed
   * @returns {integer}
   */
  this.openIncidentCounter = ko.computed(function() {
    return incidents().length;
  }, this);

  /**
   * Open transports
   *
   * @function
   * @type ko.computed
   * @returns {integer}
   */
  this.openTransportCounter = ko.computed(function() {
    return ko.utils.arrayFilter(incidents(), function(i) {
      return i.isTransport();
    }).length;
  }, this);

  /**
   * Number of units with TaskState "assigned"
   *
   * @function
   * @type ko.computed
   * @returns {integer}
   */
  this.radioCounter = ko.computed(function() {
    return ko.utils.arrayFilter(Coceso.Data.units.list(), function(u) {
      return u.hasAssigned();
    }).length;
  }, this);

  /**
   * Number of "free" units
   *
   * @function
   * @type ko.computed
   * @returns {integer}
   */

  this.freeCounter = ko.computed(function() {
    return ko.utils.arrayFilter(Coceso.Data.units.list(), function(u) {
      return u.isFree();
    }).length;
  }, this);

  /**
   * Generate warning CSS
   *
   * @param {integer} count
   * @returns {String}
   */
  this.getCss = function(count) {
    return count >= 1 ? "notification-highlight" : "notification-ok";
  };

  /**
   * CSS for open incidents
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.cssOpen = ko.computed(function() {
    return this.getCss(this.openIncidentCounter());
  }, this);

  /**
   * CSS for open transports
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.cssTransport = ko.computed(function() {
    return this.getCss(this.openTransportCounter());
  }, this);

  /**
   * CSS for units with TaskState "Assigned"
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.cssRadio = ko.computed(function() {
    return this.getCss(this.radioCounter());
  }, this);

  /**
   * CSS for "free" units
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.cssFree = ko.computed(function() {
    return this.getCss(this.freeCounter());
  }, this);

  /**
   * CSS for connection status
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.cssError = ko.computed(function() {
    return this.connectionError() ? "connection-error" : "connection-ok";
  }, this);
};

/**
 * Fix issue with droppables in background
 */

if ($.ui && $.ui.intersect) {
  var _intersect = $.ui.intersect;
  $.ui.intersect = (function() {
    return function(draggable, droppable, toleranceMode, event) {
      if (toleranceMode === "pointer" && !$.contains(droppable.element[0], document.elementFromPoint(event.pageX, event.pageY))) {
        return false;
      }
      return _intersect(draggable, droppable, toleranceMode, event);
    };
  })();
}
