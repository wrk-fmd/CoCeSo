/**
 * CoCeSo
 * Client JS - utils/destroy
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
 * @module {Object} utils/destroy
 * @param {module:knockout} ko
 */
define(["knockout"], function(ko) {
  "use strict";

  var destroy = function(obj) {
    var i;
    for (i in obj) {
      if (obj.hasOwnProperty(i) && ko.isComputed(obj[i])) {
        obj[i].dispose();
        delete obj[i];
      }
    }
  };

  return destroy;
});
