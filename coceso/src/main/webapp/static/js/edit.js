/**
 * CoCeSo
 * Client JS - edit
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
 * Get ordering for array position
 *
 * @param {Array} array
 * @param {Integer} index
 * @returns {Float} Position in array
 */
Coceso.Helpers.computeOrdering = function(array, index) {
  if (!(array instanceof Array) || index < 0 || index >= array.length) {
    return 0.0;
  }
  if (index === 0) {
    return (array.length > 1) ? array[1].ordering / 2.0 : 10.0;
  }
  if (index === array.length - 1) {
    return array[array.length - 2].ordering + 10.0;
  }
  return (array[index - 1].ordering + array[index + 1].ordering) / 2.0;
};

/**
 * Single Concern
 *
 * @constructor
 * @param {Object} data
 * @param {Coceso.ViewModels.Home} rootModel
 */
Coceso.Models.Concern = function(data, rootModel) {
  var self = this;

  data = data || {};
  this.id = data.id || null;
  this.name = data.name || "";
  this.closed = ko.observable(data.closed || false).extend({"boolean": true});

  this.isActive = ko.computed(function() {
    return rootModel.concernId() === this.id;
  }, this);

  this.select = function() {
    if (rootModel.locked()) {
      return false;
    }

    Coceso.Ajax.save({concern_id: this.id}, "setActiveConcern.json", function() {
      self.id ? $.cookie("concern", self.id) : $.removeCookie("concern");
      rootModel.error(false);
      rootModel.concernId(self.id);
      localStorage.concern = self.id || "";
    }, rootModel.saveError, rootModel.httpError);
  };

  this.close = function() {
    Coceso.Ajax.save({concern_id: this.id}, "concern/close.json", function() {
      rootModel.error(false);
      self.closed(true);
      if (self.isActive()) {
        $.removeCookie("concern");
        rootModel.concernId(null);
        localStorage.concern = "";
      }
    }, rootModel.saveError, rootModel.httpError);
  };

  this.reopen = function() {
    Coceso.Ajax.save({concern_id: this.id}, "concern/reopen.json", function() {
      rootModel.error(false);
      self.closed(false);
    }, rootModel.saveError, rootModel.httpError);
  };
};

/**
 * Create concern
 *
 * @constructor
 * @param {Coceso.ViewModels.Home} rootModel
 */
Coceso.Models.CreateConcern = function(rootModel) {
  var self = this;

  this.name = ko.observable("").extend({observeChanges: {server: ""}});

  this.save = function() {
    if (!this.name.changed()) {
      return false;
    }

    Coceso.Ajax.save(JSON.stringify({
      name: self.name()
    }), "concern/update.json", function() {
      rootModel.error(false);
      self.name("");
      rootModel.load();
    }, rootModel.saveError, rootModel.httpError);
  };
};

/**
 * Edit unit properties
 *
 * @constructor
 * @param {Object} data
 * @param {Coceso.ViewModels.EditUnits} rootModel
 */
