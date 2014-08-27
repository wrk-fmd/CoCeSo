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
 * Contains helpers
 *
 * @type Object
 */
Coceso.Helpers = {
  computeOrdering: function(array, index) {
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
  },
  errorText: function() {
    var error = this.error();

    if (error >= 1 && error <= 8) {
      return _("label.error." + error);
    }

    return "";
  }
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
  this.closed = ko.observable(data.closed || false).extend({boolean: true});

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
    Coceso.Ajax.save({concern_id: this.id}, "closeConcern.json", function() {
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
    Coceso.Ajax.save({concern_id: this.id}, "reopenConcern.json", function() {
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

  this.dependencies = ko.observableArray([this.name]).extend({arrayChanges: {}});
  this.localChange = this.dependencies.localChange;

  this.save = function() {
    if (!this.localChange()) {
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
 * @param {Coceso.ViewModels.Concern} rootModel
 */
Coceso.Models.EditableUnit = function(data, rootModel) {
  var self = this;
  data = data || {};

  this.id = data.id || null;
  this.call = ko.observable(data.call || "").extend({observeChanges: {server: data.call || ""}});
  this.ani = ko.observable(data.ani || "").extend({observeChanges: {server: data.ani || ""}});
  this.doc = ko.observable(data.withDoc || false).extend({boolean: {}, observeChanges: {server: data.withDoc || false}});
  this.vehicle = ko.observable(data.transportVehicle || false).extend({boolean: {}, observeChanges: {server: data.transportVehicle || false}});
  this.portable = ko.observable(data.portable || false).extend({boolean: {}, observeChanges: {server: data.portable || false}});
  this.info = ko.observable(data.info || "").extend({observeChanges: {server: data.info || ""}});
  this.home = ko.observable(data.home && data.home.info ? data.home.info : "").extend({observeChanges: {server: data.home && data.home.info ? data.home.info : ""}});
  this.locked = data.locked;

  this.dependencies = ko.observableArray([this.call, this.ani, this.doc, this.vehicle,
    this.portable, this.info, this.home]).extend({arrayChanges: {}});
  this.localChange = this.dependencies.localChange;

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
      if (success instanceof Function) {
        success();
      }
      if (response.unit_id) {
        self.id = response.unit_id;
      }
      self.call.setServer(self.call());
      self.ani.setServer(self.ani());
      self.doc.setServer(self.doc());
      self.vehicle.setServer(self.vehicle());
      self.portable.setServer(self.portable());
      self.info.setServer(self.info());
      self.home.setServer(self.home());
    }, rootModel.saveError, rootModel.httpError);
  };
};

/**
 * Edit concern properties
 *
 * @constructor
 * @param {Object} data
 */
Coceso.Models.EditableConcern = function(data) {
  var self = this;

  this.id = null;
  this.name = ko.observable("").extend({observeChanges: {server: ""}});
  this.pax = ko.observable(0).extend({integer: 0, observeChanges: {server: 0}});
  this.info = ko.observable("").extend({observeChanges: {server: ""}});

  this.dependencies = ko.observableArray([this.name, this.pax, this.info]).extend({arrayChanges: {}});
  this.localChange = this.dependencies.localChange;

  this.error = ko.observable(false);
  this.errorText = ko.computed(Coceso.Helpers.errorText, this);

  this.set = function(data) {
    self.id = data.id;
    self.name.setServer(data.name || "");
    self.pax.setServer(data.pax || 0);
    self.info.setServer(data.info || "");
    self.error(false);

    if (self.pax() === null) {
      self.pax(self.pax.server);
    }
  };

  if (data) {
    this.set(data);
  }

  this.load = function() {
    $.getJSON(Coceso.Conf.jsonBase + "concern/get.json", function(data, status) {
      if (status !== "notmodified" && data) {
        self.set(data);
      }
    });
  };

  this.save = function() {
    Coceso.Ajax.save(
        JSON.stringify({
          id: self.id,
          name: self.name(),
          pax: self.pax(),
          info: self.info()
        }),
        "concern/update.json",
        self.load,
        function(response) {
          self.error(response.error || 8);
        },
        function() {
          self.error(7);
        }
    );
  };
};

/**
 * Batch create units
 *
 * @param {Coceso.ViewModels.Concern} parent The parent Viewmodel (for reloading)
 * @constructor
 */
Coceso.Models.BatchUnit = function(parent) {
  var self = this;

  this.call = ko.observable("");
  this.from = ko.observable(null).extend({integer: 0});
  this.to = ko.observable(null).extend({integer: 0});
  this.doc = ko.observable(false).extend({boolean: true});
  this.vehicle = ko.observable(false).extend({boolean: true});
  this.portable = ko.observable(false).extend({boolean: true});
  this.home = ko.observable("");

  this.error = ko.observable(false);
  this.errorText = ko.computed(Coceso.Helpers.errorText, this);

  this.enable = ko.computed(function() {
    return (this.call() && this.from() !== null && this.to() !== null && this.from() <= this.to());
  }, this);

  this.save = function() {
    Coceso.Ajax.save(
        JSON.stringify({
          call: this.call(),
          from: this.from(),
          to: this.to(),
          withDoc: this.doc(),
          transportVehicle: this.vehicle(),
          portable: this.portable(),
          home: {info: this.home()}
        }),
        "unit/createUnitBatch.json",
        function() {
          self.call("");
          self.from(null);
          self.to(null);
          self.doc(false);
          self.vehicle(false);
          self.portable(false);
          self.home("");
          self.error(false);
          parent.load();
        },
        function(response) {
          self.error(response.error || 8);
        },
        function() {
          self.error(7);
        }
    );
  };
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
  this.id = data.id;
  this.name = ko.observable(data.name);
  this.ordering = data.ordering;
  this.head = data.head;

  this.selected = ko.observable(false).extend({boolean: true});

  this.subContainer = ko.observableArray($.map(data.subContainer, function(item) {
    return new Coceso.Models.Container(item, rootModel);
  }));
  this.units = ko.observableArray($.map(data.units, function(item) {
    return new Coceso.Models.SlimUnit(item);
  }));

  this.subContainer.model = this;
  this.units.model = this;

  this.save = function() {
    var cont = this;
    Coceso.Ajax.save(JSON.stringify({
      id: this.id,
      name: this.name(),
      head: this.head,
      ordering: this.ordering
    }), "unitContainer/updateContainer.json", function(response) {
      if (response.id) {
        cont.id = response.id;
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

    newcont.save.call(newcont);
    newcont.selected.set();
  };

  this.drop = function(data) {
    var parentModel = data.targetParent.model;

    if (!(parentModel instanceof Coceso.Models.Container && data.item instanceof Coceso.Models.Container)) {
      return;
    }

    data.item.head = parentModel.id;
    data.item.ordering = Coceso.Helpers.computeOrdering(data.targetParent(), data.targetIndex);
    data.item.save.call(data.item);
  };

  this.dropUnit = function(data) {
    var parentModel = data.targetParent.model;

    if (!(parentModel instanceof Coceso.Models.Container && data.item instanceof Coceso.Models.SlimUnit)) {
      return;
    }

    data.item.ordering = Coceso.Helpers.computeOrdering(data.targetParent(), data.targetIndex);

    Coceso.Ajax.save({
      container_id: parentModel.id,
      unit_id: data.item.id,
      ordering: data.item.ordering
    }, "unitContainer/updateUnit.json", null, rootModel.load, rootModel.load);
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

  // Error Handling
  this.error = ko.observable(error || false);
  this.errorText = ko.computed(Coceso.Helpers.errorText, this);

  this.saveError = function(response) {
    self.error(response.error || 8);
    self.load();
  };

  this.httpError = function() {
    self.error(7);
    self.load();
  };

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

  // Concern lists
  this.concerns = ko.observableArray([]);
  this.open = this.concerns.extend({filtered: {filters: {filter: {closed: false}}}});
  this.closed = this.concerns.extend({filtered: {filters: {filter: {closed: true}}}});

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
};

/**
 * Model for the edit concern view
 *
 * @constructor
 */
Coceso.ViewModels.Concern = function() {
  var self = this;

  this.units = ko.observableArray([]);
  this.newUnit = ko.observable(new Coceso.Models.EditableUnit(null, this));
  this.concern = new Coceso.Models.EditableConcern();
  this.batch = new Coceso.Models.BatchUnit(this);

  //Error handling for the unit list
  this.error = ko.observable(false);
  this.errorText = ko.computed(Coceso.Helpers.errorText, this);

  this.saveError = function(response) {
    self.error(response.error || 8);
    self.load();
  };

  this.httpError = function() {
    self.error(7);
    self.load();
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
  this.concern.load();

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
      self.newUnit(new Coceso.Models.EditableUnit());
    });
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

  this.dropUnit = function(data) {
    if (data.targetParent !== self.spare || !(data.item instanceof Coceso.Models.SlimUnit)) {
      return;
    }

    data.item.ordering = Coceso.Helpers.computeOrdering(data.targetParent(), data.targetIndex);

    Coceso.Ajax.save({
      container_id: 0,
      unit_id: data.item.id,
      ordering: data.item.ordering
    }, "unitContainer/updateUnit.json", null, self.load, self.load);
  };

  this.load();
};

