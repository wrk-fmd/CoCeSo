/**
 * CoCeSo
 * Client JS - main/viewmodels/unitdetail
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
 * @module main/viewmodels/unitdetail
 * @param {module:knockout} ko
 * @param {module:main/models/unit} Unit
 * @param {module:data/store/units} store
 * @param {module:utils/i18n} _
 */
define(["knockout", "../models/unit", "data/store/units", "utils/i18n"],
    function(ko, Unit, store, _) {
      "use strict";

      /**
       * Unit details
       *
       * @alias module:main/viewmodels/unitdetail
       * @constructor
       * @param {Integer} id
       */
      var UnitDetail = function(id) {
        /**
         * Used Model (reference)
         *
         * @function
         * @type ko.observable
         * @returns {module:main/models/unit}
         */
        this.model = ko.computed(function() {
          var model = store.get(id);
          return model === null ? new Unit() : model;
        }, this);

        var title = _("unit.details");
        this.dialogTitle = ko.computed(function() {
          if (!this.model()) {
            return title;
          }
          return {dialog: title + ": " + this.model().call, button: this.model().call};
        }, this);

      };
      UnitDetail.prototype = Object.create({}, /** @lends UnitDetail.prototype */ {
        /**
         * Destroy the ViewModel
         *
         * @function
         */
        destroy: {
          value: function() {
            destroy(this);
          }
        }
      });

      return UnitDetail;
    });
