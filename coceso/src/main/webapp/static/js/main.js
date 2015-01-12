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
  Coceso.poiAutocomplete = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    limit: 20,
    remote: {
      url: Coceso.Conf.jsonBase + 'poiAutocomplete.json?q=',
      replace: function(url, query) {
        return url + encodeURIComponent(query.replace("\n", ", "));
      },
      filter: function(list) {
        return $.map(list, function(item) {
          return {value: item};
        });
      }
    }
  });
  Coceso.poiAutocomplete.initialize();
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
   * List of all maps
   *
   * @type Array
   */
  Maps: [],
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
  getPoint: function(data) {
    if (!data) {
      return Coceso.Models.Point.empty;
    }
    if (!data.id) {
      return new Coceso.Models.Point(data);
    }
    if (!Coceso.Data.points[data.id]) {
      Coceso.Data.points[data.id] = new Coceso.Models.Point(data);
      ko.utils.arrayForEach(Coceso.UI.Maps, function(item) {
        item.newPoint(Coceso.Data.points[data.id]);
      });
    } else {
      Coceso.Data.points[data.id].setData(data);
    }
    return Coceso.Data.points[data.id];
  },
  incidents: {models: ko.observable({})},
  units: {models: ko.observable({})},
  patients: {models: ko.observable({})},
  points: {}
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

      if (needAO && !incident.ao().id && (nextState === s.zao || nextState === s.aao)) {
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
            {key: _("label.unit.position"), val: unit.position().info()}
          ];
        } else if (incident.isHoldPosition()) {
          if (nextState === s.aao) {
            info = _("text.holdposition.send");
          } else if (nextState === s.detached) {
            info = _("text.holdposition.end");
          }

          elements = [
            {key: _("label.unit.position"), val: incident.ao().info()}
          ];
        } else if (incident.isToHome()) {
          elements = [
            {key: _("label.incident.bo"), val: incident.bo().info()},
            {key: _("label.incident.ao"), val: incident.ao().info()}
          ];

          button = (nextState === s.zao) ? _("label.task.state.zao") : _("label.task.state.ishome");
        } else if (incident.isRelocation()) {
          elements = [
            {key: _("text.confirmation.current"), val: this.localizedTaskState()},
            {key: _("label.incident.ao"), val: incident.ao().info()},
            {key: _("label.incident.blue"), val: (incident.blue() ? _("label.yes") : _("label.no"))},
            {key: _("label.incident.info"), val: incident.info()}
          ];

          button = _("label.task.state." + nextState.toLowerCase());
        } else {
          elements = [
            {key: _("text.confirmation.current"), val: this.localizedTaskState()},
            {key: _("label.incident.bo"), val: incident.bo().info()},
            {key: _("label.incident.ao"), val: incident.ao().info()},
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
  data = data || {};

  this.id = data.id;
  this.info = ko.observable("");
  this.lat = ko.observable(null);
  this.lng = ko.observable(null);

  this.setData = function(data) {
    data = data || {};
    this.info(data.info || "");
    this.lat(data.latitude || null);
    this.lng(data.longitude || null);
  };

  this.setData(data);
};

Coceso.Models.Point.empty = new Coceso.Models.Point();

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
  this.ao = ko.observable(Coceso.Models.Point.empty);
  this.bo = ko.observable(Coceso.Models.Point.empty);
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
    self.ao(Coceso.Data.getPoint(data.ao));
    self.bo(Coceso.Data.getPoint(data.bo));
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
    return !!this.ao().id;
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
      return (this.bo().id) ? this.bo().info() : _("label.incident.nobo");
    }
    return (this.ao().id) ? this.ao().info() : _("label.incident.noao");
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

  this.home = ko.observable(Coceso.Models.Point.empty);
  this.position = ko.observable(Coceso.Models.Point.empty);
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
    self.home(Coceso.Data.getPoint(data.home));
    self.position(Coceso.Data.getPoint(data.position));
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

  this.target = ko.pureComputed(function() {
    if (!this.portable) {
      return this.home();
    }
    var target = ko.utils.arrayFirst(this.incidents(), function(task) {
      return (task.isZBO() || task.isZAO());
    });
    if (target && target.incident()) {
      return target.isZBO() ? target.incident().bo() : target.incident().ao();
    }
    return this.position();
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
    return !!this.home().id;
  }, this);

  /**
   * Last known position is home
   *
   * @function
   * @type ko.pureComputed
   * @returns {boolean}
   */
  this.isHome = this.position.extend({isValue: this.home});

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
    if (this.isHome() || !this.position().id || (this.incidentCount() > 1)) {
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
      content += "<dt><span class='glyphicon glyphicon-home'></span></dt><dd><span class='pre'>" + this.home().info().escapeHTML() + "</span></dd>";
    }
    content += "<dt><span class='glyphicon glyphicon-map-marker'></span></dt><dd><span class='pre'>" +
        (this.position().id ? this.position().info().escapeHTML() : "N/A") + "</span></dd>";

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
      title: this.call,
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
      data.bo = {info: self.position().info()};
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
        Coceso.Ajax.save(JSON.stringify({ani: this.ani}), "selcall/send.json");
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
      Coceso.ViewModels.destroyComputed(this);
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
      Coceso.ViewModels.destroyComputed(this);
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
  this.form = ko.observableArray().extend({arrayChanges: {}});

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
    this.ao.server(this.model().ao().info);
    this.blue.server(this.model().blue);
    this.bo.server(this.model().bo().info);
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

      Coceso.Ajax.save(JSON.stringify(data), "incident/update.json", this.afterSave, this.saveError, this.httpError);
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
      Coceso.ViewModels.destroyComputed(this);
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
    this.position.server(this.model().position().info);
    this.home.server(this.model().home().info);
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

      Coceso.Ajax.save(JSON.stringify(data), "unit/update.json", this.afterSave, this.saveError, this.httpError);
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
      Coceso.ViewModels.destroyComputed(this);
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
      Coceso.ViewModels.destroyComputed(this);
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

      Coceso.Ajax.save(JSON.stringify(data), "patient/update.json", this.afterSave, this.saveError, this.httpError);
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
      Coceso.ViewModels.destroyComputed(this);
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
 * Constructor for the situation map
 *
 * @constructor
 * @param {Object} options
 */
