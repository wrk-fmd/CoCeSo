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
        Coceso.UI.openIncident(_("label.incident") + " / " + _("label.add"));
      }
    });
  }

  $(".tooltipped").tooltip();
  $(document).on("click", ".panel-toggle", function() {
    $(this).parent(".panel").children(".panel-body").slideToggle();
    $(this).find(".glyphicon").toggleClass("glyphicon-chevron-down glyphicon-chevron-up");
  });

  //Load the debug view model
  Coceso.UI.Debug = new Coceso.ViewModels.Debug();

  //Load Bindings for Notifications
  Coceso.UI.Notifications = new Coceso.ViewModels.Notifications();
  ko.applyBindings(Coceso.UI.Notifications, $("#nav-notifications")[0]);

  //Load Bindings for status confirmation window
  ko.applyBindings(Coceso.UI.Dialog, $("#next-state-confirm")[0]);
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
   * @param {String} title The title of the window
   * @param {String} src The source to load the HTML from
   * @param {Object} viewmodel The viewmodel to bind with
   * @param {Object} options
   * @returns {void}
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
   * @param {Object} options
   * @param {Object} dialog Dialog options
   * @returns {boolean} false
   */
  openIncidents: function(title, options, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + "incident.html", new Coceso.ViewModels.Incidents(options || {}), $.extend({position: {at: "left+70% top+30%"}}, dialog));
    return false;
  },
  /**
   * Open a specific incident
   *
   * @param {String} title
   * @param {Object} data Additional incident data
   * @param {Object} dialog Dialog options
   * @returns {boolean} false
   */
  openIncident: function(title, data, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + "incident_form.html", new Coceso.ViewModels.Incident(data || {}), $.extend({position: {at: "left+30% top+10%"}}, dialog));
    return false;
  },
  /**
   * Open the units overview
   *
   * @param {String} title
   * @param {Object} options Viewmodel options
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openUnits: function(title, options, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + "unit.html", new Coceso.ViewModels.Units(options || {}), $.extend({position: {at: "left+20% bottom"}}, dialog));
    return false;
  },
  /**
   * Open the units overview with hierarchical View
   *
   * @param {String} title
   * @param {Object} dialog Dialog options
   */
  openHierarchyUnits: function(title, dialog) {
    var viewmodel = {top: ko.observable({name: "Loading...", units: [], subContainer: []})};

    $.getJSON(Coceso.Conf.jsonBase + "unitContainer/getSlim.json", function(topContainer) {
      viewmodel.top(new Coceso.ViewModels.UnitContainer(topContainer));
    });

    this.openWindow(title, Coceso.Conf.contentBase + "unit_hierarchy.html", viewmodel, $.extend({position: {at: "left top"}}, dialog));
    return false;
  },
  /**
   * Open the unit edit Window
   *
   * @param {String} title
   * @param {Object} data Additional unit data
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openUnit: function(title, data, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + "unit_form.html", new Coceso.ViewModels.Unit(data || {}), $.extend({position: {at: "left+10% top+20%"}}, dialog));
    return false;
  },
  /**
   * Open Add-Log Window
   *
   * @param {String} title
   * @param {Object} data Additional log data
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openLogAdd: function(title, data, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + "log_add.html", new Coceso.ViewModels.CustomLogEntry(data || {}), $.extend({position: {at: "left+20% top+20%"}}, dialog));
    return false;
  },
  /**
   * Open a list of log entries
   *
   * @param {String} title
   * @param {Object} options Viewmodel options
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openLogs: function(title, options, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + "log.html", new Coceso.ViewModels.Logs(options || {}), $.extend({position: {at: "left+30% center"}}, dialog));
    return false;
  },
  /**
   * Open the patient edit Window
   *
   * @param {String} title
   * @param {Object} data Additional patient data
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openPatient: function(title, data, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + "patient_form.html", new Coceso.ViewModels.Patient(data || {}), $.extend({position: {at: "left+40% top+30%"}}, dialog));
    return false;
  },
  /**
   * Open debug window
   *
   * @param {String} title
   * @param {Object} dialog Dialog options
   * @returns {boolean}
   */
  openDebug: function(title, dialog) {
    this.openWindow(title, Coceso.Conf.contentBase + "debug.html", this.Debug, $.extend({position: {at: "left+10% center"}}, dialog));
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
    this.openWindow(title, Coceso.Conf.contentBase + src, {}, $.extend({position: {at: "left+10% top+20%"}}, dialog));
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
    this.openWindow(title, src, {}, $.extend({position: {at: "left+30% center"}}, dialog));
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
   * @type ko.computed
   * @returns {Coceso.Models.Incident}
   */
  this.incident = ko.computed(function() {
    return Coceso.Data.getIncident(this.incident_id);
  }, this);

  /*
   * Get the associated unit
   *
   * @function
   * @type ko.computed
   * @returns {Coceso.Models.Unit}
   */
  this.unit = ko.computed(function() {
    return Coceso.Data.getUnit(this.unit_id);
  }, this);

  /**
   * Return if TaskState is "Assigned"
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isAssigned = this.taskState.extend({isValue: Coceso.Constants.TaskState.assigned});

  /**
   * Return if TaskState is "ZBO"
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isZBO = this.taskState.extend({isValue: Coceso.Constants.TaskState.zbo});

  /**
   * Return if TaskState is "ABO"
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isABO = this.taskState.extend({isValue: Coceso.Constants.TaskState.abo});

  /**
   * Return if TaskState is "ZAO"
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isZAO = this.taskState.extend({isValue: Coceso.Constants.TaskState.zao});

  /**
   * Return if TaskState is "AAO"
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isAAO = this.taskState.extend({isValue: Coceso.Constants.TaskState.aao});

  /**
   * Return if TaskState is "Detached"
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isDetached = this.taskState.extend({isValue: Coceso.Constants.TaskState.detached});

  /**
   * Return the localized taskState
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.localizedTaskState = ko.computed(function() {
    if (this.taskState()) {
      return _("label.task.state." + this.taskState().toLowerCase());
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

      if (needAO && !incident.ao.info() && (nextState === s.zao || nextState === s.aao)) {
        console.info("No AO set, opening Incident Window");
        Coceso.UI.openIncident(_("label.incident.edit"), {id: incident.id});
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
            {key: _("text.confirmation.current"), val: _("label.task.state." + this.taskState().toLowerCase())},
            {key: _("label.incident.ao"), val: incident.ao.info()},
            {key: _("label.incident.blue"), val: (incident.blue() ? _("label.yes") : _("label.no"))},
            {key: _("label.incident.info"), val: incident.info()}
          ];

          button = _("label.task.state." + nextState.toLowerCase());
        } else {
          elements = [
            {key: _("text.confirmation.current"), val: _("label.task.state." + this.taskState().toLowerCase())},
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
          title: "<strong>" + unit.call + "</strong>" + " - " + incident.localizedType(),
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
  this.ao = {info: ko.observable("")};
  this.bo = {info: ko.observable("")};
  this.units = ko.observableArray([]);
  this.blue = ko.observable(false);
  this.caller = ko.observable("");
  this.casusNr = ko.observable("");
  this.info = ko.observable("");
  this.state = ko.observable(Coceso.Constants.Incident.state.new);
  this.type = ko.observable(Coceso.Constants.Incident.type.task);

  /**
   * Method to set data
   *
   * @param {Object} data
   * @returns {void}
   */
  this.setData = function(data) {
    if (data.ao) {
      self.ao.info(data.ao.info);
    } else {
      self.ao.info("");
    }
    if (data.bo) {
      self.bo.info(data.bo.info);
    } else {
      self.bo.info("");
    }
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
    self.state(data.state || Coceso.Constants.Incident.state.new);
    self.type(data.type || Coceso.Constants.Incident.type.task);
  };

  //Set data
  this.setData(data);

  /**
   * is$Type methods
   */
  {
    /**
     * Incident is of type "Task"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isTask = this.type.extend({isValue: Coceso.Constants.Incident.type.task});

    /**
     * Incident is of type "Relocation"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isRelocation = this.type.extend({isValue: Coceso.Constants.Incident.type.relocation});

    /**
     * Incident is of type "Transport"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isTransport = this.type.extend({isValue: Coceso.Constants.Incident.type.transport});

    /**
     * Incident is of type "ToHome"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isToHome = this.type.extend({isValue: Coceso.Constants.Incident.type.tohome});

    /**
     * Incident is of type "HoldPosition"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isHoldPosition = this.type.extend({isValue: Coceso.Constants.Incident.type.holdposition});

    /**
     * Incident is of type "Standby"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isStandby = this.type.extend({isValue: Coceso.Constants.Incident.type.standby});
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
     * @returns {boolean}
     */
    this.isNew = this.state.extend({isValue: Coceso.Constants.Incident.state.new});

    /**
     * Incident has state "Open"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isOpen = this.state.extend({isValue: Coceso.Constants.Incident.state.open});

    /**
     * Incident has state "Dispo"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isDispo = this.state.extend({isValue: Coceso.Constants.Incident.state.dispo});

    /**
     * Incident has state "Working"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isWorking = this.state.extend({isValue: Coceso.Constants.Incident.state.working});

    /**
     * Incident has state "Done"
     *
     * @function
     * @type ko.computed
     * @returns {boolean}
     */
    this.isDone = this.state.extend({isValue: Coceso.Constants.Incident.state.done});
  }

  /**
   * The associated patient
   *
   * @function
   * @type ko.computed
   * @returns {Coceso.Model.Patient}
   */
  this.patient = ko.computed(function() {
    return Coceso.Data.getPatient(this.id);
  }, this);

  /**
   * Return the type as localized string
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.localizedType = ko.computed(function() {
    if (this.type()) {
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
   * Disable "AAO" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableAAO = ko.computed(function() {
    return (this.ao.info() === "");
  }, this);

  /**
   * Disable "ZAO" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableZAO = ko.computed(function() {
    return (this.isStandby() || this.isHoldPosition() || this.ao.info() === "");
  }, this);

  /**
   * Number of assigned units
   *
   * @function
   * @type ko.computed
   * @returns {integer}
   */
  this.unitCount = ko.computed(function() {
    return this.units().length;
  }, this);

  /**
   * Return the title string
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.title = ko.computed(function() {
    if (!this.disableBO()) {
      return (this.bo.info()) ? this.bo.info() : _("label.incident.nobo");
    }
    return (this.ao.info()) ? this.ao.info() : _("label.incident.noao");
  }, this);

  /**
   * Return a one-letter representation of type
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
  this.typeString = ko.computed(function() {
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
   * Title in unit dropdown
   *
   * @function
   * @type ko.computed
   * @returns {String}
   */
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
   * @returns {String}
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
        Coceso.UI.openIncident(_("label.incident.edit"), {id: id});
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
        Coceso.UI.openLogs("Incident-Log", {url: "log/getByIncident/" + this.id});
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
      Coceso.UI.openPatient(_("label.patient.edit"), {id: this.id});
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

  this.home = {info: ko.observable("")};
  this.position = {info: ko.observable("")};
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
    if (data.home) {
      self.home.info(data.home.info);
    } else {
      self.home.info("");
    }
    if (data.position) {
      self.position.info(data.position.info);
    } else {
      self.position.info("");
    }
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
   * Return the number of assigned incidents
   *
   * @function
   * @type ko.computed
   * @returns {Integer}
   */
  this.incidentCount = ko.computed(function() {
    return this.incidents().length;
  }, this);

  /**
   * List of incidents showing up in dropdown
   *
   * @function
   * @type ko.computed
   * @returns {Array}
   */
  this.dropdownIncidents = ko.computed(function() {
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
   * @type ko.computed
   * @returns {boolean}
   */
  this.dropdownActive = ko.computed(function() {
    return (this.dropdownIncidents().length > 0);
  }, this);

  /**
   * Home is set
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.hasHome = ko.computed(function() {
    return (this.home.info() !== "");
  }, this);

  /**
   * Last known position is home
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isHome = this.position.info.extend({isValue: this.home.info});

  /**
   * Unit has state "AD"
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isAD = this.state.extend({isValue: Coceso.Constants.Unit.state.ad});

  /**
   * Unit has state "EB"
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isEB = this.state.extend({isValue: Coceso.Constants.Unit.state.eb});

  /**
   * Unit has state "NEB"
   *
   * @function
   * @type ko.computed
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
    if (this.incidentCount() <= 0) {
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
   * @type ko.computed
   * @returns {boolean}
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
   * @returns {boolean}
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
   * @returns {boolean}
   */
  this.disableHoldPosition = ko.computed(function() {
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
   * Text based on the unit's assigned tasks
   *
   * @function
   * @type ko.computed
   * @returns {string} The text
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
   * @returns {string} The CSS class
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
   * @returns {Object} The popover options
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
      ko.utils.arrayForEach(this.incidents(), function(task) {
        if (task.incident() !== null) {
          content += "<p><span class='key'>" + task.incident().assignedTitle() + "</span><span>" + _("label.task.state." + task.taskState().toLowerCase()) + "</span></p>";
        }
      });
    }
    content += "</div>";

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
    Coceso.UI.openIncident(_("label.unit.new_incident"), data);
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
    Coceso.UI.openIncident(_("label.unit.report_incident"), data);
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
      Coceso.UI.openUnit(this.call + " - " + _("label.unit.edit"), {id: this.id});
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
        Coceso.UI.openLogs("Unit-Log", {url: "log/getLastByUnit/" + this.id + "/" + Coceso.Conf.logEntryLimit});
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
   * @type ko.computed
   * @returns {boolean}
   */
  this.isMale = this.sex.extend({isValue: Coceso.Constants.Patient.sex.male});

  /**
   * Patient is female
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isFemale = this.sex.extend({isValue: Coceso.Constants.Patient.sex.female});

  /**
   * Patient's sex is unknown
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.isUnknown = ko.computed(function() {
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
   * @returns {void}
   */
  this.isUnknown.state = ko.computed(function() {
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
  this.unit = data.unit ? Coceso.Data.getUnit(data.unit.id) : null;
  this.incident = data.incident ? Coceso.Data.getIncident(data.incident.id) : null;
  this.user = data.user;
  this.timestamp = data.timestamp;
  this.state = data.state;
  this.type = data.type;
  this.autoGenerated = data.autoGenerated;
  this.text = data.text;
  this.data = JSON.parse(data.json);

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
   * @function
   * @type ko.computed
   * @returns {Object}
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
  var self = this;

  /**
   * Watch dependencies
   *
   * @type ko.observableArray
   */
  this.form = ko.observableArray().extend({arrayChanges: {}});

  /**
   * Callback on error saving
   *
   * @returns {void}
   */
  this.saveError = function() {
    $("#" + self.ui + "-error").stop(true).show().fadeOut(7000);
  };

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

  //Call parent constructors
  Coceso.Models.Incident.call(this, {id: this.idObs()});
  Coceso.ViewModels.Form.call(this);

  //Initialize change detection
  this.ao.info.extend({observeChanges: {}});
  this.blue = this.blue.extend({boolean: true, observeChanges: {}});
  this.bo.info.extend({observeChanges: {}});
  this.caller.extend({observeChanges: {}});
  this.casusNr.extend({observeChanges: {}});
  this.info.extend({observeChanges: {keepChanges: true}});
  this.state.extend({observeChanges: {}});
  this.type.extend({observeChanges: {}});
  this.units.extend({arrayChanges: {}});
  this.form.push(this.units, this.type, this.blue, this.bo.info, this.ao.info, this.info, this.caller, this.casusNr, this.state);

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
    this.ao.info.setServer(this.model().ao.info);
    this.blue.setServer(this.model().blue);
    this.bo.info.setServer(this.model().bo.info);
    this.caller.setServer(this.model().caller);
    this.casusNr.setServer(this.model().casusNr);
    this.info.setServer(this.model().info);
    this.state.setServer(this.model().state);
    this.type.setServer(this.model().type);

    //Set initial data
    if (ko.computedContext.isInitial()) {
      if (data.ao) {
        this.ao.info(data.ao.info);
      }
      if (typeof data.blue !== "undefined") {
        this.blue(data.blue);
      }
      if (typeof data.bo !== "undefined") {
        this.bo.info(data.bo.info);
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
          local.taskState.setServer(task.taskState);
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
   * Disable the "Task" type button
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableTask = ko.computed(function() {
    return (this.id && !this.isTask());
  }, this);

  /**
   * Disable the "Relocation" type button
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableRelocation = ko.computed(function() {
    return (this.id && !this.isRelocation());
  }, this);

  /**
   * Disable the "Transport" type button
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableTransport = ko.computed(function() {
    return (this.id && !this.isTransport());
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
   * Disable "AAO" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableAAO = ko.computed(function() {
    return (this.ao.info() === "");
  }, this);

  /**
   * Disable "ZAO" state
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableZAO = ko.computed(function() {
    return (this.isStandby() || this.isHoldPosition() || this.ao.info() === "");
  }, this);

  /**
   * Disable IncidentState New
   *
   * @function
   * @type ko.computed
   * @returns {boolean}
   */
  this.disableNew = ko.computed(function() {
    return (this.id && this.state.orig() !== Coceso.Constants.Incident.state.new);
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
      return (!this.ao.info() && ko.utils.arrayFilter(this.units(), function(task) {
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
      bo: {info: self.bo.info()},
      ao: {info: self.ao.info()},
      info: self.info(),
      blue: self.blue(),
      type: self.type()
    };
    if (task instanceof Coceso.Models.Task) {
      data.units = {};
      data.units[task.unit_id] = task.taskState();
      task.taskState(Coceso.Constants.TaskState.detached);
      data.autoSave = true;
    }
    Coceso.UI.openIncident(_("label.incident"), data);
    this.save();
  };

  /**
   * Callback after saving
   *
   * @param {Object} data The data returned from server
   * @returns {void}
   */
  this.afterSave = function(data) {
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
        ao: {info: this.ao.info()},
        bo: {info: this.bo.info()},
        blue: this.blue(),
        caller: this.caller(),
        casusNr: this.casusNr(),
        info: this.info(),
        state: this.state(),
        type: this.type(),
        units: {}
      };

      if (data.ao.info === "") {
        data.ao = {id: -2};
      }
      if (data.bo.info === "") {
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

      Coceso.Ajax.save(JSON.stringify(data), "incident/update.json", this.afterSave, this.saveError, this.saveError);
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
   * @returns {Coceso.Models.Incident}
   */
  this.model = ko.observable(null);

  //Call parent constructors
  Coceso.Models.Unit.call(this, {id: data.id});
  Coceso.ViewModels.Form.call(this);

  //Initialize change detection
  this.position.info.extend({observeChanges: {}});
  this.home.info.extend({observeChanges: {}});
  this.info.extend({observeChanges: {keepChanges: true}});
  this.state.extend({observeChanges: {}});
  this.incidents.extend({arrayChanges: {}});
  this.form.push(this.position.info, this.info, this.state, this.incidents);

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
    this.position.info.setServer(this.model().position.info);
    this.home.info.setServer(this.model().home.info);
    this.info.setServer(this.model().info);
    this.state.setServer(this.model().state);

    //Set initial data
    if (ko.computedContext.isInitial()) {
      if (data.position) {
        this.position.info(data.position.info);
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
          local.taskState.setServer(task.taskState);
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
        position: {info: this.position.info()},
        info: this.info(),
        state: this.state(),
        incidents: {}
      };

      if (data.position.info === "") {
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

      Coceso.Ajax.save(JSON.stringify(data), "unit/update.json", null, this.saveError, this.saveError);
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
    this.given_name.setServer(this.model().given_name);
    this.sur_name.setServer(this.model().sur_name);
    this.insurance_number.setServer(this.model().insurance_number);
    this.externalID.setServer(this.model().externalID);
    this.diagnosis.setServer(this.model().diagnosis);
    this.erType.setServer(this.model().erType);
    this.info.setServer(this.model().info);
    this.sex.setServer(this.model().sex);

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

      Coceso.Ajax.save(JSON.stringify(data), "patient/update.json", null, this.saveError, this.saveError);
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
          self.loglist([]);
          ko.utils.arrayForEach(data, function(item) {
            self.loglist.push(new Coceso.Models.Log(item));
          });
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

  this.text = ko.observable(data.text || "");
  this.unit = ko.observable(data.unit || 0);
  this.error = ko.observable(false);
  this.unitList = Coceso.Data.units.list;

  this.ok = function() {
    Coceso.Ajax.save(
        JSON.stringify({
          text: this.text(),
          unit: this.unit() ? {id: this.unit()} : null
        }),
        "log/add.json", this.afterSave, this.saveError, this.saveError);
  };

  this.saveError = function() {
    self.error(true);
  };

  this.afterSave = function() {
    $("#" + self.ui).dialog("destroy");
  };
};

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
      return (i.isTransport());
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
 * Debug viewmodel
 *
 * @constructor
 */
Coceso.ViewModels.Debug = function() {
  var self = this;

  /**
   * List of errors
   *
   * @function
   * @type ko.obserableArray
   * @returns {Array}
   */
  this.errors = ko.observableArray();

  /**
   * Sorted errors
   *
   * @function
   * @type ko.observable
   * @returns {Array}
   */
  this.filtered = this.errors.extend({
    list: {
      sort: function(a, b) {
        return (b.timestamp - a.timestamp);
      }
    }
  });

  /**
   * Add a HTTP error to the list
   *
   * @param {type} jqXHR
   * @param {type} url
   * @param {type} data
   * @returns {undefined}
   */
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
