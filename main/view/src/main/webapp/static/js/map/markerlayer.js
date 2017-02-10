/**
 * CoCeSo
 * Client JS - map/popup
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
 * @module {Class} map/markerlayer
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:map/leaflet} L
 * @param {module:map/marker} Marker
 * @param {module:map/point} Point
 */
define(["jquery", "knockout", "./leaflet", "./marker", "./point"], function($, ko, L, Marker, Point) {
  "use strict";

  /**
   * LayerGroup to show the markers on
   *
   * @constructor
   * @extends L.LayerGroup
   * @param {module:map/nocoords} noCoordsControl
   */
  var MarkerLayer = L.LayerGroup.extend({
    initialize: function(noCoordsControl) {
      this._layers = {};
      this._points = {};
      this._grid = {};
      this._bo = {};
      this._ao = {};
      this._units = {};
      this._noCoordsControl = noCoordsControl;
    },
    removeLayer: function(layer) {
      if (layer instanceof Marker) {
        var x, y;
        for (x in this._grid) {
          for (y in this._grid[x]) {
            ko.utils.arrayRemoveItem(this._grid[x][y], layer);
          }
        }
      }
      L.LayerGroup.prototype.removeLayer.call(this, layer);
    },
    /**
     * Move bo of incident
     *
     * @param {module:map/incident} inc
     * @param {Object} point
     */
    moveBo: function(inc, point) {
      var mapPoint = this._getMapPoint(point);
      if (mapPoint) {
        if (this._bo[inc.id] !== mapPoint) {
          this._removeBo(inc);
          mapPoint.pushBo(inc);
          this._bo[inc.id] = mapPoint;
        }
      } else {
        this._removeBo(inc);
      }
    },
    /**
     * Move ao of incident
     *
     * @param {module:map/incident} inc
     * @param {Object} point
     */
    moveAo: function(inc, point) {
      var mapPoint = this._getMapPoint(point);
      if (mapPoint) {
        if (this._ao[inc.id] !== mapPoint) {
          this._removeAo(inc);
          mapPoint.pushAo(inc);
          this._ao[inc.id] = mapPoint;
        }
      } else {
        this._removeAo(inc);
      }
    },
    /**
     * Move unit
     *
     * @param {module:map/unit} unit
     * @param {Object} point
     */
    moveUnit: function(unit, point) {
      var mapPoint = this._getMapPoint(point);
      if (mapPoint) {
        if (this._units[unit.id] !== mapPoint) {
          this._removeUnit(unit);
          mapPoint.pushUnit(unit);
          this._units[unit.id] = mapPoint;
        }
      } else {
        this._removeUnit(unit);
      }
    },
    /**
     * Get MapPoint for a point object and move the point to correct marker
     *
     * @param {Object} point
     * @returns {module:map/point}
     */
    _getMapPoint: function(point) {
      if (!point) {
        return null;
      }

      var id = point.info + "_" + (point.coordinates === null ? "unknown" : point.coordinates.lat + "_" + point.coordinates.lng);

      var mapPoint = null;
      if (this._points[id]) {
        if (!this._points[id].checkLatLng(point)) {
          // Point exists, but has been moved
          mapPoint = this._points[id];
          mapPoint.setData(point);
        } else {
          this._points[id].info(point.info);
        }
      } else {
        // Point does not yet exist
        mapPoint = this._points[id] = new Point(point, id);
      }

      if (mapPoint) {
        // Point is either new or has been moved
        if (point.coordinates === null) {
          // Coordinates not set, move to noCoordControl
          mapPoint.moveToMarker(this._noCoordsControl);
        } else {
          // Find near points
          var x = Math.round(point.coordinates.lat * 5000) / 5000,
            y = Math.round(point.coordinates.lng * 2500) / 2500,
            x1 = x < point.coordinates.lat ? x + 0.0002 : x - 0.0002,
            y1 = x < point.coordinates.lng ? x + 0.0004 : x - 0.0004;

          var gridMarkers = $.merge($.merge(this._getGrid(x, y), this._getGrid(x, y1)), $.merge(this._getGrid(x1, y), this._getGrid(x1, y1)));
          var near = $.map(gridMarkers, function(marker) {
            var distance = marker.getLatLng().distanceTo(point.coordinates);
            return distance < 10 ? {m: marker, d: distance} : null;
          });

          if (near.length) {
            // Move to nearest existing marker
            near.sort(function(a, b) {
              return a.d - b.d;
            });
            mapPoint.moveToMarker(near[0].m);
          } else {
            // Create a new marker
            var marker = new Marker(point.coordinates, this, {noCoordsControl: this._noCoordsControl});
            if (this._grid[x]) {
              if (this._grid[x][y]) {
                this._grid[x][y].push(marker);
              } else {
                this._grid[x][y] = [marker];
              }
            } else {
              this._grid[x] = {};
              this._grid[x][y] = [marker];
            }
            mapPoint.moveToMarker(marker);
          }
        }
      }
      return this._points[id];
    },
    _getGrid: function(x, y) {
      return (this._grid[x] && this._grid[x][y]) ? $.merge([], this._grid[x][y]) : [];
    },
    /**
     * Remove bo for incident
     *
     * @param {module:map/incident} inc
     */
    _removeBo: function(inc) {
      if (this._bo[inc.id]) {
        this._bo[inc.id].bo.remove(inc);
        if (!this._bo[inc.id].count()) {
          this._bo[inc.id].destroy();
          delete this._points[this._bo[inc.id].id];
        }
        delete this._bo[inc.id];
      }
    },
    /**
     * Remove ao for incident
     *
     * @param {module:map/incident} inc
     */
    _removeAo: function(inc) {
      if (this._ao[inc.id]) {
        this._ao[inc.id].ao.remove(inc);
        if (!this._ao[inc.id].count()) {
          this._ao[inc.id].destroy();
          delete this._points[this._ao[inc.id].id];
        }
        delete this._ao[inc.id];
      }
    },
    /**
     * Remove unit
     *
     * @param {module:map/unit} unit
     */
    _removeUnit: function(unit) {
      if (this._units[unit.id]) {
        this._units[unit.id].units.remove(unit);
        if (!this._units[unit.id].count()) {
          this._units[unit.id].destroy();
          delete this._points[this._units[unit.id].id];
        }
        delete this._units[unit.id];
      }
    }
  });

  return MarkerLayer;
});
