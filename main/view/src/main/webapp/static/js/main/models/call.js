/**
 * CoCeSo
 * Client JS - models/main/call
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
 * @module main/models/call
 * @param {module:knockout} ko
 * @param {module:data/store/radio} store
 * @param {module:data/store/units} units
 * @returns {Call}
 */
define(["knockout", "data/store/radio", "data/store/units", "ko/extenders/timeformat", "ko/extenders/timeinterval"],
  function(ko, store, units) {
    "use strict";

    /**
     * @constructor
     * @alias module:main/models/call
     * @param {type} data The call data
     */
    var Call = function(data) {
      data = data || {};

      /** @type ko.observable */
      this.timestamp = ko.observable(data.timestamp).extend({timeformat: false, timeinterval: false});
      /** @type String */
      this.ani = data.ani;
      /** @type String */
      this.port = data.port;
      /** @type Boolean */
      this.emergency = (data.direction === "RX_EMG");

      /**
       * @function
       * @type ko.pureComputed
       * @returns {module:main/model/unit} The associated Unit
       */
      this.unit = ko.pureComputed(function() {
        if (store.aniMap[this.ani]) {
          return units.get(store.aniMap[this.ani]);
        }
        var id;
        var models = units.models();
        for (id in models) {
          var aniListOfUnit = models[id].ani;
          if (aniListOfUnit && aniListOfUnit.includes(this.ani)) {
            store.aniMap[this.ani] = id;
            return models[id];
          }
        }
        return null;
      }, this);
    };

    return Call;
  });
