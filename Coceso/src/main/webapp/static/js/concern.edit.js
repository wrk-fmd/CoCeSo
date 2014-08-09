/**
 * CoCeSo
 * Client JS - edit
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
 * @returns {String}
 */
var _ = $.i18n.prop;

/**
 * Object containing the main code
 *
 * @namespace CocesoEdit
 * @type Object
 */
var CocesoEdit = {};

/**
 * Some global settings
 *
 * @type Object
 */
CocesoEdit.Conf = {
  jsonBase: "",
  langBase: "",
  language: "en"
};


/**
 * Contains all the models
 *
 * @namespace CocesoEdit.Models
 * @type Object
 */
CocesoEdit.Models = {};

/**
 * Edit unit properties
 *
 * @constructor
 * @param {Object} data
 */
CocesoEdit.Models.EditableUnit = function(data) {
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
  this.saveError = ko.observable(false);

  this.save = function(success) {
    $.ajax({
      type: "POST",
      url: CocesoEdit.Conf.jsonBase + "unit/updateFull",
      dataType: "json",
      contentType: "application/json",
      processData: false,
      data: JSON.stringify({
        id: self.id,
        call: self.call(),
        ani: self.ani(),
        withDoc: self.doc(),
        transportVehicle: self.vehicle(),
        portable: self.portable(),
        info: self.info(),
        home: {info: self.home()}
      }),
      success: function(data) {
        if (data.success) {
          self.saveError(false);
          if (success instanceof Function) {
            success();
          }
          if (data.unit_id) {
            self.id = data.unit_id;
          }
          self.call.setServer(self.call());
          self.ani.setServer(self.ani());
          self.doc.setServer(self.doc());
          self.vehicle.setServer(self.vehicle());
          self.portable.setServer(self.portable());
          self.info.setServer(self.info());
          self.home.setServer(self.home());
        } else {
          self.saveError(true);
        }
      }, error: function() {
        self.saveError(true);
      }
    });
  };
};

/**
 * Edit concern properties
 *
 * @constructor
 * @param {Object} data
 */
CocesoEdit.Models.EditableConcern = function(data) {
  var self = this;

  this.id = null;
  this.name = ko.observable("").extend({observeChanges: {server: ""}});
  this.pax = ko.observable(0).extend({integer: 0, observeChanges: {server: 0}});
  this.info = ko.observable("").extend({observeChanges: {server: ""}});

  this.dependencies = ko.observableArray([this.name, this.pax, this.info]).extend({arrayChanges: {}});
  this.localChange = this.dependencies.localChange;
  this.saveError = ko.observable(false);

  this.set = function(data) {
    self.id = data.id;
    self.name.setServer(data.name || "");
    self.pax.setServer(data.pax || 0);
    self.info.setServer(data.info || "");
    self.saveError(false);

    if (self.pax() === null) {
      self.pax(self.pax.server);
    }
  };

  if (data) {
    this.set(data);
  }

  this.load = function() {
    $.getJSON(CocesoEdit.Conf.jsonBase + "concern/get", function(data, status) {
      if (status !== "notmodified" && data) {
        self.set(data);
      }
    });
  };

  this.save = function() {
    $.ajax({
      type: "POST",
      url: CocesoEdit.Conf.jsonBase + "concern/update",
      dataType: "json",
      contentType: "application/json",
      processData: false,
      data: JSON.stringify({
        id: self.id,
        name: self.name(),
        pax: self.pax(),
        info: self.info()
      }),
      success: function(data) {
        if (data.success) {
          self.load();
        } else {
          self.saveError(true);
        }
      },
      error: function() {
        self.saveError(true);
      }
    });
  };
};

/**
 * Batch create units
 *
 * @param {CocesoEdit.ViewModels.Concern} parent The parent Viewmodel (for reloading)
 * @constructor
 */
CocesoEdit.Models.BatchUnit = function(parent) {
  var self = this;

  this.call = ko.observable("");
  this.from = ko.observable(null).extend({integer: 0});
  this.to = ko.observable(null).extend({integer: 0});
  this.doc = ko.observable(false).extend({boolean: true});
  this.vehicle = ko.observable(false).extend({boolean: true});
  this.portable = ko.observable(false).extend({boolean: true});
  this.home = ko.observable("");

  this.saveError = ko.observable(false);

  this.enable = ko.computed(function() {
    return (this.call() && this.from() !== null && this.to() !== null && this.from() <= this.to());
  }, this);

  this.save = function() {
    $.ajax({
      type: "POST",
      url: CocesoEdit.Conf.jsonBase + "unit/createUnitBatch",
      dataType: "json",
      contentType: "application/json",
      processData: false,
      data: JSON.stringify({
        call: self.call(),
        from: self.from(),
        to: self.to(),
        withDoc: self.doc(),
        transportVehicle: self.vehicle(),
        portable: self.portable(),
        home: {info: self.home()}
      }),
      success: function(data) {
        if (data.success) {
          self.call("");
          self.from(null);
          self.to(null);
          self.doc(false);
          self.vehicle(false);
          self.portable(false);
          self.home("");
          self.saveError(false);
          parent.load();
        } else {
          self.saveError(true);
        }
      },
      error: function() {
        self.saveError(true);
      }
    });
  };
};

/**
 * Contains all ViewModels (including baseclasses)
 *
 * @namespace CocesoEdit.ViewModels
 * @type Object
 */
CocesoEdit.ViewModels = {};

/**
 * Model for the edit concern view
 *
 * @constructor
 */
CocesoEdit.ViewModels.Concern = function() {
  var self = this;

  this.units = ko.observableArray([]);
  this.newUnit = ko.observable(new CocesoEdit.Models.EditableUnit());
  this.concern = new CocesoEdit.Models.EditableConcern();
  this.batch = new CocesoEdit.Models.BatchUnit(this);

  this.load = function() {
    $.getJSON(CocesoEdit.Conf.jsonBase + "unit/getAllWithLocked", function(data, status) {
      if (status !== "notmodified") {
        self.units($.map(data, function(item) {
          return new CocesoEdit.Models.EditableUnit(item);
        }));
      }
    });
  };

  this.load();
  this.concern.load();

  // Remove Unit
  this.remove = function() {
    var unit = this;
    if (!(unit instanceof CocesoEdit.Models.EditableUnit) || !unit.id || unit.locked) {
      return;
    }

    $.ajax(CocesoEdit.Conf.jsonBase + "unit/remove", {
      type: "POST",
      dataType: "json",
      contentType: "application/x-www-form-urlencoded",
      data: {id: unit.id},
      success: function() {
        self.units.remove(unit);
      },
      error: function() {
        self.saveError(true);
      }
    });
  };

  // Create new Unit
  this.create = function() {
    var unit = self.newUnit();

    unit.save(function() {
      self.units.push(unit);
      self.newUnit(new CocesoEdit.Models.EditableUnit());
    });
  };
};
