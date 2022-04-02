/**
 * CoCeSo
 * Client JS - edit/models/editableunit
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Function */

/**
 * @module {Class} edit/models/editableunit
 * @param {jquery} $
 * @param {module:knockout} ko
 * @param {module:edit/models/person} Person
 * @param {module:data/save} ajaxSave
 * @param {module:utils/conf} conf
 * @param {module:utils/destroy} destroy
 */
define(["jquery", "knockout", "./person", "data/save", "utils/conf", "utils/destroy",
  "ko/extenders/changes", "ko/extenders/form"],
  function($, ko, Person, ajaxSave, conf, destroy) {
    "use strict";

    /**
     * Edit unit properties
     *
     * @alias module:edit/models/editableunit
     * @constructor
     * @param {Object} data
     */
    var EditableUnit = function(data) {
      var self = this;
      data = data || {};

      this.id = data.id || null;
      this.call = ko.observable("").extend({observeChanges: {server: ""}});
      this.ani = ko.observable("").extend({observeChanges: {server: ""}});
      this.doc = ko.observable(false).extend({"boolean": true, observeChanges: {server: false}});
      this.vehicle = ko.observable(false).extend({"boolean": true, observeChanges: {server: false}});
      this.portable = ko.observable(false).extend({"boolean": true, observeChanges: {server: false}});
      this.type = ko.observable("").extend({observeChanges: {server: ""}});
      this.info = ko.observable("").extend({observeChanges: {server: ""}});
      this.home = ko.observable("").extend({observeChanges: {server: ""}});
      this.section = ko.observable("").extend({observeChanges: {server: ""}});
      this.locked = data.locked;

      this.crew = ko.observableArray(data.crew ? $.map(data.crew, function(item) {
        return new Person(item);
      }) : []);

      this.form = ko.observableArray([this.call, this.ani, this.doc, this.vehicle,
        this.portable, this.type, this.info, this.home, this.section]).extend({form: {}});

      this.setData = function(data) {
        this.call.server(data.call || "");
        this.ani.server(data.ani || "");
        this.doc.server(data.withDoc || false);
        this.vehicle.server(data.transportVehicle || false);
        this.portable.server(data.portable || false);
        this.type.server(data.type || "");
        this.info.server(data.info || "");
        this.home.server(data.home && data.home.info ? data.home.info : "");
        this.section.server(data.section || "");
        this.locked = data.locked === null ? true : data.locked;
      };

      this.setData(data);

      this.editCrew = function() {
        conf.get("unitsModel").showForm(this);
      };

      this.save = function(success) {
        var root = conf.get("unitsModel");

        ajaxSave(JSON.stringify({
          id: self.id,
          call: self.call(),
          ani: self.ani(),
          withDoc: self.doc(),
          transportVehicle: self.vehicle(),
          portable: self.portable(),
          type: self.type() || null,
          info: self.info(),
          home: self.home(),
          section: self.section() === "" ? null : self.section()
        }), "unit/updateFull", function() {
          root.error(false);
          if (success instanceof Function) {
            success();
          }
        }, root.saveError, root.httpError, this.form.saving);
      };

      this.assignPerson = function() {
        var person = this;
        if (!(person instanceof Person) || !self.id || !person.id) {
          return;
        }

        var alreadyAssignedPerson = ko.utils.arrayFirst(self.crew(), function(item) {
          return (item.id === person.id);
        });
        if (alreadyAssignedPerson === null || alreadyAssignedPerson === undefined) {
          ajaxSave({unit_id: self.id, person_id: person.id}, "unit/assignPerson", function() {
            self.crew.push(person);
          });
        }
      };

      this.removePerson = function() {
        var person = this;
        if (!(person instanceof Person) || !self.id || !person.id) {
          return;
        }

        var assignedPerson = ko.utils.arrayFirst(self.crew(), function(item) {
          return (item.id === person.id);
        });
        if (assignedPerson !== null && assignedPerson !== undefined) {
          ajaxSave({unit_id: self.id, person_id: person.id}, "unit/removePerson", function() {
            self.crew.remove(function(item) {
              return (item.id === person.id);
            });
          });
        }
      };
    };
    EditableUnit.prototype = Object.create({}, /** @lends EditableUnit.prototype */ {
      /**
       * Destroy the object
       *
       * @function
       */
      destroy: {
        value: function() {
          destroy(this);
        }
      }
    });

    return EditableUnit;
  }
);
