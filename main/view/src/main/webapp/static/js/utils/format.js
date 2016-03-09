/**
 * CoCeSo
 * Client JS - utils/format
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(function() {
  "use strict";

  var format = {
    time: function(timestamp) {
      return new Date(timestamp).toLocaleTimeString();
    },
    interval: function(interval) {
      interval = Math.round(interval / 1000);
      if (interval <= 0) {
        return "0:00";
      }

      var string = "", val;

      val = interval % 60;
      interval = Math.floor(interval / 60);
      string = ":" + (val < 10 ? "0" : "") + val + string;

      if (interval === 0) {
        return "0" + string;
      }
      val = interval % 60;
      interval = Math.floor(interval / 60);

      if (interval === 0) {
        return val + string;
      }
      return interval + ":" + (val < 10 ? "0" : "") + val + string;
    }
  };

  return format;
});
