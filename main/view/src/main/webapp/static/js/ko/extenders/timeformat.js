/**
 * CoCeSo
 * Client JS - ko/extenders/timeformat
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Intl */

/**
 * @module ko/extenders/timeformat
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 */
define(["knockout", "utils/conf"], function(ko, conf) {
  "use strict";

  var localeTimeFormat = new Intl.DateTimeFormat(conf.get("language"), {hour: "2-digit", minute: "2-digit", second: "2-digit"});

  /**
   * Add formatted time to timestamp
   *
   * @param {ko.observable} target
   * @returns {ko.pureComputed}
   */
  ko.extenders.timeformat = function(target) {

    target.formatted = ko.pureComputed(function() {
      return localeTimeFormat.format(target());
    });

    return target;
  };
});
