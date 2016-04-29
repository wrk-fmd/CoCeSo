/**
 * CoCeSo
 * Client JS - map/wfs
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
 * @module map/wfs
 */
define([], function() {
  "use strict";

  /**
   * Get URL for WFS service
   *
   * @alias module:map/wfs
   * @param {String} serviceUrl
   * @param {String} featureType
   */
  var getWfsUrl = function(serviceUrl, featureType) {
    return serviceUrl + "?service=WFS&request=GetFeature&outputFormat=json&version=1.1.0&srsName=EPSG:4326&typeName=" + featureType;
  };
  return getWfsUrl;
});
