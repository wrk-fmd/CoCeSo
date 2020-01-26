/**
 * CoCeSo
 * Client JS - data/load
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Function */

/**
 * @module {function} data/load
 * @param {jquery} $
 * @param {module:knockout} ko
 * @param {module:data/stomp} stomp
 * @param {module:utils/conf} conf
 */
define(["jquery", "knockout", "./stomp", "utils/conf", "utils/client-logger"],
  function($, ko, stomp, conf, clientLogger) {
    "use strict";

    /**
     * @callback cbSet
     * @param {Object} data The item data
     */

    /**
     * @callback cbDelete
     * @param {Integer} id The item id
     */

    /**
     * @callback cbFull
     * @param {Object[]} items Array containing all loaded items
     */

    /**
     * @typedef {Object} LoadOptions Options for loading a specific entity
     * @property {!string} url The AJAX url
     * @property {!string} stomp The stomp topic
     * @property {Class} model The constructor for the loaded type
     * @property {ko.observable} store The observable containing the object for storing the items (by id)
     * @property {cbSet} [cbSet] The callback called on setting one item
     * @property {cbDelete} [cbDelete] The callback called on deleting an item
     * @property {cbFull} [cbFull] The callback called on loading all items
     */
    return function (options) {
      var cbSet, cbDelete;

      if (options.cbSet instanceof Function) {
        cbSet = options.cbSet;
      } else {
        cbSet = function (data) {
          if (!data.id) {
            return false;
          }
          if (options.store()[data.id] instanceof options.model) {
            options.store()[data.id].setData(data);
            return false;
          }
          options.store()[data.id] = new options.model(data);
          return true;
        };
      }

      if (options.cbDelete instanceof Function) {
        cbDelete = options.cbDelete;
      } else {
        cbDelete = function (id) {
          var item = options.store()[id];
          delete options.store()[id];
          return item;
        };
      }

      function fullLoad() {
        $.ajax({
          dataType: "json",
          url: conf.get("jsonBase") + options.url,
          success: function (result) {
            var mutated = false;
            var deleted = [];
            if (options.cbFull instanceof Function) {
              mutated = options.cbFull(result.data);
            } else {
              var found = {};
              ko.utils.arrayForEach(result.data, function (item) {
                if (item.id) {
                  found[item.id] = true;
                }
                mutated = cbSet(item) || mutated;
              });
              for (var i in options.store()) {
                if (!found[i]) {
                  deleted.push(cbDelete(i));
                  mutated = true;
                }
              }
            }

            options.hver = result.hver;
            options.seq = result.seq;

            if (options.queue) {
              ko.utils.arrayForEach(options.queue, function (item) {
                if (item.hver === options.hver && item.seq === options.seq + 1) {
                  options.seq = item.seq;
                  mutated = cbSet(item.data) || mutated;
                }
              });
              options.queue = null;
            }

            if (mutated) {
              options.store.valueHasMutated();
            }

            if (deleted) {
              ko.utils.arrayForEach(deleted, function (item) {
                if (item) {
                  item.destroy();
                }
              });
            }

            clientLogger.debugLog("Got a working websocket connection to the backend.");
            conf.get("error")(false);
          },
          error: function (xhr) {
            console.debug("Loading '%s' failed with status %d", options.url, xhr.status);
            conf.get("error")(true);
          }
        });
      }

      options.hver = 0;
      options.seq = 0;
      options.queue = [];
      stomp.subscribe(options.stomp.replace(/{c}/, localStorage.concern), function (data) {
        var body = JSON.parse(data.body);
        if (options.hver === 0) {
          // Full load not yet done
          options.queue.push(body);
        } else if (options.hver !== body.hver || options.seq + 1 !== body.seq) {
          // Something was missed
          options.hver = 0;
          options.seq = 0;
          options.queue = [];

          clientLogger.warnLog("#fullreload Received data with wrong sequence number or hver! Performing full reload of client data from URL: " + options.url);
          fullLoad();
        } else if (body["delete"]) {
          options.seq = body.seq;
          var item = cbDelete(body["delete"]);
          options.store.valueHasMutated();
          if (item) {
            item.destroy();
          }
        } else if (body.data) {
          options.seq = body.seq;
          if (cbSet(body.data)) {
            options.store.valueHasMutated();
          }
        }
      });

      clientLogger.debugLog("#fullreload Performing full load of data on startup.");
      fullLoad();
    };
  });
