/**
 * CoCeSo
 * Client JS - ko/extenders/timeinterval
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
 * @module ko/extenders/timeinterval
 * @param {module:knockout} ko
 * @param {module:utils/clock} clock
 */
define(["knockout", "utils/clock"], function(ko, clock) {
  "use strict";

  /**
   * Add interval and formatted interval to timestamp
   *
   * @param {ko.observable} target
   * @param {boolean} minutes Get interval in minutes if true
   * @returns {ko.pureComputed}
   */
  ko.extenders.timeinterval = function(target, minutes) {
    var intervalDiv = minutes ? 60000 : 1,
      fmtDiv = minutes ? 1 : 1000;


    target.interval = ko.pureComputed(function() {
      return Math.floor((clock.timestamp() - target()) / intervalDiv);
    });

    target.fmtInterval = ko.pureComputed(function() {
      var interval = Math.floor(target.interval() / fmtDiv);
      if (interval <= 0) {
        return "0:00";
      }

      var string = "", val;

      if (!minutes) {
        val = interval % 60;
        interval = Math.floor(interval / 60);
        string = ":" + (val < 10 ? "0" : "") + val + string;
      }

      if (interval === 0) {
        return "0" + string;
      }
      val = interval % 60;
      interval = Math.floor(interval / 60);

      if (interval === 0) {
        return val + string;
      }
      return interval + ":" + (val < 10 ? "0" : "") + val + string;
    });

    return target;
  };
});
