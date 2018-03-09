/**
 * CoCeSo
 * Client JS - home/viewmodel
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
 * @module {Class} home/concern
 * @param {module:knockout} ko
 * @param {module:data/save} ajaxSave
 * @param {module:js-cookie} Cookies
 */
define(["knockout", "data/save", "js-cookie", "ko/extenders/boolean"],
  function(ko, ajaxSave, Cookies) {
    "use strict";

    /**
     * Single Concern
     *
     * @alias module:home/concern
     * @constructor
     * @param {Object} data
     * @param {module:home/viewmodel} rootModel
     */
    var Concern = function(data, rootModel) {
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

        ajaxSave({concern_id: this.id}, "setActiveConcern.json", function() {
          self.id ? Cookies.set("concern", self.id, {path: ""}) : Cookies.remove("concern", {path: ""});
          rootModel.error(false);
          rootModel.concernId(self.id);
          localStorage.concern = self.id || "";
        }, rootModel.saveError, rootModel.httpError);
      };

      this.close = function() {
        ajaxSave({concern_id: this.id}, "concern/close.json", function() {
          rootModel.error(false);
          self.closed(true);
          if (self.isActive()) {
            Cookies.remove("concern", {path: ""});
            rootModel.concernId(null);
            localStorage.concern = "";
          }
        }, rootModel.saveError, rootModel.httpError);
      };

      this.reopen = function() {
        ajaxSave({concern_id: this.id}, "concern/reopen.json", function() {
          rootModel.error(false);
          self.closed(false);
        }, rootModel.saveError, rootModel.httpError);
      };
    };

    return Concern;
  }
);
