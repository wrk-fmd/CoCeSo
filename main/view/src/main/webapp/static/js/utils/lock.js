/**
 * CoCeSo
 * Client JS - utils/lock
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
 * @module {Object} utils/lock
 * @param {jquery} $
 * @param {module:utils/i18n} _
 */
define(["jquery", "utils/i18n"], function($, _) {
  "use strict";

  var lock = {
    /**
     * Get all existing locks
     *
     * @param {String} json Optional JSON string. If empty localStorage.locks is used
     * @returns {Object}
     */
    getLocks: function(json) {
      var locks = {};
      json = json || localStorage.locks;
      if (locks) {
        try {
          locks = JSON.parse(localStorage.locks) || {};
        } catch (e) {
          locks = {};
        }
      }
      return locks;
    },
    /**
     * Lock the concern selection while this page is open
     *
     * @returns {void}
     */
    lock: function() {
      var locks = lock.getLocks();
      var i = 1;
      while (typeof locks[i] !== "undefined") {
        i++;
      }

      locks[i] = true;
      localStorage.locks = JSON.stringify(locks);

      $(window).on("beforeunload", function() {
        var locks = lock.getLocks();
        delete locks[i];
        localStorage.locks = JSON.stringify(locks);
      });

      $(window).on("storage", function(e) {
        if (e.originalEvent.key === "concern") {
          if (e.originalEvent.newValue) {
            if (window.confirm(_("pagereload"))) {
              location.reload();
            }
          } else {
            if (window.confirm(_("pageclose"))) {
              window.close();
            }
          }
        }
      });
    },
    /**
     * Determine if concern is locked
     *
     * @param {type} json Optional JSON string with locks. If empty localStorage.locks is used
     * @returns {Boolean}
     */
    isLocked: function(json) {
      var locks = lock.getLocks(json);
      var i;
      for (i in locks) {
        if (locks[i] === true) {
          return true;
        }
      }
      return false;
    }
  };

  return lock;
});
