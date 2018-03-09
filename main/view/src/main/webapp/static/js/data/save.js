/**
 * CoCeSo
 * Client JS - data/save
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
 * @module {function} data/save
 * @param {jquery} $
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 */
define(["jquery", "knockout", "utils/conf"],
    function($, ko, conf) {
      "use strict";

      /**
       * @callback success
       * @param {Object} data
       */

      /**
       * @callback error
       * @param {Object} data
       */

      /**
       * @callback httpError
       * @param {Object} jqXHR
       */

      /**
       * Save entries with POST
       *
       * @alias data/save
       * @param {!Object} data
       * @param {!string} url
       * @param {success} [success]
       * @param {error} [error]
       * @param {httperror} [httperror]
       * @param {ko.writeableObservable<boolean>} [saving]
       * @param {string} [contentType] Optional content type
       */
      var save = function(data, url, success, error, httperror, saving, contentType) {
        if (ko.isWriteableObservable(saving)) {
          saving(true);
        }
        $.ajax({
          type: "POST",
          url: conf.get("jsonBase") + url,
          dataType: "json",
          contentType: contentType || ((typeof data === "string") ? "application/json" : "application/x-www-form-urlencoded"),
          data: data,
          processData: (typeof data !== "string"),
          success: function(data) {
            if (data.success) {
              if (success instanceof Function) {
                success(data);
              }
            } else {
              if (error instanceof Function) {
                error(data);
              }
            }
          },
          error: function(jqXHR) {
            if (httperror instanceof Function) {
              httperror(jqXHR);
            }
          },
          complete: function() {
            if (ko.isWriteableObservable(saving)) {
              saving(false);
            }
          }
        });
      };

      return save;
    });
