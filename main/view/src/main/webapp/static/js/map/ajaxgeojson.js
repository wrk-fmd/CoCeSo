/**
 * CoCeSo
 * Client JS - map/ajaxgeojson
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
 * @module map/ajaxgeojson
 * @param {module:jquery} $
 * @param {module:map/leaflet} L
 */
define(["jquery", "./leaflet"], function($, L) {
  "use strict";

  /**
   * Layer to show remotely loaded GeoJSON data
   *
   * @alias module:map/ajaxgeojson
   * @constructor
   * @extends L.GeoJSON
   * @param {String} url
   * @param {GeoJSONOptions} options
   */
  var ajaxGeoJSON = L.GeoJSON.extend({
    initialize: function(url, options) {
      L.GeoJSON.prototype.initialize.call(this, null, options);
      this._url = url;
      this._loaded = false;
    },
    onAdd: function(map) {
      L.GeoJSON.prototype.onAdd.call(this, map);
      if (!this._loaded) {
        var self = this;
        $.getJSON(this._url, null, function(response) {
          if (response.type && response.type === "FeatureCollection") {
            self.addData(response);
            self._loaded = true;
          }
        });
      }
    }
  });

  return ajaxGeoJSON;
});
