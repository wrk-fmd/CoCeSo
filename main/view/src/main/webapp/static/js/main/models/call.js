/**
 * CoCeSo
 * Client JS - models/main/call
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
 * @module main/models/call
 * @param {module:knockout} ko
 * @param {module:data/store/radio} store
 * @param {module:data/store/units} units
 * @param {module:utils/clock} clock
 * @param {module:utils/format} format
 * @returns {Call}
 */
define(["knockout", "data/store/radio", "data/store/units", "utils/clock", "utils/format"],
    function(ko, store, units, clock, format) {
      "use strict";

      /**
       * @constructor
       * @alias module:main/models/call
       * @param {type} data The call data
       */
      var Call = function(data) {
        data = data || {};

        /** @type Integer */
        this.timestamp = new Date(data.timestamp);
        /** @type String */
        this.time = format.time(data.timestamp);
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
          var id, models = units.models();
          for (id in models) {
            if (models[id].ani === this.ani) {
              store.aniMap[this.ani] = id;
              return models[id];
            }
          }
          return null;
        }, this);

        /**
         * @function
         * @type ko.pureComputed
         * @returns {Integer} Time since the call was sent
         */
        this.timer = ko.pureComputed(function() {
          return clock.timestamp() - this.timestamp;
        }, this);

        /**
         * @function
         * @type ko.pureComputed
         * @returns {String} Formatted interval since the call was sent
         */
        this.fmtTimer = ko.pureComputed(function() {
          return format.interval(this.timer());
        }, this);
      };

      return Call;
    });
