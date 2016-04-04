/**
 * CoCeSo
 * Client JS - map/leaflet
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["leaflet", "utils/conf"], function(L, conf) {
  "use strict";

  L.Browser.webkit3d = conf.get("map3d");
  L.Icon.Default.imagePath = conf.get("mapImagePath");
  return L.noConflict();
});
