/**
 * CoCeSo
 * Client JS - map/ajaxpolyline
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
 * @module map/ajaxpolyline
 * @param {module:jquery} $
 * @param {module:map/leaflet} L
 */
define(["jquery", "./leaflet"], function($, L) {
  "use strict";

  /**
   * Layer to show remotely loaded Polyline
   *
   * @alias module:map/ajaxpolyline
   * @constructor
   * @extends L.Polyline
   * @param {String} url
   * @param {PolylineOptions} options
   */
  var AjaxPolyline = L.Polyline.extend({
    initialize: function(url, options) {
      L.Polyline.prototype.initialize.call(this, [], options);
      this._url = url;
      this._loaded = false;
    },
    onAdd: function(map) {
      L.Polyline.prototype.onAdd.call(this, map);
      if (!this._loaded) {
        var self = this;
        $.getJSON(this._url, null, function(response) {
          if (response) {
            self.setLatLngs(response);
            self._loaded = true;
          }
        });
      }
    }
  });

  return AjaxPolyline;
});