Coceso.ViewModels.Map = function(options) {
  var self = this, map;
  options = options || {};
  this.dialogTitle = options.title || _("label.map");

  this.incidents = Coceso.Data.incidents.list.extend({list: {filter: {
        type: [Coceso.Constants.Incident.type.task, Coceso.Constants.Incident.type.transport, Coceso.Constants.Incident.type.relocation],
        isDone: false
      }}});

  var names = {
    basemap: _("label.map.basemap"),
    vienna: _("label.map.vienna"),
    hospitals: _("label.map.hospitals"),
    oneway: _("label.map.oneway"),
    defi: _("label.map.defi"),
    "ehs.in": _("label.map.ehs.in"),
    "ehs.out": _("label.map.ehs.out")
  };

  this.points = {};
  this.lines = {};

  this.init = function() {
    var baseLayers = {}, overlays = {};
    baseLayers[names.basemap] = L.tileLayer("https://{s}.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpeg", {
      subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
      bounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
      attribution: _("label.map.source") + ": <a href='http://basemap.at' target='_blank'>basemap.at</a>, " +
          "<a href='http://creativecommons.org/licenses/by/3.0/at/deed.de' target='_blank'>CC-BY 3.0</a>"
    });
    baseLayers[names.vienna] = L.layerGroup([
      L.tileLayer("https://{s}.wien.gv.at/wmts/lb/farbe/google3857/{z}/{y}/{x}.jpeg", {
        subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
        bounds: [[48.10, 16.17], [48.33, 16.58]]
      }),
      L.tileLayer("https://{s}.wien.gv.at/wmts/beschriftung/normal/google3857/{z}/{y}/{x}.png", {
        subdomains: ["maps", "maps1", "maps2", "maps3", "maps4"],
        bounds: [[48.10, 16.17], [48.33, 16.58]]
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
          layer.bindPopup("<strong>" + feature.properties.BEZEICHNUNG + "</strong><br/>" + feature.properties.ADRESSE);
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
          layer.bindPopup("<strong>" + feature.properties.ADRESSE + "</strong><br/>" + feature.properties.INFO);
        }
      }
    });

    overlays[names["ehs.out"]] = L.imageOverlay(Coceso.Conf.layerBase + "ehs_out.jpg", [[48.200655, 16.415747], [48.213634, 16.427824]]);
    overlays[names["ehs.in"]] = L.imageOverlay(Coceso.Conf.layerBase + "ehs_in.png", [[48.204912, 16.417671], [48.209496, 16.424191]]);

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
        options.c = $.map(options.c, parseFloat);
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

    map = L.map(this.ui ? this.ui + "-map-container" : "map-container", {
      center: options.c, zoom: options.z,
      minZoom: 7, maxZoom: 18,
      maxBounds: [[46.358770, 8.782379], [49.037872, 17.189532]],
      layers: layers
    });

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

    if (locate) {
      map.locate({setView: true});
    }
    L.control.layers(baseLayers, overlays).addTo(map);

    if (this.ui) {
      $("#" + this.ui).on("dialogresizestop", function() {
        map.invalidateSize();
      });
    }

    for (var i in Coceso.Data.points) {
      this.points[i] = new L.Marker.Point(Coceso.Data.points[i], map, this.incidents, Coceso.Data.units.list);
    }

    Coceso.UI.Maps.push(this);

    this._drawIncidents = ko.computed(function() {
      var found = [];
      ko.utils.arrayForEach(this.incidents(), function(inc) {
        if (!self.lines[inc.id] || self.lines[inc.id].incident !== inc) {
          if (self.lines[inc.id]) {
            //Local marker exists, but incident does not match global marker
            map.removeLayer(self.lines[inc.id]);
          }
          self.lines[inc.id] = new L.LayerGroup.Incident(inc);
          self.lines[inc.id].addTo(map);
        }
        found[inc.id] = true;
      });
      for (var id in self.lines) {
        if (!found[id]) {
          map.removeLayer(self.lines[id]);
          delete self.lines[id];
        }
      }
    }, this);
  };

  this.newPoint = function(point) {
    this.points[point.id] = new L.Marker.Point(point, map, this.incidents, Coceso.Data.units.list);
  };
};

