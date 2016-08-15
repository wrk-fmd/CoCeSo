/**
 * CoCeSo
 * Client JS - utils/clock
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
 * @module {Object} utils/clock
 * @param {jquery} $
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 */
define(["jquery", "knockout", "./conf", "ko/extenders/timeformat"], function($, ko, conf) {
  "use strict";

  /**
   * @alias module:utils/clock
   */
  var obj = {};

  /**
   * Current timestamp
   *
   * @function
   * @type ko.observable
   * @returns {Integer}
   */
  obj.timestamp = ko.observable(Date.now()).extend({timeformat: false});

  /**
   * Local clock offset to correct time
   *
   * @type integer
   */
  var offset = 0;

  //Start clock
  $.get(conf.get("jsonBase") + "timestamp", function(data) {
    if (data.time) {
      offset = Date.now() - data.time;
    }
  });
  setInterval(function() {
    obj.timestamp(Date.now() - offset);
  }, 1000);

  return obj;
});
