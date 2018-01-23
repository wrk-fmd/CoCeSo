/**
 * CoCeSo
 * Client JS - models/main/point
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
 * @module main/models/point
 * @param {Knockout} ko
 * @returns {Point}
 */
define(["knockout"], function(ko) {
  "use strict";

  /**
   * A Point
   *
   * @constructor
   * @alias module:main/models/point
   * @param {Object} data Initial data for the point
   */
  var Point = function(data) {
    this.isEmpty = ko.observable(true);
    this.info = ko.observable("");
    this.coordinates = ko.observable(null);

    this.setData = function(data) {
      if (data) {
        this.isEmpty(!data.info);
        this.info(data.info || "");
        this.coordinates(data.coordinates || null);
      } else {
        this.isEmpty(true);
        this.info("");
        this.coordinates(null);
      }
    };

    /**
     * Get static representation of the point
     *
     * @function
     * @type ko.computed
     * @returns {Object}
     */
    this.getStatic = ko.pureComputed(function() {
      return this.isEmpty() ? null : {
        info: this.info(),
        coordinates: this.coordinates()
      };
    }, this).extend({rateLimit: 5});

    if (data) {
      this.setData(data);
    }
  };

  return Point;
});