L.GeoJSON.WFS = L.GeoJSON.extend({
  initialize: function(serviceUrl, featureType, options) {
    options = options || {};
    L.GeoJSON.prototype.initialize.call(this, null, options);
    this.getFeatureUrl = serviceUrl + "?service=WFS&request=GetFeature&outputFormat=json&version=1.1.0&srsName=EPSG:4326&typeName=" + featureType;
  },
  onAdd: function(map) {
    L.LayerGroup.prototype.onAdd.call(this, map);
    if (!this.jsonData) {
      var self = this;
      this.getFeature(function() {
        self.addData(self.jsonData);
      });
    }
  },
  getFeature: function(callback) {
    var self = this;
    $.ajax({
      dataType: "json",
      url: this.getFeatureUrl,
      success: function(response) {
        if (response.type && response.type === "FeatureCollection") {
          self.jsonData = response;
          callback();
        }
      }
    });
  }
});

L.LatLng.Observable = function(lat, lng) {
  if (lat instanceof Coceso.Models.Point) {
    lng = lat.lng;
    lat = lat.lat;
  }
  this.latObservable = ko.isObservable(lat) ? lat : ko.observable(lat);
  this.lngObservable = ko.isObservable(lng) ? lng : ko.observable(lng);
  this.callbacks = [];

  try {
    L.LatLng.call(this, this.latObservable(), this.lngObservable());
  } catch (e) {
    this.lat = null;
    this.lng = null;
  }

  this.latObservable.subscribe(function(lat) {
    lat = parseFloat(lat);
    this.lat = isNaN(lat) ? null : lat;
    this._notify();
  }, this);
  this.lngObservable.subscribe(function(lng) {
    lng = parseFloat(lng);
    this.lng = isNaN(lng) ? null : lng;
    this._notify();
  }, this);
};
L.LatLng.Observable.prototype = Object.create(L.LatLng.prototype, /** @lends L.LatLng.Observable.prototype */ {
  subscribe: {
    value: function(method, obj) {
      this.callbacks.push([obj, method]);
    }
  },
  unsubscribe: {
    value: function(method) {
      ko.utils.arrayRemoveItem(this.callbacks, function(item) {
        return item[1] === method;
      });
    }
  },
  _notify: {
    value: function() {
      ko.utils.arrayForEach(this.callbacks, function(item) {
        item[1].call(item[0]);
      });
    }
  }
});

