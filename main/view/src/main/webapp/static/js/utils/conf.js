/**
 * CoCeSo
 * Client JS - utils/conf
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
 * @module {Object} utils/conf
 */
define(function() {
  "use strict";

  var defaults = {
    contentBase: "",
    jsonBase: "",
    langBase: "",
    language: "en",
    logEntryLimit: 30,
    map3d: true,
    mapImagePath: "",
    interval: 10000,
    debug: true,
    error: function() {},
    keyboardControl: true,
    keyMapping: {
      // 32: Space
      openIncidentKey: 32,
      // 89: Y, 74: J
      yesKey: 89,
      // 78: N
      noKey: 78
    },
    plugins: {}
  }, set = {};

  /**
   * @alias module:utils/conf
   */
  var conf = {};


  /**
   * @param {string} key
   * @returns {*}
   */
  conf.get = function(key) {
    if (typeof set[key] !== "undefined") {
      return set[key];
    }
    return (typeof CocesoConf === "undefined" || typeof CocesoConf[key] === "undefined") ? defaults[key] : CocesoConf[key];
  };

  /**
   * @param {string} key
   * @param {*} value
   */
  conf.set = function(key, value) {
    set[key] = value;
  };

  return conf;
});