Coceso.Models.EditableUnit = function(data, rootModel) {
  var self = this;
  data = data || {};

  this.id = data.id || null;
  this.call = ko.observable(data.call || "").extend({observeChanges: {server: data.call || ""}});
  this.ani = ko.observable(data.ani || "").extend({observeChanges: {server: data.ani || ""}});
  this.doc = ko.observable(data.withDoc || false).extend({"boolean": true, observeChanges: {server: data.withDoc || false}});
  this.vehicle = ko.observable(data.transportVehicle || false).extend({"boolean": true, observeChanges: {server: data.transportVehicle || false}});
  this.portable = ko.observable(data.portable || false).extend({"boolean": true, observeChanges: {server: data.portable || false}});
  this.info = ko.observable(data.info || "").extend({observeChanges: {server: data.info || ""}});
  this.home = ko.observable(data.home && data.home.info ? data.home.info : "").extend({observeChanges: {server: data.home && data.home.info ? data.home.info : ""}});
  this.locked = data.locked;

  this.crew = ko.observableArray(data.crew ? $.map(data.crew, function(item) {
    return new Coceso.Models.SlimPerson(item);
  }) : []);

  this.form = ko.observableArray([this.call, this.ani, this.doc, this.vehicle,
    this.portable, this.info, this.home]).extend({arrayChanges: {}});

  this.editCrew = function() {
    rootModel.showForm(this);
  };

  this.save = function(success) {
    Coceso.Ajax.save(JSON.stringify({
      id: self.id,
      call: self.call(),
      ani: self.ani(),
      withDoc: self.doc(),
      transportVehicle: self.vehicle(),
      portable: self.portable(),
      info: self.info(),
      home: {info: self.home()}
    }), "unit/updateFull.json", function(response) {
      rootModel.error(false);
      rootModel.loadContainer();
      if (response.unit_id) {
        self.id = response.unit_id;
      }
      if (success instanceof Function) {
        success();
      }
      self.call.server(self.call());
      self.ani.server(self.ani());
      self.doc.server(self.doc());
      self.vehicle.server(self.vehicle());
      self.portable.server(self.portable());
      self.info.server(self.info());
      self.home.server(self.home());
    }, rootModel.saveError, rootModel.httpError);
  };

  this.assignPerson = function() {
    var person = this;
    if (!(person instanceof Coceso.Models.SlimPerson) || !self.id || !person.id) {
      return;
    }

    if (ko.utils.arrayFirst(self.crew(), function(item) {
      return (item.id === person.id);
    }) === null) {
      Coceso.Ajax.save({unit_id: self.id, person_id: person.id}, "unit/assignPerson.json", function() {
        self.crew.push(person);
      });
    }
  };

  this.removePerson = function() {
    var person = this;
    if (!(person instanceof Coceso.Models.SlimPerson) || !self.id || !person.id) {
      return;
    }

    if (ko.utils.arrayFirst(self.crew(), function(item) {
      return (item.id === person.id);
    }) !== null) {
      Coceso.Ajax.save({unit_id: self.id, person_id: person.id}, "unit/removePerson.json", function() {
        self.crew.remove(function(item) {
          return (item.id === person.id);
        });
      });
    }
  };
};

/**
 * Edit concern properties
 *
 * @constructor
 */
Coceso.Models.EditableConcern = function() {
  var self = this;

  this.id = null;
  this.name = ko.observable("").extend({observeChanges: {server: ""}});
  this.pax = ko.observable(0).extend({integer: 0, observeChanges: {server: 0}});
  this.info = ko.observable("").extend({observeChanges: {server: ""}});

  this.form = ko.observableArray([this.name, this.pax, this.info]).extend({arrayChanges: {}});

  Coceso.Helpers.initErrorHandling(this);

  this.set = function(data) {
    self.id = data.id;
    self.name.server(data.name || "");
    self.pax.server(data.pax || 0);
    self.info.server(data.info || "");
    self.error(false);

    if (self.pax() === null) {
      self.pax(self.pax.server);
    }
  };

  this.load = function() {
    $.getJSON(Coceso.Conf.jsonBase + "concern/get.json", function(data, status) {
      if (status !== "notmodified" && data) {
        self.set(data);
      }
    });
  };

  this.load();

  this.save = function() {
    Coceso.Ajax.save(JSON.stringify({
      id: self.id,
      name: self.name(),
      pax: self.pax(),
      info: self.info()
    }), "concern/update.json", self.load, self.saveError, self.httpError);
  };
};

/**
 * Batch create units
 *
 * @param {Coceso.ViewModels.Edit} rootModel The parent Viewmodel (for reloading)
 * @constructor
 */
Coceso.Models.BatchUnit = function(rootModel) {
  var self = this;

  this.call = ko.observable("");
  this.from = ko.observable(null).extend({integer: 0});
  this.to = ko.observable(null).extend({integer: 0});
  this.doc = ko.observable(false).extend({"boolean": true});
  this.vehicle = ko.observable(false).extend({"boolean": true});
  this.portable = ko.observable(false).extend({"boolean": true});
  this.home = ko.observable("");

  Coceso.Helpers.initErrorHandling(this);

  this.enable = ko.computed(function() {
    return (this.call() && this.from() !== null && this.to() !== null && this.from() <= this.to());
  }, this);

  this.save = function() {
    Coceso.Ajax.save(JSON.stringify({
      call: this.call(),
      from: this.from(),
      to: this.to(),
      withDoc: this.doc(),
      transportVehicle: this.vehicle(),
      portable: this.portable(),
      home: {info: this.home()}
    }), "unit/createBatch.json", function() {
      self.call("");
      self.from(null);
      self.to(null);
      self.doc(false);
      self.vehicle(false);
      self.portable(false);
      self.home("");
      self.error(false);
      rootModel.load();
    }, this.saveError, this.httpError);
  };
};

