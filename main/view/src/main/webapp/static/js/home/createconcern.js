/**
 * CoCeSo
 * Client JS - home/createconcern
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
 * @module {Class} home/createconcern
 * @param {module:knockout} ko
 * @param {module:data/save} ajaxSave
 */
define(["knockout", "data/save", "ko/extenders/changes"], function(ko, ajaxSave) {
  "use strict";

  /**
   * Create concern
   *
   * @alias module:home/createconcern
   * @constructor
   * @param {module:home/viewmodel} rootModel
   */
  var CreateConcern = function(rootModel) {
    var self = this;

    this.name = ko.observable("").extend({observeChanges: {server: ""}});

    this.save = function() {
      if (!this.name.changed()) {
        return false;
      }

      ajaxSave(JSON.stringify({
        name: self.name()
      }), "concern/update", function() {
        rootModel.error(false);
        self.name("");
        rootModel.load();
      }, rootModel.saveError, rootModel.httpError);
    };
  };

  return CreateConcern;
});
