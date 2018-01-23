/**
 * CoCeSo
 * Client JS - map/point
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
 * @module map/point
 * @param {module:knockout} ko
 * @param {module:utils/constants} constants
 * @param {module:utils/destroy} destroy
 */
define(["knockout", "utils/constants", "utils/destroy"], function(ko, constants, destroy) {
  "use strict";

  /**
   * Point to show on map
   *
   * @alias module:map/point
   * @constructor
   * @param {Object} point Unwrapped Point
   */
  var Point = function(point, id) {
    this.id = id;
    this.bo = ko.observableArray([]);
    this.ao = ko.observableArray([]);
    this.units = ko.observableArray([]);
    this.info = ko.observable(point.info);
    this._latlng = point.coordinates;
    this._marker = null;

    /**
     * Total count of units and incidents at this point
     *
     * @type ko.computed
     * @returns {Integer}
     */
    this.count = ko.computed(function() {
      return this.units().length + this.bo().length + this.ao().length;
    }, this);

    /**
     * Get types of units and incidents at this point
     *
     * @function
     * @type ko.computed
     * @returns {Integer} Each bit represents a specific type
     * @see Point.prototype#types
     */
    this.type = ko.computed(function() {
      var value = 0;
      ko.utils.arrayForEach(this.units(), function(unit) {
        if (unit.portable) {
          if (unit.isFree()) {
            value |= 2;
          }
          if (unit.isHome()) {
            value |= 64;
          }
        } else {
          value |= 4;
        }
      });

      ko.utils.arrayForEach(this.bo(), function(inc) {
        if (inc.isHighlighted()) {
          value |= 1;
        }
        switch (inc.type()) {
          case constants.Incident.type.task:
          case constants.Incident.type.transport:
            value |= 32;
            if (inc.blue()) {
              value |= 8;
            }
            break;
          case constants.Incident.type.tohome:
            value |= 512;
            break;
        }
      });

      ko.utils.arrayForEach(this.ao(), function(inc) {
        if (inc.disableBO() && inc.isHighlighted()) {
          value |= 1;
        }
        switch (inc.type()) {
          case constants.Incident.type.task:
          case constants.Incident.type.transport:
            value |= 32;
            if (inc.blue()) {
              value |= 8;
            }
            break;
          case constants.Incident.type.relocation:
            value |= 16;
            break;
          case constants.Incident.type.holdposition:
            value |= 128;
            break;
          case constants.Incident.type.standby:
            value |= 256;
            break;
          case constants.Incident.type.tohome:
            value |= 512;
            break;
        }
      });
      return value;
    }, this);

    /**
     * Content for popup
     *
     * @function
     * @type ko.computed
     * @returns {String}
     */
    this.popupContent = ko.computed(function() {
      var units = "", incidents = "", content = "";

      function addIncident(inc) {
        var incContent = inc.popupContent();
        incidents += incContent[0];
        units += incContent[1];
      }

      ko.utils.arrayForEach(this.bo(), addIncident);
      ko.utils.arrayForEach(this.ao(), addIncident);
      ko.utils.arrayForEach(this.units(), function(unit) {
        units += unit.popupContent();
      });

      if (incidents) {
        content += "<ul class='list-unstyled'>" + incidents + "</ul>";
      }
      if (units) {
        content += "<ul class='list-unstyled'>" + units + "</ul>";
      }
      return content;
    }, this);

    /**
     * Title for popup
     *
     * @function
     * @type ko.computed
     * @returns {String}
     */
    this.popupTitle = ko.computed(function() {
      return "<span class='pre'>" + this.info().escapeHTML() + "</span>";
    }, this);
  };
  Point.prototype = Object.create({}, /** @lends Point.prototype */ {
    /**
     * Add incident bo to point
     *
     * @function
     * @param {Incident} inc
     */
    pushBo: {
      value: function(inc) {
        if (!ko.utils.arrayFirst(this.bo(), function(item) {
          return item === inc;
        })) {
          this.bo.push(inc);
        }
      }
    },
    /**
     * Add incident ao to point
     *
     * @function
     * @param {Incident} inc
     */
    pushAo: {
      value: function(inc) {
        if (!ko.utils.arrayFirst(this.ao(), function(item) {
          return item === inc;
        })) {
          this.ao.push(inc);
        }
      }
    },
    /**
     * Add unit to point
     *
     * @function
     * @param {Unit} inc
     */
    pushUnit: {
      value: function(unit) {
        if (!ko.utils.arrayFirst(this.units(), function(item) {
          return item === unit;
        })) {
          this.units.push(unit);
        }
      }
    },
    /**
     * Compare latlngs
     *
     * @function
     * @param {Object} latlng
     * @returns {boolean}
     */
    checkLatLng: {
      value: function(latlng) {
        return this._latlng === null || latlng === null
          ? this._latlng === null && latlng === null
          : this._latlng.lat === latlng.lat && this._latlng.lng === latlng.lng;
      }
    },
    /**
     * Update latlng and info
     *
     * @function
     * @param {Object} point
     */
    setData: {
      value: function(point) {
        this._latlng = point.coordinates;
        this.info(point.info);
      }
    },
    /**
     * Move point to another marker
     *
     * @function
     * @param {module:map/marker|module:map/nocoords} marker
     */
    moveToMarker: {
      value: function(marker) {
        if (this._marker !== marker) {
          if (this._marker) {
            this._marker.removePoint(this);
          }
          if (marker) {
            marker.addPoint(this);
          }
          this._marker = marker;
        }
      }
    },
    /**
     * Types for each bit (2^0 to 2^9) in .type()
     *
     * @type Array
     */
    types: {
      value: [
        "open", "free", "fixed", "blue", "relocation",
        "task", "home", "holdposition", "standby", "tohome"
      ]
    },
    /**
     * Get type for bitwise value
     */
    getType: {
      value: function(value) {
        var i, bit = 1;
        for (i = 0; i < this.types.length; i++) {
          if (value & bit) {
            return this.types[i];

          }
          bit *= 2;
        }
        return null;
      }
    },
    /**
     * Destroy the object
     *
     * @function
     */
    destroy: {
      value: function() {
        if (this._marker) {
          this._marker.removePoint(this);
          this._marker = null;
        }
        destroy(this);
      }
    }
  });

  return Point;
});
