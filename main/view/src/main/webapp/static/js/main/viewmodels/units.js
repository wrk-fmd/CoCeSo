/**
 * CoCeSo
 * Client JS - main/viewmodels/units
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
 * @module main/viewmodels/units
 * @param {module:knockout} ko
 * @param {module:main/viewmodels/filterable} Filterable
 * @param {module:data/store/units} store
 * @param {module:utils/i18n} _
 */
define(["knockout", "./filterable", "data/store/units", "utils/i18n", "ko/extenders/list"],
  function(ko, Filterable, store, _) {
    "use strict";

    /**
     * List of units
     *
     * @alias module:main/viewmodels/units
     * @constructor
     * @extends module:main/viewmodels/filterable
     * @param {Object} options
     */
    var Units = function(options) {
      /**
       * The selected filters
       *
       * @type Object
       */
      this.filter = {};

      //Call parent constructor
      Filterable.call(this, options);

      /**
       * Filtered view of the incidents array
       *
       * @function
       * @type ko.computed
       * @returns {Array}
       */
      this.filtered = store.list.extend({list: {filter: this.activeFilters}});

      var title = options.title || _("units");
      this.dialogTitle = ko.computed(function() {
        return {dialog: title + " (" + this.filtered().length + ")", button: title};
      }, this);
    };
    Units.prototype = Object.create(Filterable.prototype, /** @lends Units.prototype */ {
      /**
       * Available filters
       *
       * @type Object
       */
      filters: {
        value: {
          radio: {
            filter: {hasAssigned: true}
          },
          free: {
            filter: {isFree: true}
          },
          available: {
            filter: {isAvailable: true}
          }
        }
      }
    });

    return Units;
  }
);
