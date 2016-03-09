/**
 * CoCeSo
 * Client JS - models/main/point
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
    this.id = ko.observable(null);
    this.info = ko.observable("");
    this.lat = ko.observable(null);
    this.lng = ko.observable(null);

    this.setData = function(data) {
      data = data || {};
      this.id(data.id || null);
      this.info(data.info || "");
      this.lat(typeof data.latitude === "undefined" ? null : data.latitude || null);
      this.lng(typeof data.longitude === "undefined" ? null : data.longitude || null);
    };

    /**
     * Get static representation of the point
     *
     * @function
     * @type ko.computed
     * @returns {Object}
     */
    this.getStatic = ko.pureComputed(function() {
      return {
        id: this.id(),
        info: this.info(),
        lat: this.lat(),
        lng: this.lng()
      };
    }, this).extend({rateLimit: 5});

    if (data) {
      this.setData(data);
    }
  };

  return Point;
});
