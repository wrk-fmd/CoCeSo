/**
 * CoCeSo
 * Client JS - edit/viewmodels/units
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module {Class} edit/viewmodels/units
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:edit/models/editableunit} EditableUnit
 * @param {module:edit/models/person} Person
 * @param {module:data/load} load
 * @param {module:data/save} ajaxSave
 * @param {module:data/store/units} store
 * @param {module:utils/conf} conf
 * @param {module:utils/errorhandling} initErrorHandling
 */
define(["jquery", "knockout", "../models/editableunit", "../models/person", "data/load", "data/save",
  "data/store/units", "utils/conf", "utils/errorhandling",
  "bootstrap/modal", "ko/bindings/file", "ko/bindings/point", "ko/extenders/list"],
  function($, ko, EditableUnit, Person, load, ajaxSave, store, conf, initErrorHandling) {
    "use strict";

    load({
      url: "unit/edit",
      stomp: "/topic/unit/edit/{c}",
      model: EditableUnit,
      store: store.models
    });

    /**
     * ViewModel for the edit unit view
     *
     * @alias module:edit/viewmodels/units
     * @constructor
     */
    var EditUnits = function() {
      var self = this;

      conf.set("unitsModel", this);

      this.units = store.list;
      this.newUnit = ko.observable(new EditableUnit(null, this));

      // Crew editing
      this.edit = ko.observable(null);

      // Person filtering
      this.filter = ko.observable();
      this.regex = ko.computed(function() {
        var filterVal = this.filter();
        if (!filterVal) {
          return [];
        }
        return $.map(filterVal.split(" "), function(singleSearchTerm) {
          return new RegExp(RegExp.escape(singleSearchTerm.normalizeAccents()), "i");
        });
      }, this);

      // List of persons
      this.persons = ko.observableArray([]);
      this.filtered = this.persons.extend({
        list: {
          filter: {
            regex: function(personToTest) {
              var regex = self.regex(), i;
              for (i = 0; i < regex.length; i++) {
                if (!regex[i].test(personToTest.personnelId) && !regex[i].test(personToTest.normalizedFullname)) {
                  return false;
                }
              }
              return true;
            }
          },
          sort: true,
          field: "sortpersonnelId"
        }
      });

      // Show the edit form
      this.showForm = function(unit) {
        this.edit(unit);
        $("#edit_crew").modal("show");
      };

      // Load all persons
      this.loadPersons = function() {
        $.getJSON(conf.get("jsonBase") + "user/getAll", function(data, status) {
          if (status !== "notmodified") {
            self.persons($.map(data, function(item) {
              return new Person(item, self);
            }));
          }
        });
      };

      this.loadPersons();

      //Error handling for the unit list
      initErrorHandling(this);

      // Remove Unit
      this.remove = function() {
        var unit = this;
        if (!(unit instanceof EditableUnit) || !unit.id || unit.locked) {
          return;
        }

        ajaxSave({id: unit.id}, "unit/remove", function() {
          self.error(false);
        }, self.saveError, self.httpError);
      };

      // Create new Unit
      this.create = function() {
        var unit = self.newUnit();

        unit.save(function() {
          self.newUnit(new EditableUnit(null, self));
        });
      };
    };

    return EditUnits;
  }
);