/**
 * Person model for adding crew
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.SlimPerson = function(data) {
  this.id = data.id;
  this.fullname = data.sur_name + " " + data.given_name;
  this.dnr = data.dNr;
};

/**
 * Unit model for ordering
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.SlimUnit = function(data) {
  this.id = data.id;
  this.ordering = data.ordering;
  this.call = data.call;
};

/**
 * Container model
 *
 * @constructor
 * @param {Object} data
 * @param {Coceso.ViewModels.Container} rootModel
 */
Coceso.Models.Container = function(data, rootModel) {
  var self = this;

  this.id = data.id;
  this.name = ko.observable(data.name);
  this.ordering = data.ordering;
  this.head = data.head;

  this.selected = ko.observable(false).extend({"boolean": true});

  this.subContainer = ko.observableArray($.map(data.subContainer, function(item) {
    return new Coceso.Models.Container(item, rootModel);
  }));
  this.units = ko.observableArray($.map(data.units, function(item) {
    return new Coceso.Models.SlimUnit(item);
  }));

  this.subContainer.model = this;
  this.units.model = this;

  this.save = function() {
    Coceso.Ajax.save(JSON.stringify({
      id: self.id,
      name: self.name(),
      head: self.head,
      ordering: self.ordering
    }), "unitContainer/updateContainer.json", function(response) {
      if (response.id) {
        self.id = response.id;
      }
    }, rootModel.load, rootModel.load);
  };

  this.update = function() {
    this.save();
    this.selected.unset();
  };

  this.remove = function() {
    this.ordering = -2;
    this.save();
    rootModel.load();
  };

  this.add = function() {
    var newcont = new Coceso.Models.Container({
      id: null,
      name: "New",
      units: [],
      subContainer: [],
      ordering: this.subContainer().length ? this.subContainer()[this.subContainer().length - 1].ordering + 10.0 : 10.0,
      head: this.id
    }, rootModel);
    this.subContainer.push(newcont);

    newcont.save();
    newcont.selected.set();
  };
};

