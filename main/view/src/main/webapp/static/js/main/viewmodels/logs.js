/**
 * CoCeSo
 * Client JS - main/viewmodels/logs
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["knockout", "../models/log", "utils/conf", "utils/i18n"],
    function(ko, Log, conf, _) {
      "use strict";

      /**
       * List of Logs
       *
       * @constructor
       * @param {Object} options
       */
      var Logs = function(options) {
        var self = this;

        this.dialogTitle = options.title || _("log");

        /**
         * List of Logs
         *
         * @function
         * @type ko.observableArray
         * @returns {Array}
         */
        this.loglist = ko.observableArray();

        /**
         * Timeout ID for reload
         *
         * @type {integer}
         */
        this.timeout = null;

        /**
         * Load the specified data
         *
         * @param {String} url The URL to load from
         * @param {int} interval The interval to reload. 0 or false for no autoload.
         * @returns {void}
         */
        this.load = function(url, interval) {
          $.ajax({
            dataType: "json",
            url: conf.get("jsonBase") + url,
            ifModified: true,
            success: function(data, status) {
              if (status !== "notmodified") {
                self.loglist($.map(data, function(item) {
                  return new Log(item);
                }));
              }
            },
            complete: function() {
              if (interval) {
                self.timeout = window.setTimeout(self.load, interval, url, interval);
              }
            }
          });
        };

        this.load(options.url || "log/getLast/" + conf.get("logEntryLimit"), options.autoload ? conf.get("interval") : false);
      };
      Logs.prototype = Object.create({}, /** @lends Logs.prototype */ {
        /**
         * Destroy the ViewModel
         *
         * @function
         * @return {void}
         */
        destroy: {
          value: function() {
            //Stop reloading timeout
            if (this.timeout) {
              window.clearTimeout(this.timeout);
            }
          }
        }
      });

      return Logs;
    });
