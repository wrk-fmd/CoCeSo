/**
 * CoCeSo
 * Client JS - map/nocoords
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
 * @module map/nocoords
 * @param {module:knockout} ko
 * @param {module:map/leaflet} L
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define(["knockout", "./leaflet", "utils/destroy", "utils/i18n"], function(ko, L, destroy, _) {
  "use strict";

  /**
   * Marker item for the unknown coordinates list
   *
   * @constructor
   * @param {module:map/point} point
   */
  var NoCoordsMarker = function(point) {
    this.el = L.DomUtil.create("li", "clearfix");

    var icon = L.divIcon({iconSize: [18, 18]}).createIcon();
    this.el.appendChild(icon);
    var title = L.DomUtil.create('div', '', this.el);

    this._updateTitle = ko.computed(function() {
      title.innerHTML = point.popupTitle();
    });
    this._updateType = ko.computed(function() {
      icon.className = "leaflet-marker-icon leaflet-div-icon icon-"
        + point.getType(point.type());
    });
    this._updateCount = ko.computed(function() {
      icon.innerHTML = point.count() > 1 ? "<span class='glyphicon glyphicon-plus'></span>" : "";
    });
  };
  NoCoordsMarker.prototype = Object.create({}, /** @lends NoCoordsMarker.prototype */ {
    /**
     * Destroy the object
     *
     * @function
     */
    destroy: {
      value: function() {
        destroy(this);
      }
    }
  });

  /**
   * Show points with unknown coordinates
   *
   * @alias module:map/nocoords
   * @constructor
   * @extends L.Control
   * @param {ControlOptions} options
   */
  var NoCoordsControl = L.Control.extend({
    options: {
      position: "bottomleft"
    },
    initialize: function(options) {
      L.Control.prototype.initialize.call(this, options);
      this._points = {};
      this._ul = L.DomUtil.create("ul", "list-unstyled");
    },
    onAdd: function() {
      this._container = L.DomUtil.create("div", "map-nocoords");
      L.DomUtil.create("h3", '', this._container).innerText = _("map.nocoords");
      this._container.appendChild(this._ul);
      this._container.style.display = this._ul.firstChild ? "" : "none";
      return this._container;
    },
    /**
     * Add point to list
     *
     * @param {module:map/point} point
     */
    addPoint: function(point) {
      if (!this._points[point.id]) {
        this._points[point.id] = new NoCoordsMarker(point);
        this._ul.appendChild(this._points[point.id].el);
        if (this._container) {
          this._container.style.display = "";
        }
      }
    },
    /**
     * Remove point from list
     *
     * @param {module:map/point} point
     */
    removePoint: function(point) {
      var item = this._points[point.id];
      if (item) {
        this._ul.removeChild(item.el);
        item.destroy();
        delete this._points[point.id];
        if (!this._ul.firstChild) {
          this._container.style.display = "none";
        }
      }
    }
  });

  return NoCoordsControl;
});
