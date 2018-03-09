/**
 * CoCeSo
 * Client JS - map/imageoverlay
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
 * @module map/imageoverlay
 * @param {module:map/leaflet} L
 * @param {module:utils/conf} conf
 */
define(["./leaflet", "utils/conf"], function(L, conf) {
  "use strict";

  /**
   * Refreshable image overlay
   *
   * @constructor
   * @extends L.ImageOverlay
   * @param {Function} getUrl
   * @param {LatLngBounds} bounds
   * @param {ImageOverlayOptions} options
   */
  L.ImageOverlay.Refreshable = L.ImageOverlay.extend({
    initialize: function(getUrl, bounds, options) { // (String, LatLngBounds, Object)
      L.ImageOverlay.prototype.initialize.call(this, undefined, bounds, options);
      this._getUrl = getUrl;
      this._interval = null;
    },
    onAdd: function(map) {
      L.ImageOverlay.prototype.onAdd.call(this, map);
      this._getUrl.call(this);
      map.on('zoomend', this._getUrl, this);
      if (this._interval) {
        clearInterval(this._interval);
      }
      this._interval = setInterval(this._getUrl.bind(this), this.options.interval ? this.options.interval : conf.get("interval"));
    },
    onRemove: function(map) {
      if (this._interval) {
        clearInterval(this._interval);
        this._interval = null;
      }
      L.ImageOverlay.prototype.onRemove.call(this, map);
    },
    setUrl: function(url) {
      if (!url) {
        this._url = null;
        this._image.removeAttribute('src');
        this._image.style.display = 'none';
      } else {
        this._url = url;
        this._image.setAttribute('src', this._url);
        this._image.style.display = '';
      }
    },
    _initImage: function() {
      this._image = L.DomUtil.create('img', 'leaflet-image-layer');

      if (this._map.options.zoomAnimation && L.Browser.any3d) {
        L.DomUtil.addClass(this._image, 'leaflet-zoom-animated');
      } else {
        L.DomUtil.addClass(this._image, 'leaflet-zoom-hide');
      }

      this._updateOpacity();

      //TODO createImage util method to remove duplication
      L.extend(this._image, {
        galleryimg: 'no',
        onselectstart: L.Util.falseFn,
        onmousemove: L.Util.falseFn,
        onload: L.bind(this._onImageLoad, this)
      });

      this._image.style.display = 'none';
    }
  });
});
