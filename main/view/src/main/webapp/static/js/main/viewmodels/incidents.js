/**
 * CoCeSo
 * Client JS - main/viewmodels/incidents
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
 * @module main/viewmodels/incidents
 * @param {module:knockout} ko
 * @param {module:main/viewmodels/filterable} Filterable
 * @param {module:data/store/incidents} store
 * @param {module:utils/constants} _constants
 * @param {module:utils/i18n} _
 */
define(["knockout", "./filterable", "data/store/incidents", "utils/constants", "utils/i18n",
  "ko/bindings/accordion", "ko/extenders/list"],
  function(ko, Filterable, store, _constants, _) {
    "use strict";

    /**
     * List of incidents
     *
     * @alias module:main/viewmodels/incidents
     * @constructor
     * @extends module:main/viewmodels/filterable
     * @param {Object} options
     */
    var Incidents = function(options) {
      /**
       * The selected filters
       *
       * @type Object
       */
      this.filter = {
        type: ko.observableArray(),
        blue: ko.observableArray(),
        state: ko.observableArray()
      };

      //Call parent constructor
      Filterable.call(this, options);

      /**
       * Filtered view of the incidents array
       *
       * @function
       * @type ko.computed
       * @returns {Array}
       */
      this.filtered = store.list.extend({list: {filter: this.activeFilters, sort: true, field: ["isHighlighted", "priority", "blue"], asc: false}});

      var title = options.title || _("incidents");
      this.dialogTitle = ko.computed(function() {
        var highlighted = ko.utils.arrayFilter(this.filtered(), function(i) {
          return i.isHighlighted();
        }).length,
          total = this.filtered().length;

        return {dialog: title + " (" + highlighted + "/" + total + ")", button: title};
      }, this);

      this.constants = _constants.Incident;
    };

    Incidents.prototype = Object.create(Filterable.prototype, /** @lends Incidents.prototype */ {
      /**
       * Available filters
       *
       * @type Object
       */
      filters: {
        value: {
          overview: {
            filter: {
              type: [_constants.Incident.type.task, _constants.Incident.type.transport, _constants.Incident.type.relocation]
            }
          },
          active: {
            disable: {state: {done: true}},
            filter: {isDone: false}
          },
          open: {
            disable: {state: true},
            filter: {isOpen: true}
          },
          highlighted: {
            disable: {state: true},
            filter: {isHighlighted: true}
          },
          completed: {
            disable: {state: true},
            filter: {isDone: true}
          },
          transport: {
            disable: {type: true},
            filter: {isTransport: true}
          }
        }
      }
    });

    return Incidents;
  }
);
