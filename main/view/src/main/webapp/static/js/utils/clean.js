/**
 * CoCeSo
 * Client JS - utils/clean
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(function() {
  "use strict";

  /**
   * Helper function to delete all properties in an object
   *
   * @param {Object} obj
   * @returns {void}
   */
  var clean = function(obj) {
    var i;
    for (i in obj) {
      if (obj.hasOwnProperty(i)) {
        delete obj[i];
      }
    }
  };

  return clean;
});