/**
 * Person model
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.Person = function(data) {
  var self = this;
  data = data || {};

  this.id = data.id;
  this.givenname = ko.observable("");
  this.surname = ko.observable("");
  this.dnr = ko.observable(null);
  this.contact = ko.observable("");
  this.username = ko.observable("");
  this.allowlogin = ko.observable(false);

  // Authorities
  this.authorities = ko.observableArray([]);

  this.isOperator = ko.observable(false);

  this.fullname = ko.computed(function() {
    return this.surname() + " " + this.givenname();
  }, this);

  this.setData = function(data) {
    self.givenname(data.given_name || "");
    self.surname(data.sur_name || "");
    self.dnr(data.dNr || null);
    self.contact(data.contact || "");

    self.username(data.username || "");
    self.allowlogin(data.allowLogin || false);
    self.authorities(data.internalAuthorities || []);

    self.isOperator(typeof data.username !== "undefined" && data.username !== null);
  };

  this.setData(data);

  this.edit = function() {
    Coceso.rootModel.showForm(this);
  };
};

/**
 * Editable Person model
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.EditablePerson = function(data) {
  var self = this;
  data = data || {};

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
   * @returns {Coceso.Models.Person}
   */
  this.model = ko.observable(null);

  //Call parent constructors
  Coceso.Models.Person.call(this, {id: this.idObs()});

  //Initialize change detection
  this.givenname.extend({observeChanges: {}});
  this.surname.extend({observeChanges: {}});
  this.dnr = this.dnr.extend({integer: 0, observeChanges: {}});
  this.contact.extend({observeChanges: {}});
  this.username.extend({observeChanges: {}});
  this.allowlogin = this.allowlogin.extend({"boolean": true, observeChanges: {}});
  this.password = ko.observable("").extend({observeChanges: {server: ""}});
  this.password2 = ko.observable("").extend({
    observeChanges: {
      server: "",
      validate: function() {
        return (this() === self.password());
      }
    }
  });

  // Authorities
  this.authorities.orig = ko.observableArray([]);
  this.authorities.changed = ko.computed(function() {
    var a = this(), b = this.orig();
    return (a.length !== b.length || $(a).not(b).length !== 0 || $(b).not(a).length !== 0);
  }, this.authorities);
  this.authorities.reset = function() {
    self.authorities(self.authorities.orig());
  };

  this.form = ko.observableArray([
    this.givenname, this.surname, this.dnr, this.contact, this.username, this.allowlogin, this.authorities, this.password, this.password2
  ]).extend({arrayChanges: true});

  // Error Handling
  Coceso.Helpers.initErrorHandling(this);

  /**
   * "Virtual" computed observable:
   * Serves as callback on changing the id or the list of models
   *
   * @function
   * @type ko.computed
   * @returns {void}
   */
  this.modelChange = ko.computed(function() {
    var newModel = Coceso.Data.getPerson(this.idObs()),
        oldModel = this.model.peek();

    if (newModel === null) {
      if (oldModel === null) {
        //No model exists (not loaded yet or empty form), so create a dummy one
        this.model(new Coceso.Models.Person());
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
    this.givenname.server(this.model().givenname);
    this.surname.server(this.model().surname);
    this.dnr.server(this.model().dnr);
    this.contact.server(this.model().contact);
    this.username.server(this.model().username);
    this.allowlogin.server(this.model().allowlogin);
    this.authorities(this.model() && this.model().authorities() || []);
    this.authorities.orig(this.authorities.peek());
  }, this);

  this.fullname = ko.computed(function() {
    return this.surname.orig() + " " + this.givenname.orig();
  }, this);

  this.isOperator = ko.computed(function() {
    return this.model() && this.model().isOperator();
  }, this);

  this.save = function() {
    Coceso.Ajax.save(JSON.stringify({
      id: this.idObs(),
      given_name: this.givenname(),
      sur_name: this.surname(),
      dNr: this.dnr(),
      contact: this.contact(),
      username: this.username() || this.isOperator() ? this.username() : null,
      allowLogin: this.allowlogin(),
      internalAuthorities: this.authorities()
    }), "person/update.json", function(response) {
      self.error(false);
      if (response.id && response.id !== self.idObs()) {
        self.idObs(response.id);
      }

      if (self.password.changed() && self.idObs()) {
        Coceso.Ajax.save(JSON.stringify({
          id: self.idObs(),
          username: self.username(),
          password: self.password()
        }), "person/setTemporaryPassword.json", function() {
          self.password("");
          self.password2("");
        }, self.saveError, self.httpError);
      }
    }, this.saveError, this.httpError);
  };
};

/**
 * Model for the home view
 *
 * @constructor
 * @param {Integer} error
 */
Coceso.ViewModels.Home = function(error) {
  var self = this;

  // Concern locking
  this.locked = ko.observable(Coceso.Lock.isLocked());
  $(window).on("storage", function(e) {
    if (e.originalEvent.key === "locks") {
      self.locked(Coceso.Lock.isLocked());
    }
  });

  this.forceUnlock = function() {
    delete localStorage.locks;
    this.locked(false);
  };

  // Active concern
  var concernId = parseInt($.cookie("concern"));
  if (!concernId || isNaN(concernId)) {
    concernId = null;
  }
  localStorage.concern = concernId || "";
  this.concernId = ko.observable(concernId);
  $(window).on("storage", function(e) {
    if (e.originalEvent.key === "concern") {
      var newId = parseInt(e.originalEvent.newValue);
      if (!newId || isNaN(newId)) {
        newId = null;
      }
      self.concernId(newId);
    }
  });

  // Concern lists
  this.concerns = ko.observableArray([]);
  this.open = this.concerns.extend({list: {filter: {closed: false}}});
  this.closed = this.concerns.extend({list: {filter: {closed: true}}});

  this.concernName = ko.computed(function() {
    var id = this.concernId();
    if (!id) {
      return "";
    }
    var concern = ko.utils.arrayFirst(this.concerns(), function(item) {
      return item.id === id;
    });
    return (concern ? concern.name : "");
  }, this);

  // Model for the create form
  this.create = new Coceso.Models.CreateConcern(this);

  // Load concern lists
  this.load = function() {
    $.getJSON(Coceso.Conf.jsonBase + "concern/getAll.json", function(data, status) {
      if (status !== "notmodified") {
        self.concerns($.map(data, function(item) {
          return new Coceso.Models.Concern(item, self);
        }));
      }
    });
  };
  this.load();

  // Error Handling
  Coceso.Helpers.initErrorHandling(this, error, this.load);
};

/**
 * Model for the edit concern view
 *
 * @constructor
 */
Coceso.ViewModels.Edit = function() {
  Coceso.initAutocomplete();

  this.units = new Coceso.ViewModels.EditUnits(this);
  this.container = new Coceso.ViewModels.Container();
  this.concern = new Coceso.Models.EditableConcern();
  this.batch = new Coceso.Models.BatchUnit(this);

  this.load = function() {
    this.units.load();
    this.container.load();
  };

};

/**
 * ViewModel for the edit unit view
 *
 * @param {Coceso.ViewModels.Edit} rootModel The parent Viewmodel (for reloading)
 * @constructor
 */
Coceso.ViewModels.EditUnits = function(rootModel) {
  var self = this;

  this.units = ko.observableArray([]);
  this.newUnit = ko.observable(new Coceso.Models.EditableUnit(null, this));

  // Crew editing
  this.edit = ko.observable(null);

  // Person filtering
  this.filter = ko.observable();
  this.regex = ko.computed(function() {
    var filter = this.filter();
    if (!filter) {
      return [];
    }
    return $.map(filter.split(" "), function(item) {
      return new RegExp(RegExp.escape(item), "i");
    });
  }, this);

  // List of persons
  this.persons = ko.observableArray([]);
  this.filtered = this.persons.extend({
    list: {
      filter: {
        regex: function(item) {
          var regex = self.regex(), i;
          for (i = 0; i < regex.length; i++) {
            if (!regex[i].test(item.dnr) && !regex[i].test(item.fullname)) {
              return false;
            }
          }
          return true;
        }
      },
      sort: true,
      field: "sortdnr"
    }
  });

  // Show the edit form
  this.showForm = function(unit) {
    this.edit(unit);
    $("#edit_crew").modal("show");
  };

  // Load all persons
  this.loadPersons = function() {
    $.getJSON(Coceso.Conf.jsonBase + "person/getAll.json", function(data, status) {
      if (status !== "notmodified") {
        self.persons($.map(data, function(item) {
          return new Coceso.Models.SlimPerson(item, self);
        }));
      }
    });
  };

  this.load = function() {
    $.getJSON(Coceso.Conf.jsonBase + "unit/getAllWithLocked.json", function(data, status) {
      if (status !== "notmodified") {
        self.units($.map(data, function(item) {
          return new Coceso.Models.EditableUnit(item, self);
        }));
      }
    });
  };

  this.load();
  this.loadPersons();

  //Error handling for the unit list
  Coceso.Helpers.initErrorHandling(this, false, this.load);

  // Remove Unit
  this.remove = function() {
    var unit = this;
    if (!(unit instanceof Coceso.Models.EditableUnit) || !unit.id || unit.locked) {
      return;
    }

    Coceso.Ajax.save({id: unit.id}, "unit/remove.json", function() {
      self.error(false);
      self.units.remove(unit);
    }, self.saveError, self.httpError);
  };

  // Create new Unit
  this.create = function() {
    var unit = self.newUnit();

    unit.save(function() {
      self.units.push(unit);
      self.newUnit(new Coceso.Models.EditableUnit(null, self));
    });
  };

  this.loadContainer = function() {
    rootModel.container.load();
  };
};

/**
 * Viewmodel for editing containers
 *
 * @constructor
 */
Coceso.ViewModels.Container = function() {
  var self = this;

  this.spare = ko.observableArray([]);
  this.top = ko.observable(new Coceso.Models.Container({id: null, name: "Loading...", units: [], subContainer: [], ordering: -1, head: null}, this));

  this.load = function() {
    $.getJSON(Coceso.Conf.jsonBase + "unitContainer/get.json", function(data, status) {
      if (status !== "notmodified") {
        self.top(new Coceso.Models.Container(data, self));
      }
    });
    $.getJSON(Coceso.Conf.jsonBase + "unitContainer/getSpare.json", function(data, status) {
      if (status !== "notmodified") {
        self.spare($.map(data, function(item) {
          return new Coceso.Models.SlimUnit(item);
        }));
      }
    });
  };

  this.drop = function(data) {
    if (!(data.targetParent.model instanceof Coceso.Models.Container && data.item instanceof Coceso.Models.Container)) {
      return;
    }

    data.item.head = data.targetParent.model.id;
    data.item.ordering = Coceso.Helpers.computeOrdering(data.targetParent(), data.targetIndex);
    data.item.save();
  };

  this.dropUnit = function(data) {
    if (!(data.item instanceof Coceso.Models.SlimUnit)) {
      return;
    }

    var container_id;
    if (data.targetParent === self.spare) {
      data.item.ordering = -2;
      container_id = 0;
    } else if (data.targetParent.model instanceof Coceso.Models.Container) {
      data.item.ordering = Coceso.Helpers.computeOrdering(data.targetParent(), data.targetIndex);
      container_id = data.targetParent.model.id;
    } else {
      return;
    }

    Coceso.Ajax.save({
      container_id: container_id,
      unit_id: data.item.id,
      ordering: data.item.ordering
    }, "unitContainer/updateUnit.json", null, self.load, self.load);
  };

  this.load();
};

/**
 * Viewmodel for the person list
 *
 * @constructor
 */
Coceso.ViewModels.Person = function() {
  Coceso.rootModel = this;
  var self = this;

  // Error Handling
  Coceso.Helpers.initErrorHandling(this);

  // Filtering
  this.filter = ko.observable();
  this.regex = ko.computed(function() {
    var filter = this.filter();
    if (!filter) {
      return [];
    }
    return $.map(filter.split(" "), function(item) {
      return new RegExp(RegExp.escape(item), "i");
    });
  }, this);

  // AJAX options
  Coceso.Ajax.loadOptions = {
    persons: {
      url: "person/getAll.json",
      model: "Person",
      interval: false,
      id: null
    }
  };

  // List of persons
  Coceso.Data = {
    getPerson: function(id) {
      if (Coceso.Data.persons.models()[id] instanceof Coceso.Models.Person) {
        return Coceso.Data.persons.models()[id];
      }
      return null;
    },
    persons: {models: ko.observable({})}
  };

  this.persons = ko.computed(function() {
    return $.map(Coceso.Data.persons.models(), function(v) {
      return v;
    });
  });
  this.filtered = this.persons.extend({
    list: {
      filter: {
        regex: function(item) {
          var regex = self.regex(), i;
          for (i = 0; i < regex.length; i++) {
            if (!regex[i].test(item.dnr()) && !regex[i].test(item.fullname()) && !regex[i].test(item.username())) {
              return false;
            }
          }
          return true;
        }
      },
      sort: true,
      field: "dnr"
    }
  });

  // Person to edit
  this.edit = new Coceso.Models.EditablePerson();

  // Show the edit form
  this.showForm = function(person) {
    this.edit.idObs(person.id);
    this.edit.form.reset();
    $("#edit_person").modal("show");
  };

  // Show an empty edit form
  this.create = function() {
    this.showForm({id: null});
  };

  this.csv = ko.observable(null);

  this.upload = function() {
    Coceso.Ajax.save(this.csv(), "person/uploadPerson.json", function() {
      self.csv(null);
      self.error(false);
      Coceso.Ajax.load("persons");
    }, self.saveError, self.httpError, "text/csv");
  };

  Coceso.Ajax.load("persons");
};
