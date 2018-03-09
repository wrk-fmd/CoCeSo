/**
 * CoCeSo
 * Client JS - main/viewmodels/patients
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
 * @module main/viewmodels/patients
 * @param {module:knockout} ko
 * @param {module:main/viewmodels/filterable} Filterable
 * @param {module:data/store/patients} store
 * @param {module:utils/i18n} _
 */
define(["knockout", "./filterable", "data/store/patients", "utils/i18n", "ko/extenders/list"],
  function(ko, Filterable, store, _) {
    "use strict";

    /**
     * Viewmodel for the patient list
     *
     * @alias module:main/viewmodels/patients
     * @constructor
     * @extends module:main/viewmodels/filterable
     * @param {Object} options
     */
    var Patients = function(options) {
      var self = this;

      // Filtering
      this.filtertext = ko.observable(options && options.filtertext ? options.filtertext : null);
      this.regex = ko.computed(function() {
        var filter = this.filtertext();
        if (!filter) {
          return [];
        }
        return $.map(filter.split(" "), function(item) {
          return new RegExp(RegExp.escape(item), "i");
        });
      }, this);

      /**
       * The selected filters
       *
       * @type Object
       */
      this.filter = {
        regex: function(item) {
          var regex = self.regex(), i;
          for (i = 0; i < regex.length; i++) {
            if (!regex[i].test(item.externalId()) && !regex[i].test(item.firstname()) && !regex[i].test(item.lastname())) {
              return false;
            }
          }
          return true;
        }
      };

      //Call parent constructor
      Filterable.call(this, options);

      /**
       * Filtered view of the patients array
       *
       * @function
       * @type ko.computed
       * @returns {Array}
       */
      this.filtered = store.list.extend({list: {filter: this.activeFilters}});

      this.dialogTitle = options.title || _("patients");

      this.dialogState = ko.computed(function() {
        return {
          filtertext: this.filtertext()
        };
      }, this);
    };
    Patients.prototype = Object.create(Filterable.prototype, /** @lends Patients.prototype */ {
      /**
       * Available filters
       *
       * @type Object
       */
      filters: {
        value: {}
      }
    });

    return Patients;
  }
);
