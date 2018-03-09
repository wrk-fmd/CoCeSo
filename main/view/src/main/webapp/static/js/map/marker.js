/**
 * CoCeSo
 * Client JS - map/marker
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
 * @module {Class} map/marker
 * @param {module:knockout} ko
 * @param {module:map/leaflet} L
 * @param {module:map/point} Point
 * @param {module:map/popup} Popup
 * @param {module:utils/destroy} destroy
 */
define(["knockout", "./leaflet", "./point", "./popup", "utils/destroy"], function(ko, L, Point, Popup, destroy) {
  "use strict";

  /**
   * Marker for the situation map
   *
   * @constructor
   * @extends L.Marker
   * @param {Object} latlng
   * @param {module:map/markerlayer} layer
   * @param {MarkerOptions} options
   */
  var Marker = L.Marker.extend({
    initialize: function(latlng, layer, options) {
      options.icon = L.divIcon({iconSize: [18, 18]});
      L.Marker.prototype.initialize.call(this, latlng, options);

      this._layer = layer;
      this._points = ko.observableArray([]);

      /**
       * Get count of all units and incidents and set marker content accordingly
       *
       * @function
       * @type ko.computed
       * @returns {Integer}
       */
      this.count = ko.computed(function() {
        var count = 0;
        ko.utils.arrayForEach(this._points(), function(item) {
          count += item.count();
        });
        this.options.icon.options.html = count > 1 ? "<span class='glyphicon glyphicon-plus'></span>" : false;
        this.setIcon(this.options.icon);
        return count;
      }, this);

      /**
       * Set marker color according to units and incidents
       *
       * @function
       * @type ko.computed
       */
      this._type = ko.computed(function() {
        var value = 0;

        ko.utils.arrayForEach(this._points(), function(item) {
          value |= item.type();
        });

        this.options.icon.options.className = "leaflet-div-icon icon-" + Point.prototype.getType(value);
        this.setIcon(this.options.icon);
      }, this);

      /**
       * Get popup content
       *
       * @function
       * @type ko.computed
       * @returns {String}
       */
      this.popupContent = ko.computed(function() {
        if (!this._points().length) {
          return "";
        }
        if (this._points().length === 1) {
          return this._points()[0].popupContent();
        }
        var content = "";
        ko.utils.arrayForEach(this._points(), function(item) {
          content += "<h4>" + item.popupTitle() + "</h4>"
            + item.popupContent();
        });
        return content;
      }, this);

      /**
       * Get popup title
       *
       * @function
       * @type ko.computed
       * @returns {String}
       */
      this.popupTitle = ko.computed(function() {
        return this._points().length ? this._points()[0].popupTitle() : "";
      }, this);

      this.bindPopup(new Popup(this.popupTitle, this.popupContent));
    },
    /**
     * Add a point to the marker
     *
     * @param {module:map/point} point
     */
    addPoint: function(point) {
      this._points.push(point);
      this._layer.addLayer(this);
    },
    /**
     * Remove point from the marker
     *
     * @param {module:map/point} point
     */
    removePoint: function(point) {
      this._points.remove(point);
      if (!this._points().length) {
        this.destroy();
      }
    },
    /**
     * Destroy the object
     */
    destroy: function() {
      this._layer.removeLayer(this);
      destroy(this);
    }
  });

  return Marker;
});
