/**
 * CoCeSo
 * Client JS - edit_user/viewmodel
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module {Class} edit_user/viewmodel
 * @param {module:knockout} ko
 * @param {module:data/save} ajaxSave
 * @param {module:utils/errorhandling} initErrorHandling
 */
define(["knockout", "data/save", "utils/errorhandling", "ko/bindings/point", "ko/extenders/boolean", "ko/extenders/integer"],
  function(ko, ajaxSave, initErrorHandling) {
    "use strict";

    /**
     * Batch create units
     *
     * @constructor
     */
    var Batch = function() {
      var self = this;

      this.call = ko.observable("");
      this.from = ko.observable(null).extend({integer: 0});
      this.to = ko.observable(null).extend({integer: 0});
      this.doc = ko.observable(false).extend({"boolean": true});
      this.vehicle = ko.observable(false).extend({"boolean": true});
      this.portable = ko.observable(false).extend({"boolean": true});
      this.home = ko.observable("");
      this.saving = ko.observable(false);

      initErrorHandling(this);

      this.enable = ko.computed(function() {
        return (!this.saving() && this.call() && this.from() !== null && this.to() !== null && this.from() <= this.to());
      }, this);

      this.save = function() {
        ajaxSave(JSON.stringify({
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
        }, this.saveError, this.httpError, this.saving);
      };

      this.csv = ko.observable(null);

      this.upload = function() {
        ajaxSave(this.csv(), "unit/upload.json", function() {
          self.csv(null);
          self.error(false);
        }, this.saveError, this.httpError, null, "text/csv");
      };
    };

    return Batch;
  }
);
