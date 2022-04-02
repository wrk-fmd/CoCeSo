/**
 * CoCeSo
 * Client JS - edit/models/editableconcern
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
 * @module {Class} edit/models/editableconcern
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:data/save} ajaxSave
 * @param {module:utils/conf} conf
 * @param {module:utils/errorhandling} initErrorHandling
 * @param {module:utils/i18n} _
 */
define(["jquery", "knockout", "data/save", "utils/conf", "utils/errorhandling", "utils/i18n",
  "ko/extenders/changes", "ko/extenders/form"],
  function($, ko, ajaxSave, conf, initErrorHandling, _) {
    "use strict";

    var defaultOption = _("concern.section") + "...";

    /**
     * Edit concern properties
     *
     * @alias module:edit/models/editableconcern
     * @constructor
     */
    var EditableConcern = function() {
      var self = this;

      this.id = null;
      this.name = ko.observable("").extend({observeChanges: {server: ""}});
      this.info = ko.observable("").extend({observeChanges: {server: ""}});

      this.form = ko.observableArray([this.name, this.info]).extend({form: {}});

      this.sections = ko.observableArray([]);
      this.selectSections = ko.observableArray([{value: "", name: defaultOption}]);
      this.hasSections = ko.computed(function() {
        return this.selectSections().length > 1;
      }, this);
      this.section = ko.observable("");

      initErrorHandling(this);

      this.set = function(data) {
        self.id = data.id;
        self.name.server(data.name || "");
        self.info.server(data.info || "");
        self.sections($.map(data.sections || [], function(name) {
          return {
            name: name,
            remove: function() {
              ajaxSave({section: name, concern: self.id}, "concern/removeSection",
                self.load, self.saveError, self.httpError);
            }
          };
        }));

        // Dirty fix, because knockout doesn't allow specifying the default options value in select
        self.selectSections($.merge([{value: "", name: defaultOption}], $.map(data.sections || [], function(section) {
          return {value: section, name: section};
        })));

        self.error(false);
      };

      this.load = function() {
        $.getJSON(conf.get("jsonBase") + "concern/get", function(data, status) {
          if (status !== "notmodified" && data) {
            self.set(data);
          }
        });
      };

      this.load();

      this.save = function() {
        ajaxSave(JSON.stringify({
          id: self.id,
          name: self.name(),
          info: self.info()
        }), "concern/update", self.load, self.saveError, self.httpError, self.form.saving);
      };

      this.reloadingRadio = ko.observable(false);

      this.reloadRadio = function() {
        ajaxSave(null, "radio/reloadPorts", null, this.saveError, this.httpError, this.reloadingRadio);
      };

      this.addingSection = ko.observable(false);

      this.enableAddSection = ko.computed(function() {
        return !this.addingSection() && this.section().trim() !== "";
      }, this);

      this.addSection = function() {
        var name = this.section().trim();
        if (name !== "") {
          ajaxSave({section: name, concern: this.id}, "concern/addSection", function() {
            self.load();
            self.section("");
          }, this.saveError, this.httpError, this.addingSection);
        }
      };
    };

    return EditableConcern;
  }
);
