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
 * @param {module:utils/format} format
 */
define(["jquery", "knockout", "./conf", "./format"], function($, ko, conf, format) {
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
   * @returns {Date}
   */
  obj.timestamp = ko.observable(new Date());

  /**
   * Current clock time
   *
   * @function
   * @type ko.observable
   * @returns {string}
   */
  obj.time = ko.pureComputed(function() {
    return format.time(this.timestamp());
  }, obj);

  /**
   * Local clock offset to correct time
   *
   * @type integer
   */
  var offset = 0;

  //Start clock
  $.get(conf.get("jsonBase") + "timestamp", function(data) {
    if (data.time) {
      offset = new Date() - data.time;
    }
  });
  setInterval(function() {
    obj.timestamp(new Date() - offset);
  }, 1000);

  return obj;
});
