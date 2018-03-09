/**
 * CoCeSo
 * Client JS - edit/models/slimunit
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
 * @module {Class} edit/models/slimunit
 * @param {module:knockout} ko
 * @param {module:edit/models/editableunit} EditableUnit
 * @param {module:data/store/units} units
 * @param {module:utils/destroy} destroy
 */
define(["knockout", "./editableunit", "data/store/units", "utils/destroy"],
  function(ko, EditableUnit, units, destroy) {
    "use strict";

    /**
     * Unit model for ordering
     *
     * @alias module:edit/models/slimunit
     * @constructor
     * @param {Integer} id
     * @param {Double} ordering
     */
    var SlimUnit = function(id, ordering) {
      this.id = id;
      this.ordering = ordering || null;

      this.call = ko.computed(function() {
        return (units.models()[id] instanceof EditableUnit) ? units.models()[id].call() : "";
      });
    };
    SlimUnit.prototype = Object.create({}, /** @lends SlimUnit.prototype */ {
      /**
       * Destroy the model
       *
       * @function
       */
      destroy: {
        value: function() {
          destroy(this);
        }
      }
    });

    return SlimUnit;
  }
);