L.Marker.Point = L.Marker.extend({
  initialize: function(point, map, incidents, units, options) {
    this.point = point;
    L.Marker.prototype.initialize.call(this, new L.LatLng.Observable(point), options);
    this._latlng.subscribe(this.update, this);

    this.bo = incidents.extend({list: {filter: {bo: point}}});
    this.ao = incidents.extend({list: {filter: {ao: point}}});
    this.target = units.extend({list: {filter: {target: point}}});

    this.showMarker = ko.computed(function() {
      if (this.bo().length || this.ao().length || this.target().length) {
        map.addLayer(this);
        return true;
      }
      map.removeLayer(this);
      return false;
    }, this);

    var popup = L.popup();
    this.popupContent = ko.computed(function() {
      if (!this.showMarker()) {
        return "";
      }
      var content = "<strong class='pre'>" + point.info() + "</strong>";
      if (this.target().length) {
        content += "<hr/>" + _("label.units");
        ko.utils.arrayForEach(this.target(), function(unit) {
          content += "<br/>" + unit.call;
        });
      }
      ko.utils.arrayForEach(this.bo(), function(inc) {
        content += "<br/>" + inc.assignedTitle();
      });
      ko.utils.arrayForEach(this.ao(), function(inc) {
        content += "<br/>" + inc.assignedTitle();
      });
      popup.setContent(content);
      return content;
    }, this);
    this.bindPopup(popup);
  },
  destroy: function() {
    this._latlng.unsubscribe(this.update);
  }
});

L.LayerGroup.Incident = L.LayerGroup.extend({
  initialize: function(inc) {
    this.incident = inc;
    this._layers = {};
    var line = new L.Polyline([[0, 0], [0, 0]]);

    this._updateLine = ko.computed(function() {
      var aLat = inc.ao().lat(), aLng = inc.ao().lng(),
          bLat = inc.bo().lat(), bLng = inc.bo().lng();

      if (aLat && aLng && bLat && bLng) {
        line.setLatLngs([[bLat, bLng], [aLat, aLng]]);
        this.addLayer(line);
      } else {
        this.removeLayer(line);
      }
    }, this);
  }
});

/**
 * Constructor for the notification viewmodel
 *
 * @constructor
 */
Coceso.ViewModels.Notifications = function() {
  var self = this;

  /**
   * Current clock time
   *
   * @function
   * @type ko.observable
   * @returns {String}
   */
  this.clock_time = ko.observable("00:00:00");

  /**
   * Local clock offset to correct time
   *
   * @type integer
   */
  this.clock_offset = 0;

  /**
   * Update the clock
   *
   * @returns {void}
   */
  this.clock_update = function() {
    var currentTime = new Date(new Date() - self.clock_offset),
        currentHours = currentTime.getHours(),
        currentMinutes = currentTime.getMinutes(),
        currentSeconds = currentTime.getSeconds();

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
