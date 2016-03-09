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

define(["jquery", "./leaflet"], function($, L) {
  "use strict";

  /**
   * Layer to show WFS data
   *
   * @constructor
   * @extends L.GeoJSON
   * @param {String} serviceUrl
   * @param {String} featureType
   * @param {GeoJSONOptions} options
   */
  L.GeoJSON.WFS = L.GeoJSON.extend({
    initialize: function(serviceUrl, featureType, options) {
      L.GeoJSON.prototype.initialize.call(this, null, options);
      this._featureUrl = serviceUrl + "?service=WFS&request=GetFeature&outputFormat=json&version=1.1.0&srsName=EPSG:4326&typeName=" + featureType;
      this._loaded = false;
    },
    onAdd: function(map) {
      L.LayerGroup.prototype.onAdd.call(this, map);
      if (!this._loaded) {
        var self = this;
        $.ajax({
          dataType: "json",
          url: this._featureUrl,
          success: function(response) {
            if (response.type && response.type === "FeatureCollection") {
              self.addData(response);
              self._loaded = true;
            }
          }
        });
      }
    }
  });
});
